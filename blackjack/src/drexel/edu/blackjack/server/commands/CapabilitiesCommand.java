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
				"UsernameCommand.processCommand() List of capabilities allowed in this state \n" 
				+ this.getCapabilitiesInState( protocol.getState()) ).toString();	
	}
	
	
	/**This method returns a list of Capabilities for the state client is in currently.
	 * 1. It checks the state client is in currently by passing a BlackjackProtocol.STATE
	 * parameter to the method.
	 * 2. It uses an enum switch statement to list minimal (MUST) commands and other commands
	 * allowed in each state.
	 * 3. It returns the commands allowed in the state the client is curently.
	 * 4. Default state is the minimal (MUST) list as per BJP,v_1.0 p.31.   
	 * @param clientState The state client is in currently.
	 * @return capabilitiesList The list of all allowed commands in client state is in.*/
	
	public StringBuilder getCapabilitiesInState(BlackjackProtocol.STATE clientState) {
		
		StringBuilder capabilitiesList = new StringBuilder();
		
		STATE state = clientState;
		
		String stateName = state.name();
		state = Enum.valueOf(STATE.class, stateName.toUpperCase());
		
		switch ( state ) {
		
		/**
		 * Abbreviations of command names:
		 * AccountCommand acntc
		 * BetCommand betc  
		 * CommandMetadata metac
		 * HitCommand hitc
		 * JoinSessionCommand joinc
		 * LeaveSessionCommand leavc 
		 * ListgamesCommand lstGc
		 * PasswordCommand pwrdc
		 * QuitCommand quitc
		 * StandCommand stndc
		 * UnknownCommand uknwc 
		 * UsernameCommand userc
		 * VersionCommand versc 
		 */
 
		case WAITING_FOR_USERNAME: 
			
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );		
			capabilitiesList.append( quitc.getCommandWord() );
			
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( userc.getCommandWord() );
			capabilitiesList.append( "\n" );
		
		case WAITING_FOR_PASSWORD:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( userc.getCommandWord() );
			capabilitiesList.append( pwrdc.getCommandWord() );
			capabilitiesList.append( "\n" );
		
		case NOT_IN_SESSION:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( acntc.getCommandWord() );
			capabilitiesList.append( lstGmc.getCommandWord() );
			capabilitiesList.append( joinc.getCommandWord() );			
			capabilitiesList.append( "\n" );
			
		case IN_SESSION_AS_OBSERVER:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( joinc.getCommandWord() );
			capabilitiesList.append( leavc.getCommandWord() );
			capabilitiesList.append( "\n" );
			
		case IN_SESSION_AWAITING_BETS:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( betc.getCommandWord() );
			capabilitiesList.append( leavc.getCommandWord() );
			capabilitiesList.append( "\n" );
			
		case IN_SESSION_BEFORE_YOUR_TURN:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( leavc.getCommandWord() );
			capabilitiesList.append( "\n" );
			
		case IN_SESSION_AND_YOUR_TURN:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( stndc.getCommandWord() );
			capabilitiesList.append( hitc.getCommandWord());
			capabilitiesList.append( leavc.getCommandWord() );
			capabilitiesList.append( "\n" );
			
		case IN_SESSION_DEALER_BLACKJACK:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( leavc.getCommandWord() );
			capabilitiesList.append( "\n" );
			
		case IN_SESSION_AFTER_YOUR_TURN:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( leavc.getCommandWord() );
			capabilitiesList.append( "\n" );
			break;
			
		case IN_SESSION_SERVER_PROCESSING:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
			//Additional Capabilities listed allowed in specific state client is in currently
			capabilitiesList.append( leavc.getCommandWord() );
			capabilitiesList.append( "\n" );
			break;
		
		default:
			//Minimum capabilities which MUST be listed
			capabilitiesList.append("101 Capability List follows \n");
			capabilitiesList.append( versc.getCommandWord() );
			capabilitiesList.append( this.getCommandWord() );
			capabilitiesList.append( quitc.getCommandWord() );
			
		}
		
		return capabilitiesList; 
	
	
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
	
	/*************************************************************************************
	 * The Commands
	 * ***********************************************************************************/
	
	AccountCommand acntc = new AccountCommand();
	BetCommand betc = new BetCommand();
    	CommandMetadata metac = new CommandMetadata();
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
	
	
	
	
	

}


