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
