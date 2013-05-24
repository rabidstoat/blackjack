package drexel.edu.blackjack.server.game;

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
		System.out.println( "Starting the game playing thread for " + (game == null ? "a null game" : game.getId() ) );
	}

	/************************************************************
	 * Private methods
	 ***********************************************************/
}
