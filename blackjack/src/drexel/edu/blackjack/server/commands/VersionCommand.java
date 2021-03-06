/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - VersionCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This implements the VERSION protocol command by creating a response
 * code with the version information.
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
 * the ACCOUNT command from a client. Like all command classes,
 * it uses the protocol state to determine if it's in a valid
 * state. It also checks the protocol stateful variable to see
 * what associated user is making the account request.
 * 
 * @author Carol
 */
public class VersionCommand extends BlackjackCommand {

	double version = 1.0;
	
	public static final String COMMAND_WORD = "VERSION";
	
	// STATEFUL: Will hold valid states that this command operates in
	private Set<STATE> validStates = null;
	
	@Override
	public String processCommand( BlackjackProtocol protocol, CommandMetadata cm ) {
		
		//Step 0: if either object is null, there is an internal error
		if( protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"VersionCommand.processCommand() received null arguments").toString();
		}
		
		//STATEFUL: Steps 1-2: Return error if not in a valid state 
		//version command available in any connected state 
		if( !getValidStates().contains( protocol.getState() )) {
			return new ResponseCode( ResponseCode.CODE.NEED_TO_BE_AUTHENTICATED).toString();
		}
	
		//Step 3 -4: check syntax, no parameters - no parameters in version
		
		//Step 5: work that needs to be done
		//Get version number from ???

		//Step 6: Save out state variables? no state variables to save in version
		
		//Step 7: Update any change in state - no state change in version
		
		//Step 8: Format user response code
		        
		return 	new ResponseCode( ResponseCode.CODE.VERSION, "version 1.0 (CS544 implementation)").toString();
				
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
			
			//version command works in any connected states
			validStates.add( STATE.WAITING_FOR_USERNAME );
			validStates.add( STATE.WAITING_FOR_PASSWORD );
			validStates.add( STATE.NOT_IN_SESSION );
			validStates.add( STATE.IN_SESSION_AS_OBSERVER );
			validStates.add( STATE.IN_SESSION_AWAITING_BETS );
			validStates.add( STATE.IN_SESSION_BEFORE_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );
			
		}
		return validStates;
	}
	
	@Override
	public java.util.List<String> getRequiredParameterNames() {
		//Version has no required parameters
		return null;
	}
}
