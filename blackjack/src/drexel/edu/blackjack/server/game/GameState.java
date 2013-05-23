package drexel.edu.blackjack.server.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Used to pass game state information from
 * the server to client, to show what bets
 * are made, what cards are dealt, what users
 * are joining and leaving, etc.
 * 
 * See Section 2.15 of the protocol design
 * for details.
 * 
 * @author Jennifer
 *
 */
public class GameState {

	// And a logger for errors
	private final static Logger LOGGER = BlackjackLogger.createLogger(GameState.class.getName()); 

	private enum STATUS {
		ACTIVE,		// The user is an active participant
		OBSERVER,	// The user is in the session, but as an observer
		GONE,		// The user was in the session, but has left during this round
		RETURNED	// The user was in the session, left, and returned while in the same round
	}
	
	// An ordered list of players involved in the game. Gameplay will
	// occur in this order, amongst the ACTIVE users. 
	private List<User> players 		= null;
	
	// The current player in terms of play
	private User currentPlayer		= null;
	
	// This list parallels the players list. It is a way of associating a 
	// status with a User. I can't just store it in the User object, as it
	// related to the User in the context of a game session. A User could be
	// ACTIVE in one session, and GONE in another game session. Later, they
	// might be ACTIVE in multiple sessions.
	private List<STATUS> statuses	= null;
	
	// Every game has an identifier
	private String gameId			= null;

	/*********************************************************************
	 * Constructor goes here
	 ********************************************************************/
	
	/**
	 * Construct a game state object for the given game
	 * ID.
	 * 
	 * @param gameId A game's unique identifier to be used in
	 * constructed messages
	 */
	public GameState( String gameId ) {
		this.gameId = gameId;
		players = Collections.synchronizedList(new ArrayList<User>());
		statuses = Collections.synchronizedList(new ArrayList<STATUS>());
	}

	/*********************************************************************
	 * Public methods go here
	 ********************************************************************/

	/**
	 * Need to send out messages to the remaining players (those who are
	 * not GONE status, or the player themselves) and notify them about
	 * a player leaving the game.
	 * 
	 * This is a ResponseCode.CODE.PLAYER_LEFT code, and the parameters
	 * are the gameId followed by the userId 
	 *
	 * @param departedPlayer
	 */
	public void notifyOthersOfDepartedPlayer(User departedPlayer) {
		
		// They can't be null, that's bad
		if( departedPlayer != null && players != null && statuses != null ) {
			
			// Need to formulate the ResponseCode that we would send
			StringBuilder str = new StringBuilder( gameId );
			str.append( " " );
			UserMetadata metadata = departedPlayer.getUserMetadata();
			if( metadata == null || metadata.getUsername() == null ) {
				str.append( "(unknown)" );
			} else {
				str.append( metadata.getUsername() );
			}

			// And then send it to all the remaining players who are not: a) this
			// user; or, b) GONE. We need to first create a list of these users
			// in a synchronized block
			User[] copy = getCopyOfPlayersExcept(departedPlayer);
			
			// If copy is non-null, we can send our messages
			if( copy != null ) {
				ResponseCode code = new ResponseCode( ResponseCode.CODE.PLAYER_LEFT, str.toString() );
				for( int i = 0; i < copy.length; i++ ) {
					User user = ((User)copy[i]);
					if( user != null ) {
						user.sendMessage( code );
					}
				}
			}
		}
	}
	
	/**
	 * Need to send out messages to the other players (if any) about
	 * how some new player has joined the session.
	 * 
	 * @param newPlayer Who just joined
	 */
	public void notifyOthersOfJoinedPlayer( User newPlayer) {
		
		// They can't be null, that's bad
		if( newPlayer != null && players != null && statuses != null ) {
			
			// Need to formulate the ResponseCode that we would send
			StringBuilder str = new StringBuilder( gameId );
			str.append( " " );
			UserMetadata metadata = newPlayer.getUserMetadata();
			if( metadata == null || metadata.getUsername() == null ) {
				str.append( "(unknown)" );
			} else {
				str.append( metadata.getUsername() );
			}

			// And then send it to all the remaining players who are not: a) this
			// user; or, b) GONE. We need to first create a list of these users
			// in a synchronized block
			User[] copy = getCopyOfPlayersExcept(newPlayer);
			
			// If copy is non-null, we can send our messages
			if( copy != null ) {
				ResponseCode code = new ResponseCode( ResponseCode.CODE.PLAYER_JOINED, str.toString() );
				for( int i = 0; i < copy.length; i++ ) {
					User user = ((User)copy[i]);
					if( user != null ) {
						user.sendMessage( code );
					}
				}
			}
		}
	}	
	
	/**
	 * Adds a player to the tracking within the game state. Newly
	 * added players are entered in the OBSERVER state, unless...
	 * 
	 * Note that it's a possible the player is already IN the list
	 * of players, but in the GONE state. In this case, they should
	 * be in the RETURNED state.
	 */
	synchronized public boolean addPlayer( User player ) {

		// First, see if they are already there
		int index = players.indexOf(player);
		if( index != -1 ) {
			// Aha, they have returned!
			statuses.set( index, STATUS.RETURNED );
			return true;
		} 
		
		// Add the user to the end, in the OBSERVER status
		boolean success = players.add( player );
		success = success && statuses.add( STATUS.OBSERVER );
		
		// Some more consistency checking
		success = success && (statuses.size() == players.size());
		return success;
	}

	/**
	 * Removes a player to the tracking within the game state.
	 * It's important to note that this does NOT actually
	 * remove the player from the list! It merely changes
	 * their status to GONE. It is up to the game playing
	 * thread to later notify the GameState that it can
	 * safely remove players who are GONE (presumably at
	 * the end of the round).
	 */
	synchronized public boolean removePlayer( User player ) {
		
		// Find the index of the User
		int index = players.indexOf( player );
		if( index == -1 ) {
			LOGGER.severe( "Could not find the user " + player + " in the game state to remove!" );
			return false;
		}
		
		// The corresponding status better exist!
		if( index >= statuses.size() ) {
			LOGGER.severe( "Could not find the status for the user " + player + " in the game state to remove!" );
			return false;
		}
		
		// Now remove both of them
		boolean success = (players.remove(index) != null);
		success = success && (statuses.remove(index) != null);

		// Some more consistency checking
		success = success && (statuses.size() == players.size());
		return success;
	}
	
	/**
	 * Return a pointer to the current player
	 */
	synchronized public User getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Indicates to the game state that a new round
	 * is being started. A few things are done here:
	 * 
	 * <ol>
	 * <li>Players with GONE status can be removed
	 * <li>Players with OBSERVER or RETURNED status
	 *     can be set to ACTIVE
	 * <li>The currentPlayer is set to null
	 * </ol>
	 */
	synchronized public void startNewRound() {
		
		// Need to have valid lists
		if( players != null || statuses != null ) {
			removeGonePlayers();
			makeAllPlayersActive();
			currentPlayer = null;
		}
	}

	/**
	 * Advances the currentPlayer pointer to the next player
	 * in the list with ACTIVE status. If there was no 
	 * currentPlayer, it is the first User in the list
	 * with active status. If there are no more subsequent
	 * players with an ACTIVE status, it is set to null.
	 */
	synchronized public void advanceCurrentPlayer() {
		if( players == null ) {
			currentPlayer = null;
		} else if( currentPlayer == null ) {
			currentPlayer = getFirstActivePlayerFrom( 0 );
		} else {
			int index = players.indexOf( currentPlayer );
			if( index == -1 ) {
				LOGGER.severe( "Could not advance current player as we could not find them in the list." );
			} else {
				currentPlayer = getFirstActivePlayerFrom( index+1 );
			}
		}
	}
	
	/** 
	 * Returns the number of players who are present (i.e., not
	 * of GONE status) in the game stae
	 */
	synchronized public int getNumberOfPresentPlayers() {
		return getNumberOfPresentPlayersExcept(null);
	}
	
	/*********************************************************************
	 * Private methods go here
	 ********************************************************************/

	/**
	 * Walk through the players/statuses lists, and if
	 * a player has a status o GONE or RETURNED, turn
	 * them to ACTIVE.
	 * 
	 * TODO: I'm worried about this method and synchronization
	 */
	private void makeAllPlayersActive() {
		
		if( statuses != null ) {
			for( int i = 0; i < statuses.size(); i++ ) {
				STATUS status = statuses.get(i);
				if( status != null && 
						(status.equals(STATUS.OBSERVER) || status.equals(STATUS.RETURNED)) ) {
					statuses.set( i, STATUS.ACTIVE );
				}
			}
		}
	}

	/**
	 * Given a starting index, look through the statuses list for
	 * the first status on or after that value that is ACTIVE,
	 * and then return the corresponding USER. If there are no
	 * more ACTIVE statuses in the list at or after that index,
	 * then return null
	 * 
	 * TODO: I'm worried about this method and synchronization
	 * 
	 * @param startingIndex The index from which to start looking for
	 * ACTIVE status players
	 * @return The first encountered player with an ACTIVE
	 * status, or null if none are found
	 */
	private User getFirstActivePlayerFrom(int startingIndex ) {
		
		// Can't start at a negative index
		if( startingIndex < 0 ) {
			startingIndex = 0;
		}
		
		if( statuses == null || players == null ) {
			return null;
		}
		
		// Cycle through
		for( int i = startingIndex; i < statuses.size(); i++ ) {
			STATUS status = statuses.get(i);
			if( status != null && status.equals(STATUS.ACTIVE ) ) {
				if( i >= players.size() ) {
					LOGGER.severe( "Could not find a user at index " + i + " where we expected." );
				} else {
					return players.get(i);
				}
			}
		}
		
		// If we got here, we found nothing and need to return null
		return null;
	}

	/**
	 * Removes gone players by actually removing them
	 * (and their corresponding status) from the list
	 * 
	 * TODO: I'm worried about synchronization but the
	 * original method is synchronized, if this is
	 * synchronized too will there be deadlock?
	 */
	private void removeGonePlayers() {
		
		if( players != null && statuses != null && players.size() == statuses.size() ) {
			
			// First create a set of these users to remove
			Set<User> playersToRemove = new HashSet<User>();
			for( int i = 0; i < players.size(); i++ ) {
				STATUS thisStatus = statuses.get(i);
				if( thisStatus != null && thisStatus.equals(STATUS.GONE ) ) {
					playersToRemove.add( players.get(i) );
				}
			}
			
			// Now go through and remove them
			for( User playerToRemove : playersToRemove ) {
				int index = players.indexOf( playerToRemove );
				if( index == -1 ) {
					LOGGER.severe( "Thought we should truly remove " + playerToRemove + 
							" but they are no longer in the list." );
				} else {
					players.remove(index);
					statuses.remove(index);
				}
			}
		}		
	}

	/**
	 * Looks at the player list and returns how many players have a
	 * status of OBSERVER, RETURNED, or ACTIVE. Do not include the
	 * passed in player, if present, in the list.
	 * 
	 * Will return 0 if there are no players of this type. Will return
	 * -1 if there was an internal error.
	 * 
	 * @param exceptPlayer If null, this parameter is ignored. If non-null,
	 * then the count of active players will not include this player
	 * among it
	 * @return The number of active players, excluding any player who
	 * is passed as the parameter, or else -1 if an internal error
	 * occurred in processing this
	 */
	private int getNumberOfPresentPlayersExcept(User exceptPlayer) {
		
		// There must be none if everything is null
		if( players == null || statuses == null ) {
			return 0;
		}
		
		// Lists not equal is a problem
		if( players.size() != statuses.size() ) {
			return -1;
		}
		
		// Otherwise step through and increment a count
		int count = 0;
		for( int i = 0; i < statuses.size(); i++ ) {
			STATUS status = statuses.get(i);
			if( status != null && !status.equals(STATUS.GONE ) ) {
				User player = players.get(i);
				if( player != null ) {
					// If the except player is null, always count
					if( exceptPlayer == null ){
						count++;
					} else {
						// Otherwise, compare usernames, and only count if not the same
						if( !exceptPlayer.hasSameUsername(player) ) {
							count++;
						}
					}
				}
			}
		}
		
		return count;
	}

	/**
	 * Does this player have the gone status?
	 * 
	 * @param player Player in question
	 * @return True if they are gone, and false otherwise
	 */
	private boolean isGone(User player) {
		if( player != null && players != null && statuses != null ) {
			int index = players.indexOf(player);
			if( index >= 0 && index < statuses.size() ) {
				STATUS status = statuses.get(index);
				if( status != null ) {
					return status.equals(STATUS.GONE);
				}
			}
		}
		
		return false;
	}

	/**
	 * This synchronized method is used to get a copy of the users
	 * who are in the game session (that is, not of status GONE) and --
	 * if the playerToExclude is non-null -- excluding the playerToExclude
	 *  
	 * @param playerToExclude Don't include them in the list, regardless of status
	 * @return An array of players in the game state that don't have
	 * a GONE staus. If playerToExclude is not null, exclude that player in the list
	 */
	synchronized private User[] getCopyOfPlayersExcept( User playerToExclude  ) {
		int ultimateSize = getNumberOfPresentPlayersExcept(playerToExclude );
		User[] copy = new User[ultimateSize];
		int index = 0;
		for( User player : players ) {
			if( !isGone(player) && !player.hasSameUsername(playerToExclude ) ) {
				copy[index++] = player;
			}
		}
		return copy;
	}
}
