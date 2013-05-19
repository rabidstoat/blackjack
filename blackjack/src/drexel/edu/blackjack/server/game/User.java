package drexel.edu.blackjack.server.game;

import java.util.logging.Logger;

import drexel.edu.blackjack.cards.Hand;
import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.BlackjackServerThread;
import drexel.edu.blackjack.server.ResponseCode;
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
	
	// Bet they are currently making (or null if no bet)
	private Integer bet;
	
	// Game they are playing
	private Game game;
	
	// Their server thread
	private BlackjackServerThread thread = null;
	
	// For debug output
	private final static Logger LOGGER = BlackjackLogger.createLogger(User.class .getName()); 

	/***************************************************************
	 * Constructors go here
	 **************************************************************/
	
	public User() {
	}
	
	public User( UserMetadata userMetadata ) {
		this.userMetadata = userMetadata;
	}
	
	/***************************************************************
	 * Getters and setters
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
	 * @return the bet
	 */
	public Integer getBet() {
		return bet;
	}

	/**
	 * @param bet the bet to set
	 */
	public void setBet(Integer bet) {
		this.bet = bet;
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
	 * Sends a message to the presumably connected user by 
	 * sending it through to their socket
	 * 
	 * @param code
	 */
	public void sendMessage(ResponseCode code) {
		
		if( thread == null ) {
			LOGGER.severe( "Had a request to send user " + 
					(userMetadata == null ? userMetadata.getUsername() : "with no metadata" ) + 
					" a response, but couldn't find their socket." );
		} else {
			thread.sendMessage( code );
		}
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

}
