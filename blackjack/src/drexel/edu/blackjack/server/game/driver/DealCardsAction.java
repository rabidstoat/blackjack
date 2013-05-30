/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ShuffleIfNeededAction.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This game action deals cards to the players, sending out 
 * notifications of all the hands (including the dealers) to all the players.
 * Any shuffling needed gets notified as well.
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import drexel.edu.blackjack.cards.DealerShoeInterface;
import drexel.edu.blackjack.cards.DealtCard;
import drexel.edu.blackjack.cards.Hand;
import drexel.edu.blackjack.cards.Card.RANK;
import drexel.edu.blackjack.cards.Card.SUIT;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.server.game.User;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This action deals cards to the dealer, and any
 * active players in the game.
 * 
 * @author Jennifer
 *
 */
public class DealCardsAction extends GameAction {

	// And of course our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(DealCardsAction.class.getName());

	@Override
	public boolean doAction(Game game) {
		
		boolean success = false;
		
		if( game != null ) {
			GameState state = game.getGameState();
			if( state == null ) {
				LOGGER.severe( "Trying to deal cards, but there's no game state..." );
			} else {
				// Get the list of users
				User[] users = state.getCopyOfPlayers();
				if( users != null ) {
					// Then active users
					List<User> activeUsers = new ArrayList<User>();
					for( User user : users ) {
						if( user != null && user.getStatus() != null && 
								user.getStatus() == GameState.STATUS.ACTIVE ) {
							activeUsers.add( user );
						}
					}
					// IF there are no active players we automatically succeed,
					// and don't even have to deal out the dealer's cards
					if( activeUsers.size() == 0 ) {
						success = true;
					} else {
						// Otherwise, we have to deal cards
						success = dealCardsToActivePlayers( activeUsers, state );
					}
				}
			}
		}
		
		return success;
	}

	/**
	 * Deals cards (virtually) to active players in the given
	 * game state. Also sends notification about the cards
	 * being dealt.
	 * 
	 * @param users The list of active users who need cards dealt,
	 * guaranteed to be non-null and active
	 * @param state The game state that has, amongst other things,
	 * the dealer shoe. Guaranteed to be non-null
	 * @return True if the action succeeded, false otherwise
	 */
	private boolean dealCardsToActivePlayers(List<User> activeUsers, GameState state) {
		boolean success = true;
		
		// First, make sure we have a game shoe
		DealerShoeInterface shoe = state.getDealerShoe();
		if( shoe == null ) {
			success = false;
		} else {
			
			// Deal to each of the players
			for( User user : activeUsers ) {
				Hand hand = null;
				// TEST: If you comment this out, and create a player with a username of
				// user1, they will always get dealt a blackjack.
				//if( user.getUserMetadata().getUsername().equals("user1" ) ) {
				//	hand = dealBlackjack( user );
				//} else {
					hand = dealHand( user, state );
				//}
				if( hand == null ) {
					success = false;
				} else {
					// Setting the user's hand with this method will
					// automatically send out notifications to others 
					// in the same game session
					user.setHandAndNotify(hand);
				}
			}
			
			// And one hand for the dealer
			Hand hand = dealHand( null, state );
			success = success && state.setDealerHandAndNotify( hand );
		}
		
		
		return success;
	}

	/**
	 * Deals cards to a user. For blackjack, this is a facedown
	 * and a faceup card, in that order. 
	 * 
	 * @param user The user to deal for, or null if it's the
	 * dealer
	 * @param state The game state which has a reference to the
	 * dealer's shoe, from which cards are dealt
	 * @return The hand that was dealt, or null if there was any
	 * sort of proble
	 */
	private Hand dealHand(User user, GameState state) {
		
		Hand hand = new Hand(user);
		
		// First card is facedown
		hand.receiveCard( shuffleIfNeededAndDealCard( state ) );
		
		// Second card is faceup
		DealtCard card = shuffleIfNeededAndDealCard( state );
		if( card != null ) {
			card.changeToFaceUp();
			hand.receiveCard( card );
		}
		
		// If there aren't two cards in the hand, there must
		// have been a problem if some point, so set the hand
		// to null to signify this
		if( hand.getTotalNumberOfCards() != 2 ) {
			hand = null;
		}
		
		return hand;
	}

	/**
	 * For testing only
	 */
	private Hand dealBlackjack( User player ) {
		Hand hand = new Hand(player);
		hand.receiveCard( new DealtCard( RANK.ACE, SUIT.DIAMONDS) );
		hand.receiveCard( new DealtCard( RANK.QUEEN, SUIT.HEARTS) );
		return hand;
	}

}
