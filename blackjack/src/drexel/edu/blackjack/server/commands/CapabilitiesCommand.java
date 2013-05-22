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
	
	/*************************************************************************************
	 * The BlackjackCommand subclasses have a getValidStates() method each, with the 
	 * set of valid states in which the command can be called. 
	 * ***********************************************************************************/
	
	Set<STATE> acntc = new AccountCommand().getValidStates();
	Set<STATE> betc = new BetCommand().getValidStates();
    	Set<STATE> hitc = new HitCommand().getValidStates();
    	Set<STATE> joinc = new JoinSessionCommand().getValidStates();
    	Set<STATE> leavc = new LeaveSessionCommand().getValidStates();
    	Set<STATE> lstGmc = new ListgamesCommand().getValidStates();
    	Set<STATE> pwrdc = new PasswordCommand().getValidStates();
    	Set<STATE> quitc = new QuitCommand().getValidStates();
    	Set<STATE> stndc = new StandCommand().getValidStates();
    	Set<STATE> uknwc = new UnknownCommand().getValidStates();
    	Set<STATE> userc = new UsernameCommand().getValidStates();
    	Set<STATE> versc = new VersionCommand().getValidStates();
    	
    /********************************************************************************/
	
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
				"\n" + this.getCapabilitiesInState( protocol.getState()) ).toString();	
	}
	
    	/**@param clientState the current client state
    	 * @return theCommand the commands allowed in client's current state.*/
    	
		public HashSet<String> getCapabilitiesInState(BlackjackProtocol.STATE clientState) {
		
    		/**This set used to hold the getValidStates() set belonging to a
    		 * specific BlackjackCommand subclass, during iteration. */
    		
    		Set<STATE> aSetOfAllowedStates;
    		
    		/**HashSet theCommand returns the commands allowed in each state*/
    		
    		HashSet<String> theCommands = new HashSet<String>();
    		
    	/**HashSet commandAllGetValidStates holds a set of all 'getValidStates() 
    	* methods of all commands */
		
		HashSet<Set<STATE>> commandAllGetValidStates = new HashSet<Set<STATE>>();
		
		commandAllGetValidStates.add( acntc );
		commandAllGetValidStates.add( betc );
		commandAllGetValidStates.add( hitc );
		commandAllGetValidStates.add( joinc );
		commandAllGetValidStates.add( leavc );
		commandAllGetValidStates.add( lstGmc );
		commandAllGetValidStates.add( pwrdc );
		commandAllGetValidStates.add( quitc );
		commandAllGetValidStates.add( stndc );
		commandAllGetValidStates.add( uknwc );
		commandAllGetValidStates.add( userc );
		commandAllGetValidStates.add( versc );
		
		Iterator<Set<STATE>> itr1 = commandAllGetValidStates.iterator();
		
		while( itr1.hasNext() ) {
			
			aSetOfAllowedStates = itr1.next();
		
			/**Iterate through this set of states in the getValidStates() returned set;
			 * If one of these states -from this command's getValidStates() set-
			 *  is equal to the client state then add this command to the hash set. 
			 *  
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
			theCommands.add(this.toString());		
		}
	}
		return theCommands;
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


