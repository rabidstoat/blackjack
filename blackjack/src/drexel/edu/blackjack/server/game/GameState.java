package drexel.edu.blackjack.server.game;

import java.util.List;

import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.ResponseCode;

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

	/**
	 * Need to send out messages to the remaining players (if any) about
	 * how one of the players left the session.
	 * 
	 * @param remainingPlayers
	 * @param departedPlayer
	 */
	public static void notifyOthersOfDepartedPlayer(List<User> remainingPlayers,
			User departedPlayer) {
		
		// They can't be null, that's bad
		if( departedPlayer != null && remainingPlayers != null ) {
			
			// Need to formulate the message that we would send
			// TODO: This isn't the right format
			UserMetadata metadata = departedPlayer.getUserMetadata();
			String updateString = (metadata == null ? "Some User" : metadata.getFullname() ) + " left the game.";

			// And then send it to all the remaining players; better synchronize
			synchronized( remainingPlayers ) {
				for( User remainingPlayer : remainingPlayers ) {
					ResponseCode code = new ResponseCode( ResponseCode.CODE.INFORMATIVE_MESSAGE, updateString );
					remainingPlayer.sendMessage( code );
				}
			}
		}
	}
	
	/**
	 * Need to send out messages to the other players (if any) about
	 * how some new player has joined the session.
	 * 
	 * @param otherPlayers
	 * @param newPlayer
	 */
	public static void notifyOthersOfJoinedPlayer(List<User> otherPlayers,
			User newPlayer) {
		
		// They can't be null, that's bad
		if( newPlayer != null && otherPlayers != null ) {
			
			// Need to formulate the message that we would send
			// TODO: This isn't the right format
			UserMetadata metadata = newPlayer.getUserMetadata();
			String updateString = (metadata == null ? "?Some User?" : metadata.getFullname() ) + " joined the game.";

			// And then send it to all the remaining players
			synchronized( otherPlayers ) {
				for( User remainingPlayer : otherPlayers ) {
					ResponseCode code = new ResponseCode( ResponseCode.CODE.INFORMATIVE_MESSAGE, updateString );
					remainingPlayer.sendMessage( code );
				}
			}
		}
	}	

}
