package drexel.edu.blackjack.server.commands;

import drexel.edu.blackjack.server.game.User;

/**
 * This class handles the case where the user sent a command
 * that is not recognized. That is, the first token on the 
 * command string does not match any of the twelve commands
 * we have implemented.
 * 
 * @author Jennifer
 */
public class UnknownCommand extends BlackjackCommand {

	@Override
	public String processCommand(User user, CommandMetadata cm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandWord() {
		// Since this is the command to use if no valid command is specified,
		// it returns the special 'null' command word
		return null;
	}

}
