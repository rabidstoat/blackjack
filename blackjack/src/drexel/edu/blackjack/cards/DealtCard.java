/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - DealtCard.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: When cards are dealt, distinguishes between faceup and facedown
 * cards.
 ******************************************************************************/
package drexel.edu.blackjack.cards;

/**
 * The concept of a card that has been dealt and
 * might be faceup or facedown.
 * 
 * @author Duc
 */
public class DealtCard extends Card {

	private boolean faceup;

	/**
	 * construct it by using values set on a card. Defaults
	 * to being facedown.
	 * @param c The card to pull values from
	 */
	public DealtCard(Card c) {
		super(c.getRank(), c.getSuit());
		faceup = false;
	}
	
	/**
	 * construct it by specifying rank and suit. Defaults
	 * to being facedown.
	 * 
	 * @param rank The rank
	 * @param suit The suit
	 */
	public DealtCard(RANK rank, SUIT suit) {
		super(rank, suit);
		faceup = false;
	}
	
	/**
	 * construct it by copying values from a card and
	 * specifying if it's faceup or not.
	 * 
	 * @param card The card to pull values from
	 * @param isShown whether it's faceup or not
	 */
	public DealtCard(Card card, boolean isShown) {
		super(card.getRank(), card.getSuit());
		faceup = isShown;
	}
	
	/**
	 * Query if a card is face up
	 * @return True if faceup, false if facedown
	 */
	public boolean isFaceUp() {
		return faceup;
	}
	
	/**
	 * Only to turn the card up, not the other way around
	 */
	public void changeToFaceUp() {
		faceup = true;
	}
}
