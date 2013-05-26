/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is a base class which all classes that implement protocol
 * command functionality must extend. There is a one-to-one correspondence
 * between protocol commands, and classes that implement the functionality
 * related to the command. It mostly defines the methods that need to be
 * implemented by these command classes.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.Set;
import java.util.List;

import drexel.edu.blackjack.server.BlackjackProtocol;

/**
 * <b>STATEFUL:</b> Commands that the server interprets will implement this class.
 * Pretty much everything in this class is concerned with state.
 * 
 * These commands should keep track of no state. The state should
 * be derived from the BlackjackProtocol object. The commands will be shared
 * across multiple connections, so they just have the logic that
 * operate on parameters that are passed into them.
 * 
 * @author Jennifer
 */
public abstract class BlackjackCommand {

	/**
	 * The algorithm for all command states should look something
	 * like this (possibly incomplete):
	 * 
	 * <UL>
	 * <LI>0. If either parameter is null, return an internal error
	 * response code
	 * <LI><b>STATEFUL:</b> 1. Check to see if in a state (get it from the protocol
	 * object) where this command is valid.
	 * <LI>1a. If not, send the error response code (this is hopefully
	 * specified in our protocol spec, if not, need to figure
	 * out what the code should be)
	 * <LI>2b. If it is in a correct protocol state, keep going
	 * <LI>3. Make sure the syntax of the command is correct (e.g.,
	 * if it's the USERNAME command they MUST have a single
	 * parameter -- you can get parameters off the CommandMetadata
	 * object, getParameters() method)
	 * <LI>4a. If not, send a response code (again, look in document,
	 * might be 502 Syntax Error code or might be something
	 * more specific)
	 * <LI> 5. Do whatever work needs to be done for the command
	 * <LI><b>STATEFUL:</b> 6. Save out any state variables (e.g., on the USERNAME
	 * command, use BlackjackProtocol.setUsername() to save
	 * the username)
	 * <LI><b>STATEFUL:</b> 7. Update any change of state using the setState()
	 * method on BlackjackProtocol (e.g., if you were in the
	 * WAITING_FOR_USERNAME state update the state to
	 * WAITING_FOR_PASSWORD)
	 * <LI>8. Generate the proper response code sting and return
	 * it
	 * </UL>
	 * 
	 * @param protocol The protocol connection that made that
	 * command. From there the user, state, and all sorts of
	 * good information can be found.
	 * @param cm Information derived from the client associated
	 * with the user, what it sent in to the server
	 * @return The string that should be sent back to the client
	 * as the response. Potentially multi-line.
	 */
	public String processCommand( BlackjackProtocol protocol, CommandMetadata cm ) {
		
		// The default implementation should be overridden!
		StringBuilder str = new StringBuilder( "500 Someone needs to implement the " );
		str.append( this.getClass().toString() );
		str.append( " class." );
		return str.toString();
		
	}

	/**
	 * This is the word that commands of this type begin with
	 * 
	 * @return The command word, like BET or USERNAME, as per
	 * the protocol spec
	 */
	public abstract String getCommandWord();
	
	/**
	 * This returns a set of states that the command can validly be used in.
	 * The CAPABILITIES command will use this to get a list of capabilities
	 * for the current protocol state to return
	 * 
	 * @return The set of all states in which this command can
	 * validly be used.
	 **/
	public abstract Set<BlackjackProtocol.STATE> getValidStates();
	
	/**
	 * This returns a list of parameters that the command requires.
	 * For example, for USERNAME there is a 'username' parameter
	 * that the command needs. For BET, there is the 'amount'
	 * parameter. The CAPABILITIES command makes use of this
	 * for sending the capabilities list
	 * 
	 * @return An ordered list of names for the parameters that
	 * are required for this command, in human-readable terms.
	 * If there are no parameters, both a null and an empty
	 * list are okay to return.
	 */
	public abstract List<String> getRequiredParameterNames();

}
