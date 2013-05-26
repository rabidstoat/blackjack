/*******************************************************************************
 * Functionality for the server 'playing' blackjack
 * <P>
 * CS544 Computer Networks Spring 2013
 * <P>
 * 5/26/2013
 * <P>
 * <b>Purpose:</b> This package has code that lets the server 'play' a single
 * blackjack game. Each game has its own thread, and a driver that cycles 
 * through the various stages of gameplay. The thread starts when the first
 * user joins, and terminates when the last user left; if a user returns, the
 * thread is re-created and re-started. The stages of gameplay are represented
 * as commands that operate on the Game object in a looping, ordered sequence.
 * @author Jennifer Lautenschlager
 * @author Constantine Lazarakis
 * @author Carol Greco
 * @author Duc Anh Nguyen
 * @version 1.0
 ******************************************************************************/
package drexel.edu.blackjack.server.game.driver;