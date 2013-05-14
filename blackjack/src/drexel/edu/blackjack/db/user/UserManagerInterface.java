package drexel.edu.blackjack.db.user;

/**
 * Interface that any persistent store for users has to implement.
 * 
 * The data that has to be stored is a number of user accounts,
 * of which all the data that is in the UserMetadata class has
 * to be stored. There might be 0 users in the user manager, or
 * one, or 100. This could be implemented as a database, a flat
 * file, an interface to a LDAP server, or whatever.
 * 
 * @author Jennifer
 */
public interface UserManagerInterface {

	/**
	 * This loads the data from the persistent storage (e.g.,
	 * the database or flatfile or whatever) into memory,
	 * where it can be accessed programatically. The actual
	 * initialization of the user manager will need to be done
	 * on the class that implements it, and called prior to
	 * loading. Recommendation is to do this in the constructor.
	 * 
	 *  @return True if the users were loaded successfully,
	 *  False is there was any error in the loading that has
	 *  the internal representation in a possibly inconsistent
	 *  or incorrect state.
	 */
	public boolean load();
	
	/**
	 * This takes whatever users are in memory (which may have
	 * been modified by other parts of the application) and
	 * saves them out to the persistent storage.
	 * 
	 * @return True if the users were stored successfully.
	 * False if there was any error in the storing where
	 * the contents of the persistent storage may not be
	 * correct.
	 */
	public boolean save();
	
	/**
	 * This method should only be used AFTER a successful load()
	 * was executed at some point.
	 * 
	 * By examining the UserMetadata in memory, that was 
	 * previously loaded, look to see if a username and password
	 * combination as indicated exists. If so, return the
	 * UserMetadata object -- not a copy of it, the actual 
	 * object, as the balance may be updated on it elsewhere
	 * in the program, with the expectation that it is later
	 * stored.
	 * 
	 * If multiple UserMetadata objects exist with the same
	 * username and password passed in, the behavior is undefined.
	 * Returning any of them is considered valid (as this is 
	 * really an error condition we're not worrying about).
	 * 
	 * @param username Corresponds to username on the UserMetadata
	 * object
	 * @param passord Corresponds to password on the UserMetadata
	 * object
	 * @return If some UserMetadata object exists with an equivalent
	 * username and password string, then return the object (not
	 * the object, a copy itself). Otherwise, return null.
	 */
	public UserMetadata loginUser( String username, String password );
}