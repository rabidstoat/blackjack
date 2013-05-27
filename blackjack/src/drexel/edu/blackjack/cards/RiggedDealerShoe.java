/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - SimpleDealerShoe.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Used only in development and testing, to 'rig the game' in order
 * to test certain outcomes.
 ******************************************************************************/
package drexel.edu.blackjack.cards;

/**
 * This is a rigged dealer shoe, for testing purposes only.
 * 
 * @author Jennifer
 */
public class RiggedDealerShoe extends SimpleDealerShoe {

	// This is the same as the SimpleDealerShoe
	public static final int FAIR_AND_SQUARE = 0;
	
	// The first card is ALWAYS Ace of Diamonds. The
	// next card is ALWAYS queen of hearts. Then it repeats.
	public static final int ALWAYS_DEAL_BLACKJACK = 1;
	
	// Some variables for our ALWAYS_DEAL_BLACKJACK mode
	private static final DealtCard ACE_DIAMONDS_CARD = new DealtCard( Card.RANK.ACE, Card.SUIT.DIAMONDS );
	private static final DealtCard QUEEN_HEARTS_CARD = new DealtCard( Card.RANK.QUEEN, Card.SUIT.HEARTS );
	private boolean blackjackDealAce = false;
	
	private int mode = FAIR_AND_SQUARE;

	/**
	 * Constructs the shoe around a specified number of
	 * decks, in a certain rigged mode.
	 * 
	 * @param numDeck The number of decks
	 */
	public RiggedDealerShoe(int numDeck, int riggedMode) {
		super( numDeck );
		this.mode = riggedMode;
	}

	@Override
	public DealtCard dealTopCard() {
		if( mode == ALWAYS_DEAL_BLACKJACK ) {
			blackjackDealAce = !blackjackDealAce;
			if( blackjackDealAce ) {
				return ACE_DIAMONDS_CARD;
			} else {
				return QUEEN_HEARTS_CARD;
			}
		} else {
			return super.dealTopCard();
		}
	}
}
