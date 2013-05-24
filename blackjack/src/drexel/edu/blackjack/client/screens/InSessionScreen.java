package drexel.edu.blackjack.client.screens;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
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
	private static String INFO_OPTION			= "I";
	
	// Which of the states the screen is in
	private Integer state;
	
	// Current bet information
	private Integer acceptedBet = null;		// Server has verified this is your bet
	private Integer requestedBet = null;	// This is what's being requested, but it might be denied
	
	// Current card hand 
	private String cards = "NONE";
	
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
			synchronized(state) {
				if( state == NEED_BET ) {
					System.out.println( "***********************************************************" );
					System.out.println( "                 Making a Bet Screen                       " );
					System.out.println( "***********************************************************" );
					System.out.println( "How much would you like to bet?" );
					System.out.println( "***********************************************************" );
				} else if( state == NEED_PLAY ) {
					// TODO: Yeah
					reset();
				} else {
					System.out.println( "***********************************************************" );
					System.out.println( "                 Playing Blackjack Screen                  " );
					System.out.println( "***********************************************************" );
					System.out.println( playingBlackjackStatusLine() );
					System.out.println( "***********************************************************" );
					System.out.println( "Please enter the letter or symbol of the option to perform:" );
					System.out.println( VERSION_OPTION + ") See what version of the game is running (for debug purposes)" );
					System.out.println( CAPABILITIES_OPTION + ") See what capabilities the game implements (for debug purposes)" );
					System.out.println( LEAVE_OPTION + ") Leave the game" );
					System.out.println( ACCOUNT_OPTION + ") Account balance request" );
					System.out.println( INFO_OPTION + ") Info about the game" );
					System.out.println( QUIT_OPTION + ") Quit playing entirely" );
					System.out.println( MENU_OPTION + ") Repeat this menu of options" );
					System.out.println( "***********************************************************" );
				}
			}
		}
		
	}

	/**
	 * Generate a fixed-width line that reports the cards held, 
	 * and the current bet placed.
	 * 
	 * @return Fixed length string of this
	 */
	private String playingBlackjackStatusLine() {
		
		// We might just have the word NONE for the cards, might have some real values
		// We really don't want it to be null, though
		if( cards == null ) {
			cards = "NONE";
		}
		StringBuilder str = new StringBuilder( "* Cards: " );
		str.append( cards );
		
		// Now, the length of the cards string, plus number of spaces, should equal 20
		for( int i = 0; i < (20-cards.length()); i++ ) {
			str.append( " " );
		}
		
		// Come up with a string for the bet part
		String bet = "Bet: " + (acceptedBet == null ? "NONE" : "$" + acceptedBet.toString() );
		
		// We need to fill 28 spaces to the end of the bet line
		for( int i = 0; i < (28-bet.length()); i++ ) {
			str.append( " " );
		}
		str.append( bet );
		
		// And finally end it off
		str.append( " *" );
		
		return str.toString();
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
			
			synchronized( state ) {
				// This is bad.
				if( code == null ) {
					reset();
				} else if( code.hasSameCode( ResponseCode.CODE.INVALID_BET_OUTSIDE_RANGE ) ) {
					System.out.println( "Your bet was outside the range allowed in the game." );
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.INVALID_BET_TOO_POOR ) ) {
					System.out.println( "Your account is not large enough for that bet." );
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_BET ) ) {
					System.out.println( "Your bet amount has been deducted from your account." );
					acceptedBet = requestedBet;
					state = WATCHING_GAME;
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_HIT ) ) {
					// TODO
					System.out.println( "Need to implement the response to a successful hit." );
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_LEFT_SESSION_FORFEIT_BET) ) {
					System.out.println( "You left the game mid-play, forfeiting $" + code.getFirstParameterAsString() + "." );
					state = WATCHING_GAME;	// Reset the internal state just in case....
					showPreviousScreen();	// Move to the previous screen
				} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_LEFT_SESSION_NOT_MIDPLAY ) ) {
					System.out.println( "You left the game between hands, and did not forfeit a bet." );
					state = WATCHING_GAME;	// Reset the internal state just in case....
					showPreviousScreen();	// Move to the previous screen
				} else if( code.hasSameCode( ResponseCode.CODE.SUCCESSFULLY_STAND ) ) {
					// TODO
					System.out.println( "Need to implement the response to a successful stand." );
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.TIMEOUT_EXCEEDED_WHILE_BETTING ) ) {
					System.out.println( "You did not place a bet in time, and have been removed from the game. No money was lost." );
					state = WATCHING_GAME;	// Reset the internal state just in case....
					showPreviousScreen();	// Move to the previous screen
				} else if( code.hasSameCode( ResponseCode.CODE.TIMEOUT_EXCEEDED_WHILE_PLAYING ) ) {
					// TODO: Something better here
					System.out.println( "You did not choose your play in time, and have been removed from the game." );
					state = WATCHING_GAME;	// Reset the internal state just in case....
					showPreviousScreen();	// Move to the previous screen
				} else if( code.hasSameCode( ResponseCode.CODE.USER_BUSTED ) ) {
					System.out.println( "You BUSTED. That's over 21, and you have lost." );
					state = WATCHING_GAME;
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.REQUEST_FOR_BET ) ) {
					state = NEED_BET;
					displayMenu();
				} else if( code.hasSameCode( ResponseCode.CODE.REQUEST_FOR_GAME_ACTION ) ) {
					state = NEED_PLAY;
					displayMenu();
				} else {
					super.handleResponseCode( code );
				}
			}
		}
	}	

	@Override
	public void handleUserInput(String str) {
		if( this.isActive ) {

			synchronized( state ) {
				if( state == NEED_BET ) {
					if( str == null ) {
						reset();
					} else {
						interpretUserBet( str );
					}
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
						// TODO need to implement
						System.out.println( "Not implemented yet. Try leaving the game first, then quit." );
					} else if( str.trim().equals(INFO_OPTION) ) {
						// TODO need to implement
						System.out.println( "The info option is not implemnted yet." );
					} else if( str.trim().equals(VERSION_OPTION) ) {
						sendVersionRequest();
					} else if( str.trim().equals(ACCOUNT_OPTION) ) {
						sendAccountRequest();
					} else if( str.trim().equals(LEAVE_OPTION) ) {
						sendLeaveGameRequest();
					} else {
						System.out.println( "Unrecognized user input: " + str );
						displayMenu();
					}
				}
			}
		}
	}


	/**
	 * Sends a request to leave the session. No prompting
	 * of the user to confirm, sucks to be them.
	 */
	private void sendLeaveGameRequest() {
		System.out.println( "Exiting you from the game now..." );
		helper.sendLeaveSessionRequest();
	}

	/**
	 * Upon being requested to enter a bet, the user entered
	 * something. Hopefully it's a number...
	 */
	private void interpretUserBet( String str ) {
		if( str == null ) {
			reset();
		} else {
			// Try to interpret the amount as a number
			Integer bet = null;
			try {
				bet = Integer.parseInt(str.trim());
				System.out.println( "One moment, submitting your bet to the dealer..." );
				requestedBet = bet;
				helper.sendBetRequest( bet );
			} catch( NumberFormatException e ) {
				System.out.println( "You need to enter a number for the bet amount." );
				displayMenu();
			}
		}
	}
}
