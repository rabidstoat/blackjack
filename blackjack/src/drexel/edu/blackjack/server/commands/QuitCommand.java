package drexel.edu.blackjack.server.commands;

import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

public class QuitCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "QUIT";

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getRequiredParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
