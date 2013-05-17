package drexel.edu.blackjack.cards;




/**
 * A playing card.
 * @author Jennifer
 * @author CLaz
 *
 */
public class Card implements Comparable {
	
	
	/**Instances of card ranks allowed in Blackjack version.1.0*/
	public enum RANK {
		  
		ACE (1),	
		TWO(2),		
		THREE(3),	
		FOUR(4),	
		FIVE(5),	
		SIX(6),		
		SEVEN(7),	
		EIGHT(8),	
		NINE(9),	
		TEN(10),	
		JACK(10),	
		QUEEN(10),	
		KING(10);	
		
		private final int rank;
		
		RANK( int rank ) {
			this.rank = rank;	
		}
		
		public int getRank() {
			return rank;
		}

	}
	
	/**Instances of card suits*/
	public enum SUIT {
		  
		 CLUBS("C"),		
		 SPADES("S"),	
		 HEARTS("H"),	
		 DIAMONDS("D"), ;
		 
		 private final String suit;
		 
		 SUIT( String suit ) {
			 this.suit = suit;
		 }
		 
		 public String getSuit() {
			 return suit;
		 }

	}
	
	/**Local variables*/
	private Integer rank = null;
	private String suit = null;
	
	public int getRank() {
		return rank;
	}
	
	public String getSuit() {
		return suit;
	}
	
	
	/** @param rank a valid rank
	 *  @param suit a valid suit */	
	public Card( RANK rank, SUIT suit ) {
		
		if( rank == null || suit == null) {
			throw new IllegalArgumentException( "The rank or suit cannot be null");
		}
		
		this.rank = getRank();
		this.suit = getSuit();	
	}
	
	public int[] getValues(){
		return null;
		
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

	@Override
	public int compareTo(Object arg0) {
		
		return 0;
	}

}
