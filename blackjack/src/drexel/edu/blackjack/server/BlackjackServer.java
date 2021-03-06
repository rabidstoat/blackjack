/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackServer.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is the main class of the blackjack server. It handles TLS
 * negotiation, then sits in never-ending loop of accepting client connections
 * and setting up their own thread for handling the connection.
 ******************************************************************************/
package drexel.edu.blackjack.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import drexel.edu.blackjack.server.locator.BlackjackLocatorThread;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * <b>SERVICE:</b> The main class for the blackjack server. It
 * binds a secure socket to the default protocol port (55555)
 * for the purpose of receiving connections. The exact spot in
 * the code where this is done is commented with the word
 * SERVICE.
 * <P>
 * <b>CONCURRENT:</b> It is in this main thread that the socket 
 * accepts connections, and creates a separate server thread for
 * each connection. This way, multiple clients can be handled.
 * The exact spot in the code where this is done is commented
 * with the word CONCURRENT.
 * <P>
 * <b>SECURITY:</b> Sockets are encrypted with TLS
 * <P>
 * <b>EXTRACREDIT:</b> We start a locator service here, which
 * is implemented in a class on its own thread.
 */
public class BlackjackServer {

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
	
	/**
	 *  Use this for our EOL when sending message. Determined by protocol.
	 */
	public static final String EOL						= "\n";
	
	/**
	 * How many bytes per line is the max? Determined by protocol.
	 */
	public static final int MAX_BYTES_PER_LINE			= 1024;
	
	private final static Logger LOGGER = BlackjackLogger.createLogger(BlackjackServer.class .getName()); 
	
	/************************************************************
	 * Main method is here! And constructor!
	 ***********************************************************/
	
	/**
	 * Creates an instance of the blackjack server.
	 */
	public BlackjackServer() {
	}
	
	/**
	 * The main method that starts the server up
	 * 
	 * @param args No arguments expected
	 */
	public static void main(String[] args) {
		
		BlackjackServer server = new BlackjackServer();
		server.runServer();
				
	}

	/**
	 * Start the socket, using TLS encryption, and listen 
	 * for connections. When a connection is received,
	 * create a thread to handle the connection.
	 */
	private void runServer() {
		
		// We need to keep track of this
		ServerSocket serverSocket = null;
		
        try {
        	// SECURITY: In here is where it creates a secure server socket
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
            SSLServerSocketFactory ssf = sc.getServerSocketFactory();
            
            // SERVICE: This is where the socket binds to its predetermined port.
            serverSocket = ssf.createServerSocket(PORT);
            LOGGER.info( "Started a server on port " + PORT );
            System.out.println( "The server is now ready to accept connections on port " + PORT + "." );
            
            // EXTRACREDIT: This is where we start our locator service, for handling
            // client inquiries on the LAN about where a BJP 1.0 server is
            BlackjackLocatorThread locatorThread = new BlackjackLocatorThread();
            locatorThread.start();
            
            // Now we do an endless loop, accepting clients
            while( true ) {
            	// CONCURRENT: This is where each new connection gets its
            	// own thread to deal with it
            	new BlackjackServerThread( serverSocket.accept() ).start();
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
        	if( serverSocket != null ) {
        		try {
					serverSocket.close();
				} catch (IOException e) {
					// We're about to exit anyway, so oh well
					e.printStackTrace();
				}
        	}
        }

	}

}
