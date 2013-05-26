/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ListgamesCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class implements the LISTGAMES command. It uses a singleton
 * instance of the ActiveGameCoordinator to get a list of games, and relies on
 * a helper method in the Game object for what is, in essence, serializing 
 * game information for the response.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.game.ActiveGameCoordinator;
import drexel.edu.blackjack.server.game.Game;

public class ListgamesCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "LISTGAMES";

	private Set<STATE> validStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		//Step 0: If either object is null, it's an internal error
		if (protocol == null || cm == null) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
				"ListgamesCommand.processCommand() received null arguments").toString();
		}
		
		// Steps 1-2: Return an error in not in a valid state
		if( !getValidStates().contains( protocol.getState()) ) {
			return new ResponseCode( ResponseCode.CODE.NEED_TO_BE_AUTHENTICATED ).toString();
		}
		
		// Step 3-4: Check syntax; irrelevant as no parameters
		
		// Step 5: Do work that needs doing
		// Just quick and dirty for testing, get all games
		ActiveGameCoordinator coordinator = ActiveGameCoordinator.getDefaultActiveGameCoordinator();
		Set<Game> games = coordinator.getAllGames();
		
		// Set up something to return
		StringBuilder str = new StringBuilder( (games == null ? 0 : games.size()) + " games follow:\n");
		if( games != null ) {
			for( Game game : games ) {
				str.append( game.getGameDescriptor() );
			}
		}
		
		// Step 6: Save out state variables? There are none
		// Step 7: Update any change in state? There is none
		
		// Step 8: Format the user response code
		ResponseCode code = new ResponseCode( ResponseCode.CODE.GAMES_FOLLOW, str.toString() );
		return code.toString();
	}

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public Set<STATE> getValidStates() {
		if( validStates == null ) {
			// Valid if authorized and connected
			validStates = new HashSet<STATE>();
			validStates.add( STATE.IN_SESSION_AFTER_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );
			validStates.add( STATE.IN_SESSION_AS_OBSERVER );
			validStates.add( STATE.IN_SESSION_AWAITING_BETS );
			validStates.add( STATE.IN_SESSION_BEFORE_YOUR_TURN  );
			validStates.add( STATE.IN_SESSION_DEALER_BLACKJACK );
			validStates.add( STATE.IN_SESSION_SERVER_PROCESSING );
			validStates.add( STATE.NOT_IN_SESSION );
		}
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		// TODO Auto-generated method stud
		return null;
	}

}
