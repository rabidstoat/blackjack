/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - DealerShoeInterface.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Object-oriented interface defining functionality for a dealer
 * shoe, which is a large deck comprised of multiple decks of cards
 ******************************************************************************/
package drexel.edu.blackjack.cards;

/**
 * A shoe is potentially multiple decks of
 * cards that the dealer deals from.
 * @author Jennifer
 *
 */
public interface DealerShoeInterface {
  
  /**
   * Randomizes the cards that are in multiple decks. For
   * each deck there should be one card of each 52 types.
   * So if there's one deck, there should be one ace of club,
   * one ace of diamonds, one ace of spades, etc., etc.
   * If two decks, then two aces of clubs, two aces of diamonds,
   * etc. 
   * 
   * Note that internally you don't have to keep a randomized
   * listing, in which case this just 'resets' however you're
   * representing it. Might be easiest to keep 'undealt' and
   * 'dealt' card lists, and just pick a random undealt card
   * for dealTopCard(), then move it to the 'dealt' card list.
   * Whatever.
   */
  void shuffle();
  
  /** 
   * Returns the top card, which is really just a randomized
   * card from the deck. The same card shouldn't be returned
   * multiple times. Though, if there are four decks, then up
   * to four Ace of Hearts can be returned, but not a fifth.
   * 
   * @return
   */
	Card dealTopCard();
	
	/**
	 * If there's no more cards that can be dealt with dealTopCard().
	 * @return
	 */
	boolean isEmpty();
	
	/**
	 * This is just a getter/setter method.
	 * @return
	 */
	int getNumberDecks();
	
	/**
	 * Figure out how many cards have been dealt with
	 * the dealTopCard() method since the last shuffle().
	 * @return
	 */
	int getNumberOfDealtCards();
	
	/**
	 * This is just math, dealt versus undealt cards.
	 * @return
	 */
	float getPercentageOfDealtCards();
	
	/**
	 * This would just be the number of cards * 52.
	 * @return
	 */
	int getTotalNumberOfCards();

}
