/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - JoinSessionCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Pupose: This cass implements the JOINSESSION protocol command. After it is
 * determined whether the command request is valid, it sends an appropriate
 * response and updates the protocol state.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.db.game.FlatfileGameManager;
import drexel.edu.blackjack.db.game.GameManagerInterface;
import drexel.edu.blackjack.db.game.GameMetadata;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.game.ActiveGameCoordinator;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.User;

/**
 * <b>STATEFUL:</b> Implements the logic needed to respond to 
 * the JOINSESSION command from a client. Like all command classes,
 * it uses the protocol state to determine if it's in a valid
 * state. 
 * 
 * @author Jennifer
 */
public class JoinSessionCommand extends BlackjackCommand {

	public static final String COMMAND_WORD = "JOINSESSION";

	// STATEFUL: Will hold valid states that this command operates in
	private Set<STATE> validStates = null;

	@Override
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

		// Step 3-4: Check syntax; must have one parameter that indicates the session
		if( cm.getParameters() == null || cm.getParameters().size() != 1 ) {
			return new ResponseCode( ResponseCode.CODE.SYNTAX_ERROR, 
					"Must include a single parameter indicating the session to join" ).toString();
		}

		// Step 5: Do work that needs doing
		String sessionName = cm.getParameters().get(0);
		
		// First, does such a game exist
		GameManagerInterface gameManager = FlatfileGameManager.getDefaultGameManager();
		GameMetadata gameMetadata = gameManager.getGame( sessionName );
		if( gameMetadata == null ) {
			return new ResponseCode ( ResponseCode.CODE.JOIN_SESSION_DOES_NOT_EXIST ).toString();
		}
		
		// Next, does it have too many players?
		ActiveGameCoordinator coordinator = ActiveGameCoordinator.getDefaultActiveGameCoordinator();
		Game game = coordinator.getGame( sessionName );
		// If the game is actively played assume we're good to join; it's only when the game
		// is being played that we have to check the number of players
		if( game != null && !game.stillHasRoom() ) {
			return new ResponseCode( ResponseCode.CODE.JOIN_SESSION_AT_MAX_PLAYERS ).toString();
		}
		
		// Finally, make sure that the player can cover the minimum bet
		User user = protocol.getUser();
		
		// If we don't have a user, something weird has happened
		if( user == null || user.getUserMetadata() == null ) {
			return new ResponseCode(ResponseCode.CODE.INTERNAL_ERROR,
					"In JOIN SESSION, should have had a user associated with the protocol, but we didn't.")
					.toString();
		}
		if( user.getUserMetadata().getBalance() < gameMetadata.getMinBet() ) {
			return new ResponseCode( ResponseCode.CODE.JOIN_SESSION_TOO_POOR).toString();
		}
		
		// Finally if we get this far they can join the game! Make sure it succeeded.
		// If it succeeded this should set the game associated with the user object, too.
		game = coordinator.addPlayer( sessionName, user );
		if( game == null ) {
			return new ResponseCode(ResponseCode.CODE.INTERNAL_ERROR,
					"In JOIN SESSION, failed to join a game we thought we could because Coordinator.addPlayer() returned null.")
					.toString();
		}

		// Step 6: Save out state variables? There are none

		// STATEFUL: Step 7: Update the change in state
		protocol.setState( STATE.IN_SESSION_AS_OBSERVER );

		// Step 8: Format the user response code
		return new ResponseCode(ResponseCode.CODE.SUCCESSFULLY_JOINED_SESSION).toString();
	}

	/**
	 * The system is in some state where joinin a session is not allowed. Need
	 * to return an error message that's appropriate.
	 * 
	 * @param protocol Associated protocol object
	 * @return The appropriate response code to return for the invalid state passed in
	 */
	private String getResponseStingForInvalidState(BlackjackProtocol.STATE state) {
		
		// One case is that they're not authenticated...
		if( state.equals(STATE.WAITING_FOR_PASSWORD) || state.equals(STATE.WAITING_FOR_USERNAME) ) {
			return new ResponseCode( ResponseCode.CODE.NEED_TO_BE_AUTHENTICATED ).toString();
		}
		
		// The other case is that they're already in a session, which is ever other wrong state
		return new ResponseCode( ResponseCode.CODE.ALREADY_IN_SESSION ).toString();
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		if( validStates == null ) {
			// Only valid if NOT_IN_SESSION
			validStates = new HashSet<STATE>();
			validStates.add( STATE.NOT_IN_SESSION );
		}
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add( "sessionname" );
		return names;
	}

}
