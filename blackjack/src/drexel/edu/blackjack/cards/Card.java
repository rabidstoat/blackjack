package drexel.edu.blackjack.cards;




/**
 * A playing card.
 * @author Jennifer
 * @author CLaz
 *
 */
public class Card {
	
	/**Instances of card ranks allowed in Blackjack version.1.0.
	 * Explanations on HARD and SOFT HANDs:
	 * HARD HAND: 
	 * In this hand a player may or may not have an Ace, it may have one, but 
	 * it is not required. If the player has an Ace, the value of that Ace is always
	 * one (1) and is inflexible.
	 * SOFT HAND: 
	 * Is a hand that has an Ace. In this hand the value of the Ace is 
	 * eleven (11). In this hand the Ace and its value are key elements.
	 * This type of hand gives the player an advantage - they choose to 
	 * add a third card without worrying about going “bust” as the value 
	 * of the Ace can change to a 1. For example, the term “soft seventeen” 
	 * is used in the protocol description. In this scenario, the player 
	 * initially receives an Ace and a six (6) card. Suit is unimportant. 
	 * The player can choose the HIT command, be dealt a card (say a 10) 
	 * that would normally bring the card count to over twenty-one (21) - 
	 * however, in the soft hand the player is safe as the Ace value would 
	 * change to a one (1).
	 */
	public enum RANK {
		  
		ACE ("1"),   
		TWO("2"),		
		THREE("3"),	
		FOUR("4"),	
		FIVE("5"),	
		SIX("6"),		
		SEVEN("7"),	
		EIGHT("8"),	
		NINE("9"),	
		TEN("10"),	
		JACK("J"),	
		QUEEN("Q"),	
		KING("K");	
		
		//The String value used to concatenate with suit to form a card
		private final String cardRank;
		
		RANK( String cardRank ) {
			this.cardRank = cardRank;	
		}
		
		/**This getter can be used during the concatenation of a rank with a suit
		 *in the method this.toString(). E.g. Jack of Diamonds is rank J, suit D
		 *for a card of JD. */
		public String getRank() {
			return cardRank;
		}
		

	}
	
	/**Instances of card suits*/
	public enum SUIT {
		  
		 CLUBS("C"),		
		 SPADES("S"),	
		 HEARTS("H"),	
		 DIAMONDS("D"), ;
		 
		 //The String value used to concatenate with rank to form a card. 
		 private final String cardSuit;
		 
		 SUIT( String cardSuit ) {
			 this.cardSuit = cardSuit;
		 }
		 
		 /**This getter can be used during the concatenation of a rank with a suit
		  *in the method this.toString(). E.g. Jack of Diamonds is rank J, suit D
		 *for a card of JD. */
		 public String getSuit() {
			 return cardSuit;
		 }

	}
	
	/******************************
	* Local variables*/
	/******************************/
	
	private String rank = null;
	private String suit = null;
	
	public String getRank() {
		return rank;
	}
	
	public String getSuit() {
		return suit;
	}
	
	
	/** Constructor of a card creates a card from a rank and a suit 
	 * @param rank a valid rank
	 *  @param suit a valid suit */	
	public Card( RANK rank, SUIT suit ) {
		
		if( rank == null || suit == null) {
			throw new IllegalArgumentException( "The rank or suit cannot be null");
		}
		
		this.rank = rank.getRank();
		this.suit = suit.getSuit();	
	}
	
	
	
	/**
	 *Gets the point value of the card. An Array is used since the Ace card, 
	 * can be take a value of 1 or 11, so need to return an array of those two values .
	 * 
	 * Note: To avoid the array, could possibly declare an 
	 * ACE with a value of 1 and an ACE_SOFT instance with a value of 11 and 
	 * switch between the two, depending on the choice of the player in 
	 * specific session instance).
	  */
	
	public int[] getValues(){
		
		 
		RANK rank = RANK.ACE;
		
		String rankName = rank.name();
		rank = Enum.valueOf(RANK.class, rankName.toUpperCase());
		 
		  int[] value = new int[2];
		  
		  switch( rank) {
		  case ACE:
			  value[0] = 1;
			  value[1] = 11;
			  break;
		  case TWO:
			  value[0] = 2;
			  value[1] = 2;
			  break;
		  case THREE:
			  value[0] = 3;
			  value[1] = 3;
			  break;
		  case FOUR:
			  value[0] = 4;
			  value[1] = 4;
			  break;
		  case FIVE:
			  value[0] = 5;
			  value[1] = 5;
			  break;
		  case SIX:
			  value[0] = 6;
			  value[1] = 6;
			  break;
		  case SEVEN:
			  value[0] = 7;
			  value[1] = 7;
			  break;
		  case EIGHT:
			  value[0] = 8;
			  value[1] = 8;
			  break;
		  case NINE:
			  value[0] = 9;
			  value[1] = 9;
			  break;
		  case TEN:
			  value[0] = 10;
			  value[1] = 10;
			  break;
		  case JACK:
			  value[0] = 10;
			  value[1] = 10;
			  break;
		  case QUEEN:
			  value[0] = 10;
			  value[1] = 10;
			  break;
		  case KING:
			  value[0] = 10;
			  value[1] = 10;
			  break;
		  
		  } 
		
		return value;
		
	}
	
	/**Concatenates rank and suit into a string to create a playing card*/
	public String toString() {
		
		StringBuilder str = new StringBuilder();

		str.append(this.rank);
		str.append(this.suit );
		
		return str.toString();
	}
	
	public int hashCode() {
		
		return (Integer) null;
	}

}
