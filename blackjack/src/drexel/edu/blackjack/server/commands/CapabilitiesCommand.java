package drexel.edu.blackjack.server.commands;

import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

public class CapabilitiesCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "CAPABILITIES";

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
	public List<String> getRequiredParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
