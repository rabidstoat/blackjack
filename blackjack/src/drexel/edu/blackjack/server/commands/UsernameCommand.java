package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;

public class UsernameCommand extends BlackjackCommand {
	
	private static final String COMMAND_WORD = "USERNAME";

	Set<STATE> validUsernameStates = null;
	
	
	
		/**Check to see if in a state (get it from protocol object) 
		 * where this command is valid; if not, send error message*/
		public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
			
			//Step 0: If either object is null, it's an internal error
			if (protocol == null || cm == null) {
				return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"UsernameCommand.processCommand() received null arguments").toString();
			}
			
			// Step 1-2: Return an error if not in valid state for USERNAME command
			if(!getValidStates().contains( protocol.getState()) ) {
				return new ResponseCode( ResponseCode.CODE.NOT_EXPECTING_USERNAME,
					"UsernameCommand.processCommand() received out-of-context command").toString();
			}
			return super.processCommand(protocol, cm);	
		}


		/**
		 * This returns a set of states that the command can validly be used in.
		 * The CAPABILITIES command will use this to get a list of capabilities
		 * for the current protocol state to return
		 **/	

		public Set<STATE> getValidStates() {
				
				validUsernameStates = new HashSet<STATE>();
				
				//Add the only allowed state which is WAITING_FOR_USERNAME
				validUsernameStates.add(STATE.WAITING_FOR_USERNAME);
				
				
				return validUsernameStates;
		}
		
		/**
		 * This returns a list of parameters that the command requires.
		 * For USERNAME there is a 'username' parameter
		 * that the command needs. The CAPABILITIES command makes use of this
		 * for sending the capabilities list
		 * 
		 * @return
		 */
		public ArrayList<String> getRequiredParameterNames() {
			
			ArrayList<String> requiredParameterNames = new ArrayList<String>();
			
			//Parse the USERNAME command and enter the parameter tokens in List.
			// Use this to extract whitespace-delineated tokens
			
			StringTokenizer strtok = new StringTokenizer(COMMAND_WORD);
			if( strtok.hasMoreTokens() ) {
				String commandWord = strtok.nextToken();	// command word is always first
			}
			// See if any parameters are left
			while( strtok.hasMoreTokens() ) {
				requiredParameterNames.add( strtok.nextToken() );
			}
			return requiredParameterNames;
		}


		@Override
		public String getCommandWord() {
			return COMMAND_WORD;
		}
		
}

