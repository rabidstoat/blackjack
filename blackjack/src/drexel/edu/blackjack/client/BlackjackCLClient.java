/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackCLClient.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is the main class for the client, which runs the whole client-
 * side application. The CL stands for 'Command Line', which it is. Currently
 * this is where the TLS-encrypted socket is established, and has the thread
 * from which user input is read from standard in. It starts another thread
 * for reading input from the server.
 ******************************************************************************/
package drexel.edu.blackjack.client;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.locator.BlackjackHostSeeker;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.client.screens.AbstractScreen;
import drexel.edu.blackjack.client.screens.InSessionScreen;
import drexel.edu.blackjack.client.screens.LoginInputScreen;
import drexel.edu.blackjack.client.screens.NotInSessionScreen;
import drexel.edu.blackjack.client.screens.util.ClientSideGame;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This is the main class of the blackjack client.
 * <P>
 * <b>UI:</b> The work involved in setting up the user interface
 * is done in this class.
 * <P>
 * <b>CLIENT:</b> The specification of the hostname happens here.
 * <P>
 * <b>SERVICE:</b> The port to connect to is hardcoded. See
 * specific comments with this keyword in them in the source.
 * <p>
 * <b>SECURITY:</b> Sockets are encrypted with TLS
 * <p>
 * <b>EXTRACREDIT:</b> If no host is specified, it searches
 * for one on the LAN.
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
	
	// SERVICE: Finally, the port that the server will run on
	private static final int PORT						= 55555;
	
	// This system property is set true if we should show the message frame
	private static final String SHOW_MESSAGES			= "showmessages";
	
	// Our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(BlackjackCLClient.class .getName()); 
	
	// Host to connect to, local if not set in arguments
	private String host;
	
	// And keep track of the 'screen' that is 'up' on the clint
	private AbstractScreen currentScreen = null;
	
	// Keeps track of the games that the client knows about
	private Map<String,ClientSideGame> gameMap = new HashMap<String,ClientSideGame>();
	
	// And the current game they're in, if known
	private ClientSideGame currentGame = null;
	
	// ALong with the input and output
	private ClientOutputToServerHelper output = null;
	private ClientInputFromServerThread input = null;
	
	// If it's been told to operate in debug mode, and using an alternate UI
	private boolean debugMode = false;
	
	// Whether or not the client is in headless mode (with -Djava.awt.headless=true)
	private Boolean headless = null;
	
	/************************************************************
	 * Main method is here! And constructor!
	 ***********************************************************/
	
	/**
	 * Set up host ip in constructor, default to Localhost
	 */
	public BlackjackCLClient(String host, boolean debugMode) {
		// CLIENT: This is where we set the host as a local class variable
		this.host = host;
		this.debugMode = debugMode;
	}
	
	/**
	 * The main class, mostly just handles the command-line arguments (if any)
	 * before creating an instance of this class to run things.
	 * 
	 * @param args remote's host ip and "--debug" if debug mode is desired, both are optional
	 * e.g. "144.118.34.12 --debug"
	 */
	public static void main(String[] args) {
		// Maybe cross-platform will work on mac
		try {
			UIManager.setLookAndFeel(
			        UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
		BlackjackCLClient client;
		boolean debugMode = false;
		
		// EXTRACREDIT: We initialize the hostIP to null, which will be our
		// signal to use the locator service to figure out where the
		// blackjack server is running on the local LAN
		String hostIP = null;
		if (args.length > 0 && args[args.length - 1].equals("--debug")) 
			debugMode = true;
		
		// CLIENT: But here is where it is possible to specify a host (IP
		// or by name) on the command line, if desired
		if (args.length > 0 && !args[0].equals("--debug")) {
			hostIP = args[0];
		}
		
		// EXTRACREDIT:If the host wasn't specified we will use our
		// locator service to try to find it
		if( hostIP == null ) {
			try {
				// EXTRACREDIT: Use our host seeker to try to find a blackjack server on the LAN
				BlackjackHostSeeker seeker = new BlackjackHostSeeker();
				hostIP = seeker.tryToLocateServerOnLAN();
				
				// EXTRACREDIT: Report on success or failure
				if( hostIP == null ) {
					System.out.println( "Unable to find a blackjack server on the LAN. Sorry." );
				} else {
					System.out.println( "Located a BJP 1.0 server at " + hostIP + " to connect to!" );
				}
			} catch (UnknownHostException e) {
				// Silently fail as we have no idea how to recover
			} catch (SocketException e) {
				// Silently fail as we have no idea how to recover
			} catch (IOException e) {
				// Silently fail as we have no idea how to recover
			}
		}
		
		// Do we have an address? Hopefully!
		if( hostIP != null ) {
			// CLIENT: But if we do, that is what we pass into the constructor
			client = new BlackjackCLClient(hostIP, debugMode);
			client.runClient();
		}
				
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
        	// SECURITY: In here a secure socket is established with TLS as encryption
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
            System.out.println("Connecting to server at " + host + "...");
            // SERVICE: Use the protocol's defined port
            // CLIENT: Use the host that we passed into the constructor
            socket = ssf.createSocket( host, PORT );
            LOGGER.info( "Started a client connecting to " + host + " on port " + PORT );
            
            // Create the thread to handle input and start it up
            input = new ClientInputFromServerThread( this, socket );
            input.start();
            
            // Create the helper to handle output
            output = new ClientOutputToServerHelper( socket, isHeadless() );
            
            if (debugMode) {
            	
            	setScreen(new DebugClientScreen(this, input, output), true);
            	
            } else {
	
	            // Set the screen to the login input screen
	            setScreen( LoginInputScreen.getDefaultScreen( this, input, output ), true );
            	
                // Set up the messages frame
                if( "true".equals(System.getProperty(SHOW_MESSAGES)) && !isHeadless() ) {
                	MessageFrame.getDefaultMessageFrame().setLocationRelativeTo(null);
                	MessageFrame.getDefaultMessageFrame().setVisible(true);
                }
            }
            
            // We read in input from the user from standard in, though
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

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
        	System.err.println( "Unable to open a connection to the blackjack server at " + host + "." );
            System.exit(1);
        } catch( KeyStoreException e ) {
            System.err.println("Keystore exception, uh oh, something is wrong with the security.");
            e.printStackTrace();
            System.exit(1);
        } catch( NoSuchAlgorithmException e ) {
            System.err.println("No such security algorithm exception, uh oh, this has never happened before!");
            e.printStackTrace();
            System.exit(1);
        } catch( CertificateException e ) {
            System.err.println("Certificate exception. This is probably really bad." );
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

	/**
	 * <b>UI:</b> Establishes the default screen. It should set any previous
	 * screen to active, set this new screen as active, and dispay
	 * the menu.
	 * 
	 * @param screen The screen to advance (or retreat) to
	 * @param displayMenu True if it should immediately show the menu
	 */
	private void setScreen(AbstractScreen screen, boolean displayMenu ) {
		
		// Set any previous screen inactive
		if( this.currentScreen != null ) {
			this.currentScreen.setIsActive( false );
		}
		
		// Handle establishing this new screen
		if( screen != null ) {
			
			screen.setIsActive( true );
			if( displayMenu ) {
				screen.displayMenu();
			}
			currentScreen = screen;
			
		}
	}

	/**
	 * <b>UI:</b> Do whatever needs to be done to show the next interface screen.
	 * This typically involves looking at the current screen, then
	 * finding an appropriate new screen, and setting it.
	 * 
	 * @param displayMenu True if it should immediately show the menu
	 */
	public void showNextScreen( boolean displayMenu ) {
		
		if( currentScreen == null ) {
			LOGGER.severe( "Could not showNextScreen() because no current screen was set." );
		} else if( currentScreen.getScreenType() != null && 
				currentScreen.getScreenType().equals(AbstractScreen.SCREEN_TYPE.LOGIN_SCREEN) ) {
			// If we were showing the login screen, now need to show the NotInSessionScreen
			this.setScreen( NotInSessionScreen.getDefaultScreen(this, input, output), displayMenu );
		} else if( currentScreen.getScreenType() != null && 
				currentScreen.getScreenType().equals(AbstractScreen.SCREEN_TYPE.NOT_IN_SESSION_SCREEN) ) {
			// If we were showing the NotInSession screen, now need to show the InSessionScreen
			this.setScreen( InSessionScreen.getDefaultScreen(this, input, output), displayMenu );
		} else {
			// This is a weird error
			LOGGER.severe( "Had a request to move to the next UI screen, but there was no next one defined." );
		}
	}

	/**
	 * <b>UI:</b> Do whatever needs to be done to show the previous interface screen.
	 * Probably this only makes sense moving from 'in session' to 'not
	 * in session' user interfaces.
	 * 
	 * @param displayMenu True if it should immediately show the menu
	 */
	public void showPreviousScreen( boolean displayMenu ) {
		
		if( currentScreen == null ) {
			LOGGER.severe( "Could not showPreviousScreen() because no current screen was set." );
		} else if( currentScreen.getScreenType() != null && 
				currentScreen.getScreenType().equals(AbstractScreen.SCREEN_TYPE.IN_SESSION_SCREEN) ) {
			this.setScreen( NotInSessionScreen.getDefaultScreen(this, input, output), displayMenu );
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
	
	/**
	 * The client keeps track of games in a hash map
	 * 
	 * @param gameMap Set that map
	 */
	public void setGameMap( Map<String,ClientSideGame> gameMap ) {
		this.gameMap = gameMap;
	}

	/**
	 * Sometimes (hopefully always) the client is told which game
	 * the user is playing. This is so information can be presented
	 * to the user about the game, if needed.
	 *  
	 * @param gameId Should be to a valid game in a previously
	 * set game map
	 */
	public void setCurrentGameById(String gameId) {
		currentGame = (gameMap == null ? null : gameMap.get(gameId) );
	}
	
	/**
	 * Returns the game the player is currently (or most
	 * recently) are playing. This shouldn't be null if
	 * things are working right, but it might be.
	 * 
	 * @return The game that the user is playing
	 */
	public ClientSideGame getCurrentGame() {
		return currentGame;
	}
	
	/**
	 * <b>UI:</b> If the message frame is showing, hide it. If it's
	 * hiding, show it. Note that this can only be done if the 
	 * client is NOT in headless mode, which is specified with 
	 * the -Djava.awt.headless=true property
	 */
	public void toggleMessageFrame() {
		
		if( isHeadless() ) {
			System.out.println( "The message frame is unavailable in headless mode." );
		} else {
			MessageFrame frame = MessageFrame.getDefaultMessageFrame();
			if( frame != null ) {
				frame.setVisible( !frame.isVisible() );
			}
		}
	}

	/**
	 * Checks to see if the client has been designated
	 * as running in a headless environment or not. Return
	 * true if it's designated as headless, false otherwise.
	 * 
	 * @return True if headless, false otherwise
	 */
	public boolean isHeadless() {
		if( headless == null ) {
			GraphicsEnvironment ge = 
			        GraphicsEnvironment.getLocalGraphicsEnvironment();
			headless = Boolean.valueOf( ge.isHeadlessInstance() );
		}
		
		return headless.booleanValue();
	}

}
