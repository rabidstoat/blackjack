package drexel.edu.blackjack.cards;

import java.util.ArrayList;
import java.util.Collections;

public class SimpleDealerShoe implements DealerShoeInterface {
	
	private final int numDecks;
	private final ArrayList<Card> deck;
	private final ArrayList<Card> bin;
	
	public SimpleDealerShoe(int numDeck) {
		numDecks = numDeck;
		deck = new ArrayList<Card>();
		bin = new ArrayList<Card>();
		for (int i=0; i < numDeck; i++) {
			for (Card.SUIT suit:Card.SUIT.values()) {
				for (Card.RANK rank: Card.RANK.values()) {
					deck.add(new Card(rank, suit));
				}
			}
		}
	}

	@Override
	public void shuffle() {
		Collections.shuffle(deck);
	}

	@Override
	public Card dealTopCard() {
		if (isEmpty()) return null;
		Card c = deck.remove(0);
		bin.add(c);
		return c;
	}

	@Override
	public boolean isEmpty() {
		return deck.size() == 0;
	}

	@Override
	public int getNumberDecks() {
		return numDecks;
	}

	@Override
	public int getNumberOfDealtCards() {
		return bin.size();
	}

	@Override
	public float getPercentageOfDealtCards() {
		return bin.size()/(bin.size() + deck.size());
	}

	@Override
	public int getTotalNumberOfCards() {
		return deck.size();
	}

}
