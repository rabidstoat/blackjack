/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - GameAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: The GamePlayingThread keeps an ordered list of game actions, which
 * are basically using the Command design pattern so that there is a 'do work'
 * method, which receives the Game object as its parameter.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import drexel.edu.blackjack.cards.DealtCard;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;

/**
 * This interface represents the general concept
 * of an action that takes place in a game. The idea
 * here is that when a game is run, it goes through
 * a sequential, predetermined series of actions.
 * These actions involve the Game object, and things
 * that are known to it.
 * <P>
 * Therefore, one could create an ordered list of
 * these games actions and call them, one by one,
 * passing in the relevant game object, in order
 * to run a game.
 * <P>
 * An example of an action would be 'starting a new
 * round' or 'requesting bets' or 'dealing cards'
 * <P>
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
	 * Constant that is true on planet earth.
	 */
	protected int SECOND_IN_MILLISECONDS	= 1000;
	
	/**
	 * When we are waiting for players to make bets
	 * or make game plays, we check and see if they
	 * have every this often.
	 */
	protected int SWEEP_DELAY				= 500;

	/**
	 * The only thing an action has to do is perform. This
	 * is the method that requests that the action 'do its
	 * thing', whatever that is. This may involve changing
	 * the game state, sending messages to clients, updating
	 * the state on protocol -- if it's achievable just 
	 * by having a reference to the Game object, it's fair
	 * game.
     * <P>
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
	
	/** 
	 * Given a state containing a dealer's shoe, check and see
	 * if the shoe needs to be shuffled. If it does, do so,
	 * notifying players involved in the game.
	 * 
	 * @param state Contains a shoe that might need reshuffling
	 * @return True if it was reshuffled, false otherwise
	 */
	protected boolean shuffleIfNecessary(GameState state) {
		boolean reshuffled = false;
		
		if( state != null && state.needToShuffle() ) {
			state.shuffle();
			reshuffled = true;
		}
		
		return reshuffled;
	}

	/** 
	 * Given a state containing a dealer's shoe, deal a card
	 * from the top of the shoe. First, however, check and see
	 * if the shoe needs to be reshuffled. If it does, do so,
	 * and notify involved players, and THEN deal a card off
	 * the top.
	 * 
	 * @param state Contains a shoe from which to deal a card,
	 * possibly reshuffling first
	 * @return The card dealt, in a facedown state, or null if 
	 * there was a problem
	 */
	protected DealtCard shuffleIfNeededAndDealCard(GameState state) {
		DealtCard card = null;
		
		shuffleIfNecessary( state );
		if( state != null && state.getDealerShoe() != null ) {
			card = state.getDealerShoe().dealTopCard();
		}
		
		return card;
	}
}
