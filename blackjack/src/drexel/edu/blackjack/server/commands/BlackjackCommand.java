package drexel.edu.blackjack.server.commands;

import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

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
	
	/**
	 * This returns a set of states that the command can validly be used in.
	 * The CAPABILITIES command will use this to get a list of capabilities
	 * for the current protocol state to return
	 **/
	public abstract Set<BlackjackProtocol.STATE> getValidStates();
	
	/**
	 * This returns a list of parameters that the command requires.
	 * For example, for USERNAME there is a 'username' parameter
	 * that the command needs. For BET, there is the 'amount'
	 * parameter. The CAPABILITIES command makes use of this
	 * for sending the capabilities list
	 * 
	 * @return
	 */
	public abstract Set<String> getRequiredParameterNames();

}
