/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ShuffleIfNeededAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This game action determines if a shuffle is needed. Current rules are
 * that you shuffle at the beginning of a game, and if the game shoe has 50%
 * or more of its cards already dealt. If a shuffle is made, a game update is
 * sent to all client connections of all players in the game.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.logging.Logger;

import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This action determines whether or not the dealer
 * shoe has to be shuffled. If it does, it does so,
 * alerting the players as to the fact.
 * 
 * @author Jennifer
 *
 */
public class ShuffleIfNeededAction extends GameAction {

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(ShuffleIfNeededAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		if( game != null ) {
			GameState state = game.getGameState();
			if( state == null ) {
				LOGGER.severe( "Trying to decide if we should shuffle, but there's no game state..." );
			} else {
				// We let the state handle it, mostly
				if( state.needToShuffle() ) {
					state.shuffle();
				}
				success = true;
			}
		}
		
		return success;
	}

}
