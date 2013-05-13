package drexel.edu.blackjack.server.game;

/**
 * Used to pass game state information from
 * the server to client, to show what bets
 * are made, what cards are dealt, what users
 * are joining and leaving, etc.
 * 
 * See Section 2.15 of the protocol design
 * for details.
 * 
 * @author Jennifer
 *
 */
public class GameState {
  
  AWAITING_BIDS,  //Awaiting Bids
	DEALING_CARDS,	//Dealing Cards
	IN_PLAY,		//In Play
	SETTING_BETS;	//Settling Bets

}
