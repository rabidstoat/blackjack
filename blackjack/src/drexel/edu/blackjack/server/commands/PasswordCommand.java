package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;
public class PasswordCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "PASSWORD";
	
	Set<STATE> validPasswordStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		
		//Step 0: If either object is null, it's an internal error
		if (protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"PasswordCommand.processCommand() received null arguments").toString();	
		}
		
		// Step 1-2: Return an error if not in valid state for PASSWORD command
		if(!getValidStates().contains( protocol.getState()) ) {
			return new ResponseCode( ResponseCode.CODE.NOT_EXPECTING_PASSWORD,
					"PasswordCommand.processCommand() received out-of-context command").toString();
		}
		
		// TODO: Steps 0-2 look right, but you need to implement steps 3-8 instead of 
		// calling super.processCommand().
			return super.processCommand(protocol, cm);
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		validPasswordStates = new HashSet<STATE>();
		
		//Add the only allowed state which is WAITING_FOR_PASSWORD
		validPasswordStates.add(STATE.WAITING_FOR_PASSWORD);
		
		return validPasswordStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
	
		// TODO: See my comment on the username command. It's the same here, but
		// the PASSWORD command takes a single parameter, and its name is
		// password. 
		ArrayList<String> requiredParameterNames = new ArrayList<String>();
		
		//Parse the PASSWORD command and enter the parameter tokens in List.
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

}
