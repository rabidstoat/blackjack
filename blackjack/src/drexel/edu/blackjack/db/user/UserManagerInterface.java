/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - UserManagerInterface.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This defines the interface that can be used to load, store, and
 * interact with the users who are defined for a game server. It also defines
 * methods for logging users in and out of the system.
 ******************************************************************************/
package drexel.edu.blackjack.db.user;

import java.util.List;

/**
 * Interface that any persistent store for users has to implement.
 * <P>
 * The data that has to be stored is a number of user accounts,
 * of which all the data that is in the UserMetadata class has
 * to be stored. There might be 0 users in the user manager, or
 * one, or 100. This could be implemented as a database, a flat
 * file, an interface to a LDAP server, or whatever.
 * <p>
 * <b>SECURITY:</b> There is a method here to handle username/password
 * authentication.
 * 
 * @author Jennifer
 */
public interface UserManagerInterface {
	
	public static class UserNotFoundException extends Exception {
		private static final long serialVersionUID = 2145337834380938724L;
	}

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
	 * This method tries to add a new user with his metadata to the
	 * current list
	 * 
	 * @return True if the user is added succesfully
	 */
	public boolean add(UserMetadata user);
	
	/**
	 * This method should only be used AFTER a successful load()
	 * was executed at some point.
     * <P>
	 * By examining the UserMetadata in memory, that was 
	 * previously loaded, look to see if a username and password
	 * combination as indicated exists. If so, return the
	 * UserMetadata object -- not a copy of it, the actual 
	 * object, as the balance may be updated on it elsewhere
	 * in the program, with the expectation that it is later
	 * stored.
     * <P>
	 * If multiple UserMetadata objects exist with the same
	 * username and password passed in, the behavior is undefined.
	 * Returning any of them is considered valid (as this is 
	 * really an error condition we're not worrying about).
     * <P>
	 * Users who have logged in need to be tracked, and if a
	 * username is logged in for a second time (with a correct
	 * password) an exception about them being already logged
	 * in must be thrown.
	 * <p>
	 * <b>SECURITY:</b> This is the method that handle authentication.
	 * 
	 * @param username Corresponds to username on the UserMetadata
	 * object
	 * @param password Corresponds to password on the UserMetadata
	 * object
	 * @return If some UserMetadata object exists with an equivalent
	 * username and password string, then return the object (not
	 * the object, a copy itself). Otherwise, return null.
	 * @throws An exception if the user is already logged in
	 */
	public UserMetadata loginUser( String username, String password ) throws AlreadyLoggedInException;
	
	/**
	 * Removes the user with this username from the tracked
	 * list of users who have logged into the session.
	 * 
	 * @param username
	 * @return True if they were indeed noted as already logged
	 * in but have been successfully removed, or false it they
	 * either weren't listed as being logged in OR it failed
	 * in some way
	 */
	public boolean logoutUser( String username );

	
	/**
	 * This returns a list of the UserMetadata objects
	 * that were loaaded with the load() method. Though
	 * you can call this before you've loaded the users,
	 * it'll just return null.
	 * 
	 * @return If the load() command has been used, this
	 * should return a valid list of user, though perhaps
	 * of size zero if no games were loaded. If the load()
	 * command was not called, or if it failed, it should
	 * return null.
	 */
	public List<UserMetadata> getUsers();
	
	/**
	 * Remove user from user list
	 * @param username of user
	 * @return true if removed successfully, otherwise false
	 */
	public boolean remove(String username) throws UserNotFoundException;
	
	/**
	 * Edit one specific user's password from user list
	 * @param username of user
	 * @return true if edited successfully, otherwise false
	 */
	public boolean changePassword(String username, String newPassword) throws UserNotFoundException;
}
