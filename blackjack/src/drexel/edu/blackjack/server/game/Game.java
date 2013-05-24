package drexel.edu.blackjack.server.game;

import java.util.logging.Logger;

import drexel.edu.blackjack.db.game.GameMetadata;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Holds information about the blackjack game.
 * @author Jennifer
 *
 */
public class Game {

	/*******************************************************************************
	 * These are used in game descriptors
	 *****************************************************************************/
	public static final String ATTRIBUTE_KEYWORD = "ATTRIBUTE";
	public static final String NUM_DECKS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " NUMDECKS";
	public static final String MIN_BET_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MINBET";
	public static final String MAX_BET_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MAXBET";
	public static final String NUM_PLAYERS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " NUMPLAYERS";
	public static final String MIN_PLAYERS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MINPLAYERS";
	public static final String MAX_PLAYERS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " MAXPLAYERS";
	public static final String ACTIVE_STATUS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " STATUS ACTIVE";
	public static final String INACTIVE_STATUS_ATTRIBUTE = ATTRIBUTE_KEYWORD + " STATUS INACTIVE";
	public static final String RECORD_START_KEYWORD = "GAME";
	public static final String RECORD_END_KEYWORD = "ENDGAME";
	
	/*******************************************************************************
	 * private variables
	 *****************************************************************************/
	private GameState state;
	private GameMetadata metadata;
	private final static Logger LOGGER = BlackjackLogger.createLogger(Game.class.getName()); 
	
	/*******************************************************************************
	 * Constructor
	 *****************************************************************************/
	
	/**
	 * Constructs a new active game based on the gamemetadata passed in.
	 * 
	 * @param metadata
	 */
	public Game(GameMetadata metadata) {
		this.metadata = metadata;
		if( metadata != null && metadata.getId() != null ) {
			state = new GameState( metadata.getId() );
		}
	}
	
	/*******************************************************************************
	 * Public methods
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
	 * 
	 *  If something went wrong and they can't be removed (like,
	 *  if they weren't in there in the first place) just return
	 *  null
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
		
		state.notifyOthersOfDepartedPlayer( player );
		
		// Were they in a state where they forfeited their bet?
		// TODO: Implement the distinction between being midplay, and not
		// being midplay. FOr now, just assume not midplay
		return new ResponseCode( ResponseCode.CODE.SUCCESSFULLY_LEFT_SESSION_NOT_MIDPLAY,
				"Have not coded logic to see if a bet was forfeited.");
	}
	
	/**
	 * The game descriptor is a string, with newlines in it, capable of
	 * being inserted directly into the response of a LISTGAMES command.
	 * For example, one string response might look like this:
	 * GAME game1 This is sample game 1
	 * ATTRIBUTE STATUS INACTIVE
	 * ATTRIBUTE MAXPLAYERS 6
	 * ATTRIBUTE MINPLAYERS 1
	 * ATTRIBUTE MINBET 5
	 * ATTRIBUTE MAXBET 10
	 * ATTRIBUTE NUMPLAYERS 0
	 * ATTRIBUTE NUMDECKS 4
	 * ENDGAME
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
			str.append( ACTIVE_STATUS_ATTRIBUTE + "\n" );
		} else {
			str.append( INACTIVE_STATUS_ATTRIBUTE + "\n" );
		}
		str.append( MIN_PLAYERS_ATTRIBUTE + " " + metadata.getMinPlayers() + "\n");
		str.append( MAX_PLAYERS_ATTRIBUTE + " " + metadata.getMaxPlayers() + "\n");
		str.append( MIN_BET_ATTRIBUTE + " " + metadata.getMinBet() + "\n");
		str.append( MAX_BET_ATTRIBUTE + " " + metadata.getMaxBet() + "\n");
		str.append( NUM_PLAYERS_ATTRIBUTE + " " + state.getNumberOfPlayers() + "\n");
		str.append( NUM_DECKS_ATTRIBUTE + " " + metadata.getNumDecks() + "\n");
		str.append( RECORD_END_KEYWORD + "\n" );
		return str.toString();
	}
	
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

	/**
	 * Starting the game involves starting a new round
	 * of play, then requesting bets.
	 */
	public void start() {
		if( state == null ) {
			LOGGER.severe( "Trying to start a game with a null state can't be good..." );
		} else {
			// This will set players active and set the current player
			state.startNewRound();
		}
	}
}
