/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - LoginInputScreen.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is the 'screen' (really just command line UI prompts) used 
 * when a user logs into the system. It prompts for username and password and
 * uses the USERNAME and PASSWORD protocol commands.
 ******************************************************************************/
package drexel.edu.blackjack.client.screens;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This handles the user interface for logging into the
 * server with a username and password.
 * 
 * @author Jennifer
 */
public class LoginInputScreen extends AbstractScreen {
	
	// This screen can be in one of these three states
	private static final int JUST_STARTED	= 0;
	private static final int ENTER_USERNAME	= 1;
	private static final int ENTER_PASSWORD = 2;
	
	// Track which one it's in
	private int loginScreenState;
	
	// And keep a copy to itself for the singleton pattern
	private static LoginInputScreen loginInputScreen = null;
	
	private LoginInputScreen( BlackjackCLClient client, ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		// It starts in the ENTER_USERNAME state, but inactive
		super(client, thread,helper);
		setScreenType( AbstractScreen.SCREEN_TYPE.LOGIN_SCREEN );
		loginScreenState = JUST_STARTED;
		setIsActive( false );
		
	}

	@Override
	public void processMessage(ResponseCode code) {
		
		if( this.isActive ) {
			
			if( code == null ) {
				// This is bad
				reset();
			} else if( code.hasSameCode( ResponseCode.CODE.WAITING_FOR_PASSWORD ) ) {
				// One message we might receive says that the server is ready for the password
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
				showNextScreen( true );	// Move to the next screen
			} else if( code.hasSameCode(ResponseCode.CODE.LOGIN_ATTEMPTS_EXCEEDED ) ) {
				System.out.println( "Exceeded the allowed number of login attempts." );
				// They're about to get disconnected....
			} else if( code.hasSameCode(ResponseCode.CODE.ALREADY_LOGGED_IN ) ) {
				System.out.println( "That user is aleady logged in somewhere else." );
				// They're about to get disconnected....
			} else {
				super.handleResponseCode( code );
			}
		}
	}
	
	/**
	 * Used to display whatever sort of command-line 'menu'
	 * to the screen that is appropriate.
	 */
	public void displayMenu() {

		if( this.isActive ) {
			// We only want to show this when they first log in, not
			// on subsequent attempt to reconnect
			if( loginScreenState == JUST_STARTED ) {
				System.out.println( "***********************************************************" );
				System.out.println( "              Welcome to the Blackjack Game!" );
				System.out.println( "***********************************************************" );
				loginScreenState = ENTER_USERNAME;
			} 
			
			if( loginScreenState == ENTER_USERNAME ) {
				System.out.println( ">>>Please enter your username: " );
			} else if( loginScreenState == ENTER_PASSWORD ) {
				System.out.println( ">>>Please enter your password: " );
			} else {
				// This should never happen
				System.out.println( "We should never get here in the code. Oh great, you broke it!!!" );
			}
		}
		
	}

	/**
	 * This is used in the singleton pattern so that only
	 * one login screen is instantiated at a time.
	 * 
	 * @param thread Need a valid and active client input thread
	 * @return The login screen
	 */
	public static AbstractScreen getDefaultScreen( BlackjackCLClient client, 
			ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		if( loginInputScreen == null ) {
			loginInputScreen = new LoginInputScreen( client, thread, helper );
		}
		
		return loginInputScreen;
		
	}

	@Override
	public void reset() {
	
		if( this.isActive ) {
			// For us, resetting the screen involves prompting for a login
			System.out.println( "Whoops, the user interface got confused. Let's try this again." );
			loginScreenState = ENTER_USERNAME;
			displayMenu();
		}
		
	}

	@Override
	public void handleUserInput(String str) {
		
		if( this.isActive ) {
			if( str == null || str.trim().length() == 0 ) {
				// If they just hit enter, we repeat whatever prompt we're at
				displayMenu();
			} else if( loginScreenState == ENTER_USERNAME ) {
				helper.sendUsername( str.trim() );
			} else if( loginScreenState == ENTER_PASSWORD ) {
				helper.sendPassword( str.trim() );
			} else {
				// This is weird....
				reset();
			}
		}

	}

}
