/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - MessagesFromServerListener.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This defines the interface that observers/listeners must implement,
 * in order to register interest in messages received from the server.
 ******************************************************************************/
package drexel.edu.blackjack.client.in;

import drexel.edu.blackjack.server.ResponseCode;

/**
 * This is an interface that classes which respond to messages
 * from the server need to implement. A class is able to add
 * themselves as a listener for certain response codes. When
 * they do, they get notified when the server sends a response
 * matching that code.
 * 
 * @author Jennifer
 *
 */
public interface MessagesFromServerListener {

	/**
	 * This is what is called when some sort of
	 * response is received from the server.
	 */
	public void receivedMessage( ResponseCode code );
}
