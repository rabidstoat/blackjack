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
	
	/**********************************************************************
	 * Private variables.
	 *********************************************************************/

	// This screen can be in one of these two states
	private static final int MAIN_MENU	= 0;
	private static final int JOIN_GAME	= 1;

	// The different options for this menu
	private static String JOIN_OPTION			= "J";
	private static String VERSION_OPTION		= "V";
	private static String CAPABILITIES_OPTION	= "C";
	private static String QUIT_OPTION			= "Q";
	private static String MENU_OPTION			= "?";
	
	// Which of the states the screen is in
	private int state;
	
	// And keep a copy to itself for the singleton pattern
	private static NotInSessionScreen notInSessionScreen = null;
	
	/**********************************************************************
	 * Private constructor.
	 *********************************************************************/
	
	private NotInSessionScreen( BlackjackCLClient client, ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		// It starts in the ENTER_USERNAME state, but inactive
		super(client, thread,helper);
		setScreenType( AbstractScreen.SCREEN_TYPE.NOT_IN_SESSION_SCREEN );
		state = MAIN_MENU;
		setIsActive( false );
		
	}

	/**********************************************************************
	 * Public methods.
	 *********************************************************************/
	
	@Override
	public void processMessage(ResponseCode code) {

		if( this.isActive ) {
			// This is bad.
			if( code == null ) {
				reset();
				return;
			}
			
			if( code.hasSameCode( ResponseCode.CODE.VERSION ) ) {
				displayVersion( code );
			} else if( code.hasSameCode( ResponseCode.CODE.CAPABILITIES_FOLLOW ) ) {
				displayCapabilities( code );
			} else if( code.hasSameCode( ResponseCode.CODE.GAMES_FOLLOW ) ) {
				displayGameList( code );
				state = JOIN_GAME;
			} else if( code.hasSameCode( ResponseCode.CODE.JOIN_SESSION_AT_MAX_PLAYERS ) ) {
				System.out.println( "The game you selected already has the maximum number of players." );
				state = MAIN_MENU;
			} else if( code.hasSameCode( ResponseCode.CODE.JOIN_SESSION_DOES_NOT_EXIST ) ) {
				System.out.println( "That game no longer is hosted by the server. Sorry about that." );
				state = MAIN_MENU;
			} else if( code.hasSameCode( ResponseCode.CODE.JOIN_SESSION_TOO_POOR ) ) {
				System.out.println( "You don't have enough money in your account to cover the minimum bet." );
				state = MAIN_MENU;
			} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_JOINED_SESSION ) ) {
				System.out.println( "You joined the session, hooray! But I've not implemented this." );
				// TODO: Implement
			} else {
				super.handleResponseCode( code );
			}
			
			// And show them the menu
			displayMenu();
		}

	}

	/**
	 * Display the game list for the user to select from.
	 * Save it out so that we can send the write 'JOINSESSION'
	 * command later.
	 */
	private void displayGameList( ResponseCode code ) {
		// TODO: Something a LOT nicer
		System.out.println( code.getText() );
	}

	/**
	 * Used to display whatever sort of command-line 'menu'
	 * to the screen that is appropriate.
	 */
	public void displayMenu() {

		if( this.isActive ) {
			if( state == JOIN_GAME ) {
				System.out.println( "Have not implemented join game menu yet, whoops!" );
				state = MAIN_MENU;
			} else {
				System.out.println( "Please enter the letter or symbol of the option to perform:" );
				System.out.println( VERSION_OPTION + ") See what version of the game is running (for debug purposes)" );
				System.out.println( CAPABILITIES_OPTION + ") See what capabilities the game implements (for debug purposes)" );
				System.out.println( JOIN_OPTION + ") Join a game" );
				System.out.println( QUIT_OPTION + ") Quit playing" );
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
		
		if( notInSessionScreen == null ) {
			notInSessionScreen = new NotInSessionScreen( client, thread, helper );
		}
		
		return notInSessionScreen;
		
	}

	@Override
	public void reset() {

		if( this.isActive ) {
			// For us, resetting the screen involves showing the menu again
			System.out.println( "Whoops, the user interface got confused. Let's try this again." );
			state = MAIN_MENU;
			displayMenu();
		}
		
	}

	@Override
	public void handleUserInput(String str) {

		if( this.isActive ) {
			if( state == MAIN_MENU ) {

				if( str == null ) {
					reset();
				} else if( str.trim().equals(CAPABILITIES_OPTION) ) {
					sendCapabilitiesRequest();
				} else if( str.trim().equals(JOIN_OPTION) ) {
					sendListGamesRequest();
				} else if( str.trim().equals(MENU_OPTION) ) {
					displayMenu();
				} else if( str.trim().equals(QUIT_OPTION) ) {
					quit();
				} else if( str.trim().equals(VERSION_OPTION) ) {
					sendVersionRequest();
				} else {
					System.out.println( "Unrecognized user input: " + str );
					displayMenu();
				}
				
			} else {
				System.out.println( "Regretably have not implemented any options here." );
			}
		}

	}

	private void quit() {
		System.out.println( "There is no quitting, mwahahaha!" );
		displayMenu();
	}

	private void sendVersionRequest() {
		System.out.println( "One moment, fetching the version from the server..." );
		helper.sendVersionRequest();
	}

	private void sendListGamesRequest() {
		System.out.println( "One moment, fetching a list of games from the server..." );
		helper.sendListGamesRequest();
	}

	private void sendCapabilitiesRequest() {
		System.out.println( "One moment, fetching a list of capabilities from the server..." );
		helper.sendCapabilitiesRequest();
	}

}
