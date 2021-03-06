/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - QuitCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This implements the QUIT command. It is only responsible for sending
 * a response to the client. The actual severing of the connection is done
 * elsewhere.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.Set;
import java.util.HashSet;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.commands.BlackjackCommand;
import drexel.edu.blackjack.server.commands.CommandMetadata;


/**
 * <b>STATEFUL:</b> Implements the logic needed to respond to 
 * the QUIT command from a client. Like all command classes,
 * it uses the protocol state to determine if it's in a valid
 * state. 
 * 
 * @author Carol
 */
public class QuitCommand extends BlackjackCommand {

	public static final String COMMAND_WORD = "QUIT";
	
	// STATEFUL: Will hold valid states that this command operates in
	private Set<STATE> validStates = null;
	
	@Override
	public String processCommand( BlackjackProtocol protocol, CommandMetadata cm ) {
		
		//Step 0: if either object is null, there is an internal error
		if( protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"QUITCommand.processCommand() received null arguments").toString();
		}
		
		//STATEFUL: Steps 1-2: Return error if not in a valid state 
		//quit command available in any connected state 
		if( !getValidStates().contains( protocol.getState() )) {
			return new ResponseCode( ResponseCode.CODE.NEED_TO_BE_AUTHENTICATED).toString();
		}
		//Step 3 -4: check syntax, no parameters in quit
		//Step 5: work that needs to be done - ONLY NEED TO GENERATE RESPONSE (STEP 8)
		//Step 6: Save out state variables? not applicable in qui
		//Step 7: Update any change in state- disconnect
		//Step 8: Format user response code
		return 	new ResponseCode( ResponseCode.CODE.SUCCESSFULLY_QUIT).toString();
	}
	
	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}
	
	@Override
	public Set<STATE> getValidStates() {
		//Keep this around so we only create once
		if( validStates == null ) {
			validStates = new HashSet<STATE>();
			
			//quit command works in any connected states
			validStates.add( STATE.NOT_IN_SESSION );
			
		}
		return validStates;
	}
	
	@Override
	public java.util.List<String> getRequiredParameterNames() {
		//QUIT has no required parameters
		return null;
	}
}
