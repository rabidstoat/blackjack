/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - Game.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class represents a game that is hosted by the server. There are
 * two important variables in here: the GameMetadata and the GameState. The
 * metadata is mostly general information like the number of decks and bet range.
 * The GameState is mostly dynamic information like who the players are and
 * what stage of the game its in. This class basically pulls those two types of
 * information together.
 ******************************************************************************/
package drexel.edu.blackjack.server.game;

import java.util.logging.Logger;

import drexel.edu.blackjack.db.game.GameMetadata;
import drexel.edu.blackjack.server.BlackjackServer;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Coordinates information about the blackjack game. Metadata
 * that doesn't vary as the game runs (e.g., number of decks,
 * minimum bet, house rules) is stored in a referenced
 * {@link drexel.edu.blackjack.db.game.GameMetadata} class, while
 * dynamic information that changes as the game progresses
 * (e.g., involved players, the dealer shoe of cards) is stored
 * in a referenced {@link GameState} class.
 * <p>
 * <b>SECURITY:</b> When displaying information about the hands
 * of different players in the game, it makes use of a method on
 * the Hand class that requires the requesting user be passed in.
 * In this way, it only reveals facedown cards for the user who
 * holds them.
 * 
 * @author Jennifer
 *
 */
public class Game {

	/*******************************************************************************
	 * These are used in game descriptors and such
	 *****************************************************************************/
	
	/**
	 * In a LISTGAMES response, signifies an attribute is forthcoming
	 */
	public static final String ATTRIBUTE_KEYWORD = "ATTRIBUTE";
	/**
	 * In a LISTGAMES response, signifies an attribute for the number of decks
	 */
	public static final String NUM_DECKS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " NUMDECKS";
	/**
	 * In a LISTGAMES response, signifies an attribute for the minimum bet
	 */
	public static final String MIN_BET_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MINBET";
	/**
	 * In a LISTGAMES response, signifies an attribute for the maximum bet
	 */
	public static final String MAX_BET_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MAXBET";
	/**
	 * In a LISTGAMES response, signifies an attribute for the current number of players
	 */
	public static final String NUM_PLAYERS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " NUMPLAYERS";
	/**
	 * In a LISTGAMES response, signifies an attribute for the minimum required number of players
	 */
	public static final String MIN_PLAYERS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MINPLAYERS";
	/**
	 * In a LISTGAMES response, signifies an attribute for the maximum allowed number of players
	 */
	public static final String MAX_PLAYERS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MAXPLAYERS";
	/**
	 * In a LISTGAMES response, signifies that a game is active
	 */
	public static final String ACTIVE_STATUS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " STATUS ACTIVE";
	/**
	 * In a LISTGAMES response, signifies that a game is inactive
	 */
	public static final String INACTIVE_STATUS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " STATUS INACTIVE";
	/**
	 * In a LISTGAMES response, signifies that a rule definition is on this line
	 */
	public static final String RULE_KEYWORD = "RULE"; 
	/**
	 * In a LISTGAMES response, signifies the start of a game record
	 */
	public static final String RECORD_START_KEYWORD = "GAME";
	/**
	 * In a LISTGAMES response, signifies the end of a game record
	 */
	public static final String RECORD_END_KEYWORD = "ENDGAME";
	/**
	 * In a GAMESTATUS response, signifies that the gamestage is on this line
	 */
	public static final String GAMESTAGE_KEYWORD = "GAMESTAGE";
	/**
	 * In a GAMESTATUS response, signifies an unknown keyword
	 */
	public static final String UNKNOWN_KEYWORD = "UNKNOWN";
	/**
	 * In a GAMESTATUS response, signifies a bet amount is on this line
	 */
	public static final String BET_KEYWORD = "BET";
	/**
	 * In a GAMESTATUS response, signifies a card hand is on this line
	 */
	public static final String HAND_KEYWORD = "HAND";
	/**
	 * In a GAMESTATUS response, signifies that the user is an active player
	 */
	public static final String ACTIVE_PLAYER = "ACTIVE_PLAYER";
	/**
	 * In a GAMESTATUS response, signifies that the user is an observer currently
	 */
	public static final String OBSERVER_KEYWORD = "OBSERVER";
	/**
	 * In a GAMESTATUS response, signifies an unknown username
	 */
	public static final String UNKNOWN_USERNAME = "(unknown)";
	
	/*******************************************************************************
	 * private variables
	 *****************************************************************************/
	
	// This holds all of our dynamic game information
	private GameState state;
	
	// This holds all of our static game information
	private GameMetadata metadata;
	
	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(Game.class.getName());
	
	/*******************************************************************************
	 * Constructor
	 *****************************************************************************/
	
	/**
	 * Constructs a new active game based on the gamemetadata passed in.
	 * 
	 * @param metadata Set game metadata
	 */
	public Game(GameMetadata metadata) {
		this.metadata = metadata;
		if( metadata != null && metadata.getId() != null ) {
			state = new GameState( metadata.getId(), metadata.getNumDecks() );
		}
	}
	
	
	/*******************************************************************************
	 * Methods that have to do with players coming and going
	 *****************************************************************************/
	
	
	/**
	 * Each active game has a maximum number of players based on
	 * its metadata. By comparing the size of the players list to
	 * this limit, it can be determined if the game still has room
	 * for more player.
	 * 
	 * @return True if there's room, else false.
	 */
	public boolean stillHasRoom() {
		
		// If we don't have metadata it's a weird error state, so 
		// assume we don't have room. This shouldn't happen either.
		if( metadata == null ) {
			return false;
		}
		
		// Make a comparison here
		return state.getNumberOfPlayers() < metadata.getMaxPlayers();
	}
	
	/**
	 * Add a player to the game. 
	 * @return True if it worked, false otherwise
	 */
	public synchronized boolean addPlayer(User player) {
		
		// This would be bad
		if( player == null ) {
			return false;
		}
		
		boolean successfullyAdded = state.addPlayer(player);
		if( successfullyAdded && state != null ) {
			state.notifyOthersOfJoinedPlayer( player );
		}
		
		return successfullyAdded;
	}
	
	/**
	 * Remove a player from the game. A response code has to be
	 * generated to specify if they forfeited a bet by leaving in
	 * the current state; if they did forfeit a bet, this routine
	 * also has to debit the user's bank account. 
     * <P>
	 *  If something went wrong and they can't be removed (like,
	 *  if they weren't in there in the first place) just return
	 *  null.
	 *  
	 *  @return A response code corresponding to the proper
	 *  response to the instigating client, or null if there
	 *  was some sort of error that prevented it from working
	 */
	public ResponseCode removePlayer(User player) {
		
		if( player == null ) {
			LOGGER.severe( "Trying to remove a null player is not allowed." );
			return null;
		}
		
		// Hopefully we can remove them
		if( !state.removePlayer(player) ) {
			LOGGER.severe( "Something went wonky in trying to remove the player from the game." );
			return null;
		}
		
		// If we got here, we succeeded!
		player.setGame(null);
		state.notifyOthersOfDepartedPlayer( player );
		
		// Were they in a state where they forfeited their bet?
		// TODO: Implement the distinction between being midplay, and not
		// being midplay. FOr now, just assume not midplay
		return new ResponseCode( ResponseCode.CODE.SUCCESSFULLY_LEFT_SESSION_NOT_MIDPLAY,
				"Have not coded logic to see if a bet was forfeited.");
	}
	

	/*******************************************************************************
	 * Handles notifying various players about various game actions
	 *****************************************************************************/

	
	/**
	 * Requests that a message be sent out to notify others
	 * in the game that a user has placed a bet
	 * 
	 * @param user Who bet
	 * @param bet What they bet
	 */
	public boolean notifyOfPlayerBet( User user, int bet ) {
		boolean success = false;
		if( state != null ) {
			success = state.notifyOthersOfBetPlaced( user, bet );
		}
		return success;
	}

	/**
	 * Requests that a message be sent out to notify others
	 * in the game that a user has new cards. The message
	 * is sent to everyone, including the player themselves.
	 * The hand of cards is on the user object.
	 * 
	 * @param user Who has new cards
	 */
	public boolean notifyOfPlayerNewCards(User user ) {
		boolean success = false;
		if( state != null ) {
			success = state.notifyAllOfNewCards( user );
		}
		return success;
	}	

	
	/*******************************************************************************
	 * Some getters/setters that aren't overly complex in logic
	 *****************************************************************************/
	
	
	/**
	 * An active game has at least one player in it and an active
	 * game thread running it. 
	 * @return True if the game is active, false otherwise
	 */
	public boolean isActive() {
		return state.getNumberOfPlayers() > 0;
	}
	
	/**
	 * Gets the game metadata associated with this game
	 * @return The metadata, or null if not set
	 */
	public GameMetadata getMetadata() {
		return metadata;
	}
	
	/**
	 * Gets the game state associated with this game
	 * @return The state, or null if not set
	 */
	public GameState getGameState() {
		return state;
	}
	
	/**
	 * Convenience method that gets the ID from the game
	 * metadata. If it's not set, this will return null.
	 * 
	 * @return The ID from the metadata, or null if not set
	 */
	public String getId() {
		if( metadata != null ) {
			return metadata.getId();
		}
		return null;
	}

	
	/*******************************************************************************
	 * Methods that have to do with supplying needed information about the game
	 *****************************************************************************/

	
	/**
	 * The game descriptor is a string, with newlines in it, capable of
	 * being inserted directly into the response of a GAMESTATUS command.
	 * This describes active details about the game, that is, information
	 * about the session it's in -- what players, what are their bets,
	 * what are their cards.
	 * 
	 * @param user The user on whose behalf to get the status. This is needed
	 * because the way the hands are shown differ depending on if it's
	 * the user's hand, or someone else's, where facedown cards are not
	 * shown.
	 * @return A string representing the game suitable of putting
	 * into the response of a GAMESTATUS command. It appears to have
	 * an extra newline at the end that I can't remove so there you go
	 */	
	public String getGameStatus( User user ) {
		
		// First line has the session (aka game) id
		StringBuilder str = new StringBuilder( getId() );
		str.append( BlackjackServer.EOL );
		
		// Next line is either GAMESTAGE STARTED or GAMESTAGE NOT_STARTED
		str.append( GAMESTAGE_KEYWORD );
		str.append( " " );
		
		if( this.state != null && state.getGameStage() != null ) {
			str.append( state.getGameStage() );
		} else {
			str.append( UNKNOWN_KEYWORD );
		}
		str.append( BlackjackServer.EOL );
		
		// Then we list of all players
		User[] players = null;
		if( state != null ) {
			players = state.getCopyOfPlayers();
		}
		
		if( players == null ) {
			LOGGER.severe( "Could not get a list of players for the game status response." );
		} else {
			// Print the list of active players
			for( User player : players ) {
				// They need to have active status 
				if( player.getStatus() != null && player.getStatus().equals(GameState.STATUS.ACTIVE ) ) {
					str.append( concatKeywordAndUsername(ACTIVE_PLAYER, player) );
					str.append( BlackjackServer.EOL );
				}
			}

			// Print the list of observer players
			for( User player : players ) {
				// They need to have active status 
				if( player.getStatus() != null && player.getStatus().equals(GameState.STATUS.OBSERVER ) ) {
					str.append( concatKeywordAndUsername(OBSERVER_KEYWORD, player) );
					str.append( BlackjackServer.EOL );
				}
			}

			// Print out the bets
			for( User player : players ) {
				// They need to have active status 
				if( player.hasSpecifiedBet() ) {
					str.append( concatKeywordAndUsername(BET_KEYWORD, player) );
					// And then the amount
					str.append( " " );
					str.append( player.getBet() );
					str.append( BlackjackServer.EOL );
				}
			}
			
			// Print out the player hands
			for( User player : players ) {
				// They need to have active status 
				if( player.getHand() != null ) {
					str.append( concatKeywordAndUsername(HAND_KEYWORD, player) );
					// And then the hand
					str.append( " " );
					if( user != null ) {
						// SECURITY: Get the representaion of the hand specific to this user
						str.append( player.getHand().toString(user) );
					}
					str.append( BlackjackServer.EOL );
				}
			}
			
			// And the dealer's hand
			if( state != null && state.getDealerHand() != null ) {
				str.append( concatKeywordAndUsername(HAND_KEYWORD, null) );
				str.append( " " );
				// SECURITY: Get the representaion of the hand specific to this user
				str.append( state.getDealerHand().toString(user) );
				str.append( BlackjackServer.EOL );
			}
		}
		
		return str.toString();
	}

	/**
	 * Lots of lines for messages that need to get sent, for notification
	 * about activity in the game, start off with a keyword followed by a 
	 * username. This creates such a string.
	 * @param keyword The keyword, to be followed by a space and the username
	 * @param player The player whose username to use
	 */
	private String concatKeywordAndUsername(String keyword, User player) {
		
		StringBuilder str = new StringBuilder( keyword );
		str.append( " " );
		// It's either the dealer if the player if null
		if( player == null ) {
			str.append( GameState.DEALER_USERNAME );
		} else if( player.getUserMetadata() != null && player.getUserMetadata().getUsername() != null ) {
			// Or they need a username
			str.append( player.getUserMetadata().getUsername() );
		} else {
			LOGGER.warning( "Could not find a non-null username for a player in game " + getId() );
			str.append( UNKNOWN_USERNAME );
		}
		return str.toString();
	}
	
	/**
	 * The game descriptor is a string, with newlines in it, capable of
	 * being inserted directly into the response of a LISTGAMES command.
	 * This describes game metadata, basically, as the only 'active'
	 * detail about the game that is shown is whether or not it's active.
	 * For example, one string response might look like this:
	 * <p>
	 * <pre>
	 * GAME game1 This is sample game 1
	 * ATTRIBUTE STATUS INACTIVE
	 * ATTRIBUTE MAXPLAYERS 6
	 * ATTRIBUTE MINPLAYERS 1
	 * ATTRIBUTE MINBET 5
	 * ATTRIBUTE MAXBET 10
	 * ATTRIBUTE NUMPLAYERS 0
	 * ATTRIBUTE NUMDECKS 4
	 * ENDGAME
	 * </pre>
	 * 
	 * @return A string representing the game suitable of putting
	 * into the response of a LISTGAMES command
	 */
	public String getGameDescriptor() {

		if( metadata == null ) {
			// This is bad
			LOGGER.severe( "Could not create the game descriptor because of null metadata." );
			return null;
		}
		
		StringBuilder str = new StringBuilder();
		str.append( RECORD_START_KEYWORD + " " + metadata.getId() + " Blackjack\n" );
		if( isActive() ) {
			str.append( ACTIVE_STATUS_ATTRIBUTE + BlackjackServer.EOL );
		} else {
			str.append( INACTIVE_STATUS_ATTRIBUTE + BlackjackServer.EOL );
		}
		str.append( MIN_PLAYERS_ATTRIBUTE + " " + metadata.getMinPlayers() + BlackjackServer.EOL);
		str.append( MAX_PLAYERS_ATTRIBUTE + " " + metadata.getMaxPlayers() + BlackjackServer.EOL);
		str.append( MIN_BET_ATTRIBUTE + " " + metadata.getMinBet() + BlackjackServer.EOL);
		str.append( MAX_BET_ATTRIBUTE + " " + metadata.getMaxBet() + BlackjackServer.EOL);
		str.append( NUM_PLAYERS_ATTRIBUTE + " " + state.getNumberOfPlayers() + BlackjackServer.EOL);
		str.append( NUM_DECKS_ATTRIBUTE + " " + metadata.getNumDecks() + BlackjackServer.EOL);
		if( metadata.getRules() != null && metadata.getRules().size() > 0 ) {
			for( String rule : metadata.getRules() ) {
				StringBuilder ruleLine = new StringBuilder( RULE_KEYWORD );
				ruleLine.append( " " );
				ruleLine.append( rule );
				ruleLine.append( BlackjackServer.EOL );
				if( ruleLine.toString().trim().length() <= (RULE_KEYWORD.length() + 2) ) {
					LOGGER.warning( "Game " + metadata.getId() + " had a rule that appeared empty." );
				} else {
					str.append( ruleLine.toString() );
				}
			}
		}
		str.append( RECORD_END_KEYWORD + BlackjackServer.EOL );
		return str.toString();
	}

}
