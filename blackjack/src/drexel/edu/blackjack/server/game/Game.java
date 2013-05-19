package drexel.edu.blackjack.server.game;

import java.util.ArrayList;
import java.util.List;
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
	private List<User> players;
	private GameState state;
	private User currentPlayer;
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
		players = new ArrayList<User>();
		state = new GameState();
		currentPlayer = null;
		this.metadata = metadata;
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
		
		// This shouldn't happen
		if( players == null ) {
			players = new ArrayList<User>();
		}
		
		// If we don't have metadata it's a weird error state, so 
		// assume we don't have room. This shouldn't happen either.
		if( metadata == null ) {
			return false;
		}
		
		// Make a comparison here
		return players.size() < metadata.getMaxPlayers();
	}
	
	/**
	 * Add a player to the game. 
	 * @return True if it worked, false otherwise
	 */
	public boolean addPlayer(User player) {
		// TODO: Implement
		return false;
	}
	
	/**
	 * Remove a player from the game. A response code has to be
	 * generated to specify if they forfeited a bet by leaving in
	 * the current state; if they did forfeit a bet, this routine
	 * also has to debit the user's bank account. 
	 * 
	 *  If something went wrong and they can't be removed (like,
	 *  if they weren't in there in the frist place) just return
	 *  null
	 */
	public ResponseCode removePlayer(User player) {
		// TODO: Implement
		return null;
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
		str.append( RECORD_START_KEYWORD + " " + metadata.getId() + " A friendly game of blackjack\n" );
		if( isActive() ) {
			str.append( this.ACTIVE_STATUS_ATTRIBUTE + "\n" );
		} else {
			str.append( this.INACTIVE_STATUS_ATTRIBUTE + "\n" );
		}
		str.append( MIN_PLAYERS_ATTRIBUTE + " " + metadata.getMinPlayers() + "\n");
		str.append( MAX_PLAYERS_ATTRIBUTE + " " + metadata.getMaxPlayers() + "\n");
		str.append( MIN_BET_ATTRIBUTE + " " + metadata.getMinBet() + "\n");
		str.append( MAX_BET_ATTRIBUTE + " " + metadata.getMaxBet() + "\n");
		str.append( NUM_PLAYERS_ATTRIBUTE + " " + (players == null ? 0 : players.size()) + "\n");
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
		return players != null && players.size() > 0;
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
}
