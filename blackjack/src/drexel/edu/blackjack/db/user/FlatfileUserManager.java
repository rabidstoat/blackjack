package drexel.edu.blackjack.db.user;

/**
 * User metadata is stored out in a flat file. The format
 * of the flat file is:
 * 
 * <Duc: Add documentation here about the format of the
 * file.>
 * 
 * @author DAN
 */
public class FlatfileUserManager implements UserManagerInterface {
	
	// Keep a single instance around for the singleton design pattern
	private static FlatfileUserManager userManager = null;

	/**
	 * Following the singleton design pattern, the constructor
	 * is kept private.
	 */
	private FlatfileUserManager() {
		// TODO: Implement. It's okay to use a hard-coded filename.
	}

	
	@Override
	public boolean load() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UserMetadata loginUser(String username, String password) {
		// TODO Write the real method, this is a stub method for
		// testing purposes. The rule is: if they enter 'password' 
		// as their password, create a valid UserMetadata object and
		// return it. Otherwise, return null
		if( password != null && password.trim().equalsIgnoreCase("password") ) {
			UserMetadata metadata = new UserMetadata();
			metadata.setBalance(500);
			metadata.setFullname( username );
			metadata.setUserame(username);
			metadata.setPassword(password);
			return metadata;
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
			userManager = new FlatfileUserManager();
		}
		
		return userManager;
	}

}
