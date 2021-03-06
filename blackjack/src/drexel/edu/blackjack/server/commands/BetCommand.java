/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BetCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class handles the BET protocol command. It has logic for
 * checking to see if the bet is allowed, updating the protocol state as
 * needed, and returning a proper response code.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * <b>STATEFUL:</b>: Response messages related to the BET command.
 * Like all command classes,
 * it uses the protocol state to determine if it's in a valid
 * state. It also checks the protocol stateful variable to see
 * what associated user is making the bet command, and to store
 * what the bet value is as a stateful variable.
 *
 * @author Constantine
 */

public class BetCommand extends BlackjackCommand {

	public static final String COMMAND_WORD = "BET";
	
	// STATEFUL: Will hold valid states that this command operates in
	private Set<STATE> validStates = null;

	@Override
	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		
		// Step 0: If either object is null, it's an internal error
		if( protocol == null || cm == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR, 
					"BetCommand.processCommand() received null arguments" ).toString();
		}
		
		// STATEFUL: Steps 1-2: Return an error in not in a valid state for BET command
		if( !getValidStates().contains( protocol.getState()) ) {
			return new ResponseCode( ResponseCode.CODE.INVALID_BET_NOT_EXPECTED,
					"BetCommand.processCommand(): Received out-of-context Bet command").toString();
		}
					
		// Step 3-4: Check syntax
		if ((cm.getParameters() == null || (cm.getParameters().size() != 1 )) )  {		
			return new ResponseCode( ResponseCode.CODE.SYNTAX_ERROR ,
					"Must include a single parameter indicating 'amount'").toString();
		}
					
		// Step 5: Do work that needs doing
		// Start by figuring out what their desired bet is
		Integer desiredBet = null;
		List<String> params = cm.getParameters();
		if( params != null && params.size() == 1 ) {
			try {
				desiredBet = Integer.parseInt( params.get(0) );
			} catch( NumberFormatException e ) {
				// Ah well
			}
		}
		
		// If we couldnt' parse out a number, that's a syntax error
		if( desiredBet == null ) {
			return new ResponseCode( ResponseCode.CODE.SYNTAX_ERROR ,
					"Must include a single numeric parameter indicating 'amount'").toString();
		}
		
		//5.1 Error: Funds are insufficient
		int balance = protocol.getUser().getUserMetadata().getBalance();
		if(desiredBet > balance) {
			return new ResponseCode( ResponseCode.CODE.INVALID_BET_TOO_POOR,
					" Funds in balance insufficient").toString();
		}
		
		//5.2 Error Bet lower than MINBET 
		if(desiredBet < protocol.getUser().getGame().getMetadata().getMinBet()) {
			return new ResponseCode( ResponseCode.CODE.INVALID_BET_OUTSIDE_RANGE ,
					" Bet less than minimum bet allowed").toString();	
		}
					
		//5.3 Error Bet higher than MAXBET
		if(desiredBet > protocol.getUser().getGame().getMetadata().getMaxBet()) {
			return new ResponseCode( ResponseCode.CODE.INVALID_BET_OUTSIDE_RANGE ,
					" Bet is over maximum bet allowed").toString();	
		}
					
		// STATEFUL: 5.4 Success! Need to store the amount on the protocol and update the tsate
		protocol.setBet( desiredBet );
		protocol.setState( STATE.IN_SESSION_BEFORE_YOUR_TURN );
		
		// Success! Now handle all the odds and ends that have to be done when a bet is placed
		protocol.getUser().handlePlacedBet( desiredBet );
		
		// And return a successfully response
		return new ResponseCode( ResponseCode.CODE.SUCCESSFULLY_BET ,
				" Bet Command completed").toString();			
	}
				
	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		
		if( validStates == null ) {
			validStates = new HashSet<STATE>();

			//States where Bet command is allowed
			validStates.add(STATE.IN_SESSION_AWAITING_BETS);
		}
		
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add( "amount" );
		return names;
	}

}
