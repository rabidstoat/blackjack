package drexel.edu.blackjack.cards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import drexel.edu.blackjack.server.game.User;

/**
 * A user's hand with all cards dealt from dealer
 * @author DAN
 *
 */

public class Hand {
	
	private final ArrayList<DealtCard> cards = new ArrayList<DealtCard>();
	private final HashSet<Integer> points = new HashSet<Integer>();
	private final User user;
	
	public Hand(User user) {
		this.user = user;
		points.add(0);
	}
	
	// get a card from dealer, it must be faceup or facedown using DealtCard class
	// this method also interprets the card and calculate all possible values 
	// there are many other simpler ways, but this is more generic
	public void receiveCard(DealtCard card) {
		
		cards.add(card);
		
		ArrayList<Integer> temp = new ArrayList<Integer>();
		
		for (int v: card.getValues()) {
			// calculate all ways to interpret the card value(s) (Ace)
			ArrayList<Integer> spoints = new ArrayList<Integer>(points);
			for (int i=0; i<spoints.size(); i++) {
				spoints.set(i, spoints.get(i) + v);
			}
			temp.addAll(spoints);
		}
		
		points.clear();
		points.addAll(temp);
	}
	
	/**
	 * @return all possibly interpreted values of hand
	 */
	public List<Integer> getPossibleValues() {
		return new ArrayList<Integer>(points);
	}
	
	public List<Card> getFaceupCards() {
		ArrayList<Card> temp = new ArrayList<Card>();
		for (DealtCard c:cards) {
			if (c.isFaceUp()) {
				temp.add(c);
			}
		}
		return temp;
	}
	
	public List<Card> getFacedownCards() {
		ArrayList<Card> temp = new ArrayList<Card>();
		for (DealtCard c:cards) {
			if (!c.isFaceUp()) {
				temp.add(c);
			}
		}
		return temp;
	}
	
	/**
	 * @return true if the hand is definitely busted
	 */
	public boolean getIsBusted() {
		for (int i: points) {
			if (i<=21) {
				// not busted if any way to calculate points <= 21
				return false;
			}
		}
		return true;
	}
	
	/**
	 * In the modern game, a blackjack refers to any hand of an ace
	 *  plus a ten or face card, regardless of suits or colours
	 *  http://www.casino.org/games/blackjack/history.php
	 * @return
	 */
	public boolean getIsBlackJack() {
		// a black jack if there are only two cards, and possible value is 21
		if (cards.size() != 2) return false;
		for (int i:points) {
			if (i == 21) return true;
		}
		return false;
	}
}
