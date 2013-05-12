package drexel.edu.blackjack.server.commands;

import drexel.edu.blackjack.server.BlackjackProtocol;

/**
 * Commands that the server interprets will implement this class.
 * 
 * These commands should keep track of no state. The state should
 * be derived from the user object. The commands will be shared
 * across multiple connections, so they just have the logic that
 * operate on parameters that are passed into them.
 * 
 * @author Jennifer
 */
public abstract class BlackjackCommand {

	/**
	 * The user object should have a way to get at the connection
	 * and the current game the user is in, if any. That should 
	 * be enough to enable the command to processed.
	 * 
	 * @param user The protocol connection that made that
	 * command. From there the user, state, and all sorts of
	 * good information can be found.
	 * @param cm Information derived from the client associated
	 * with the user, what it sent in to the server
	 * @return The string that should be sent back to the client
	 * as the response. Potentially multi-line.
	 */
	public String processCommand( BlackjackProtocol protocol, CommandMetadata cm ) {
		
		// The default implementation should be overridden!
		StringBuilder str = new StringBuilder( "Someone needs to implement the " );
		str.append( this.getClass().toString() );
		str.append( " class." );
		return str.toString();
		
	}
	
	// This is the word that commands of this type begin with
	public abstract String getCommandWord();
}
