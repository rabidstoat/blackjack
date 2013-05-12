package drexel.edu.blackjack.server;

/**
 * Our protocol class is where we keep track of the user
 * that is attached to this connection, along with the
 * protocol state. The user is initially null until they
 * authenticate. Local variables connected to state are
 * also stored here.
 */
public class BlackjackProtocol {

	/**
	 * This is how we handle messages.
	 * 
	 * @param inputLine
	 * @return
	 */
	public String processInput(String inputLine) {
		return  "All work and no play makes Jack a dull boy.";
	}

}
