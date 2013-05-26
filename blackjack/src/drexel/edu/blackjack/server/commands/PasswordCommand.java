/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - PasswordCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Password: This implements the PASSWORD command. It obtains the previously 
 * specified username from the protocol state, checks with the UserManager
 * if the credentials are valid, and sends an appropriate response to the 
 * client. If the login IS valid, it updates the protocol state with user
 * information.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.db.user.AlreadyLoggedInException;
import drexel.edu.blackjack.db.user.FlatfileUserManager;
import drexel.edu.blackjack.db.user.UserManagerInterface;
import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.game.User;

/**
 * <b>STATEFUL:</b> Implements the logic needed to respond to 
 * the PASSWORD command from a client. Like all command classes,
 * it uses the protocol state to determine if it's in a valid
 * state.
 * 
 * @author Constantine
 * @author Jennifer
 */
public class PasswordCommand extends BlackjackCommand {
	
	// They get 3 tries to login before they're booted
	public static final int INCORRECT_LOGIN_LIMIT = 3;

	private static final String COMMAND_WORD = "PASSWORD";

	// STATEFUL: Will hold valid states that this command operates in
	private Set<STATE> validPasswordStates = null;
	
	/**
	 * Process the command 
	 * @param protocol The protocol connection that made that
	 * command. From there the user, state, and all sorts of
	 * good information can be found.
	 * @param cm Information derived from the client associated
	 * with the user, what it sent in to the server
	 * @return The string that should be sent back to the client
	 * as the response.
	 */
	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {

		/** Step 0: Error 1: If either object is null, it's an internal error */
		if (protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"PasswordCommand.processCommand() received null arguments").toString();	
		}

		/** STATEFUL: Step 1-2: Error 2: Return an error if not in valid state for PASSWORD command */
		if(!getValidStates().contains( protocol.getState()) ) {
			
			return new ResponseCode( ResponseCode.CODE.NOT_EXPECTING_PASSWORD,
					"PasswordCommand.processCommand() received out-of-context command").toString();
		}

		/** Steps 3-4: Error 3: If the PASSWORD password parameter is not exactly one string,
		 * send an error message.*/
		if ((cm.getParameters() == null) || cm.getParameters().size() != 1)  {
			
			return new ResponseCode( ResponseCode.CODE.SYNTAX_ERROR,
					"Must include a single parameter indicating password").toString();
		}
		
		//Get username from parameter protocol passed
		String username = protocol.getUsername();
		
		//Get password from cm parameter passed; extract from list, the first -and only- token. 
		String password = cm.getParameters().get(0);
		
		//Sigleton FlatfileUserManager
		UserManagerInterface userManager = FlatfileUserManager.getDefaultUserManager();

		// We need to return this
		ResponseCode code = new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR );

		// Try to log the user in
		UserMetadata userMetadata = null;
		try {
			userMetadata = userManager.loginUser(username, password);
			// Null metadata means they were unsuccessful
			if( userMetadata == null) {
				
				// We keep track of how many times they've failed
				protocol.incrementIncorrectLogins();
				
				
				// Make sure they didn't hit a threshold
				if( protocol.getIncorrectLogins() >= INCORRECT_LOGIN_LIMIT ) {
					code = new ResponseCode( ResponseCode.CODE.LOGIN_ATTEMPTS_EXCEEDED );
				} else {
					// Here, we let them try again
					protocol.setState(STATE.WAITING_FOR_USERNAME);
					code = new ResponseCode( ResponseCode.CODE.INVALID_LOGIN_CREDENTIALS, 
							"PasswordCommand.loginCredentialsCommand() received invalid login credentials; try logging in again.");
				}
			} else {
				// While non-null metadata is good
				/**Associate the new user with the protocol*/
				
				User user = new User(userMetadata);
					
				protocol.setUser(user);
				
				/**STATEFUL: Step 7: Update state. The client has authenticated but is not in a session*/
				
				protocol.setState(STATE.NOT_IN_SESSION);
					
				/**Step 8: Generate the proper response */
				
				code = new ResponseCode( ResponseCode.CODE.SUCCESSFULLY_AUTHENTICATED, 
						"PasswordCommand.loginCredentialsCommand() received valid login credentials; " +
						"still not in session" );
			}
		} catch (AlreadyLoggedInException e) {
			// Logging in twice is invalid
			code = new ResponseCode( ResponseCode.CODE.ALREADY_LOGGED_IN );
		} 

		// Return whatever happened
		return code.toString();
	}
	
	@Override
	public Set<STATE> getValidStates() {
		validPasswordStates = new HashSet<STATE>();

		//Add the only allowed state which is WAITING_FOR_PASSWORD
		validPasswordStates.add(STATE.WAITING_FOR_PASSWORD);

		return validPasswordStates;
	}

	/**
	 * This returns a list of parameters that the command requires.
	 * For PASSWORD there is a 'password' parameter
	 * that the command needs. The CAPABILITIES command makes use of this
	 * for sending the capabilities list
	 * Parse the password command word into command word and parameter
	 * and pass back -in an ArrayList- the only parameter PASSWORD command word is allowed
	 * to have.
	 * 
	 * @return parameter The List with just one string, the password command-word
	 * parameter.
	 */
	@Override
	public List<String> getRequiredParameterNames() {
		
		List<String> names = new ArrayList<String>();
		names.add( "password" );
		return names;
	}
	
	@Override
	public String getCommandWord() { 
		return COMMAND_WORD;
	}

}
