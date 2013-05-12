package drexel.edu.blackjack.cards;

public class SimpleDealerShoe implements DealerShoeInterface {
  
  	private ArrayList<Card> undealtCards;
  	private ArrayList<Card> alreadyDealtCards;
	private int numberOfDecks;
	
	public SimpleDealerShoe(int numberOfDecks) {
		
	}

	@Override
	public void shuffle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Card dealTopCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNumberDecks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfDealtCards() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPercentageOfDealtCards() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalNumberOfCards() {
		// TODO Auto-generated method stub
		return 0;
	}


}
