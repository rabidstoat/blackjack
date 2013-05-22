package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
				"UsernameCommand.processCommand() received null arguments").toString();
		}

		/**Step 1-2: Return an error if not in valid state for CAPABILITIES command:
		 * These steps are ignored in this command; a client may execute this command
		 * at any state in the protocol, including the non-authenticated state.  */
		
		/**Step 3-4* /
		
		
		/**Step 7: Update state: Send client back to the state client is currently */
		
		protocol.setState(protocol.getState());
		
		
		/**Step 8: Generate a response listing the capabilities allowed.*/
		
		return 	new ResponseCode( ResponseCode.CODE.CAPABILITIES_FOLLOW,
				"UsernameCommand.processCommand() List of capabilities allowed in state: " +
				protocol.getState() + "\n" + this.getCapabilitiesInState( protocol.getState()) ).toString();	
	}
	
	
	/**This method returns a list of Capabilities for the state, client is in currently.
	 * 1. It checks the state client is in currently by passing a BlackjackProtocol.STATE
	 * parameter to the method.
	 * 2. It uses an enum switch statement to list minimal (MUST) commands and other commands
	 * allowed in each state.
	 * 3. It returns the commands allowed in the state the client is curently.
	 * 4. Default state is the minimal (MUST) list as per BJP,v_1.0 p.31.   
	 * @param clientState The state client is in currently.
	 * @return capabilitiesList The list of all allowed commands in client state is in.*/
	
	/*************************************************************************************
	 * The Commands
	 * ***********************************************************************************/
	
	AccountCommand acntc = new AccountCommand();
	BetCommand betc = new BetCommand();
    	HitCommand hitc = new HitCommand();
    	JoinSessionCommand joinc = new JoinSessionCommand();
    	LeaveSessionCommand leavc = new LeaveSessionCommand();
    	ListgamesCommand lstGmc = new ListgamesCommand();
    	PasswordCommand pwrdc = new PasswordCommand();
    	QuitCommand quitc = new QuitCommand();
    	StandCommand stndc = new StandCommand();
    	UnknownCommand uknwc = new UnknownCommand();
    	UsernameCommand userc = new UsernameCommand();
    	VersionCommand versc = new VersionCommand();
	
	
    	/**@param the current client
    	 * @return theCommand the commands allowed in client's current state.*/
    	public HashSet<String> getCapabilitiesInState(BlackjackProtocol.STATE clientState) {
		
    		/**This set holds the next getValidStates() set belonging to a
    		 * specific BlackjackCommand subclass. */
    		
    		Set<STATE> aSetOfAllowedStates;
    		
    		/**The hash set returns the commands allowed in each state*/
    		
    		HashSet<String> theCommand = new HashSet<String>();
    		
    		/**This array holds a List of all 'getValidStates() methods of all commands  */
		
		ArrayList<Set<STATE>> commandAllGetValidStates = new ArrayList<Set<STATE>>();
		
		commandAllGetValidStates.add( acntc.getValidStates() );
		commandAllGetValidStates.add( betc.getValidStates() );
		commandAllGetValidStates.add( hitc.getValidStates() );
		commandAllGetValidStates.add( joinc.getValidStates() );
		commandAllGetValidStates.add( leavc.getValidStates() );
		commandAllGetValidStates.add( lstGmc.getValidStates() );
		commandAllGetValidStates.add( pwrdc.getValidStates() );
		commandAllGetValidStates.add( quitc.getValidStates() );
		commandAllGetValidStates.add( stndc.getValidStates() );
		commandAllGetValidStates.add( uknwc.getValidStates() );
		commandAllGetValidStates.add( userc.getValidStates() );
		commandAllGetValidStates.add( versc.getValidStates() );
		
		Iterator<Set<STATE>> itr1 = commandAllGetValidStates.iterator();
		
		while( itr1.hasNext() ) {
			
			aSetOfAllowedStates = itr1.next();
		
			/**Now iterate through this set of states in this getValidStates() returned set;
			 * If one of these states -from this command's getValidStates() set-
			 *  is equal to the client state then add this command to the hash set. 
			 *  Iterate through the whole List of getValidStates() methods
			 *  from all commands and find which commands are allowed in this state. Add then
			 *  finally, return the commands allowed in the state client is in.
			 **/
			
			//String theState takes the next state value contained in allowedCommandStates set.
			String theState;
		
			Iterator<STATE> itr2 = aSetOfAllowedStates.iterator();
			while( itr2.hasNext() ) {
			theState = itr2.next().toString();
			
			//The string builder that will add the command if one of the states-allowed found is equal
			//to the clientState passed in to the method.
			
			
			int compareClientState = clientState.toString().compareTo(theState.toString());
			
			//If the strings are not equal move to next state in set and compare.
			if(!(compareClientState == 0) ) {
				itr2.next();
				
			}
			//else add this command to the string builder to be returned.
			theCommand.add(this.toString());		
		}
	}
		return theCommand;
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
	 * @return
	 */
	@Override
	public ArrayList<String> getRequiredParameterNames() {

		//Command word CAPABILITIES has no parameters; hence, this method does apply.
		
		return null;
	}
}


