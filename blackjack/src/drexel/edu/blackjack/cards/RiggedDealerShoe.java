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

	/**
	 * This is the same as the SimpleDealerShoe, in theory
	 */
	public static final int FAIR_AND_SQUARE = 0;
	
	/**
	 *The first card is ALWAYS Ace of Diamonds. The
	 * next card is ALWAYS queen of hearts. Then it repeats.
	 */
	public static final int ALWAYS_DEAL_BLACKJACK = 1;
	
	/**
	 * There's a method {@link #setWhatITellYou(Card)} to tell 
	 * it what to deal next, and it uses that if set. If not, 
	 * it deals randomly.
	 */
	public static final int DEAL_WHAT_I_TELL_YOU = 2;
	private Card whatITellYou;
	
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
		
		DealtCard cardToReturn = null;
		
		if( mode == ALWAYS_DEAL_BLACKJACK ) {
			blackjackDealAce = !blackjackDealAce;
			if( blackjackDealAce ) {
				cardToReturn = ACE_DIAMONDS_CARD;
			} else {
				cardToReturn = QUEEN_HEARTS_CARD;
			}
		} else if( mode == DEAL_WHAT_I_TELL_YOU && whatITellYou != null ) {
			cardToReturn = new DealtCard(whatITellYou);
			whatITellYou = null;
		}
		
		if( cardToReturn == null ) {
			cardToReturn = super.dealTopCard();
		}
		
		return cardToReturn;
	}

	/**
	 * If mode is set to {@link #DEAL_WHAT_I_TELL_YOU}, it will
	 * deal this card.
	 * 
	 * @return the whatITellYou
	 */
	public Card getWhatITellYou() {
		return whatITellYou;
	}

	/**
	 * If mode is set to {@link #DEAL_WHAT_I_TELL_YOU}, it will
	 * deal this card.
	 * 
	 * @param whatITellYou the whatITellYou to set
	 */
	public void setWhatITellYou(Card whatITellYou) {
		this.whatITellYou = whatITellYou;
	}
}
