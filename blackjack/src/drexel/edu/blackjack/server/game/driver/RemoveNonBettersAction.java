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
