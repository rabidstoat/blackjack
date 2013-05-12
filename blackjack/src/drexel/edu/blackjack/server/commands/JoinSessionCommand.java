package drexel.edu.blackjack.server.commands;

import drexel.edu.blackjack.server.BlackjackProtocol;

public class JoinSessionCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "JOINSESSION";

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		// We need to implement something here....
		return super.processCommand(protocol, cm);
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

}
