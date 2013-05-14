package drexel.edu.blackjack.server.game;

import drexel.edu.blackjack.cards.Hand;
import drexel.edu.blackjack.db.user.UserMetadata;

/**
 * Think of this as the dynamic instantiation of
 * the UserMetadata. A User object is only created
 * when a user logs into the server.
 * 
 * @author Jennifer
 *
 */
public class User {
  
	private UserMetadata userMetadata;
	private Hand hand;
	private int bet;
	
	// Auto-generated getters nd setters below
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
	public int getBet() {
		return bet;
	}
	/**
	 * @param bet the bet to set
	 */
	public void setBet(int bet) {
		this.bet = bet;
	}

}
