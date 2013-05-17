package drexel.edu.blackjack.db.game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import drexel.edu.blackjack.db.user.UserMetadata;

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
	private static FlatfileGameManager tgameManager = null;
	private HashMap<String, GameMetadata> games;
	private String objectFile;
	public final static String GAME_RECORDS = "games_serialized";

	/**
	 * Following the singleton design pattern, the constructor
	 * is kept private.
	 */
	private FlatfileGameManager(String filename) {
		if (!load()) {
			games = new HashMap<String, GameMetadata>();
		}
	}
	
	@Override
	public boolean load() {
		try {
			FileInputStream fis = new FileInputStream(GAME_RECORDS);
			ObjectInputStream ois = new ObjectInputStream(fis);
			games = (HashMap<String, GameMetadata>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			games = new HashMap<String, GameMetadata>();
			// when there is no stored file, we consider as loaded successfully
			return true;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean add(GameMetadata game) {
		if (games.containsKey(game.getId())) {
			return false;
		}
		games.put(game.getId(), game);
		if (save())
			return true;
		else
			return false;
	}

	@Override
	public boolean remove(String id) {
		if (!games.containsKey(id)) {
			return false;
		}
		games.remove(id);
		if (save())
			return true;
		else
			return false;
	}

	@Override
	public boolean save() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(GAME_RECORDS));
			out.writeObject(games);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public List<GameMetadata> getGames() {
		return new ArrayList<GameMetadata>(games.values());
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
			gameManager = new FlatfileGameManager(GAME_RECORDS);
		}
		
		return gameManager;
	}
	
	public static GameManagerInterface getTestGameManager() {
		if (tgameManager == null) {
			tgameManager = new FlatfileGameManager(GAME_RECORDS + "_test");
		}
		return tgameManager;
	}

	@Override
	public GameMetadata getGame(String id) {
		if (games.containsKey(id)) {
			return games.get(id);
		} else {
			return null;
		}
	}
}
