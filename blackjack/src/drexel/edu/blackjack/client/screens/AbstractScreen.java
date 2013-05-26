/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - AbstractScreen.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is the abstract base clas for all client UI screens. It
 * specifies what methods all screens must implement, such as responding to
 * messages from the server and listening to input from the keyboard, as well
 * as provides some common functionality shared by all UI screens that is
 * related to sending and receiving messages with the server.
 ******************************************************************************/
package drexel.edu.blackjack.client.screens;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.in.MessagesFromServerListener;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.client.screens.util.ClientSideGame;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
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
		IN_SESSION_SCREEN
		
	}

	
	/*****************************************************************
	 * Local variables here
	 ***************************************************************/


	// Some menu options that are shared amongst multiple screens
	protected static String VERSION_OPTION			= "V";	// Request to see the version of the server
	protected static String CAPABILITIES_OPTION		= "C";	// Request to see the capabilities of the server
	protected static String QUIT_OPTION				= "Q";	// Request to quit the client entirely
	protected static String MENU_OPTION				= "?";	// Request to repeat the last menu
	protected static String ACCOUNT_OPTION			= "A";	// Request to view the user's bank account balance
	protected static String TOGGLE_MONITOR_OPTION	= "T";	// Request to toggle the frame that shows messages sent and receivd
	protected static String BACK_OPTION				= "back";	// Request to go back a menu

	// What type of screen is it
	private SCREEN_TYPE screenType = SCREEN_TYPE.LOGIN_SCREEN;	// We start with the login
	
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
	 * Constructor goes here
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
	 * Some getters and setters, essentially, with at most a small
	 * amount of necessary logic
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
	protected void setScreenType(SCREEN_TYPE screenType) {
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
			clientThread.addListener( this );
		} else { 
			clientThread.removeListener( this );
		}
		
		this.isActive = isActive;
	}
	
	/**
	 * Request that the user interface show the 'next screen', which is
	 * based on what the currentScreen is
	 * 
	 * @param displayMenu True if it should immediately show the menu
	 */
	public void showNextScreen( boolean displayMenu ) {
		
		if( client == null ) {
			LOGGER.severe( "Cannot show the next user interface screen as we don't seem to have a client set." );
		} else {
			client.showNextScreen( displayMenu );
		}
	}
	
	/**
	 * Request that the user interface show the 'previous screen', which is
	 * based on what the currentScreen is
	 * 
	 * @param displayMenu True if it should immediately show the menu
	 */
	public void showPreviousScreen( boolean displayMenu ) {
		
		if( client == null ) {
			LOGGER.severe( "Cannot show the previous user interface screen as we don't seem to have a client set." );
		} else {
			client.showPreviousScreen( displayMenu );
		}
	}

	
	/*****************************************************************
	 * These methods have to do with handling responses from the
	 * server. Typically this would be overridden by the extending
	 * class so they can add their own specific handling out of
	 * methods, but defaults are provided here for many things.
	 ***************************************************************/

	
	/**
	 * Does the best it can to handle a response code that the
	 * implementing screen did not handle. This might be because
	 * it's a 'general error' or 'common message', or it might 
	 * simply be something that was totally unexpected.
	 * 
	 * Handling the message might involve printing to the console
	 * or it might just be handled internally in a silent manner.
	 * 
	 * If handling the code requires redisplaying the menu, do that
	 * here. Typically this is only done if you reset, if you have
	 * changed menus, or if you have received a message that prints
	 * a lot to the screen, such that displaying the menu again would
	 * be helpful.
	 * 
	 * @param code What was received
	 */
	protected void handleResponseCode(ResponseCode code) {

		// Only handle if it's active
		if( isActive ) {
			
			if( code == null ) {
				// Internal error or something
				LOGGER.severe( "Received null code from the server and don't know what to do." );
				reset();
			} else if( code.isError() || code.isMalformed() ) {
				// Internal error? Or syntax error? Just reset the screen
				LOGGER.warning( "Received unhandled error code of '" + code.toString() + "'." );
				reset();
			} else if( code.isInformative() ) {
				
				if( code.hasSameCode(ResponseCode.CODE.VERSION ) ) {
					displayVersion( code );
				} else if( code.hasSameCode( ResponseCode.CODE.CAPABILITIES_FOLLOW ) ) {
					displayCapabilities( code );
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.ACCOUNT_BALANCE ) ) {
					displayAccountBalance( code );
				} else {
					LOGGER.info( "Received unhandled informative code of '" + code.toString() + "'." );
				}
				
			} else if( code.isGameState() ) {
				
				if( code.hasSameCode(ResponseCode.CODE.PLAYER_JOINED ) ) {
					displayPlayerMovement( code );
				} else if( code.hasSameCode(ResponseCode.CODE.PLAYER_LEFT ) ) {
					displayPlayerMovement( code );
				} else if( code.hasSameCode(ResponseCode.CODE.PLAYER_BET ) ) {
					displayPlayerBet( code );
				} else if( code.hasSameCode(ResponseCode.CODE.PLAYER_ACTION ) ) {
					displayPlayerAction( code );
				} else {
					// TODO: Handle game state codes
					LOGGER.info( "Received unhandled game state code of '" + code.toString() + "'." );
				}
				
			} else if( code.isCommandComplete() ) {
				
				if( code.hasSameCode(ResponseCode.CODE.SUCCESSFULLY_QUIT ) ) {
					quitTheGame();
				} else {
					LOGGER.info( "Received unhandled command-complete code of '" + code.toString() + "'." );
				}
				
			} else {
				// TODO: Not sure what to do here
				LOGGER.info( "Received some other unhandled code of '" + code.toString() + "'." );
			}
		}
	}

	/**
	 * This handles codes about players (or the dealer) peforming
	 * actions. The first variable is the game ID. The second 
	 * variable is the username. The third variable is the action.
	 * 
	 * @param code Hopefully of type ResponseCode.CODE.PLAYER_ACTION
	 */
	private void displayPlayerAction(ResponseCode code) {
		
		if( code != null  ) {

			// Our parameters
			List<String> params = code.getParameters();

			// All player update messages start the same
			StringBuilder str = createStringBuilderForUserUpdate(params);
			str.append( " " );
			
			// What was the action? It's in parameter two
			String action = null;
			if( params != null && params.size() >= 3 ) {
				action = params.get(2);
			}
			
			// Decide what to state based on the action
			if( action == null ) {
				str.append( "performed an unknown action" );
			} else if( action.equalsIgnoreCase( GameState.SHUFFLED_KEYWORD ) ) {
				str.append( "shuffled all the cards in the shoe" );
			} else if( action.equalsIgnoreCase( GameState.BUST_KEYWORD ) ) {
				str.append( "just went bust!" );
			} else if( action.equalsIgnoreCase( GameState.HIT_KEYWORD ) ) {
				str.append( "decided to take another card" );
			} else if( action.equalsIgnoreCase( GameState.STAND_KEYWORD ) ) {
				str.append( "decided to stand" );
			} else if( action.equalsIgnoreCase( GameState.BLACKJACK_KEYWORD ) ) {
				str.append( "has a blackjack!" );
			} else {
				str.append( "performed an unknown action" );
			}

			str.append( "." );
			
			// Display to the string
			updateStatus( str.toString() );
		}
	}


	/**
	 * This handles codes about players entering or leaving the
	 * game. The first variable is the game ID. The second variable
	 * is the username.
	 * 
	 * @param code Hopefully of type ResponseCode.CODE.PLAYER_JOINED
	 * or ResponseCode.CODE.PLAYER_LEFT
	 */
	private void displayPlayerMovement(ResponseCode code) {
		
		if( code != null  ) {
			
			// Our parameters
			List<String> params = code.getParameters();

			// All player update messages start the same
			StringBuilder str = createStringBuilderForUserUpdate(params);
			
			// Have they left or joined?
			str.append( " has " );
			if( code.hasSameCode(ResponseCode.CODE.PLAYER_JOINED) ) {
				str.append( "joined" );
			} else if( code.hasSameCode(ResponseCode.CODE.PLAYER_LEFT) ) {
				str.append( "left" );
			} else { 
				str.append( "performed an unknown action in " );
			}
			str.append( " the game." );
			
			// Display to the string
			updateStatus( str.toString() );
		}
	}

	/**
	 * This handles codes about players placing a bet. The first 
	 * variable is the game ID. The second variable is the username.
	 * The third variable is the bet amount.
	 * 
	 * @param code Hopefully of type ResponseCode.CODE.PLAYER_BET
	 */
	private void displayPlayerBet(ResponseCode code) {
		
		if( code != null  ) {

			// Our parameters
			List<String> params = code.getParameters();

			// All player update messages start the same
			StringBuilder str = createStringBuilderForUserUpdate(params);
			
			// How much did they bet?
			str.append( " has placed a bet of " );
			if( params == null || params.size() < 3 ) {
				str.append( "an unknown amount" );
			} else {
				String amount = params.get(2);
				str.append( "$" );
				str.append( amount );
			}
			str.append( "." );
			
			// Display to the string
			updateStatus( str.toString() );
		}
	}


	/**
	 * Creates a string builder to be used for displaying messages about a
	 * particular user, by starting it off with a line like: "The player <username>",
	 * unless the username is the special DEALER_USERNAME, in which case it starts
	 * off with "The dealer".
	 * 
	 * @param The parameters. The second parameter should have the username. This
	 * corresponds to the format of player update messages from the server
	 * @return The stringbuilder, initialized to this leadin phrase
	 */
	private StringBuilder createStringBuilderForUserUpdate(List<String> params) {
		// Start with their username
		StringBuilder str = new StringBuilder( "The player " );
		if( params == null || params.size() < 2 ) {
			str.append( GameState.UNKNOWN_USERNAME );
		} else {
			String username = params.get(1);
			
			if( username == null ) {
				str.append( GameState.UNKNOWN_USERNAME );
			} else if( username.equals( GameState.DEALER_USERNAME ) ) {
				// Have to replace the whole thing
				str = new StringBuilder( "The dealer" );
			} else {
				str.append( username );
			}
		}
		return str;
	}

	/**
	 * Print to the screen something about the capabilities
	 * @param code
	 */
	protected void displayCapabilities(ResponseCode code) {
		
		// Make sure this is a valid capabilities list first
		if( code == null ||  
				!code.hasSameCode( ResponseCode.CODE.CAPABILITIES_FOLLOW ) ) {
			updateStatus( "Internal error, sorry. Can't display the capabilities list." );
		} else {
			updateStatus( "The server supports " + (code.getNumberOfLines()-1) + " protocol commands in this current state." );
			updateStatus( "They are: " );
			for( int i = 1; i < code.getNumberOfLines(); i++ ) {
				updateStatus( i + ". " + code.getMultiline(i) );
			}
		}				
	}

	/**
	 * Print to the screen something about the account balance
	 * @param code
	 */
	protected void displayAccountBalance(ResponseCode code) {
		
		// Make sure this is a valid account balance response first
		if( code == null ||  
				!code.hasSameCode( ResponseCode.CODE.ACCOUNT_BALANCE ) ) {
			updateStatus( "Internal error, sorry. Can't display the account balance." );
		} else {
			updateStatus( "Your account balance is $" + code.getFirstParameterAsString() + "." );
		}
				
	}

	/**
	 * Print to the screen something about the version
	 * @param code
	 */
	protected void displayVersion(ResponseCode code) {
		// Make sure this is a valid version response first
		if( code == null ||  
				!code.hasSameCode( ResponseCode.CODE.VERSION ) ) {
			updateStatus( "Internal error, sorry. Can't display the version." );
		} else {
			updateStatus( "Server version " + code.getText().trim() );
		}
	}

	
	/**
	 * Does whatever to update the user as to a status, which can
	 * be displayed as a text string
	 * @param str
	 */
	protected void updateStatus(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss");
		System.out.println( "[" + sdf.format( new Date() ) + "] " + str );
	}

	/**
	 * Server acknowledges a quit, so we can cleanly exit
	 */
	protected void quitTheGame() {
		client.notifyOfShutdown();
	}

	
	/***********************************************************************************
	 * Interacts with the server as needed to handle various user requests
	 **********************************************************************************/

	
	protected void sendVersionRequest() {
		updateStatus( "One moment, fetching the version from the server..." );
		helper.sendVersionRequest();
	}

	protected void sendAccountRequest() {
		updateStatus( "One moment, fetching your account balance from the server..." );
		helper.sendAccountRequest();
	}

	protected void sendCapabilitiesRequest() {
		updateStatus( "One moment, fetching a list of capabilities from the server..." );
		helper.sendCapabilitiesRequest();
	}

	protected void toggleMessageMonitorFrame() {
		if( client != null ) {
			client.toggleMessageFrame();
		}
	}

	
	/***********************************************************************************
	 * This is what handles the interface that we are implementing. It takes input
	 * and calls a method that causes the input to be processed.
	 **********************************************************************************/

	
	@Override
	public void receivedMessage(ResponseCode code) {
		// Only ask an implementing class to process the message
		// if the screen is active
		if( isActive ) {
			processMessage( code );
		}
	}

	/**********************************************************************
	 * These methods handle tracking the list of available games so that
	 * they can be displayed to the user and, when the user picks one
	 * to join, the proper request for joining it can be made.
	 *********************************************************************/
	
	
	/**
	 * This helper function can read what's in a response code and,
	 * from it, create a bunch of ClientSideGame records to track
	 * 
	 * @param code The response code
	 * @return The map of the games' IDs to the games
	 */
	protected Map<String, ClientSideGame> generateGameMap(ResponseCode code) {

		// This is what we will return
		Map<String, ClientSideGame> map = new HashMap<String,ClientSideGame>();
		
		// We keep queueing up game description lines until we
		// have reached the end for the game, then interpret them
		String numDecks = null;
		String minBet = null;
		String maxBet = null;
		String gameId = null;
		String gameDescription = null;
		ArrayList<String> rules = new ArrayList<String>();
		
		if( code != null && code.isMultilineCode() ) {
			for( int i = 1; i < code.getNumberOfLines(); i++ ) {
				String currentLine = code.getMultiline(i);
				if( currentLine != null ) {
					if( currentLine.startsWith( Game.RECORD_START_KEYWORD ) ) {

						// Initialize all values
						numDecks = minBet = maxBet = null;
						gameId = gameDescription = null;
						rules.clear();
						
						// Figure out the ID and description
						// Use a tokenizer just because
						StringTokenizer strtok = new StringTokenizer(currentLine);
						if( strtok.hasMoreTokens() ) {
							// First token is the word 'GAME', which we don't care about
							strtok.nextToken();
							if( strtok.hasMoreTokens() ) {
								// This token, however, is the ID
								gameId = strtok.nextToken();
							}
						}
						
						// Try to figure out what the description should be
						int index = currentLine.indexOf( gameId, Game.RECORD_START_KEYWORD.length() );
						gameDescription = currentLine.substring( index + gameId.length() ).trim();
					} else if( currentLine.startsWith( Game.MAX_BET_ATTRIBUTE ) ) {
						maxBet = currentLine.substring( Game.MAX_BET_ATTRIBUTE.length() ).trim();
					} else if( currentLine.startsWith( Game.MIN_BET_ATTRIBUTE ) ) {
						minBet = currentLine.substring( Game.MIN_BET_ATTRIBUTE.length() ).trim();
					} else if( currentLine.startsWith( Game.NUM_DECKS_ATTRIBUTE ) ) {
						numDecks = currentLine.substring( Game.NUM_DECKS_ATTRIBUTE.length() ).trim();
					} else if( currentLine.startsWith( Game.RULE_KEYWORD ) ) {
						rules.add( currentLine.substring( Game.RULE_KEYWORD.length() ).trim() );
					} else if( currentLine.startsWith( Game.RECORD_END_KEYWORD ) ) {
						// We need to create, and cache, a ClientSideGame
						ClientSideGame game = new ClientSideGame( gameId, numDecks,
								rules, minBet, maxBet, gameDescription );
						map.put( gameId, game );
					}
				}
			}
		}
		
		return map;
	}	
}
