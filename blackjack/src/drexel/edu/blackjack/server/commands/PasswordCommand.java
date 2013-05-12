package drexel.edu.blackjack.server.commands;

import drexel.edu.blackjack.server.game.User;

public class PasswordCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "PASSWORD";

	@Override
	public String processCommand(User user, CommandMetadata cm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

}
