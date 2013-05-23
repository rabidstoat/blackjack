
package drexel.edu.blackjack.server.commands;

import java.util.Set;
import java.util.HashSet;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.commands.BlackjackCommand;
import drexel.edu.blackjack.server.commands.CommandMetadata;


public class QuitCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "QUIT";
	
	private Set<STATE> validStates = null;
	
	@Override
	public String processCommand( BlackjackProtocol protocol, CommandMetadata cm ) {
		
		//Step 0: if either object is null, there is an internal error
		if( protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"QUITCommand.processCommand() received null arguments").toString();
		}
		
		//Steps 1-2: Return error if not in a valid state 
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
			validStates.add( STATE.WAITING_FOR_USERNAME );
			validStates.add( STATE.WAITING_FOR_PASSWORD );
			validStates.add( STATE.NOT_IN_SESSION );
			validStates.add( STATE.IN_SESSION_AS_OBSERVER );
			validStates.add( STATE.IN_SESSION_AWAITING_BETS );
			validStates.add( STATE.IN_SESSION_BEFORE_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_DEALER_BLACKJACK );
			validStates.add( STATE.IN_SESSION_SERVER_PROCESSING );
			
		}
		return validStates;
	}
	
	@Override
	public java.util.List<String> getRequiredParameterNames() {
		//QUIT has no required parameters
		return null;
	}
}
