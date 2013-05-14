package drexel.edu.blackjack.server.commands;

import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

/**
 * This class handles the case where the user sent a command
 * that is not recognized. That is, the first token on the 
 * command string does not match any of the twelve commands
 * we have implemented.
 * 
 * @author Jennifer
 */
public class UnknownCommand extends BlackjackCommand {

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		// We need to implement something here....
		return super.processCommand(protocol, cm);
	}

	@Override
	public String getCommandWord() {
		// Since this is the command to use if no valid command is specified,
		// it returns the special 'null' command word
		return null;
	}

	@Override
	public Set<STATE> getValidStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
