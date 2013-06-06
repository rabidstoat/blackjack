package drexel.edu.blackjack.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Random;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import drexel.edu.blackjack.util.BlackjackLogger;

public class SimpleFuzzTest extends Thread {

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
	
	// Start this many connections to test
	private final static int NUMBER_OF_CONNECTIONS = 100;
	
	// And send this many 'garbage lines' to the server
	private final static int MAXIMUM_GARBAGE_LINES = 100;
	
	// Minimum and maximum length (in bytes) of the line
	private final static int MAXIMUM_LENGTH = 500;
	
	// Give each thread a number
	private int threadNumber;

	
	/**
	 * Simple constructor
	 */
	public SimpleFuzzTest( String hostIP ) {
		this.hostIP = hostIP;
	}
	
	/**
	 * First optional argument is server host address.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Sort out the host
		String hostIP = "127.0.0.1";
		if( args != null && args.length > 0 ) {
			hostIP = args[0];
		}
		
		// Create a bunch of connections
		boolean success = true;
		for( int i = 0; i < NUMBER_OF_CONNECTIONS && success; i++ ) {
			SimpleFuzzTest tester = new SimpleFuzzTest(hostIP);
			if( tester.connect(i) ) {
				tester.start();
			} else {
				LOGGER.severe( "Could not connect to server " + hostIP );
				success = false;
			}
		}
	}

	/**
	 * Main testing method
	 */
	public void run() {
		
		// Get an output stream
		 try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			Random random = new Random();
			for( int i = 0; i < MAXIMUM_GARBAGE_LINES; i++ ) {
				// Make a byte array of random length
				byte[] bytes = new byte[random.nextInt(MAXIMUM_LENGTH) + 1];
				random.nextBytes(bytes);
				out.write(bytes);
				try {
					Thread.sleep( random.nextInt(200)+10 );
				} catch( InterruptedException e ) {
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
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

}
