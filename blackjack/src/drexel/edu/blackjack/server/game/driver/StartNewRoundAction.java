/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - StartNewRoundAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This game action starts a new round by resetting state variables
 * appropriately. Any players who were in the observer status (because they
 * joined after the round started) are promoted to an active status. Also,
 * it sends out the initial request for bets to the related game clients.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.logging.Logger;

import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Starting a new round involves cleaning up any
 * internal state-related issues (like resetting
 * bets, changing any observers to active status,
 * that sort of thing) and then sending out the
 * requests for bets.
 * 
 * @author Jennifer
 *
 */
public class StartNewRoundAction extends GameAction {

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(StartNewRoundAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		if( game != null ) {
			GameState state = game.getGameState();
			if( state == null ) {
				LOGGER.severe( "Trying to start a new round with a null state can't be good..." );
			} else {
				// This will set players active and set the current player
				state.startNewRound();
				success = true;
			}
		}
		
		return success;
	}

}
