/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - StandCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class handles the STAND protocol method, by alerting the game-
 * playing thread in the server about the action.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.server.game.User;

public class StandCommand extends BlackjackCommand {

	public static final String COMMAND_WORD = "STAND";

	private Set<STATE> validStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		// Step 0: If either object is null, it's an internal error
		if( protocol == null || cm == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"AccountCommand.processCommand() received null arguments" ).toString();
		}
		
		// STATEFUL: Steps 1-2: Return an error in not in a valid state
		if( !getValidStates().contains( protocol.getState()) ) {
			return new ResponseCode( ResponseCode.CODE.NOT_EXPECTING_STAND ).toString();
		}
		
		// Step 3-4: Check syntax; irrelevant as no parameters
		// Step 5: Do work that needs doing

		// We better have a user object
		User user = protocol.getUser();
		if( user == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"StandCommand.processCommand() had a problem getting the user object").toString();
		}
		
		// Note on the user object that they made their gameplay, and that they are done for the round
		user.setNeedsToMakeAPlay( false );
		user.setHasFinishedGamePlayThisRound( true );
		
		// Better have a game state
		GameState state = (user.getGame() == null ? null : user.getGame().getGameState());
		if( state == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"StandCommand.processCommand() had a problem getting the game state object").toString();
		}
		
		// Because we need to notify others that the player chose to stand
		state.notifyOthersOfGameAction(user, GameState.STAND_KEYWORD );
		
		// Step 6: Save out state variables? There are none
		// Step 7: Update any change in state?
		// STATEFUL: It's now after their turn
		protocol.setState( STATE.IN_SESSION_AFTER_YOUR_TURN );
		
		// Step 8: Format the user response code
		ResponseCode code = new ResponseCode( ResponseCode.CODE.SUCCESSFULLY_STAND );
		return code.toString();
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		// Keep this around so we only create it once
		if( validStates == null ) {
			validStates = new HashSet<STATE>();
			
			// Needs to be awaiting your turn to use this command
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );			
		}
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
