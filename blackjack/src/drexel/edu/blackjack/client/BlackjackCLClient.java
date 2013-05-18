package drexel.edu.blackjack.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.client.screens.ErrorDisplay;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Need some comments.
 */
public class BlackjackCLClient {

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
	
	private final static Logger LOGGER = BlackjackLogger.createLogger(BlackjackCLClient.class .getName()); 
	
	/************************************************************
	 * Main method is here! And constructor!
	 ***********************************************************/
	
	/**
	 * Constructor needs a comment
	 */
	public BlackjackCLClient() {
		// Don't really do anything
	}
	
	/**
	 * Again, with the no comments!
	 * 
	 * @param args No arguments expected
	 */
	public static void main(String[] args) {
		
		BlackjackCLClient client = new BlackjackCLClient();
		client.runClient();
				
	}

	/***********************************************************
	 * Private methods go here.
	 *********************************************************/
	
	/**
	 * Start the client and enter a loop to read input from keyboard.
	 * This will need to be much smarter to work as a true client.
	 */
	private void runClient() {

		// Here are some IO-related variables
        Socket socket = null;
		
        try {
            // Keystore
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
            socket = ssf.createSocket( "127.0.0.1", PORT );
            LOGGER.info( "Started a client connecting to localhost on port " + PORT );
            
            // Create the thread to handle input and start it up
            ClientInputFromServerThread input = new ClientInputFromServerThread( 
            		this, socket, new ErrorDisplay() );
            input.start();
            
            // Create the helper to handle output
            ClientOutputToServerHelper output = new ClientOutputToServerHelper( socket );
            
            // We read in input from the user from standard in, though
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Loop through as long as we have input
            String fromUser = stdIn.readLine();
            while( fromUser != null ) {
            	// We send whatever they typed to the server. When we really have a client
            	// implemented, there will be a command line UI and we'll have to generate
            	// real valid comands to send. But for now, let them type whatever.
            	output.sendRawText( fromUser );
            	
            	// And read the next line
            	fromUser = stdIn.readLine();
            }
            
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT + ".");
            e.printStackTrace();
            System.exit(1);
        } catch( KeyStoreException e ) {
            System.err.println("Keystore exception, uh oh.");
            e.printStackTrace();
            System.exit(1);
        } catch( NoSuchAlgorithmException e ) {
            System.err.println("No such algorithm exception, uh oh.");
            e.printStackTrace();
            System.exit(1);
        } catch( CertificateException e ) {
            System.err.println("Certificate exception." );
            e.printStackTrace();
            System.exit(1);
        } catch( KeyManagementException e ) {
            System.err.println("Key management exception." );
            e.printStackTrace();
            System.exit(1);
        } catch( UnrecoverableKeyException e ) {
            System.err.println("Unrecoverable key exception." );
            e.printStackTrace();
            System.exit(1);
        } finally {
        	// Always nice to clean up
        	if( socket != null ) {
        		try {
					socket.close();
				} catch (IOException e) {
					// We're about to exit anyway, so oh well
				}
        	}
        }

	}

	/**
	 * This method is called when the client needs to be shut down,
	 * for example, because the server has broken the connection.
	 * Probably it should be printing a message and calling the
	 * System.exit() method.
	 */
	public void notifyOfShutdown() {
		
		System.out.println( "You have been disconnected from the server." );
		System.exit(0);
		
	}


}
