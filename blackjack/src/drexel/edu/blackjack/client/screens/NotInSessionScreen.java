package drexel.edu.blackjack.client.screens;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This is the user interface that shows when the
 * user is waiting to join a session.
 * 
 * @author Jennifer
 */
public class NotInSessionScreen extends AbstractScreen {
	
	// And keep a copy to itself for the singleton pattern
	private static NotInSessionScreen notInSessionScreen = null;
	
	public NotInSessionScreen( BlackjackCLClient client, ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		// It starts in the ENTER_USERNAME state, but inactive
		super(client, thread,helper);
		setScreenType( AbstractScreen.SCREEN_TYPE.NOT_IN_SESSION_SCREEN );
		setIsActive( false );
		
	}

	@Override
	public void processMessage(ResponseCode code) {

		/**
		// One message we might receive says that the server is ready for the password
		if( code.hasSameCode( ResponseCode.CODE.WAITING_FOR_PASSWORD ) ) {
			loginScreenState = ENTER_PASSWORD;
			displayMenu();
		} else if( code.hasSameCode(ResponseCode.CODE.INVALID_LOGIN_CREDENTIALS) ) {
			// Another message says that the login was incorrect
			System.out.println( "The username and password you supplied is incorrect." );
			loginScreenState = ENTER_USERNAME;
			displayMenu();
		} else if( code.hasSameCode(ResponseCode.CODE.SUCCESSFULLY_AUTHENTICATED ) ) {
			// This needs to say that they successfully logged in, and move them to the next screen
			System.out.println( "Successfully logged in. Welcome to the game." );
			loginScreenState = ENTER_PASSWORD;	// Reset the internal state just in case....
			showNextScreen();	// Move to the next screen
		} else {
			super.handleResponseCode( code );
		}
		**/
		super.handleResponseCode( code );
	}
	
	/**
	 * Used to display whatever sort of command-line 'menu'
	 * to the screen that is appropriate.
	 */
	public void displayMenu() {

		System.out.println( "Someone needs to write the NotInSessionScreen.displayMenu() method." );
		System.out.println( "For now, just type in raw protocol commands." );
		
	}

	/**
	 * This is used in the singleton pattern so that only
	 * one not-in-session screen is instantiated at a time.
	 * 
	 * @param client Need a valid client
	 * @param thread Need a valid and active client input thread
	 * @param helper Need smoething for output
	 * @return The not-in-session screen
	 */
	public static AbstractScreen getDefaultScreen( BlackjackCLClient client, 
			ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		if( notInSessionScreen == null ) {
			notInSessionScreen = new NotInSessionScreen( client, thread, helper );
		}
		
		return notInSessionScreen;
		
	}

	@Override
	public void reset() {
		
		// For us, resetting the screen involves showing the menu again
		System.out.println( "Whoops, the user interface got confused. Let's try this again." );
		displayMenu();
		
	}

	@Override
	public void handleUserInput(String str) {
		
		// TODO: Something more sensible
		helper.sendRawText( str );

	}

}
