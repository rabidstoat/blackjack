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

public class DealtCard extends Card {

	private boolean faceup;
	
	public DealtCard(Card c) {
		super(c.getRank(), c.getSuit());
		faceup = false;
	}
	
	public DealtCard(RANK rank, SUIT suit) {
		super(rank, suit);
		faceup = false;
	}
	
	public DealtCard(Card card, boolean isShown) {
		super(card.getRank(), card.getSuit());
		faceup = isShown;
	}
	
	public boolean isFaceUp() {
		return faceup;
	}
	
	/*
	 * Only to turn the card up, not the other way around
	 */
	public void changeToFaceUp() {
		faceup = true;
	}
}
