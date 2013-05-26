/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - RemoveNonBettersAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This game action cycles through the list of active players in the
 * game, presumably after the betting period is over, and removes users who
 * have not placed their bets in time. Removing users is done by setting their
 * protocol state to NOT_IN_SESSION, and sending their client a notice.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.logging.Logger;

import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Removes players who have not made their bets in time.
 * Looks at all the players who are active. If there are any
 * who don't have their bet set, they need to be idle-bumped
 * 
 * @author Jennifer
 *
 */
public class RemoveNonBettersAction extends GameAction {

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(RemoveNonBettersAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		if( game != null ) {
			GameState state = game.getGameState();
			if( state == null ) {
				LOGGER.severe( "Trying to remove players without bets with a null state can't be good..." );
			} else {
				// We let the state handle it
				state.removeActivePlayersWithNoBet();
				success = true;
			}
		}
		
		return success;
	}
}
