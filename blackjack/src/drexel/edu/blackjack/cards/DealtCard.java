package drexel.edu.blackjack.cards;

public class DealtCard extends Card {

	private boolean faceup;
	
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
}
