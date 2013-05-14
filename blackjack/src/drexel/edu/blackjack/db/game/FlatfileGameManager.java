package drexel.edu.blackjack.db.game;

import java.util.List;

/**
 * Game metadata is stored out in a flat file. The format
 * of the flat file is:
 * 
 * <Duc: Add documentation here about the format of the
 * file.>
 * 
 * @author DAN
 */
public class FlatfileGameManager implements GameManagerInterface {

	// Keep a single instance around for the singleton design pattern
	private static FlatfileGameManager gameManager = null;

	/**
	 * Following the singleton design pattern, the constructor
	 * is kept private.
	 */
	private FlatfileGameManager() {
		// TODO: Implement. It's okay to use a hard-coded filename.
	}
	
	@Override
	public boolean load() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<GameMetadata> getGames() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Following the singleton pattern, return a reference to
	 * the singleton game manager.
	 * 
	 * @return A reference to the only game manager that should
	 * be instantiated.
	 */
	public static GameManagerInterface getDefaultGameManager() {
		
		if( gameManager == null ) {
			gameManager = new FlatfileGameManager();
		}
		
		return gameManager;
	}
}
