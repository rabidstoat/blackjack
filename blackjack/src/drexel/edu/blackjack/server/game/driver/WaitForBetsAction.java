/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - WaitForBetsAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class represents the game waiting for bets to be placed. It
 * basically does a loop for however long is stipulated, checking every few
 * seconds to see if all the active players have placed bets or not. If so, it
 * returns immediately. If not, it repeats the sleeping-checking process, until
 * it has exceeded the maximum time. At this point it returns, even though
 * some player have not bet. Those players are dealt with in a different action.
 ******************************************************************************/
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

	// We give people up to this long to place their bets
	private int BETTING_WAIT_TIME		= 60 * SECOND_IN_MILLISECONDS;
	
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
