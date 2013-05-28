/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ShuffleIfNeededAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This game action is done at the very end of the game. For remaining
 * players, it looks to see the state of their hand, comparing it to the
 * dealer's hand, and decides who won and who lost. Accounts are credited if
 * needed.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import drexel.edu.blackjack.cards.Hand;
import drexel.edu.blackjack.cards.Hand.COMPARISON_RESULT;
import drexel.edu.blackjack.db.user.FlatfileUserManager;
import drexel.edu.blackjack.db.user.UserManagerInterface;
import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.server.game.User;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * Plays out the dealer's hand. As per the rules of blackjack
 * the dealer hits on 16, and stands on 17 or higher.
 * 
 * @author Jennifer
 */
public class FigureOutResultsAction extends GameAction {

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(FigureOutResultsAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		if( game != null ) {
			GameState state = game.getGameState();
			if( state == null ) {
				LOGGER.severe( "Trying to tally results, but there's no game state..." );
			} else if( state.getDealerHand() == null ) {
				LOGGER.severe( "Trying to tally results, but there's no non-null hand..." );
			} else {
				// Get a list of the active players
				User[] users = state.getCopyOfPlayers();
				List<User> activePlayers = new ArrayList<User>();
				for( User user : users ) {
					if( user.isActive() ) {
						activePlayers.add( user );
					}
				}
				
				// And process their results, one by one
				for( User player : activePlayers ) {
					computeResults( player, state );
				}
				
				success = true;
			}
		}
		
		return success;
	}

	/**
	 * Calculate if they won, lost, or tied. Send out the 
	 * message for that. Finally, update and save the user's 
	 * bank account in the event of tis or wins
	 * 
	 * @param user A non-null user
	 * @param state A non-null game state with a non-null dealer
	 * hand
	 */
	private void computeResults( User user, GameState state ) {

		// Figure out the user's hand, and the dealer's hand
		Hand playerHand = user.getHand();
		Hand dealerHand = state.getDealerHand();
		
		if( playerHand == null ) {
			UserMetadata metadata = user.getUserMetadata();
			LOGGER.severe( "Can't compute the results for " + 
					(metadata == null ? "user with null metadata" : metadata.getUsername()) + 
					" because their hand is null!" );
		} else if (dealerHand == null ) {
			LOGGER.severe( "Encountered a null dealer hand when calculating result!" );
		} else {
			Hand.COMPARISON_RESULT result = playerHand.compareToDealerHand( dealerHand );
			state.notifyAllOfGameOutcome( user, result );
			
			// IF it's a win or tie, then the user's account has to be modified, based
			// on their original bet. Tie refunds the bet amount. A win takes double the
			// amount and puts it in the account
			if( result != COMPARISON_RESULT.LOSE ) {
				
				// For handling the account deposits
				UserManagerInterface manager = FlatfileUserManager.getDefaultUserManager();
				
				// ANd need to know their original bet
				Integer bet = user.getBet();
				UserMetadata metadata = user.getUserMetadata();
				if( manager == null ) {
					LOGGER.severe( "Can't credit the user's account because the user manager interface is null?" );
				} else if( bet == null ) {
					LOGGER.severe( "Can't credit the user's account because their bet was null?" );
				} else if( metadata == null ) {
					LOGGER.severe( "Can't credit the user's account because their metadata was null?" );
				} else if( result == COMPARISON_RESULT.TIE ) {
					// THey get back their original bet
					int originalAccount = metadata.getBalance();
					metadata.setBalance( originalAccount + bet );
					manager.save();
				} else {
					// THey get back twice their original bet
					int originalAccount = metadata.getBalance();
					metadata.setBalance( originalAccount + (2*bet) );
					manager.save();
				}
			}
		}

	}

}
