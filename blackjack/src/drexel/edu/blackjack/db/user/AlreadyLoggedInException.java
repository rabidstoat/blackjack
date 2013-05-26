/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - AlreadyLoggedInException.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This exception gets thrown if an attempt is made to log in someone
 * who is already logged in.
 ******************************************************************************/
package drexel.edu.blackjack.db.user;

/**
 * This exception is thrown by the login() command if
 * the user is alraeady logged into the server.
 * 
 * @author Jennifer
 */
public class AlreadyLoggedInException extends Exception {

	private static final long serialVersionUID = 1L;

	public AlreadyLoggedInException( String message ) {
		super( message );
	}
}
