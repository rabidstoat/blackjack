package drexel.edu.blackjack.server.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Command metadata expresses the client input, which
 * is a string, as an object. Commands in the blackjack
 * protocol are single lined, with the first word being
 * the command word and potentially multiple parameters,
 * space-or-tab delineated. Having an object means it
 * only has to be parsed in one place.
 * 
 * @author Jennifer
 *
 */
public class CommandMetadata {
	
	/******************************************************************
	 * Local variables
	 *****************************************************************/
	
	// One of the 12 command words, like 'BET' or 'USERNAME'
	private String commandWord = null;
	
	// Whatever comes after the command word will be parsed out, with one
	// 'token' being a string in the list. For example, if the command string
	// was 'BET 50' then the parameters would be a single string of "50"
	private List<String> parameters = null;

	/******************************************************************
	 * Constructor
	 *****************************************************************/

	/**
	 * Instantiates the metadata based off the command
	 * string received from the client
	 * 
	 * @param commandString What the client sent, terminated
	 * with the CRLF
	 */
	public CommandMetadata( String commandString ) {
		
		// So it's not null
		parameters = new ArrayList<String>();
		
		// Use this to extract whitespace-delineated tokens
		StringTokenizer strtok = new StringTokenizer(commandString);
		if( strtok.hasMoreTokens() ) {
			commandWord = strtok.nextToken();	// command word is always first
		}
		// See if any parameters are left
		while( strtok.hasMoreTokens() ) {
			parameters.add( strtok.nextToken() );
		}
	}

	/******************************************************************
	 * Public methods
	 *****************************************************************/

	/**
	 * @return the commandWord
	 */
	public String getCommandWord() {
		return commandWord;
	}

	/**
	 * @param commandWord the commandWord to set
	 */
	public void setCommandWord(String commandWord) {
		this.commandWord = commandWord;
	}

	/**
	 * @return the parameters
	 */
	public List<String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param parameter the parameter to add
	 */
	public void addParameter( String parameter ) {
		if( parameters == null ) {
			parameters = new ArrayList<String>();
		}
		
		parameters.add( parameter );
	}
}
