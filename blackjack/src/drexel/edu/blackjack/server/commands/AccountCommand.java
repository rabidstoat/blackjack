/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - AccountCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class handles the ACCOUNT protocol message. It has logic for
 * looking up the user's account and returning the balance.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;

public class AccountCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "ACCOUNT";
	
	private Set<STATE> validStates = null;

	@Override
	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		
		// Step 0: If either object is null, it's an internal error
		if( protocol == null || cm == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"AccountCommand.processCommand() received null arguments" ).toString();
		}
		
		// Steps 1-2: Return an error in not in a valid state
		if( !getValidStates().contains( protocol.getState()) ) {
			return new ResponseCode( ResponseCode.CODE.NEED_TO_BE_AUTHENTICATED ).toString();
		}
		
		// Step 3-4: Check syntax; irrelevant as no parameters
		
		// Step 5: Do work that needs doing
		
		// Look up the user balance
		if( protocol.getUser() == null || protocol.getUser().getUserMetadata() == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"AccountCommand.processCommand() had a problem with the protocol object").toString();
		}
		int balance = protocol.getUser().getUserMetadata().getBalance();
		// Step 6: Save out state variables? There are none
		// Step 7: Update any change in state? There is none
		
		// Step 8: Format the user response code
		ResponseCode code = ResponseCode.createAccountBalanceResponseCode( balance );
		return code.toString();
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		
		// Keep this around so we only create it once
		if( validStates == null ) {
			validStates = new HashSet<STATE>();
			
			// You need to be authorized for this command,so it's definitely
			// true when you're in a sessaion
			validStates.add( STATE.IN_SESSION_AFTER_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AS_OBSERVER );
			validStates.add( STATE.IN_SESSION_AWAITING_BETS );
			validStates.add( STATE.IN_SESSION_BEFORE_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_DEALER_BLACKJACK );
			validStates.add( STATE.IN_SESSION_SERVER_PROCESSING );
			
			// And also the one other state
			validStates.add( STATE.NOT_IN_SESSION );
		}
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		// There aren't any parameters for this command
		return null;
	}

}
