package drexel.edu.blackjack.client.screens;

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
	
	public LoginInputScreen( ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		// It starts in the ENTER_USERNAME state, but inactive
		super(thread,helper);
		loginScreenState = JUST_STARTED;
		setIsActive( false );
		
	}

	@Override
	public void processMessage(ResponseCode code) {
		
		// One message we might receive says that the server is ready for the password
		if( code.equals( ResponseCode.CODE.WAITING_FOR_PASSWORD ) ) {
			loginScreenState = ENTER_PASSWORD;
			displayMenu();
		} else if( code.equals(ResponseCode.CODE.INVALID_LOGIN_CREDENTIALS) ) {
			// Another message says that the login was incorrect
			System.out.println( "The username and password you supplied is incorrect." );
			loginScreenState = ENTER_USERNAME;
			displayMenu();
		} else {
			super.handleResponseCode( code );
		}
	}
	
	/**
	 * Used to display whatever sort of command-line 'menu'
	 * to the screen that is appropriate.
	 */
	public void displayMenu() {
		
		// We only want to show this when they first log in, not
		// on subsequent attempt to reconnect
		if( loginScreenState == JUST_STARTED ) {
			System.out.println( "Welcome to the Blackjack Game!" );
			loginScreenState = ENTER_USERNAME;
		} 
		
		if( loginScreenState == ENTER_USERNAME ) {
			System.out.println( "Please enter your username: " );
		} else if( loginScreenState == ENTER_PASSWORD ) {
			System.out.println( "Please enter your password: " );
		} else {
			// This should never happen
			System.out.println( "We should never get here in the code. Oh great, you broke it!!!" );
		}
	}

	/**
	 * This is used in the singleton pattern so that only
	 * one login screen is instantiated at a time.
	 * 
	 * @param thread Need a valid and active client input thread
	 * @return The login screen
	 */
	public static AbstractScreen getDefaultScreen( ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		if( loginInputScreen == null ) {
			loginInputScreen = new LoginInputScreen( thread, helper );
		}
		
		return loginInputScreen;
		
	}

	@Override
	public void reset() {
		
		// For us, resetting the screen involves prompting for a login
		System.out.println( "Whoops, the user interface got confused. Let's try this again." );
		loginScreenState = ENTER_USERNAME;
		displayMenu();
		
	}

	@Override
	public void handleUserInput(String str) {
		
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
