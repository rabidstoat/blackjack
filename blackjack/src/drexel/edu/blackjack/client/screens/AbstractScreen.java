package drexel.edu.blackjack.client.screens;

import java.util.logging.Logger;

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
	 * Local variables here
	 ***************************************************************/
	
	// Whether or not this particular screen is active. In general,
	// only one screen should be active at a time.
	protected boolean isActive;
	
	// Keep a pointer to the thread and helper that are associated with
	// this I/O for this screen.
	protected ClientInputFromServerThread clientThread;
	protected ClientOutputToServerHelper helper;
	
	// Our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(AbstractScreen.class .getName()); 
	
	/*****************************************************************
	 * Construct here
	 ***************************************************************/
	public AbstractScreen( ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
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


}
