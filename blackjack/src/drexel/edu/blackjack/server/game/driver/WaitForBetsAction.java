package drexel.edu.blackjack.server.game.driver;

import java.util.logging.Logger;

import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This starts up a loop for BETTING_WAIT_TIME milliseconds,
 * checking periodically to see if all the ACTIVE players have
 * placed their bets yet.
 * 
 * @author Jennifer
 *
 */
public class WaitForBetsAction extends GameAction {

	// Constant
	private int SECOND_IN_MILLISECONDS	= 1000;
	
	// We give people up to this long to place their bets
	private int BETTING_WAIT_TIME		= 60 * SECOND_IN_MILLISECONDS;
	
	// We check every this many ms to see if everyone has bet yet
	private int SWEEP_DELAY				= 500;

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(WaitForBetsAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		GameState state = (game == null ? null : game.getGameState() );
		if( state == null ) {
			LOGGER.severe( "While waiting for bets somehow have a null state object." );
		} else {
			// This is when we started this whole process
			long start = System.currentTimeMillis();
			
			// This is how long we've been waiting up until now
			long delta = System.currentTimeMillis() - start;
			
			while( state.arePlayersWithOutstandingBets() && delta < BETTING_WAIT_TIME ) {
				
				// Sleep a while to give them time to check
				try {
					Thread.sleep( SWEEP_DELAY );
				} catch( InterruptedException e ) {
					// It's just waking us up
				}
				
				// Recalculate delta before checking again
				delta = System.currentTimeMillis() - start;
			}
			
			success = true;
		}
		
		// Success does NOT mean that everyone placed bets! It means that
		// the process of waiting for bets was successful.
		return success;
	}
}
