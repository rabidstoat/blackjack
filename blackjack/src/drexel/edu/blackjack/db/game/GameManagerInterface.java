package drexel.edu.blackjack.db.game;

import java.util.List;

/**
 * Interface that any persistent store for games has to implement.
 * 
 * The data that has to be stored is a number of game descriptors,
 * of which all the data that is in the GameMetadata class has
 * to be stored. 
 * 
 * @author Jennifer
 */
public interface GameManagerInterface {

	/**
	 * This loads the data from the persistent storage (e.g.,
	 * the database or flatfile or whatever) into memory,
	 * where it can be accessed programatically. The actual
	 * initialization of the game manager will need to be done
	 * on the class that implements it, and called prior to
	 * loading. Recommendation is to do this in the constructor.
	 * 
	 *  @return True if the games were loaded successfully,
	 *  False is there was any error in the loading that has
	 *  the internal representation in a possibly inconsistent
	 *  or incorrect state.
	 */
	public boolean load();
	
	/**
	 * This returns a list of the GameMetadata objects
	 * that were loaaded with the load() method. Though
	 * you can call this before you've loaded the games,
	 * it'll just return null.
	 * 
	 * @return If the load() command has been used, this
	 * should return a valid list of games, though perhaps
	 * of size zero if no games were loaded. If the load()
	 * command was not called, or if it failed, it should
	 * return null.
	 */
	public List<GameMetadata> getGames();
	

}
