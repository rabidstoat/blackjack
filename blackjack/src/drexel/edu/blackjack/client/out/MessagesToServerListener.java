/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - MessagesToServerListener.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This defines the interface that observers/listeners must implement,
 * in order to register interest in messages being sent to the server.
 ******************************************************************************/
package drexel.edu.blackjack.client.out;

/**
 * This is an interface that classes which respond to messages
 * destined to the server need to implement. A class is able to add
 * themselves as a listener for these messages. When
 * they do, they get notified when a message is sent to the server.
 * 
 * @author Jennifer
 *
 */
public interface MessagesToServerListener {

	/**
	 * This is what is called when some sort of
	 * message is being sent to the server.
	 */
	public void sendingToServer( String message );
}
