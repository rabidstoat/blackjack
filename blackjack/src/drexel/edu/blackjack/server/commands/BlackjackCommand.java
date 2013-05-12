package drexel.edu.blackjack.server.commands;

import drexel.edu.blackjack.server.game.User;

/**
 * Commands that the server interprets will implement this class.
 * 
 * @author Jennifer
 */
public abstract class BlackjackCommand {

	/**
	 * The user object should have a way to get at the connection
	 * and the current game the user is in, if any. That should 
	 * be enough to enable the command to processed.
	 * 
	 * @param user Active user in the system
	 * @param cm Information derived from the client associated
	 * with the user, what it sent in to the server
	 * @return The string that should be sent back to the client
	 * as the response. Potentially multi-line.
	 */
	public String processCommand( User user, CommandMetadata cm ) {
		
		// The default implementation should be overridden!
		StringBuilder str = new StringBuilder( "Someone needs to implement the " );
		str.append( this.getClass().toString() );
		str.append( " command." );
		return str.toString();
		
	}
}
