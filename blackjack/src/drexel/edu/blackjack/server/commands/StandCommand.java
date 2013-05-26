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
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

public class StandCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "STAND";

	private Set<STATE> validStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		// We need to implement something here....
		return super.processCommand(protocol, cm);
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
