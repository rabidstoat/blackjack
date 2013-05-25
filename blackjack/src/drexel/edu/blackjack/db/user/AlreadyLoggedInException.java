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
