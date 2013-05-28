/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ShuffleIfNeededAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This game action checks to see if the dealer has a blackjack. If it
 * does, the appropriate notification is sent out. It returns true regardless
 * as to the state of dealer blackjack! Remember, the doAction() returns if 
 * the check was successful, not what the result was.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.logging.Logger;

import drexel.edu.blackjack.cards.DealtCard;
import drexel.edu.blackjack.cards.Hand;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This action checks and sees if the dealer has blackjack.
 * 
 * @author Jennifer
 *
 */
public class CheckForDealerBlackjackAction extends GameAction {

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(CheckForDealerBlackjackAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		if( game != null ) {
			GameState state = game.getGameState();
			if( state == null ) {
				LOGGER.severe( "Trying to check for dealer blackjack, but there's no game state..." );
			} else if( state.getDealerHand() == null ) {
				LOGGER.severe( "Trying to check for dealer blackjack, but there's no non-null hand..." );
			} else {
				Hand hand = state.getDealerHand();
				if( hand.getIsBlackJack() ) {
					state.notifyPlayersOfDealerBlackjack();
					// Flip over the card to reveal it
					for( DealtCard card : hand.getFacedownCards() ) {
						card.changeToFaceUp();
					}
					// And notify players about the change so they see too
					state.notifyOthersOfUpdatedHand( null, hand );
				}
				success = true;
			}
		}
		
		return success;
	}

}
