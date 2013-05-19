package drexel.edu.blackjack.client.screens;

import java.util.logging.Logger;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.in.MessagesFromServerListener;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * A screen is something the presents some information
 * to the user, and expects them to enter something in
 * response.
 */
public abstract class AbstractScreen implements MessagesFromServerListener {

	/*****************************************************************
	 * Enum to define the type of screen
	 ***************************************************************/
	public enum SCREEN_TYPE {
		
		LOGIN_SCREEN,
		NOT_IN_SESSION_SCREEN,
		OTHER_SCREEN 
		
	}

	/*****************************************************************
	 * Local variables here
	 ***************************************************************/

	// What type of screen is it
	private SCREEN_TYPE screenType = SCREEN_TYPE.OTHER_SCREEN;	// Default to 'other'
	
	// Whether or not this particular screen is active. In general,
	// only one screen should be active at a time.
	protected boolean isActive;
	
	// Keep a pointer to the thread and helper that are associated with
	// this I/O for this screen.
	protected ClientInputFromServerThread clientThread;
	protected ClientOutputToServerHelper helper;
	
	// And to our client
	protected BlackjackCLClient client;
	
	// Our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(AbstractScreen.class .getName()); 
	
	/*****************************************************************
	 * Construct here
	 ***************************************************************/
	public AbstractScreen( BlackjackCLClient client, ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		this.client = client;
		this.clientThread = thread;
		this.helper = helper;
	}
	
	/*****************************************************************
	 * Abstract methods to be defined elsewhere
	 ***************************************************************/
	
	/**
	 * Used to display whatever sort of command-line 'menu'
	 * to the screen that is appropriate.
	 */
	public abstract void displayMenu();
	
	/**
	 * Resets the 'screen' to its starting state. This might
	 * be done after an error, for example.
	 */
	public abstract void reset();
	
	/**
	 * Process a response code from the server. This will only
	 * be called on an active screen.
	 */
	public abstract void processMessage( ResponseCode code );

	/**
	 * Used to pass user input to the screen. Do whatever is appropriate
	 */
	public abstract void handleUserInput( String str );

	/*****************************************************************
	 * Concrete methods implemented here
	 ***************************************************************/

	/**
	 * @return the screenType
	 */
	public SCREEN_TYPE getScreenType() {
		return screenType;
	}

	/**
	 * @param screenType the screenType to set
	 */
	public void setScreenType(SCREEN_TYPE screenType) {
		this.screenType = screenType;
	}	


	/**
	 * Notifies the screen that it is now the 'active' screen,
	 * and should start doing its I/O, if the value is true.
	 * Otherwise it's notifying it that it's no longer active
	 * and should stop doing I/O.
	 */
	public void setIsActive( boolean isActive ) {
		
		// If you're activating yourself, set yourself up as the default listener
		if( isActive ) {
			clientThread.setDefaultListener( this );
		}
		
		this.isActive = isActive;
	}
	
	/**
	 * Request that the user interface show the 'next screen', which is
	 * based on what the currentScreen is
	 */
	public void showNextScreen() {
		
		if( client == null ) {
			LOGGER.severe( "Cannot show the next user interface screen as we don't seem to have a client set." );
		} else {
			client.showNextScreen();
		}
	}
	
	/**
	 * Does the best it can to handle a response code that the
	 * implementing screen did not handle. This might be because
	 * it's a 'general error', or it might simply be something
	 * that was totally unexpected.
	 * 
	 * Handling the message might involve printing to the console
	 * or it might just be handled internally in a silent manner.
	 * 
	 * @param code
	 */
	public void handleResponseCode(ResponseCode code) {

		// Only handle if it's active
		if( isActive ) {
			
			if( code.isError() || code.isMalformed() ) {
				// Internal error? Or syntax error? Just reset the screen
				LOGGER.warning( "Received unhandled error code of '" + code.toString() + "'." );
				reset();
			} else if( code.isInformative() ) {
				// TODO: Handle general informative codes
				LOGGER.info( "Received unhandled informative code of '" + code.toString() + "'." );
			} else if( code.isGameState() ) {
				// TODO: Handle game state codes
				LOGGER.info( "Received unhandled game state code of '" + code.toString() + "'." );
			} else {
				// TODO: Not sure what to do here
				LOGGER.info( "Received some other unhandled code of '" + code.toString() + "'." );
			}
		}
	}

	@Override
	public void receivedMessage(ResponseCode code) {
		// Only ask an implementing class to process the message
		// if the screen is active
		if( isActive ) {
			processMessage( code );
		}
	}

	/**
	 * Print to the screen something about the capabilities
	 * @param code
	 */
	protected void displayCapabilities(ResponseCode code) {
		// TODO: Make this prettier
		System.out.println( code.getText() );
	}

	/**
	 * Print to the screen something about the version
	 * @param code
	 */
	protected void displayVersion(ResponseCode code) {
		// TODO: Make this prettier
		System.out.println( code.getText() );
	}

}
