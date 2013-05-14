package drexel.edu.blackjack.server.commands;

import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

public class UsernameCommand extends BlackjackCommand {
	
	private static final String COMMAND_WORD = "USERNAME";
	// Takes command words, like 'BET' or 'USERNAME'
	private String commandWord = null;

	/**Check to see if in a state (get it from protocol object) 
		 * where this command is valid; if not, send error message*/
	@Override
	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
	String stateWord = protocol.getState().toString(); 
			
			//Compare to only allowed state for USERNAME command "WAITING_FOR_USERNAME"; send error message if not equal.	
			if(!stateWord.equals("WAITING_FOR_USERNAME")) {
				System.out.println("404 USERNAME is not expected/allowed in the " +  stateWord + " state");
			}
			return super.processCommand(protocol, cm);	
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	/**
	 * This returns a set of states that the command can validly be used in.
	 * The CAPABILITIES command will use this to get a list of capabilities
	 * for the current protocol state to return
	 **/
	@Override
	public Set<STATE> getValidStates() {
			STATE WAITING_FOR_USERNAME = null;
				
				Set<STATE> validUsernameStates = new HashSet<STATE>();
				
				//Add the only allowed state which is WAITING_FOR_USERNAME
				validUsernameStates.add(WAITING_FOR_USERNAME);
				
				return validUsernameStates;
			}
	}
	
	/**
	* This returns a list of parameters that the command requires.
	* For USERNAME there is a 'username' parameter
	* that the command needs. The CAPABILITIES command makes use of this
	* for sending the capabilities list
	* 
	* @return
	*/
		 
	@Override
	public List<String> getRequiredParameterNames() {
		ArrayList<String> requiredParameterNames = new ArrayList<String>();
			
			//Parse the USERNAME command and enter the parameter tokens in List.
			// Use this to extract whitespace-delineated tokens
			
			StringTokenizer strtok = new StringTokenizer(COMMAND_WORD);
			if( strtok.hasMoreTokens() ) {
				commandWord = strtok.nextToken();	// command word is always first
			}
			// See if any parameters are left
			while( strtok.hasMoreTokens() ) {
				requiredParameterNames.add( strtok.nextToken() );
			}
			return requiredParameterNames;
		}

}
