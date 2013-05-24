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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.client.screens.AbstractScreen;
import drexel.edu.blackjack.client.screens.ClientSideGame;
import drexel.edu.blackjack.client.screens.InSessionScreen;
import drexel.edu.blackjack.client.screens.LoginInputScreen;
import drexel.edu.blackjack.client.screens.NotInSessionScreen;
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
	
	// This system property is set true if we should show the message frame
	private static final String SHOW_MESSAGES			= "ShowMessages";
	
	// Our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(BlackjackCLClient.class .getName()); 
	
	// And keep track of the 'screen' that is 'up' on the clint
	private AbstractScreen currentScreen = null;
	
	// Keeps track of the games that the client knows about
	private Map<String,ClientSideGame> gameMap = new HashMap<String,ClientSideGame>();
	
	// ALong with the input and output
	private ClientOutputToServerHelper output = null;
	private ClientInputFromServerThread input = null;
	
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
            input = new ClientInputFromServerThread( 
            		this, socket );
            input.start();
            
            // Create the helper to handle output
            output = new ClientOutputToServerHelper( socket );
            
            // We read in input from the user from standard in, though
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Set the screen to the login input screen
            setScreen( LoginInputScreen.getDefaultScreen( this, input, output ) );
            
            // Set up the messages frame
            if( "true".equals(System.getProperty(SHOW_MESSAGES)) ) {
            	MessageFrame.getDefaultMessageFrame().setLocationRelativeTo(null);
            	MessageFrame.getDefaultMessageFrame().setVisible(true);
            }

            // Loop through as long as we have input
            String fromUser = stdIn.readLine();
            while( fromUser != null ) {
            	
            	// We pass whatever they entered to to the default screen
            	if( this.currentScreen == null ) {
            		LOGGER.severe( "There was no default screen to funnel user input through." );
            	} else {
                	currentScreen.handleUserInput( fromUser );
            	}
            	
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
	 * Establishes the default screen. It should set any previous
	 * screen to active, set this new screen as active, and dispay
	 * the menu.
	 * 
	 * @param screen
	 */
	private void setScreen(AbstractScreen screen) {
		
		// Set any previous screen inactive
		if( this.currentScreen != null ) {
			this.currentScreen.setIsActive( false );
		}
		
		// Handle establishing this new screen
		if( screen != null ) {
			
			screen.setIsActive( true );
			screen.displayMenu();
			currentScreen = screen;
			
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

	/**
	 * Do whatever needs to be done to show the next interface screen.
	 * This typically involves looking at the current screen, then
	 * finding an appropriate new screen, and setting it.
	 */
	public void showNextScreen() {
		
		if( currentScreen == null ) {
			LOGGER.severe( "Could not showNextScreen() because no current screen was set." );
		} else if( currentScreen.getScreenType() != null && 
				currentScreen.getScreenType().equals(AbstractScreen.SCREEN_TYPE.LOGIN_SCREEN) ) {
			// If we were showing the login screen, now need to show the NotInSessionScreen
			this.setScreen( NotInSessionScreen.getDefaultScreen(this, input, output) );
		} else if( currentScreen.getScreenType() != null && 
				currentScreen.getScreenType().equals(AbstractScreen.SCREEN_TYPE.NOT_IN_SESSION_SCREEN) ) {
			// If we were showing the NotInSession screen, now need to show the InSessionScreen
			this.setScreen( InSessionScreen.getDefaultScreen(this, input, output) );
		} else {
			// This is a weird error
			LOGGER.severe( "Had a request to move to the next UI screen, but there was no next one defined." );
		}
	}

	/**
	 * Do whatever needs to be done to show the previous interface screen.
	 * Probably this only makes sense moving from 'in session' to 'not
	 * in session' user interfaces.
	 */
	public void showPreviousScreen() {
		
		if( currentScreen == null ) {
			LOGGER.severe( "Could not showPreviousScreen() because no current screen was set." );
		} else if( currentScreen.getScreenType() != null && 
				currentScreen.getScreenType().equals(AbstractScreen.SCREEN_TYPE.IN_SESSION_SCREEN) ) {
			this.setScreen( NotInSessionScreen.getDefaultScreen(this, input, output) );
		} else {
			// This is a weird error
			LOGGER.severe( "Had a request to move to the next UI screen, but there was no next one defined." );
		}
	}
		
	/**
	 * The client keeps track of games in a hash map.
	 * 
	 * @return That map
	 */
	public Map<String,ClientSideGame> getGameMap() {
		return gameMap;
	}
	
	public void setGameMap( Map<String,ClientSideGame> gameMap ) {
		this.gameMap = gameMap;
	}

}
