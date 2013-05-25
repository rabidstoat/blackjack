package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.game.User;

public class GameStatusCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "GAMESTATUS";

	private Set<STATE> validStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		
		//Step 0: If either object is null, it's an internal error
		if (protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
				"ListgamesCommand.processCommand() received null arguments").toString();
		}
		
		// Steps 1-2: Return an error in not in a valid state
		if( !getValidStates().contains( protocol.getState()) ) {
			return new ResponseCode( ResponseCode.CODE.NOT_EXPECTING_GAMESTATUS ).toString();
		}
		
		// Step 3-4: Check syntax; needs single parameter for session id
		if ((cm.getParameters() == null || (cm.getParameters().size() != 1 )) )  {		
			return new ResponseCode( ResponseCode.CODE.SYNTAX_ERROR ,
					"Must include a single parameter indicating 'sessionid'").toString();
		}
		String requestedSessionId = cm.getParameters().get(0);
		
		// Step 5: Do work that needs doing

		// Start by checking that the user is in the session they requested status for
		User user = protocol.getUser();
		if( user == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"Inside GameStatusCommand.processCommand() the user was null." ).toString();
		}
		if( user.getGame() == null || user.getGame().getId() == null ||
				!user.getGame().getId().equals(requestedSessionId) ) {
			return new ResponseCode( ResponseCode.CODE.GAMESTATUS_DOES_NOT_EXIST,
					"Either that sessionid does not exist, or the user is not in the session." ).toString();
		}

		// Step 6: Save out state variables? There are none
		// Step 7: Update any change in state? There is none
		
		// Step 8: Format the user response code; we add the extra newline
		// because this is a multiline response that must end with an extra
		// newline.
		ResponseCode code = new ResponseCode( ResponseCode.CODE.GAME_STATUS, user.getGame().getGameStatus() + "\n" );
		return code.toString();
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		if( validStates == null ) {
			// Valid if authorized and connected and in a game session
			validStates = new HashSet<STATE>();
			validStates.add( STATE.IN_SESSION_AFTER_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AS_OBSERVER );
			validStates.add( STATE.IN_SESSION_AWAITING_BETS );
			validStates.add( STATE.IN_SESSION_BEFORE_YOUR_TURN  );
			validStates.add( STATE.IN_SESSION_DEALER_BLACKJACK );
			validStates.add( STATE.IN_SESSION_SERVER_PROCESSING );
		}
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		List<String> params = new ArrayList<String>();
		params.add( "sessionid" );
		return params;
	}

}
