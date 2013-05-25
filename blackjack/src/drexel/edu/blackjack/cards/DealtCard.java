package drexel.edu.blackjack.cards;

public class DealtCard extends Card {

	private boolean faceup;
	
	public DealtCard(RANK rank, SUIT suit) {
		super(rank, suit);
		faceup = false;
	}
	
	public DealtCard(Card card, boolean isShown) {
		super(Card.RANK.valueOf(card.getRank()), Card.SUIT.valueOf(card.getSuit()));
		faceup = isShown;
	}
	
	public boolean isFaceUp() {
		return faceup;
	}
}
