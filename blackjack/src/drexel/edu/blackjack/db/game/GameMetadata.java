/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - GameMetadata.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is an object-oriented representation of the static metadata
 * associated with a game.
 ******************************************************************************/
package drexel.edu.blackjack.db.game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Persistent data about games to store in the database
 * Builder class is provided to construct GameMetadata
 */
public class GameMetadata implements Serializable {

	/**
	 * For serialization purpose
	 */
	private static final long serialVersionUID = -7693657981904675021L;
	
	private final String id;
	private final int numDecks;
	private final ArrayList<String> rules;
	private final int minBet;
	private final int maxBet;
	private final int minPlayers;
	private final int maxPlayers;
	
	private GameMetadata(String id, int numDecks, ArrayList<String> rules,
			int minBet, int maxBet, int minPlayers, int maxPlayers) {
		this.id = id;
		this.numDecks = numDecks;
		this.rules = rules;
		this.minBet = minBet;
		this.maxBet = maxBet;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}
	
	public String getId() {
		return id;
	}

	public int getNumDecks() {
		return numDecks;
	}

	public ArrayList<String> getRules() {
		return rules;
	}

	public int getMinBet() {
		return minBet;
	}

	public int getMaxBet() {
		return maxBet;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("id: ").append(id).append("\n");
		b.append("decks: ").append(numDecks).append("\n");
		b.append("rules: ").append(rules).append("\n");
		b.append("minBet: ").append(minBet).append("\n");
		b.append("maxBet: ").append(maxBet).append("\n");
		b.append("minPlayers: ").append(minPlayers).append("\n");
		b.append("maxPlayers: ").append(maxPlayers).append("\n");
		b.append("***\n");
		return b.toString();
	}

	public static class Builder {

		public String id;
		public int numDecks, minBet, maxBet, minPlayers, maxPlayers;
		public ArrayList<String> rules; 
		
		public void setId(String id) {
			this.id = id;
		}

		public void setNumDecks(int numDecks) {
			this.numDecks = numDecks;
		}

		public void setMinBet(int minBet) {
			this.minBet = minBet;
		}

		public void setMaxBet(int maxBet) {
			this.maxBet = maxBet;
		}

		public void setMinPlayers(int minPlayers) {
			this.minPlayers = minPlayers;
		}

		public void setMaxPlayers(int maxPlayers) {
			this.maxPlayers = maxPlayers;
		}

		public void setRules(ArrayList<String> rules) {
			this.rules = rules;
		}
		
		public GameMetadata build() {
			return new GameMetadata(id, numDecks, rules, minBet, maxBet, minPlayers, maxPlayers);
		}
	}
}
