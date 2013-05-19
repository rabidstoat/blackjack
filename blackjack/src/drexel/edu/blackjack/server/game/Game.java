package drexel.edu.blackjack.server.game;

import java.util.ArrayList;
import java.util.List;

import drexel.edu.blackjack.db.game.GameMetadata;

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
	
	public void addPlayer(User player) {
		
	}
	
	public void removePlayer(User player) {
		
	}
	
	public String getGameDescriptor() {
		return null;
	}
	

}
