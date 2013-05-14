package drexel.edu.blackjack.server.commands;

import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

public class UsernameCommand extends BlackjackCommand {
	
	private static final String COMMAND_WORD = "USERNAME";

	@Override
	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		// We need to implement something here....
		return super.processCommand(protocol, cm);
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	/**
	 * This returns a set of states that the command can validly be used in.
	 * The CAPABILITIES command will use this to get a list of capabilities
	 * for the current protocol state to return
	 **/
	@Override
	public Set<STATE> getValidStates() {
			STATE WAITING_FOR_USERNAME = null;
				
				Set<STATE> validUsernameStates = new HashSet<STATE>();
				
				//Add the only allowed state which is WAITING_FOR_USERNAME
				validUsernameStates.add(WAITING_FOR_USERNAME);
				
				return validUsernameStates;
			}
	}



	@Override
	public List<String> getRequiredParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
