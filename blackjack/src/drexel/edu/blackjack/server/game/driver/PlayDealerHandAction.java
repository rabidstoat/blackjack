/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ShuffleIfNeededAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This game action looks to see if the dealer has blackjack and, if
 * not, plays out the dealer's hand by hitting and standing as appropriate.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.logging.Logger;

import drexel.edu.blackjack.cards.DealerShoeInterface;
import drexel.edu.blackjack.cards.DealtCard;
import drexel.edu.blackjack.cards.Hand;
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
public class PlayDealerHandAction extends GameAction {

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(PlayDealerHandAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		if( game != null ) {
			GameState state = game.getGameState();
			if( state == null ) {
				LOGGER.severe( "Trying to check for dealer blackjack, but there's no game state..." );
			} else if( state.getDealerHand() == null ) {
				LOGGER.severe( "Trying to check for dealer blackjack, but there's no non-null hand..." );
			} else if( state.getDealerShoe() == null ) {
				LOGGER.severe( "Trying to check for dealer blackjack, but there's no non-null shoe..." );
			} else {
				Hand hand = state.getDealerHand();
				if( !hand.getIsBlackJack() ) {
					playOutDealerHand( hand, state );
				}
				success = true;
			}
		}
		
		return success;
	}

	/**
	 * Play out the dealer hand by hitting on 16, standing on
	 * 17+.
	 * 
	 * @param hand A non-null hand that belongs to the dealer
	 * @param state A non-null state with a non-null shoe
	 */
	private void playOutDealerHand( Hand hand, GameState state ) {
		
		// Just loop around hitting, so long as the dealer should hit on the hand
		DealerShoeInterface shoe = state.getDealerShoe();
		
		// First has to turn over their facedown cards
		for( DealtCard card : hand.getFacedownCards() ) {
			card.changeToFaceUp();
		}
		
		// And notify players about the change
		state.notifyOthersOfUpdatedHand( null, hand );
		 
		// Figure out if the dealer even needs to play. If everyone
		// else busted (or left), the dealer doesn't need to
		boolean dealerNeedsToPlay = doesDealerNeedToPlay( state );

		// Keep hitting until we should hit no more
		while( dealerNeedsToPlay && hand.getDealerShouldHit(null) ) {
			
			// This just pauses a tiny bit, otherwise the dealer makes his
			// play in like 2 ms, and that's just too fast, it looks silly
			pauseAMoment();
			
			// Notify that the dealer decided to hit
			state.notifyOthersOfGameAction( null, GameState.HIT_KEYWORD );
			
			// Deal a single card
			DealtCard card = shoe.dealTopCard();
			card.changeToFaceUp();
			hand.receiveCard(card);
			
			// By calling the set method, it'll notify other users
			state.setDealerHandAndNotify( hand );
		}
		
		// Now here, the dealer either decided to stand, or busted
		// Need to send a message to that effect -- unless all the other
		// places busted, then we don't have to tell anything
		if( hand.getIsBusted() ) {
			state.notifyOthersOfGameAction( null, GameState.BUST_KEYWORD);
		} else if( dealerNeedsToPlay ) {
			state.notifyOthersOfGameAction( null, GameState.STAND_KEYWORD );
		}
	}

	/**
	 * Dealer doesn't need to play if everyone else has
	 * busted or left.
	 * 
	 * @param state Guaranteed non-null
	 * @return True if the dealer needs to play, false
	 * otherwise
	 */
	private boolean doesDealerNeedToPlay(GameState state) {
		
		// Start off assuming he doesn't
		boolean needToPlay = false;
		
		User[] players = state.getCopyOfPlayers();
		if( players != null ) {
			for( User player : players ) {
				Hand hand = player.getHand();
				if( hand != null ) {
					// If at least one player is not busted, dealer
					// has to play.
					if( !hand.getIsBusted() ) {
						needToPlay = true;
					}
				}
			}
		}
		
		return needToPlay;
	}
}
