package drexel.edu.blackjack.server.game.driver;

import drexel.edu.blackjack.server.game.Game;

/**
 * This interface represents the general concept
 * of an action that takes place in a game. The idea
 * here is that when a game is run, it goes through
 * a sequential, predetermined series of actions.
 * These actions involve the Game object, and things
 * that are known to it.
 * 
 * Therefore, one could create an ordered list of
 * these games actions and call them, one by one,
 * passing in the relevant game object, in order
 * to run a game.
 * 
 * An example of an action would be 'starting a new
 * round' or 'requesting bets' or 'dealing cards'
 * 
 * I made this an abstract class, not an interface,
 * in case any common utility methods can be put
 * here.
 * 
 * Also, it is recommended not to use the singleton
 * pattern on subclasses. Sharing them across multiple
 * threads might cause problems, and I'm not knowledgeable
 * enough about threads to really know for sure.
 * 
 * @author Jennifer
 */
public abstract class GameAction {

	/**
	 * The only thing an action has to do is perform. This
	 * is the method that requests that the action 'do its
	 * thing', whatever that is. This may involve changing
	 * the game state, sending messages to clients, updating
	 * the state on protocol -- if it's achievable just 
	 * by having a reference to the Game object, it's fair
	 * game.
	 * 
	 * Of important note here is the return value. It DOES
	 * NOT indicate if the action itself had a successful
	 * account, but only if the process of performing it
	 * behaved properly. For example, consider an action 
	 * that involves waiting for users to place bets. It
	 * could be that no user made their bets in time. The
	 * return value would still be true, though, because
	 * the process of waiting for bets succeeded. 
	 * 
	 * @param game The game to perform the action on
	 * @return True if it was performed successfully,
	 * and false if it was not. Typically the response
	 * to a failure is terminating the game and evicting
	 * all the players, so be cautious in returning false.
	 */
	public abstract boolean doAction( Game game );
}
