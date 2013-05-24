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
	
	// A game playing thread is responsible for one and only one game
	private Game game = null;
	
	// Controls our perpetual game-playing loop
	private boolean stillPlaying = true;
	
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
		
		stillPlaying = true;

		// This loops through until something sets this to false somewhere
		while( stillPlaying ) {
			
			// Start a new round, and ask for bets
			game.startNewRound();
			
			// Wait for the bets to be placed
			game.waitForBetsToBePlaced();
			
			// People who didn't bet in time get idled out
			game.removeActivePlayersWithNoBets();
			
			// For now, we just do this once
			stillPlaying = false;
		}
		
		System.out.println( "Done with the big game loop." );
	}
}
