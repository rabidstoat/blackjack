/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - GamePlayingThread.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This thread is used to implement the functionality related to playing
 * a game of blackjack. Every time a game is started, a thread gets started;
 * when the game is over (all players left), it is destroyed. It is mostly a
 * big loop that drives through the various commands that represent activity
 * at different stages of the game (e.g., shuffling, dealing cards, taking
 * bets) and figuring out when everyone has left, to exit.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This is the class that actually handles playing a 
 * game by requesting bets, dealing cards, requesting
 * plays, etc., etc.
 * 
 * @author Jennifer
 */
public class GamePlayingThread extends Thread {

	/************************************************************
	 * Private class variables
	 ***********************************************************/
	
	// This holds the list of actions that we have to perform, in order, in
	// the process of running a game.
	private List<GameAction> gameActions = null;
	
	// This is the current action we're on. Hopefully it's between 0 and
	// the number of gameActions minus one...
	private int gameActionIndex = 0;
	
	// A flag we can use to stop gameplay on internal errors
	private volatile boolean keepPlaying = true;
	
	// A game playing thread is responsible for one and only one game
	private Game game = null;
	
	private final static Logger LOGGER = BlackjackLogger.createLogger( GamePlayingThread.class.getName() );

	/************************************************************
	 * Constructor
	 ***********************************************************/
	
	/**
	 * Creates the thread. Does not start it.
	 * @param game Game to play in this thread.
	 */
	public GamePlayingThread( Game game ) {
		// Need to know the game we're playing
		this.game = game;
		
		// Create an ordered list of actions for playing the game
		gameActions = new ArrayList<GameAction>();
		gameActions.add( new StartNewRoundAction() );
		gameActions.add( new WaitForBetsAction() );
		gameActions.add( new RemoveNonBettersAction() );
		gameActions.add( new ShuffleIfNeededAction() );
		
		// And note that we're at the first one
		this.gameActionIndex = 0;
	}

	/************************************************************
	 * Public methods
	 ***********************************************************/
	
	/**
	 * @return Game the thread is playing
	 */
	public Game getGame() {
		return game;
	}
	
	@Override
	public void run() {
		LOGGER.info( "Starting the game playing thread for " + (game == null ? "a null game" : game.getId() ) );
		
		// First off, need to start the game and send 'gimme your bet' messages to all the players
		if( game == null ) {
			LOGGER.severe( "Somehow in a game playing thread with a null game." );
		} else {
			bigGameLoop();
		}
	}

	/************************************************************
	 * Private methods
	 ***********************************************************/
	
	/**
	 * The big game loop moves through the actions involved
	 * in playing the game. At some point it realizes that 
	 * there are no more players, and nothing else to do, and it
	 * finally ends.
	 */
	private void bigGameLoop() {
		
		// This loops through until something sets this to false somewhere
		while( keepPlaying() ) {

			// This should never happen
			if( gameActions == null || gameActionIndex < 0 || gameActionIndex >= gameActions.size() ) {
				LOGGER.severe( "Something went wrong in our bigGameLoop() with internal variables." );
				this.keepPlaying = false;
			} else {
				GameAction nextAction = gameActions.get(gameActionIndex);
				if( !nextAction.doAction( game ) ) {
					LOGGER.severe( "Something went wrong in the game action " + nextAction.getClass().getName() + ".doAction() method" );
					this.keepPlaying = false;
				} else {
					// Advance the game index, looping to the beginning if we pass the end
					gameActionIndex = (gameActionIndex+1) % gameActions.size();
				}		
			}
			
		}
		
		LOGGER.info( "Done with the big game loop." );
	}

	/**
	 * Right now, we keep playing as long as there are players
	 */
	private boolean keepPlaying() {
		if( game == null ) {
			return false;
		}
		
		return keepPlaying && game.isActive();
	}
}
