/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackProtocol.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Connects multiple threads to the server and sends randomly created
 * data to it.
 ******************************************************************************/
package drexel.edu.blackjack.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import drexel.edu.blackjack.server.BlackjackServer;
import drexel.edu.blackjack.server.commands.AccountCommand;
import drexel.edu.blackjack.server.commands.BetCommand;
import drexel.edu.blackjack.server.commands.PasswordCommand;
import drexel.edu.blackjack.server.commands.UsernameCommand;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This is a very simple, very naive fuzz testing class. It launches a number
 * of threads, which are of one of three types: 1) the type that sends random binary
 * data of a random length within a predetermined range from the start; 2) the type 
 * that logs in first (relies on having usernames and passwords set up of the format 
 * 'user#/password', so for example a username of 'user1' and a password of 
 * 'password', base 0) and then send random binary data of a random length within a
 * predetermined range; or, 3) the type that logs in and executes semi-random
 * but valid commands.
 * <P>
 * There are default values set for the number of threads of each type, the
 * number of messages to send, and the maximum length of randomly created
 * strings. They can be overridden with environment variables. The environment
 * variables, and the default values, are shown below:
 * <P>
 * <UL>
 * <LI><b>type1threads:</b> The number of type 1 threads to run simultaneously.
 * Defaults to 20.
 * <LI><b>type2threads:</b> The number of type 2 threads to run simultaneously.
 * Defaults to 20.
 * <LI><b>type3threads:</b> The number of type 3 threads to run simultaneously.
 * Defaults to 20.
 * <LI><b>type3threads:</b> The number of type 3 threads to run simultaneously.
 * Defaults to 20.
 * <LI><b>minlength:</b> For type 1 and 2, the minimum length of the binary
 * stream that gets passed to the server. Defaults to 4.
 * <LI><b>maxlength:</b> For type 1 and 2, the maximum length of the binary
 * stream that gets passed to the server. Defaults to 1000.
 * <LI><b>minnumber:</b> For type 1 and 2, the minimum number of messages that
 * will get sent to the server. Defaults to 50.
 * <LI><b>maxnumber:</b> For type 1 and 2, the maximum number of messages that
 * will get sent to the server. Defaults to 200.
 * </UL> 
 */
public class SimpleFuzzTest extends Thread {

	/************************************************************
	 * Environment variable names
	 *********************************************************/
	// Override number of type 1 threads to run
	private static final String TYPE_1_THREADS	= "type1threads";
	// Override number of type 2 threads to run
	private static final String TYPE_2_THREADS	= "type2threads";
	// Override number of type 3 threads to run
	private static final String TYPE_3_THREADS	= "type3threads";
	// Override minimum length of generated random binary strings
	private static final String MIN_LENGTH		= "minlength";
	// Override maximum length of generated random binary strings
	private static final String MAX_LENGTH		= "maxlength";
	// Override minimum number of messages/commands to send
	private static final String MIN_NUMBER		= "minnumber";
	// Override maximum number of messages/commands to send
	private static final String MAX_NUMBER		= "maxnumber";
	
	/************************************************************
	 * Some hard-coded security information is here. Maybe
	 * there is a better place for this?
	 ***********************************************************/
	
	// I think JKS = Java KeyStore
	private static final String KEYSTORE_TYPE			= "JKS";
	
	// This will be read from the classpath. The keystore has the single
	// blackjack certificate in it that both the client and the server
	// use. We could just have the certificate hosted and have people
	// install them in their own local keystore, but that's a real pain
	// just for development purposes.
	private static final String KEYSTORE_FILE			= "blackjack.keystore";	
	
	// Super secret password is... 'password'. It's the password for the
	// keystore.
	private static final String KEYSTORE_PASSWORD		= "password";
	
	// The certificate password is the same
	private static final String CERTIFICATE_PASSWORD	= "password";
	
	// This is the type of the certificate that was self-signed
	private static final String CERTIFICATE_TYPE		= "SunX509";
	
	// This is the protocol we'll use for security
	private static final String SECURITY_PROTOCOL		= "TLS";
	
	// Finally, the port that the server will run on
	private static final int PORT						= 55555;
	
	// Our host
	private String hostIP								= null;
	
	// Here are some IO-related variables
    private Socket socket = null;	

	/************************************************************
	 * Other things
	 ***********************************************************/
	
	// Our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(SimpleFuzzTest.class .getName()); 
	
	// How many connections of each type, by default
	private final static int DEFAULT_TYPE_1_CONNECTIONS = 20;
	private final static int DEFAULT_TYPE_2_CONNECTIONS = 20;
	private final static int DEFAULT_TYPE_3_CONNECTIONS = 20;
	
	// And send this many 'garbage lines' to the server, by default
	private final static int DEFAULT_MINIMUM_LINES 		= 50;
	private final static int DEFAULT_MAXIMUM_LINES 		= 200;
	
	// Minimum and maximum length (in bytes) of the line, by default
	private final static int DEFAULT_MINIMUM_LENGTH 	= 4;
	private final static int DEFAULT_MAXIMUM_LENGTH 	= 1000;
	
	// Actual number of connections of each type
	private static int numberOfType1Connections 	= DEFAULT_TYPE_1_CONNECTIONS;
	private static int numberOfType2Connections 	= DEFAULT_TYPE_2_CONNECTIONS;
	private static int numberOfType3Connections 	= DEFAULT_TYPE_3_CONNECTIONS;
	
	// Actual range of lines to send the server
	private static int minimumLines 				= DEFAULT_MINIMUM_LINES;
	private static int maximumLines					= DEFAULT_MAXIMUM_LINES;
	
	// Actual range of message length
	private static int minimumLength				= DEFAULT_MINIMUM_LENGTH;
	private static int maximumLength				= DEFAULT_MAXIMUM_LENGTH;
	
	// Give each thread a number. For type 2 and 3 threads, there is the expectation
	// that a username of 'user#' with a password 'password' will exist.
	private int threadNumber;
	
	// What type of testing it is (1-3)
	private int type;
	
	// Output streams and writers
	private DataOutputStream out;
	private PrintWriter writer;
	
	// For randomization
	private Random random;
	
	// For pausing between commands slightly, pause for at least this many milliseconds
	private int PAUSE_BASE		= 10;
	// And up to this many additional milliseconds
	private int PAUSE_VARIABLE	= 20;
	
	// An array of random command lines to send
	private List<String> commandStrings = null;
	
	/*******************************************************************************************
	 * Methods related to initializing the system
	 ******************************************************************************************/
	
	/**
	 * Simple constructor
	 * 
	 * @param hostIP The IP address to connect to
	 * @param type The type of test this thread will run. Types are:
	 * <OL>
	 * <LI>Connect and send random binary data
	 * <LI>Connect, authenticate, and send random binary data
	 * <LI>Connect, authenticate, and send random commands
	 * </OL>
	 */
	public SimpleFuzzTest( String hostIP, int type ) {
		this.hostIP = hostIP;
		this.type = type;
		initializeCommandStrings();
	}
	
	/**
	 * Create an ordered list of command strings
	 */
	private void initializeCommandStrings() {
		commandStrings = new ArrayList<String>();
		
		commandStrings.add( AccountCommand.COMMAND_WORD );
		commandStrings.add( BetCommand.COMMAND_WORD );
	}

	/**
	 * First optional argument is server host address.
	 * 
	 * @param args First one is the optional IP address, default to localhost
	 */
	public static void main(String[] args) {
		
		// Sort out the host
		String hostIP = "127.0.0.1";
		if( args != null && args.length > 0 ) {
			hostIP = args[0];
		}
		
		// Read environment variables to override defaults
		checkOverriddenDefaults();
		
		// Create a bunch of connections
		boolean success = true;
		int userid=0;
		
		for( int i = 0; i < numberOfType1Connections && success; i++ ) {
			SimpleFuzzTest tester = new SimpleFuzzTest(hostIP,1);
			if( tester.connect(userid++) ) {
				tester.start();
			} else {
				LOGGER.severe( "Could not connect to server " + hostIP );
				success = false;
			}
		}

		for( int i = 0; i < numberOfType2Connections && success; i++ ) {
			SimpleFuzzTest tester = new SimpleFuzzTest(hostIP,2);
			if( tester.connect(userid++) ) {
				tester.start();
			} else {
				LOGGER.severe( "Could not connect to server " + hostIP );
				success = false;
			}
		}
		
		for( int i = 0; i < numberOfType3Connections && success; i++ ) {
			SimpleFuzzTest tester = new SimpleFuzzTest(hostIP,3);
			if( tester.connect(userid++) ) {
				tester.start();
			} else {
				LOGGER.severe( "Could not connect to server " + hostIP );
				success = false;
			}
		}
	}

	/**
	 * Connect to the given server, using TLS encryption
	 * 
	 * @param threadNumber a unique number, just for debugging
	 * @return True if connection was established, else false
	 */
	private boolean connect( int threadNumber ) {

		this.threadNumber = threadNumber;
		
		// Pessimistic!
		boolean success = false;
		
    	// In here a secure socket is established with TLS as encryption Keystore
		try {
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
	        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream( KEYSTORE_FILE );
	        ks.load(inputStream,KEYSTORE_PASSWORD.toCharArray() );

	        // Key manager factory
	        KeyManagerFactory kmf =
	           KeyManagerFactory.getInstance(CERTIFICATE_TYPE);
	        kmf.init( ks, CERTIFICATE_PASSWORD.toCharArray() );

	        // Trust manager factory
	        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
	              TrustManagerFactory.getDefaultAlgorithm() );
	        tmf.init(ks);

	        // Together into the SSL context
	        SSLContext sc = SSLContext.getInstance(SECURITY_PROTOCOL);
	        sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(),null);

	        // And finally for a socket
	        SSLSocketFactory ssf = sc.getSocketFactory();
	        System.out.println("Connecting to server at " + hostIP + "...");
	        socket = ssf.createSocket( hostIP, PORT );
	        
	        LOGGER.info( "Started a SimpleFuzzTest connecting to " + hostIP + " on port " + PORT );
	        
	        success = true;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		        
        return success;
	}

	/**
	 * Looks for environment variables that override defaults defined
	 * in the code for variable things like what tests to run, how
	 * many, what size data, etc.
	 */
	private static void checkOverriddenDefaults() {
		
		String value = System.getProperty(TYPE_1_THREADS);
		if( value != null ) {
			try {
				numberOfType1Connections = Integer.parseInt(value);
			} catch( NumberFormatException e ) {
				System.err.println( "Incorrect specification of " + TYPE_1_THREADS + ": " + value );
			}
		}
		
		value = System.getProperty(TYPE_2_THREADS);
		if( value != null ) {
			try {
				numberOfType2Connections = Integer.parseInt(value);
			} catch( NumberFormatException e ) {
				System.err.println( "Incorrect specification of " + TYPE_2_THREADS + ": " + value );
			}
		}

		value = System.getProperty(TYPE_3_THREADS);
		if( value != null ) {
			try {
				numberOfType3Connections = Integer.parseInt(value);
			} catch( NumberFormatException e ) {
				System.err.println( "Incorrect specification of " + TYPE_3_THREADS + ": " + value );
			}
		}

		value = System.getProperty(MIN_LENGTH);
		if( value != null ) {
			try {
				minimumLength = Integer.parseInt(value);
			} catch( NumberFormatException e ) {
				System.err.println( "Incorrect specification of " + MIN_LENGTH + ": " + value );
			}
		}
		
		value = System.getProperty(MAX_LENGTH);
		if( value != null ) {
			try {
				maximumLength = Integer.parseInt(value);
			} catch( NumberFormatException e ) {
				System.err.println( "Incorrect specification of " + MAX_LENGTH + ": " + value );
			}
		}

		value = System.getProperty(MIN_NUMBER);
		if( value != null ) {
			try {
				minimumLines = Integer.parseInt(value);
			} catch( NumberFormatException e ) {
				System.err.println( "Incorrect specification of " + MIN_NUMBER + ": " + value );
			}
		}
		
		value = System.getProperty(MAX_NUMBER);
		if( value != null ) {
			try {
				maximumLines = Integer.parseInt(value);
			} catch( NumberFormatException e ) {
				System.err.println( "Incorrect specification of " + MAX_NUMBER + ": " + value );
			}
		}
	}
	
	/***************************************************************************************
	 * Methods related to running the tests.
	 **************************************************************************************/

	/**
	 * Main testing method
	 */
	public void run() {
		
		try {
			// Establish needed variables
			out = new DataOutputStream(socket.getOutputStream());
			writer = new PrintWriter(socket.getOutputStream(), true);
			random = new Random();
			
			// Run the test
			if( type == 1 ) {
				doType1Testing( computeNumberOfLines() );
			} else if( type == 2 ) {
				doType2Testing( computeNumberOfLines() );
			} else {
				doType3Testing( computeNumberOfLines() );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			// Free up the resources
			if( out != null ) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if( writer != null ) {
				writer.close();
			}
			if( socket != null ) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}

	}

	/**
	 * Type 1 testing connects and, without authenticating, sends a
	 * number of randomly created bytes to the server.
	 * 
	 *  @param numberOfLines How many lines (streams) to send
	 */
	private void doType1Testing( int numberOfLines ) {
		 sendRandomBinaryData(numberOfLines);		
	}

	/**
	 * Type 2 testing connects, authenticates, and sends a
	 * number of randomly created bytes to the server. 
	 * 
	 *  @param numberOfLines How many lines (streams) to send
	 */
	private void doType2Testing( int numberOfLines ) {
		authenticate();
		sendRandomBinaryData(numberOfLines);		
	}

	/**
	 * Type 3 testing connects, authenticates, and sends random
	 * commands to the server.
	 * 
	 *  @param numberOfLines How many lines (streams) to send
	 */
	private void doType3Testing( int numberOfLines ) {
		authenticate();
		sendRandomCommands(numberOfLines);
	}

	/*****************************************************************************
	 * And some more helper methods for doing so.
	 ****************************************************************************/

	/**
	 * Figure out how many messages/commands to send the server. 
	 * This should be a number between minimumNumber and
	 * maximumNumber
	 * 
	 * @return A number between minimumNumber and maximumNumber
	 */
	private int computeNumberOfLines() {
		int delta = maximumLines + minimumLines;
		return minimumLines + random.nextInt(delta);
	}

	/**
	 * Figure out what length to use for the next stream of binary
	 * data. This should be a number between minimumLength and
	 * maximumLength
	 * 
	 * @return A number between minimumLength and maximumLength
	 */
	private int computeStreamLength() {
		int delta = maximumLength - minimumLength;
		return minimumLength + random.nextInt(delta);
	}

	/**
	 * Given the connection class variables, and a number of lines
	 * (streams) to send, send out this number of random binary
	 * streams. Pause very slightly between sends.
	 * 
	 * @param numberOfLines How many streams
	 */
	private void sendRandomBinaryData(int numberOfLines) {
		try {
			for( int i = 0; i < numberOfLines; i++ ) {
				// Make a byte array to send
				byte[] bytes = new byte[computeStreamLength()];
				random.nextBytes(bytes);
				out.write(bytes);
				
				// Pause slightly
				try {
					Thread.sleep( random.nextInt(PAUSE_VARIABLE)+PAUSE_BASE );
				} catch( InterruptedException e ) {
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Given the connection class variables, and a number of lines
	 * to send, send out this number of random commands (both
	 * correctly formatted and incorrectly formatted) to the server.
	 * Pause very slightly between sends.
	 * 
	 * @param numberOfLines How many commands
	 */
	private void sendRandomCommands(int numberOfLines) {
		for( int i = 0; i < numberOfLines; i++ ) {
			sendRawText( getRandomCommand() );
			
			// Pause slightly
			try {
				Thread.sleep( random.nextInt(PAUSE_VARIABLE)+PAUSE_BASE );
			} catch( InterruptedException e ) {
			}
		}
	}

	/**
	 * Comes up with a random command to send.
	 *  
	 * @return The string to send, probably EOL terminated but maybe not
	 */
	private String getRandomCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Authenticates the user with the server, sending USERNAME/PASSWORD
	 * protocol messages. Username is 'user<threadNumber>' and password
	 * is 'password'.
	 */
	private void authenticate() {
		
		sendRawText( UsernameCommand.COMMAND_WORD + " user" + threadNumber + BlackjackServer.EOL );
		sendRawText( PasswordCommand.COMMAND_WORD + " password"+ BlackjackServer.EOL );
		
	}

	/**
	 * Sends raw text to the server, exactly as it's presented.
	 * 
	 * @param text What to send
	 * @return True if it was successfully sent, else false
	 */
	public boolean sendRawText( String text ) {
		
		// Can't send a message if we didn't open the writer
		if( writer == null ) {
			return false;
		}
		
		// Otherwise, try to do what we can do
		try {
			writer.println( text );
			writer.flush();
		} catch( Exception e ) {
			LOGGER.severe( "Had a problem writing to the socket." );
			e.printStackTrace();
			return false;
		}
		
		// If we got this far, assume success!
		return true;
	}
	
}
