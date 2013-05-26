/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - HitCommand.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class handles the HIT protocol method, by alerting the game-
 * playing thread in the server about the action.
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import drexel.edu.blackjack.cards.DealtCard;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * <b>STATEFUL:</b> Implements the logic needed to respond to 
 * the HIT command from a client. Like all command classes,
 * it uses the protocol state to determine if it's in a valid
 * state. It also checks the protocol stateful variable to see
 * what associated user is making the account request.
 * 
 * @author Duc
 */
public class HitCommand extends BlackjackCommand {

	private static final String COMMAND_WORD = "HIT";

	// STATEFUL: Set of states in which th command is valid
	private Set<STATE> validStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		if (protocol == null || cm == null) { 
			return new ResponseCode(ResponseCode.CODE.INTERNAL_ERROR).toString();
		}
		if (cm.getParameters() != null && cm.getParameters().size() != 0) {
			return new ResponseCode(ResponseCode.CODE.SYNTAX_ERROR).toString();
		}
		// STATEFUL: Make sure it's their turn
		if (protocol.getState() != STATE.IN_SESSION_AND_YOUR_TURN) {
			return new ResponseCode(ResponseCode.CODE.NOT_EXPECTING_HIT).toString();
		}
		DealtCard c = protocol.getUser().getGame().getGameState().getDealerShoe().dealTopCard();
		if (protocol.getUser().getHand().getFaceupCards().size() == 0) {
			c.changeToFaceUp();
		}
		protocol.getUser().getHand().receiveCard(c);
		
		// TODO: Not sure the hit/stand logic goes here, versus in the game
		// controller, I think it should just call a method on the game
		// controller to handle. In any event, this wouldn't work as it
		// doesn't check to see if the player busts, and return an error
		// code (and make a state transition) if so
		return new ResponseCode(ResponseCode.CODE.SUCCESSFULLY_HIT).toString();
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
			
			// Needs to be awaiting your turn to use this command
			validStates.add( STATE.IN_SESSION_AND_YOUR_TURN );			
		}
		return validStates;
	}

	@Override
	public List<String> getRequiredParameterNames() {
		return null;
	}

}
