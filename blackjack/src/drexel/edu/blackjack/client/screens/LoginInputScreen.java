package drexel.edu.blackjack.client.screens;

import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.in.MessagesFromServerListener;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This handles the user interface for logging into the
 * server with a username and password.
 * 
 * @author Jennifer
 */
public class LoginInputScreen extends AbstractScreen implements MessagesFromServerListener {
	
	// This screen can be in one of these two states
	private static final int ENTER_USERNAME	= 1;
	private static final int ENTER_PASSWORD = 2;
	
	// Track which one it's in
	private int loginScreenState;
	
	public LoginInputScreen( ClientInputFromServerThread thread ) {
		
		// It starts in the ENTER_USERNAME state, but inactive
		loginScreenState = ENTER_USERNAME;
		setIsActive( false );
	}

	@Override
	public void receivedMessage(ResponseCode code) {
		// TODO Auto-generated method stub
		
	}
	
	public void displayMenu() {
		
		if( loginScreenState == ENTER_USERNAME ) {
			System.out.println( "Please enter your username: " );
		} else if( loginScreenState == ENTER_PASSWORD ) {
			System.out.println( "Please enter your password: " );
		} else {
			// This should never happen
			System.out.println( "Oh no, you broke it!!!" );
		}
	}

}
