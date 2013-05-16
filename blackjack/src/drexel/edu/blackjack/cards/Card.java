package drexel.edu.blackjack.cards;

/**
 * A playing card.
 * @author Jennifer
 * @author CLaz
 *
 */
public class Card implements Comparable {
	
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
		
		/**Constructor*/
		RANK( int rank ) {
			this.rank = rank;	
		}
		
		public void setRank( RANK rank ){
			
		}
		
		public int getRank() {
			return rank;
		}

	}
	
	public enum SUIT {
		  
		 CLUBS('C'),		
		 SPADES('S'),	
		 HEARTS('H'),	
		 DIAMONDS('D'), ;
		 
		 private final char suit;
		 
		 /**Constructor*/
		 SUIT( char suit ) {
			 this.suit = suit;
		 }
		 
		 public char getSuit() {
			 return suit;
		 }
		 
		 public void setSuit ( SUIT suit ) {
			 
		 }

	}
	
		
	/**Constructor*/
	public Card(SUIT suit, RANK rank){
		
		
		
	}
	
	public int[] getValues(){
		return null;
		
	}
	
	public String toString() {
		return null;
	}
	
	public int hashCode() {
		return (Integer) null;
	}

	@Override
	public int compareTo(Object arg0) {
		
		return 0;
	}

}
