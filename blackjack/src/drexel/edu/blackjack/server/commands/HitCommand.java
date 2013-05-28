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

import drexel.edu.blackjack.cards.DealerShoeInterface;
import drexel.edu.blackjack.cards.DealtCard;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.server.game.User;
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

	public static final String COMMAND_WORD = "HIT";

	// STATEFUL: Set of states in which th command is valid
	private Set<STATE> validStates = null;

	public String processCommand(BlackjackProtocol protocol, CommandMetadata cm) {
		
		if (protocol == null || cm == null) { 
			return new ResponseCode(ResponseCode.CODE.INTERNAL_ERROR).toString();
		}

		// STATEFUL: Make sure it's their turn
		if (protocol.getState() != STATE.IN_SESSION_AND_YOUR_TURN) {
			return new ResponseCode(ResponseCode.CODE.NOT_EXPECTING_HIT).toString();
		}
		
		// We better have a user object
		User user = protocol.getUser();
		if( user == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"HitCommand.processCommand() had a problem getting the user object").toString();
		}
		
		// Note on the user object that they made their gameplay
		user.setNeedsToMakeAPlay( false );
		
		// Better have a game state
		GameState state = (user.getGame() == null ? null : user.getGame().getGameState());
		if( state == null ) {
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"HitCommand.processCommand() had a problem getting the game state object").toString();
		}
		
		// Because we need to notify others that the player chose to hit
		state.notifyOthersOfGameAction(user, GameState.HIT_KEYWORD );
		
		// Now we better have a dealer shoe
		DealerShoeInterface shoe =state.getDealerShoe();
		if( shoe == null ){
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"HitCommand.processCommand() had a problem getting the dealer shoe").toString();
		}
		
		// And a player's hand
		if( user.getHand() == null ){
			return new ResponseCode( ResponseCode.CODE.INTERNAL_ERROR,
					"HitCommand.processCommand() had a problem getting the user's hand").toString();
		}
		
		// At last! We can deal a card, set it face up, and add it to the user's hand
		DealtCard card = shoe.dealTopCard();
		card.changeToFaceUp();
		user.getHand().receiveCard(card);
		
		// Notify everyone about this new hand
		state.notifyAllOfNewCards( user );

		// Assume they didn't bust, and set the parameter to the card dealt
		ResponseCode code = new ResponseCode( ResponseCode.CODE.SUCCESSFULLY_HIT, card.toString() );
		if( user.getHand().getIsBusted() ) {
			// If they busted, that's a different, sadder code
			code = new ResponseCode( ResponseCode.CODE.USER_BUSTED, card.toString() );
			
			// We have to tell everyone about the player busting, too
			state.notifyOthersOfGameAction(user, GameState.BUST_KEYWORD );
			
			// Mark the user that it's the end of their gameplay for the round
			user.setHasFinishedGamePlayThisRound( true );
			
			// STATEFUL: And, finally, change the state, as it's no longer their turn
			protocol.setState( STATE.IN_SESSION_AFTER_YOUR_TURN );
		}
		
		// Finally we can return
		return code.toString();
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
