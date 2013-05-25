package drexel.edu.blackjack.server.game;

import java.util.logging.Logger;

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
	
	// This is fairly hard-coded, how many steps make up a round of play
	private static final int NUMBER_OF_STEPS = 3;
	
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
		this.game = game;
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
	 * The big game loop moves through the steps of a
	 * round of cards. It periodically gets interrupts
	 * about things external to the game loop that are
	 * happening. Sometimes it realizes that there are
	 * no more players, and nothing else to do, and it
	 * finally ends.
	 */
	private void bigGameLoop() {
		
		// This loops through until something sets this to false somewhere
		while( keepPlaying() ) {
			
			for( int i = 0; keepPlaying() && i < NUMBER_OF_STEPS; i++ ) {
				doStep(i);
			}
			
		}
		
		LOGGER.info( "Done with the big game loop." );
	}

	/**
	 * The steps the thread has to do are ordered. This method
	 * performs the step specified by the stepNumber
	 * 
	 * @param stepNumber Which step to perform
	 */
	private void doStep(int stepNumber ) {
		
		if( stepNumber == 0 ) {
			// Start a new round, and ask for bets
			game.startNewRound();
		} else if( stepNumber == 1 ) {
			// Wait for the bets to be placed
			game.waitForBetsToBePlaced();
		} else if( stepNumber == 2 ) {
			// People who didn't bet in time get idled out
			game.removeActivePlayersWithNoBets();
		}
				
	}
	
	/**
	 * Right now, we keep playing as long as there are players
	 */
	private boolean keepPlaying() {
		if( game == null ) {
			return false;
		}
		
		return game.isActive();
	}
}
