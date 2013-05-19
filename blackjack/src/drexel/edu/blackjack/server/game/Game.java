package drexel.edu.blackjack.server.game;

import java.util.ArrayList;
import java.util.List;

import drexel.edu.blackjack.db.game.GameMetadata;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * Holds information about the blackjack game.
 * @author Jennifer
 *
 */
public class Game {

	/*******************************************************************************
	 * private variables
	 *****************************************************************************/
	private List<User> players;
	private GameState state;
	private User currentPlayer;
	private GameMetadata metadata;
	
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
	
	public String getGameDescriptor() {
		return null;
	}
	

}
