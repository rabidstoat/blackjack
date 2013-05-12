package drexel.edu.blackjack.cards;

/**
 * A playing card.
 * @author Jennifer
 *
 */
public class Card implements Comparable {
	
	private Suit suit;
	private Rank rank;
	
	public Card(Suit suit, Rank rank){
		
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
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
		// TODO Auto-generated method stub
		return 0;
	}

}
