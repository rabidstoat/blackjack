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
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Think of this as the dynamic instantiation of
 * the UserMetadata. A User object is only created
 * when a user logs into the server.
 * 
 * @author Jennifer
 *
 */
public class User {
  
	/***************************************************************
	 * Local variabls go here.
	 **************************************************************/

	// Their metadata
	private UserMetadata userMetadata;
	
	// Hand they have in the current game
	private Hand hand;
	
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
	
	
	public User() {
	}
	
	public User( UserMetadata userMetadata ) {
		this.userMetadata = userMetadata;
	}
	
	
	/***************************************************************
	 * Simple, straightforward Getters and setters
	 **************************************************************/

	
	// Auto-generated getters and setters below
	/**
	 * @return the user
	 */
	public UserMetadata getUserMetadata() {
		return userMetadata;
	}
	/**
	 * @param user the user to set
	 */
	public void setUserMetadata(UserMetadata user) {
		this.userMetadata = user;
	}
	/**
	 * @return the hand
	 */
	public Hand getHand() {
		return hand;
	}
	/**
	 * @param hand the hand to set
	 */
	public void setHand(Hand hand) {
		this.hand = hand;
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * If the user is connected (and they should be), keep a pointer
	 * to their server thread around, so messages can get sent
	 * 
	 * @param blackjackProtocol
	 */
	public void setBlackjackServerThread(BlackjackServerThread thread) {
		this.thread = thread;
	}
	
	/**
	 * Used for other people to set the user's status. Typically the
	 * game will set the status on the user as they move between
	 * observer and active status
	 */
	protected void setStatus( GameState.STATUS status ) {
		this.status = status;
	}
	
	/**
	 * Return the status in the game of this player
	 */
	protected GameState.STATUS getStatus() {
		return status;
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
	 */
	public boolean hasSpecifiedBet() {
		
		if( thread != null && thread.getProtocol() != null ) {
			return thread.getProtocol().getBet() != null;
		}
		
		// THis would be bad
		return false;
	}

	/**
	 * Remove any bets that are associated with this player
	 */
	public void clearBet() {
		if( thread != null && thread.getProtocol() != null ) {
			thread.getProtocol().setBet(null);
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
	 * @param code
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
