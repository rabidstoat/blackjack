package drexel.edu.blackjack.client.screens;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.client.screens.NotInSessionScreen.ListOfGames;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This is the user screen to display when someone is playing
 * the game. It should inform them of changes in game status,
 * prompt them when it's time to bet or make moves, etc.
 * 
 * @author Jennifer
 *
 */
public class InSessionScreen extends AbstractScreen {

	/**********************************************************************
	 * Private variables.
	 *********************************************************************/

	// This screen can be in one of these two states
	private static final int NEED_BET		= 0;
	private static final int NEED_PLAY		= 1;
	private static final int WATCHING_GAME	= 3;

	// The different options for this menu
	private static String LEAVE_OPTION			= "L";
	private static String VERSION_OPTION		= "V";
	private static String CAPABILITIES_OPTION	= "C";
	private static String QUIT_OPTION			= "Q";
	private static String MENU_OPTION			= "?";
	private static String ACCOUNT_OPTION		= "A";
	
	// Which of the states the screen is in
	private int state;
	
	// And keep a copy to itself for the singleton pattern
	private static InSessionScreen inSessionScreen = null;
	
	/**********************************************************************
	 * Private constructor.
	 *********************************************************************/
	
	private InSessionScreen( BlackjackCLClient client, ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		// It starts in the ENTER_USERNAME state, but inactive
		super(client, thread,helper);
		setScreenType( AbstractScreen.SCREEN_TYPE.IN_SESSION_SCREEN );
		state = WATCHING_GAME;
		setIsActive( false );
		
	}


	@Override
	public void displayMenu() {
		
		if( this.isActive ) {
			if( state == NEED_BET ) {
				// TODO: Yeah
				reset();
			} else if( state == NEED_PLAY ) {
				// TODO: Yeah
				reset();
			} else {
				System.out.println( "Please enter the letter or symbol of the option to perform:" );
				System.out.println( VERSION_OPTION + ") See what version of the game is running (for debug purposes)" );
				System.out.println( CAPABILITIES_OPTION + ") See what capabilities the game implements (for debug purposes)" );
				System.out.println( LEAVE_OPTION + ") Leave the game" );
				System.out.println( ACCOUNT_OPTION + ") Account balance request" );
				System.out.println( QUIT_OPTION + ") Quit playing entirely" );
				System.out.println( MENU_OPTION + ") Repeat this menu of options" );
			}
		}
		
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
		
		if( inSessionScreen == null ) {
			inSessionScreen = new InSessionScreen( client, thread, helper );
		}
		
		return inSessionScreen;
		
	}

	@Override
	public void reset() {
		if( this.isActive ) {
			// For us, resetting the screen involves showing the menu again
			System.out.println( "Whoops, the user interface got confused. Let's try this again." );
			state = WATCHING_GAME;
			displayMenu();
		}
	}

	@Override
	public void processMessage(ResponseCode code) {
		if( this.isActive ) {
			// This is bad.
			if( code == null ) {
				reset();
				return;
			}
			
			else if( code.hasSameCode( ResponseCode.CODE.INVALID_BET_OUTSIDE_RANGE ) ) {
				// TODO
			} else if( code.hasSameCode( ResponseCode.CODE.INVALID_BET_TOO_POOR ) ) {
				// TODO
			} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_BET ) ) {
				// TODO
			} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_HIT ) ) {
				// TODO
			} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_LEFT_SESSION_FORFEIT_BET) ) {
				// TODO
			} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_LEFT_SESSION_NOT_MIDPLAY ) ) {
				// TODO: Implement
			} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_STAND ) ) {
				// TODO: Implement
			} else if( code.hasSameCode( ResponseCode.CODE.TIMEOUT_EXCEEDED_WHILE_BETTING ) ) {
				// TODO: Implement
			} else if( code.hasSameCode( ResponseCode.CODE.TIMEOUT_EXCEEDED_WHILE_PLAYING ) ) {
				// TODO: Implement
			} else if( code.hasSameCode( ResponseCode.CODE.USER_BUSTED ) ) {
				// TODO: Implement
			} else {
				super.handleResponseCode( code );
			}
			
			// And show them the menu
			displayMenu();
		}
	}	

	@Override
	public void handleUserInput(String str) {
		if( this.isActive ) {
			if( state == NEED_BET ) {
				// TODO: Yeah
				reset();
			} else if( state == NEED_PLAY ) {
				// TODO: Yeah
				reset();
			} if( state == WATCHING_GAME ) {
				if( str == null ) {
					reset();
				} else if( str.trim().equals(CAPABILITIES_OPTION) ) {
					sendCapabilitiesRequest();
				} else if( str.trim().equals(MENU_OPTION) ) {
					displayMenu();
				} else if( str.trim().equals(QUIT_OPTION) ) {
					//TODO
					//quit();
				} else if( str.trim().equals(VERSION_OPTION) ) {
					sendVersionRequest();
				} else if( str.trim().equals(ACCOUNT_OPTION) ) {
					sendAccountRequest();
				} else if( str.trim().equals(LEAVE_OPTION) ) {
					//TODO
					//sendLeaveGameRequest();
				} else {
					System.out.println( "Unrecognized user input: " + str );
					displayMenu();
				}
			} else {
				System.out.println( "Uh oh. The UI got into a weird state, resetting it for you." );
				reset();
			}
		}
	}

}
