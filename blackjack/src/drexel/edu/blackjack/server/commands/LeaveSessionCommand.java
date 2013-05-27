/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - LeaveSessionCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class implements the LEAVESESSION protocol command. It updates
 * the protocol state as appropriate based on whether or not it succeeds.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.User;

/**
 * <b>STATEFUL:</b> Implements the logic needed to respond to 
 * the LEAVESESSION command from a client. Like all command classes,
 * it uses the protocol state to determine if it's in a valid
 * state. 
 * 
 * @author Jennifer
 */
public class LeaveSessionCommand extends BlackjackCommand {

	public static final String COMMAND_WORD = "LEAVESESSION";

	// STATEFUL: Will hold valid states that this command operates in
	private Set<STATE> validStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		// Step 0: If either object is null, it's an internal error
		if (protocol == null || cm == null) {
			return new ResponseCode(ResponseCode.CODE.INTERNAL_ERROR,
					"JoinSessionCommand.processCommand() received null arguments")
					.toString();
		}

		// STATEFUL: Steps 1-2: Return an error in not in a valid state
		if (!getValidStates().contains(protocol.getState())) {
			return getResponseStingForInvalidState(protocol.getState());
		}

		// Step 3-4: Check syntax; must have 0 parameters
		if( cm.getParameters() != null && cm.getParameters().size() > 0 ) {
			return new ResponseCode( ResponseCode.CODE.SYNTAX_ERROR, 
					"The LEAVESESSION command requires no parameters." ).toString();
		}

		// Step 5: Do work that needs doing
		
		// They need to be in a game if we're here. If they don't have a valid game
		// object we have some sort of internal error
		User user = protocol.getUser();
		if( user == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"In the LEAVESESSION command, the user was null." ).toString();
		}
		Game game = user.getGame();
		if( game == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"In the LEAVESESSION command, the game was null." ).toString();
		}
		
		// There are a few separate response codes that can get returned, based
		// on whether the user is forfeiting a bet. This will be sorted out elsewhere
		ResponseCode code = game.removePlayer( user );
		
		// Step 6: Save out state variables? There are none

		// STATEFUL: Step 7: Update the change in state
		protocol.setState( STATE.NOT_IN_SESSION );

		// Step 8: Format the user response code
		// Use what was returned earlier, unless it's null (which is a syntax error)
		if( code == null ) {
			code = new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"game.removePlayer() returned a null in LeaveSessionCommand.processCommand()" );
		}
		return code.toString();
	}

	/**
	 * The system is in some state where leave a session is not allowed. Need
	 * to return an error message that's appropriate.
	 * 
	 * @param protocol The associated protocol object
	 * @return The response code to return for the invalid state passed in
	 */
	private String getResponseStingForInvalidState(BlackjackProtocol.STATE state) {
		
		// One case is that they're not authenticated...
		if( state.equals(STATE.WAITING_FOR_PASSWORD) || state.equals(STATE.WAITING_FOR_USERNAME) ) {
			return new ResponseCode( ResponseCode.CODE.NEED_TO_BE_AUTHENTICATED ).toString();
		}
		
		// The other case is that they're not in a session
		return new ResponseCode( ResponseCode.CODE.USER_NOT_IN_GAME_ERROR).toString();
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		
		if( validStates == null ) {
			validStates = new HashSet<STATE>();
			// Available in any of the IN_SESSION states
			validStates.add( STATE.IN_SESSION_AFTER_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AS_OBSERVER );
			validStates.add( STATE.IN_SESSION_AWAITING_BETS );
			validStates.add( STATE.IN_SESSION_BEFORE_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_DEALER_BLACKJACK );
			validStates.add( STATE.IN_SESSION_SERVER_PROCESSING);
		}
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		return null;
	}

}
