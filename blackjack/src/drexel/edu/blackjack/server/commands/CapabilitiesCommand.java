/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - CapabilitiesCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class implements the CAPABILITIES command. It uses the protocol
 * state, along with knowledge of what commands can be used in what states, to
 * generate a state-specific list of capabilities.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;

public class CapabilitiesCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "CAPABILITIES";
	
	Set<STATE> validStates = null;
		
    /**@param protocol The protocol connection that made that
	 * command. From there the user, state, and all sorts of
	 * good information can be found.
	 * @param cm Information derived from the client associated
	 * with the user, what it sent in to the server
	 * @return The string that should be sent back to the client
	 * as the response. 
	 */
	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		
		//Step 0: If either object is null, it's an internal error
		if (protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
				"CapabilitiesCommand.processCommand() received null arguments").toString();
		}

		// 1. Get set of all valid commands, in any state
		Set<BlackjackCommand> commands = protocol.getAllValidCommands();

		// 2. Get the current state
		STATE currentState = protocol.getState();
		
		// Build up capabilities list here
		StringBuilder capabilities = new StringBuilder();

		for( BlackjackCommand command : commands ) {
			
			//Get the states in which command is valid
			Set<STATE> stateSet = command.getValidStates();
			
			if(stateSet.contains(currentState)){

				//append to string builder:the command word, a space, and any parameters. 
				StringBuilder str = new StringBuilder(command.getCommandWord());
				if( command.getRequiredParameterNames() != null ) {
					for( String parameter : command.getRequiredParameterNames() ) {
						str.append( " " );
						str.append( parameter );
					}
				}
				str.append( "\n" );
				capabilities.append( str.toString() );
			}			
		}
			
		return 	new ResponseCode( ResponseCode.CODE.CAPABILITIES_FOLLOW,
				"CapabilitiesCommand.processCommand() List of capabilities allowed in state: " +
				"\n" + capabilities.toString()).toString();	
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		validStates = new HashSet<STATE>();

		//States where capabilities command is allowed
		validStates.add(STATE.WAITING_FOR_USERNAME);
		validStates.add(STATE.WAITING_FOR_PASSWORD);
		validStates.add(STATE.NOT_IN_SESSION);
		validStates.add(STATE.IN_SESSION_SERVER_PROCESSING);
		validStates.add(STATE.IN_SESSION_BEFORE_YOUR_TURN);
		validStates.add(STATE.IN_SESSION_AND_YOUR_TURN);
		validStates.add(STATE.IN_SESSION_AWAITING_BETS);
		validStates.add(STATE.IN_SESSION_AFTER_YOUR_TURN);
		validStates.add(STATE.IN_SESSION_AS_OBSERVER);
		validStates.add(STATE.IN_SESSION_DEALER_BLACKJACK);

		return validStates;
	}
	
	/**
	 * This returns a list of parameters that the command requires.
	 * For CAPABILITIES there is no parameter that the command needs.
	 * The CAPABILITIES command makes use of this for sending the 
	 * capabilities list.
	 * 
	 * @return A null list as there are no parameters needed
	 */
	@Override
	public ArrayList<String> getRequiredParameterNames() {

		//Command word CAPABILITIES has no parameters; hence, this method does apply.
		
		return null;
	}
}


