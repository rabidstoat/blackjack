package drexel.edu.blackjack.db.user;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * User metadata is stored out in a flat file. The format
 * of the flat file is:
 * 
 * The user list is only a hash map, mapping from username to user's metadata
 * To keep the user list persistent, this class uses serialization.
 * which requires writing permission, to write serialized file to disk
 * 
 * @author DAN
 */
public class FlatfileUserManager implements UserManagerInterface {
	
	// Keep a single instance around for the singleton design pattern
	private static FlatfileUserManager userManager = null;
	private static FlatfileUserManager tuserManager = null;
	private HashMap<String, UserMetadata> users;
	public final static String USER_RECORDS = "users_serialized";
	private final String objectFile;

	/**
	 * Following the singleton design pattern, the constructor
	 * is kept private.
	 */
	private FlatfileUserManager(String filename) {
		objectFile = filename;
		if (!load()) {
			users = new HashMap<String, UserMetadata>();
		}
	}

	
	@Override
	public boolean load() {
		try {
			FileInputStream fis = new FileInputStream(USER_RECORDS);
			ObjectInputStream ois = new ObjectInputStream(fis);
			users = (HashMap<String, UserMetadata>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			users = new HashMap<String, UserMetadata>();
			// when there is no stored file, we consider as loaded successfully
			return true;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean add(UserMetadata user) {
		if (users.containsKey(user.getUsername())) {
			return false;
		}
		users.put(user.getUsername(), user);
		return true;
	}

	@Override
	public boolean save() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_RECORDS));
			out.writeObject(users);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public UserMetadata loginUser(String username, String password) {
		if (users.containsKey(username)) {
			if (users.get(username).getPassword().equals(password)) {
				return users.get(username);
			}
		}
		return null;
	}
	
	/**
	 * Following the singleton pattern, return a reference to
	 * the singleton user manager.
	 * 
	 * @return A reference to the only user manager that should
	 * be instantiated.
	 */
	public static UserManagerInterface getDefaultUserManager() {
		
		if( userManager == null ) {
			userManager = new FlatfileUserManager(FlatfileUserManager.USER_RECORDS);
		}
		
		return userManager;
	}
	
	public static UserManagerInterface getDefaultTestUserManager() {
		
		if( tuserManager == null ) {
			tuserManager = new FlatfileUserManager(FlatfileUserManager.USER_RECORDS + "_test");
		}
		
		return tuserManager;
	}

}
