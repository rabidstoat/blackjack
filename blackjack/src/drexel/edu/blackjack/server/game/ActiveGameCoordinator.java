package drexel.edu.blackjack.server.game;

import java.util.HashMap;
import java.util.Map;

/**
 * The active game coordinator handles the threads that
 * are involved in playing games that are active on the
 * server. 
 * 
 * @author Jennifer
 *
 */
public class ActiveGameCoordinator {

	/******************************************************************************
	 * Class variables go here
	 *****************************************************************************/

	// For the singleton pattern
	private static ActiveGameCoordinator coordinator = null;
	
	// Need something to map game IDs to the game-playing threads.
	private Map<String,GamePlayingThread> activeGames = null;
	
	/******************************************************************************
	 * Constructor goes here
	 *****************************************************************************/
	private ActiveGameCoordinator() {
		activeGames = new HashMap<String,GamePlayingThread>();
	}

	/******************************************************************************
	 * Public methods go here
	 *****************************************************************************/

	/**
	 * Following the game singleton pattern, we only have one of
	 * these ever instantiated.
	 * @return
	 */
	public static ActiveGameCoordinator getDefaultActiveGameCoordinator() {
		if( coordinator == null ) {
			coordinator = new ActiveGameCoordinator();
		}
		return coordinator;
	}
	
	/**
	 * Given an ID, return the active game corresponding to the
	 * ID. If no game is actively played, return null.
	 * 
	 * @param id An identifier for the game
	 * @return Either the game itself or, if it's not being played,
	 * a null
	 */
	public Game getGame( String id ) {
		
		// This shouldn't happen
		if( activeGames == null ) {
			activeGames = new HashMap<String,GamePlayingThread>();
		}
		
		GamePlayingThread thread = activeGames.get( id );
		if( thread != null ) {
			return thread.getGame();
		}
		
		// If we get here, we weren't playing the game anywhere, just return null
		return null;
	}

	/**
	 * Add a player to the game with the indicated sessionName.
	 * 
	 * If the sessionName doesn't correspond to any gamemetadata,
	 * this is a problem.
	 * 
	 * Otherwise, look to see if a game is already active. If so,
	 * make sure that it has room. If not, it's a problem. If it
	 * does, add the player
	 * 
	 * If there is no active game, create one, add the user, and
	 * start it.
	 * 
	 * @param sessionName Should correspond to some identifier in gamemetadata
	 * @param user The user to add
	 * @return IF the game was successfully joined, return that game object.
	 * IF it wasn't, return a null
	 */
	public Game addPlayer(String sessionName, User user) {
		// TODO: Implement
		return null;
	}
}
