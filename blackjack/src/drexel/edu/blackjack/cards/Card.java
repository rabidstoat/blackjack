package drexel.edu.blackjack.cards;

/**
 * A playing card.
 * @author Jennifer
 *
 */
public class Card implements Comparable {
	
	public enum Rank {
		  
		ACE,	// = Ace
		TWO,	// = 2
		THREE,	// = 3
		FOUR,	// = 4
		FIVE,	// = 5
		SIX,	// = 6
		SEVEN,	// = 7
		EIGHT,	// = 8
		NINE,	// = 9
		TEN,	// = 10
		JACK,	// = Jack
		QUEEN,	// = Queen
		KING;	// = King

	}
	
	public enum Suit {
		  
		 CLUBS,		//Clubs
		 SPADES,	//Spades
		 HEARTS,	//Hearts
		 DIAMONDS;	//Diamonds
		

	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
