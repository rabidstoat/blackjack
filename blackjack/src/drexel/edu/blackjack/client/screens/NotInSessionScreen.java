/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - NotInSessionScreen.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This implements the 'screen' (set of command-line UI prompts) that
 * is used when the user is not in a game. In hides the protocol from the user.
 * For example, joining a game for the user looks like seeing a list of game
 * information and selecting which number to join. In code, this involves the
 * LISTGAMES and JOINSESSION protocol messages, and responses.
 ******************************************************************************/
package drexel.edu.blackjack.client.screens;

import java.util.HashMap;
import java.util.Map;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.client.screens.util.ClientSideGame;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This is the user interface that shows when the
 * user is waiting to join a session.
 * 
 * <b>STATEFUL:</b> In terms of the protocol, this screen
 * is used when the protocol DFA is in the 
 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION} 
 * protocol state . It therefore only sends messages that are valid
 * for that state. 
 * <p>
 * <b>UI:</b> This is where part of the user interface on
 * the client is implemented. Note that it extends the
 * {@link AbstractScreen} class, which defines some of
 * the functionality that must be provided. The majority
 * of comments related to the client-side UI can be found
 * in that class. 
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

	// The different options specific for menus in this state
	private static String JOIN_OPTION			= "J";	// Request to join a game
	
	// Which of the states the screen is in
	private int state;
	
	// Once we read a list of games, need a way to map from their menu
	// number, to their ID (which is needed for joining the session)
	private Map<Integer,String> menuNumberToGameIdMap = new HashMap<Integer,String>();
	
	// When the user requests to join a game, keep track of it here. If the join
	// request is successful the client will need to be informed which game
	private String requestedGameId = null;
	
	// And keep a copy to itself for the singleton pattern
	private static NotInSessionScreen notInSessionScreen = null;
	
	
	/**********************************************************************
	 * Private constructor and singleton access method
	 *********************************************************************/
	
	
	/**
	 * This is a private constructor for the singleton design pattern
	 * @param client Reference to the client
	 * @param thread Reference to the thread that receives messages from server
	 * @param helper Reference to the helper that sends messages to the server
	 */
	private NotInSessionScreen( BlackjackCLClient client, ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper ) {
		
		// It starts in the ENTER_USERNAME state, but inactive
		super(client, thread,helper);
		setScreenType( AbstractScreen.SCREEN_TYPE.NOT_IN_SESSION_SCREEN );
		state = MAIN_MENU;
		setIsActive( false );
		
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


	/**********************************************************************
	 * Methods related to messages received from the server
	 *********************************************************************/
	
	
	@Override
	public void processMessage(ResponseCode code) {

		if( this.isActive ) {
			// This is bad.
			if( code == null ) {
				reset();
				return;
			}
			
			else if( code.hasSameCode( ResponseCode.CODE.GAMES_FOLLOW ) ) {
				displayGameList( code );
				state = JOIN_GAME;
				displayMenu();
			} else if( code.hasSameCode( ResponseCode.CODE.NO_GAMES_HOSTED ) ) {
				updateStatus( "Unfortunately, no games are hosted on this server" );
				state = MAIN_MENU;
				displayMenu();
			} else if( code.hasSameCode( ResponseCode.CODE.JOIN_SESSION_AT_MAX_PLAYERS ) ) {
				updateStatus( "The game you selected already has the maximum number of players." );
				state = MAIN_MENU;
				displayMenu();
			} else if( code.hasSameCode( ResponseCode.CODE.JOIN_SESSION_DOES_NOT_EXIST ) ) {
				updateStatus( "That game no longer is hosted by the server. Sorry about that." );
				state = MAIN_MENU;
				displayMenu();
			} else if( code.hasSameCode( ResponseCode.CODE.JOIN_SESSION_TOO_POOR ) ) {
				updateStatus( "You don't have enough money in your account to cover the minimum bet." );
				state = MAIN_MENU;
				displayMenu();
			} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_JOINED_SESSION ) ) {
				// This needs to say that they successfully joined a game, and move them to the next screen
				updateStatus( "Successfully joined the game." );
				state = MAIN_MENU;	// Reset the internal state just in case....
				client.setCurrentGameById( requestedGameId );
				showNextScreen( false );	// Move to the next screen, but don't show the menu
				
				// And request some basic give info to show them
				helper.sendGameStatusRequest( requestedGameId );
			} else {
				super.handleResponseCode( code );
			}
		}

	}

	/**
	 * <b>UI:</b> Display the game list for the user to select from.
	 * Save it out so that we can send the right 'JOINSESSION'
	 * command later. That is, we don't want the user to have
	 * to type the session ID, they shouldn't even know that
	 * session IDs are used. They type the number of a game
	 * to join, and the code has to translate that to a JOINSESSION
	 * command with the proper session iD.
	 */
	private void displayGameList( ResponseCode code ) {

		// Set the list of games in the client
		Map<String,ClientSideGame> gameMap = generateGameMap(code);
		client.setGameMap( gameMap );
		
		// Present to user, while tracking menu option number to game ID
		menuNumberToGameIdMap.clear();
		Integer menuNumber = 1;
		for( ClientSideGame game : gameMap.values() ) {
			System.out.println( menuNumber + ") " + game.toString() );
			menuNumberToGameIdMap.put( menuNumber, game.getId() );
			menuNumber++;
		}
	}

	
	/**********************************************************************
	 * General methods related to showing menus and getting responses.
	 *********************************************************************/

	
	/**
	 * Used to display whatever sort of command-line 'menu'
	 * to the screen that is appropriate.
	 */
	public void displayMenu() {

		if( this.isActive ) {
			if( state == JOIN_GAME ) {
				System.out.println( "Enter the number of the game you wish to join." );
				System.out.println( "(Or you can type '" + BACK_OPTION + "' to go back to the previous menu.)" );
				System.out.println( "***********************************************************" );
			} else {
				System.out.println( "***********************************************************" );
				System.out.println( "                Looking for a Game Screen                  " );
				System.out.println( "***********************************************************" );
				System.out.println( "Please enter the letter or symbol of the option to perform:" );
				System.out.println( JOIN_OPTION + ") Join a game" );
				System.out.println( ACCOUNT_OPTION + ") Account balance request" );
				System.out.println( QUIT_OPTION + ") Quit playing" );
				System.out.println( VERSION_OPTION + ") See what version of the game is running (for debug purposes)" );
				System.out.println( CAPABILITIES_OPTION + ") See what capabilities the game implements (for debug purposes)" );
				System.out.println( TOGGLE_MONITOR_OPTION + ") Toggle the message monitor window (for debug purposes)" );
				System.out.println( MENU_OPTION + ") Repeat this menu of options" );
				System.out.println( "***********************************************************" );
			}
		}
		
	}

	@Override
	public void reset() {

		if( this.isActive ) {
			// For us, resetting the screen involves showing the menu again
			updateStatus( "Whoops, the user interface got confused. Let's try this again." );
			state = MAIN_MENU;
			displayMenu();
		}
		
	}


	/***********************************************************************************
	 * These methods process user input
	 **********************************************************************************/

	
	@Override
	public void handleUserInput(String str) {

		if( this.isActive ) {
			if( state == MAIN_MENU ) {

				if( str == null ) {
					reset();
				} else if( str.trim().equalsIgnoreCase(CAPABILITIES_OPTION) ) {
					sendCapabilitiesRequest();
				} else if( str.trim().equalsIgnoreCase(JOIN_OPTION) ) {
					sendListGamesRequest();
				} else if( str.trim().equalsIgnoreCase(MENU_OPTION) ) {
					displayMenu();
				} else if( str.trim().equalsIgnoreCase(QUIT_OPTION) ) {
					sendQuitRequest();
				} else if( str.trim().equalsIgnoreCase(VERSION_OPTION) ) {
					sendVersionRequest();
				} else if( str.trim().equalsIgnoreCase(ACCOUNT_OPTION) ) {
					sendAccountRequest();
				} else if( str.trim().equalsIgnoreCase(TOGGLE_MONITOR_OPTION) ) {
					toggleMessageMonitorFrame();
				} else {
					updateStatus( "Unrecognized user input: " + str );
					displayMenu();
				}
				
			} else if( state == JOIN_GAME ) {
				
				processJoinGameResponse( str );
				
			} else {
				updateStatus( "Uh oh. The UI got into a weird state, resetting it for you." );
				reset();
			}
		}

	}

	
	/***********************************************************************************
	 * Interacts with the server to handle various user requests
	 **********************************************************************************/

	
	/**
	 * Send a QUIT protocol command to the server.
	 * 
	 * <b>STATEFUL:</b> The QUIT protocol command is valid in the
	 * NOT_IN_SESSION protocol state, which is the only state this
	 * screen supports.
	 */
	private void sendQuitRequest() {
		updateStatus( "One moment, informing the server of your departure..." );
		helper.sendQuitRequest();
	}

	/**
	 * Send a LISTGAMES protocol command to the server.
	 * 
	 * <b>STATEFUL:</b> The LISTGAMES protocol command is valid in the
	 * NOT_IN_SESSION protocol state, which is the only state this
	 * screen supports.
	 */
	private void sendListGamesRequest() {
		updateStatus( "One moment, fetching a list of games from the server..." );
		System.out.println( "***********************************************************" );
		System.out.println( "                  Viewing Games Screen                    " );
		System.out.println( "***********************************************************" );
		helper.sendListGamesRequest();
	}

	/**
	 * Send a JOINSESSION protocol command to the server.
	 * 
	 * <b>STATEFUL:</b> The JOINSESSION protocol command is valid in the
	 * NOT_IN_SESSION protocol state, which is the only state this
	 * screen supports.
	 */
	private void sendJoinGameRequest( String id ) {
		updateStatus( "Alerting the server that you wish to join this game..." );
		// Remember to track it internally
		requestedGameId = id;
		helper.sendJoinSessionRequest( id );
	}	
	
	/**
	 * This handles whatever the user typed in for joining a game.
	 * It had better be a number that is in range!!!
	 * 
	 * <b>UI:</b> Because the user shouldn't have to know
	 * about game session ID, much less type them, there is
	 * a lookup process requried to convert the game number
	 * (which is a concept only in the UI) to the game session
	 * ID (which is a concept the protocol needs.)
	 * 
	 * @param str What the user typed in
	 */
	private void processJoinGameResponse(String str) {

		Integer number = null;
		if( str != null ) {
			try {
				number = Integer.valueOf(Integer.parseInt(str));
			} catch( NumberFormatException e ) {
				// Too bad, but nothing we need to report now
			}
		}
		
		// Make sure the number is in range
		if( number != null && (number < 1 || number > menuNumberToGameIdMap.size() ) ) {
			number = null;
		}
		
		// Okay, did we get a valid number? 
		if( number == null ) {
			// Maybe they just wanted to go back to the previous menu?
			if( str != null && str.equalsIgnoreCase(BACK_OPTION) ) {
				updateStatus( "Returning you to the previous menu." );
				state = MAIN_MENU;
				displayMenu();
			} else {				
				// If not, force them to try again
				updateStatus( "You need to enter a number from 1-" + menuNumberToGameIdMap.size() + "," );
				updateStatus( "or '" + BACK_OPTION + "' to return to the previous menu. Try again." );
				sendListGamesRequest();
			}
		} else {
			// We got a valid number, so we need to join the game. Since the
			// user typed in a number, and the JOINSESSION command requires
			// a game session ID, we have to look up which one to use...
			String id = menuNumberToGameIdMap.get( Integer.valueOf(number) );
			if( id == null ) {
				updateStatus( "Whoops. Could not find the game to request due to an internal error!" );
				reset();
			} else {
				sendJoinGameRequest( id );
			}
		}
	}
}
