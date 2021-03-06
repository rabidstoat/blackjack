/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - User.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is an object-oriented view of the user as logged into the
 * server. It contains a reference to their metadata, but also references to
 * more active data, like their current card hand (since a user can only be
 * in one game at a time) and their connection to the user's client.
 ******************************************************************************/
package drexel.edu.blackjack.server.game;

import java.util.logging.Logger;

import drexel.edu.blackjack.cards.Hand;
import drexel.edu.blackjack.db.user.FlatfileUserManager;
import drexel.edu.blackjack.db.user.UserManagerInterface;
import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackServerThread;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.game.GameState.STATUS;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Think of this as the dynamic instantiation of
 * the UserMetadata. A User object is only created
 * when a user logs into the server.
 * 
 * @author Jennifer
 */
public class User {
  
	/***************************************************************
	 * Local variabls go here.
	 **************************************************************/

	// Their metadata
	private UserMetadata userMetadata;
	
	// Hand they have in the current game
	private Hand hand;

	// This variable is very similar to the next one, so here is the distinction:
	// this flag is whether or not they have ended their gameplay this round. It's
	// not relevant if the player isn't active. But if they are, this should be true
	// only after, in this round, one of the following things happened: a) they timed
	// out and no longer are in the game; b) they chose to STAND; c) they chose to hit
	// and busted.
	private boolean hasFinishedGameplayThisRound = true;
	
	// This variable is very similar to the previous one, so here is the distinction:
	// this flag is whether or not they have chosen to HIT or STAND, since the last
	// request for them to do so has been made. They might still have more plays to
	// make this round, so it's not necessary that the hasFinishedGameplayThisRound
	// be true if this variable is true. Whenever someone does a HIT or STAND command,
	// this variable should be set to false
	private boolean needsToMakeAPlay = false;
	
	// Game they are playing
	private Game game;
	
	// Their status within that game
	private GameState.STATUS status;
	
	// Their server thread
	private BlackjackServerThread thread = null;
	
	// For debug output
	private final static Logger LOGGER = BlackjackLogger.createLogger(User.class.getName()); 

	
	/***************************************************************
	 * Constructors go here
	 **************************************************************/
	

	/**
	 * Create a user with no associated metadata. 
	 * Presumably it will be specified later.
	 */
	public User() {
	}
	
	/**
	 * Create a user with the specified metadata
	 * referenced.
	 * 
	 * @param userMetadata The user's metadata
	 */
	public User( UserMetadata userMetadata ) {
		this.userMetadata = userMetadata;
	}
	
	
	/***************************************************************
	 * Simple, straightforward Getters and setters
	 **************************************************************/

	
	// Auto-generated getters and setters below
	/**
	 * Get the user metadata
	 * @return the user metadata
	 */
	public UserMetadata getUserMetadata() {
		return userMetadata;
	}
	/**
	 * Set the user metadata
	 * @param user the user metadata to set
	 */
	public void setUserMetadata(UserMetadata user) {
		this.userMetadata = user;
	}
	/**
	 * Get the user's hand. Since our implementation allows the
	 * player to be in one game at a time, we can store this on
	 * their user object directly.
	 * 
	 * @return the hand of cards, or null if not currently
	 * dealt cards
	 */
	public Hand getHand() {
		return hand;
	}
	/**
	 * Set the user's hand of cards.
	 * @param hand the hand to set
	 */
	public void setHand(Hand hand) {
		this.hand = hand;
	}

	/**
	 * Get the game the user is in. Since our implementation
	 * allows for only one game per user, we can keep it on
	 * the user object.
	 * 
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Set the game the user is in.
	 * 
	 * @param game the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Set whether or not a user has finished their game
	 * play for this round. 
	 * 
	 * @param state True if they have, else false
	 */
	public void setHasFinishedGamePlayThisRound(boolean state) {
		this.hasFinishedGameplayThisRound = state;
	}

	/**
	 * Get whether or not a user has finished their game
	 * play for this round.
	 * 
	 * @return True if they have, else false
	 */
	public boolean getFinishedGamePlayThisRound() {
		return this.hasFinishedGameplayThisRound;
	}

	/**
	 * Set whether or not a user has satisfied the most
	 * recent request for them to make a game play.
	 * 
	 * @param state True if they have, else false
	 */
	public void setNeedsToMakeAPlay(boolean state) {
		this.needsToMakeAPlay = state;
	}

	/**
	 * Get whether or not a user has satisfied the most
	 * recent request for them to make a game play.
	 * 
	 * @return True if they have, else false
	 */
	public boolean getNeedsToMakeAPlay() {
		return this.needsToMakeAPlay;
	}

	/**
	 * If the user is connected (and they should be), keep a pointer
	 * to their server thread around, so messages can get sent
	 * 
	 * @param thread The server thread to set
	 */
	public void setBlackjackServerThread(BlackjackServerThread thread) {
		this.thread = thread;
	}
	
	/**
	 * Used for other people to set the user's status. Typically the
	 * game will set the status on the user as they move between
	 * observer and active status
	 * 
	 * @param status The game status to set
	 */
	protected void setStatus( GameState.STATUS status ) {
		this.status = status;
	}
	
	/**
	 * Return the status in the game of this player
	 * 
	 * @return The game status
	 */
	public GameState.STATUS getStatus() {
		return status;
	}

	/**
	 * Return true if the user has a non-null status
	 * that is set to ACTIVE, false otherwise
	 * 
	 * @return True if active, false otherwise
	 */
	public boolean isActive() {
		return getStatus() != null && getStatus().equals(STATUS.ACTIVE);
	}

	
	/***************************************************************
	 * More complex ways of getting and setting things, with more
	 * logic than just setting or returning a simple variable
	 **************************************************************/

	
	/**
	 * Sets the state on the protocol object associated with this user's
	 * connection. 
	 * 
	 * @return True if it was successful, otherwise false
	 */
	public boolean setProtocolState( BlackjackProtocol.STATE state ) {
		
		if( thread != null && thread.getProtocol() != null ) {
			thread.getProtocol().setState( state );
			return true;
		}
		
		return false;
	}

	/**
	 * A user has specified a bet if they have a non-null protocol
	 * object somewhere, and there is a bet value set on it.
	 * 
	 * @return True if they have a specified bet, else false
	 */
	public boolean hasSpecifiedBet() {
		
		if( thread != null && thread.getProtocol() != null ) {
			return thread.getProtocol().getBet() != null;
		}
		
		// THis would be bad
		return false;
	}

	/**
	 * A user has specified a bet if they have a non-null protocol
	 * object somewhere, and there is a bet value set on it.
	 * 
	 * @return The bet amount, if a bet is currently made, or else
	 * null.
	 */
	public Integer getBet() {
		
		if( thread != null && thread.getProtocol() != null ) {
			return thread.getProtocol().getBet();
		}
		
		// This is sort of bad....
		return null;
	}

	/**
	 * Remove any bets that are associated with this player,
	 * clear out their current card hand, and reset their need
	 * to make a game-playing move to 'true'
	 */
	public void resetForNextRound() {
		if( thread != null && thread.getProtocol() != null ) {
			thread.getProtocol().setBet(null);
			this.setHand(null);
			this.setHasFinishedGamePlayThisRound( false );
		}
	}
	
	
	/***************************************************************
	 * Other public methods, typically these have to do with
	 * interacting with the socket.
	 **************************************************************/

	
	/**
	 * Sends a message to the presumably connected user by 
	 * sending it through to their socket
	 * 
	 * @param code What response code to send
	 */
	public boolean sendMessage(ResponseCode code) {
		
		boolean success = false;
		
		if( thread == null ) {
			LOGGER.severe( "Had a request to send user " + 
					(userMetadata == null ? userMetadata.getUsername() : "with no metadata" ) + 
					" a response, but couldn't find their socket." );
		} else {
			thread.sendMessage( code );
			success = true;
		}
		
		return success;
	}

	/**
	 * If a user is forced to timeout while betting, they
	 * have their state changed to not being in a session,
	 * and a response sent to them alerting them of this
	 */
	public void forceTimeoutWhileBetting() {
		
		// First change the state
		if( thread != null && thread.getProtocol() != null ) {
			thread.getProtocol().setState( STATE.NOT_IN_SESSION );
		}
		
		// THen send the response code
		ResponseCode code = new ResponseCode( ResponseCode.CODE.TIMEOUT_EXCEEDED_WHILE_BETTING );
		this.sendMessage( code );
	}
	
	/**
	 * If a user is forced to timeout while playing, they
	 * have their state changed to not being in a session,
	 * and a response sent to them alerting them of this
	 */
	public void forceTimeoutWhilePlaying() {
		// First change the state
		if( thread != null && thread.getProtocol() != null ) {
			thread.getProtocol().setState( STATE.NOT_IN_SESSION );
		}
		
		// THen send the response code
		ResponseCode code = new ResponseCode( ResponseCode.CODE.TIMEOUT_EXCEEDED_WHILE_PLAYING );
		this.sendMessage( code );
	}



	/**
	 * Placing a bet involves several things:
	 * 
	 * <OL>
	 * <LI>The protocol state needs to be updated to reflect the bet
	 * <LI>The user's account needs to be deducted
	 * <LI>Others in the game need to be notified
	 * </OL>
	 * @param desiredBet
	 */
	public void handlePlacedBet(Integer desiredBet) {
		if( desiredBet != null ) {
			// First, set the protocol state
			if( thread != null && thread.getProtocol() != null ) {
				thread.getProtocol().setBet(desiredBet);
			}
			
			// Next, deduct from the user's account, and force a save
			if( userMetadata != null ) {
				userMetadata.setBalance( userMetadata.getBalance() - desiredBet );
				UserManagerInterface userManager = FlatfileUserManager.getDefaultUserManager();
				userManager.save();
			}
			
			// Finally, alert others in the game about the bet
			if( game != null ) {
				game.notifyOfPlayerBet( this, desiredBet );
			}
		}
	}

	/**
	 * This method not only sets the other hand, it also
	 * sends out notifications to others who are in the
	 * same game session.
	 * 
	 * @param hand The hand to set
	 */
	public void setHandAndNotify(Hand hand) {
		
		// Set the hand
		this.hand = hand;
		
		// ANd notify others
		if( game !=null ) {
			game.notifyOfPlayerNewCards( this );
		}
	}

	/**
	 * STATEFUL
	 * When it's the player's turn, their protocol object
	 * needs to be set to reflect this by changing state
	 */
	public void setIsPlayerTurn() {
		if( thread != null && thread.getProtocol() != null ) {
			thread.getProtocol().setState( STATE.IN_SESSION_AND_YOUR_TURN );
		} else {
			LOGGER.severe( "We were unable to move the user to the 'YOUR_TURN' state" );
		}
	}

	
	/***************************************************************
	 * Needed to implement the equals() method
	 **************************************************************/

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userMetadata == null) ? 0 : userMetadata.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userMetadata == null) {
			if (other.userMetadata != null)
				return false;
		} else if (!userMetadata.equals(other.userMetadata))
			return false;
		return true;
	}

	/**
	 * This is basically equality based on username value
	 * 
	 * @return True if the usernames are both non-null
	 * and identical, false otherwise
	 */
	public boolean hasSameUsername(User player) {
		if( player != null && player.getUserMetadata() != null &&
				player.getUserMetadata().getUsername() != null &&
				this.getUserMetadata() != null &&
				this.getUserMetadata().getUsername() != null ) {
			return this.getUserMetadata().getUsername().equals( player.getUserMetadata().getUsername() );
		}
		
		return false;
	}

}
