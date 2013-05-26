package drexel.edu.blackjack.client.screens.util;

import java.util.ArrayList;

import drexel.edu.blackjack.db.game.GameMetadata;

public class ClientSideGame {

	private GameMetadata gameMetadata;
	
	private String description;		// Should really be in the game metadata eventually

	/******************************************************
	 * Constructor goes here.
	 *****************************************************/

	public ClientSideGame( String id, String numDecksAsString, ArrayList<String> rules,
			String minBetAsString, String maxBetAsString, String description ) {

		// Hopefully this gets moved to metadata
		this.description = description;
		
		// Parse out the numbers
		Integer numDecks = parseAsNumber( numDecksAsString );
		Integer maxBet = parseAsNumber( maxBetAsString );
		Integer minBet = parseAsNumber( minBetAsString );

		// Create the metadata
		GameMetadata.Builder b = new GameMetadata.Builder();
		b.setId(id);
		b.setMaxBet(maxBet);
		b.setMinBet(minBet);
		b.setNumDecks(numDecks);
		b.setRules(rules);
		gameMetadata = b.build();
	}

	/**
	 * Given a string, try to interpret it as an integer.
	 * If it doesn't parse, fail silently and return null
	 * 
	 * @param str String representation
	 * @return Integer representation, or null if it didn't parse
	 */
	private Integer parseAsNumber(String str ) {
		Integer value = null;
		
		try {
			value = Integer.parseInt(str);
		} catch( NumberFormatException e ) {
			// Fail silently
		}
		
		return value;
	}

	/******************************************************
	 * Getters just pass through to the 
	 * metadata object.
	 *****************************************************/

	public String getId() {
		return gameMetadata.getId();
	}

	public Integer getNumDecks() {
		return gameMetadata.getNumDecks();
	}

	public ArrayList<String> getRules() {
		return gameMetadata.getRules();
	}

	public Integer getMinBet() {
		return gameMetadata.getMinBet();
	}

	public Integer getMaxBet() {
		return gameMetadata.getMaxBet();
	}

	/******************************************************
	 * The whole reason we are wrapping it is to print
	 * it the way we want to print it.
	 *****************************************************/
	
	@Override
	public String toString() {
		// Finally we can create a string that represents the game!
		StringBuilder str = new StringBuilder();
		
		// Need description
		str.append( description );
		
		// Some metadata on the same line
		str.append( " [" );
		str.append( getBetRestriction() );
		str.append(", " );
		if( getNumDecks() == null ) {
			str.append( "with an unspecified number of decks used" );
		} else {
			str.append( "with " + getNumDecks() + " deck" + (getNumDecks() > 1 ? "s" : "" ) + " used" );
		}
		str.append( "]" );
		
		// Rules, if any, in a bullet list below
		for( String rule : getRules() ) {
			str.append( "\n    o " );
			str.append( rule );
		}
		
		return str.toString();
	}

	/**
	 * Get a text description of the bet range of this game.
	 * 
	 * @return A sentence that stands on its own describing
	 * the restrictions for bets on this game, with no
	 * ending punctuation
	 */
	public String getBetRestriction() {
		
		String betRestriction = "Bet restrictions unknown";
		
		if( getMinBet() == null && getMaxBet() == null ) {
			betRestriction = "Unlimited bet range";
		} else if( getMinBet() == null ) {
			betRestriction = "Bets up to $" + getMaxBet();
		} else if( getMaxBet() == null ) {
			betRestriction = "Bets start at $" + getMinBet();
		} else {
			betRestriction = "Bets from $" + getMinBet() + " - $" + getMaxBet();
		}
		
		return betRestriction;
	}
}
