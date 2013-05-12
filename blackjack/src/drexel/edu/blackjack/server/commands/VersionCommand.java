package drexel.edu.blackjack.server.commands;

import drexel.edu.blackjack.server.game.User;

public class VersionCommand extends BlackjackCommand {
	
	private static final String COMMAND_WORD = "VERSION";

	@Override
	public String processCommand(User user, CommandMetadata cm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandWord() {
		// TODO Auto-generated method stub
		return COMMAND_WORD;
	}

}
