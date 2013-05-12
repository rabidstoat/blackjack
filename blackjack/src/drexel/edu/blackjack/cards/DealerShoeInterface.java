package drexel.edu.blackjack.cards;

/**
 * A shoe is potentially multiple decks of
 * cards that the dealer deals from.
 * @author Jennifer
 *
 */
public interface DealerShoeInterface {
  
  
  void shuffle();
  
	Card dealTopCard();
	
	boolean isEmpty();
	
	int getNumberDecks();
	
	int getNumberOfDealtCards();
	
	float getPercentageOfDealtCards();
	
	int getTotalNumberOfCards();

}
