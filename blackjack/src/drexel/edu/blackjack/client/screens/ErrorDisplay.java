package drexel.edu.blackjack.client.screens;

import drexel.edu.blackjack.client.in.MessagesFromServerListener;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This is the 'screen' that is used to display something for messages
 * that no other 'screen' is signed up to handle. Really, everything
 * should be signed up somewhere. But when things are under development,
 * that might not be the case.
 * 
 * All it does is display the raw protocol message.
 * 
 * @author Jennifer
 *
 */
public class ErrorDisplay implements MessagesFromServerListener {

	@Override
	public void receivedMessage(ResponseCode code) {
		System.out.println( "DefaultScreen> " + code.getCode() + " " + code.getText() );
	}

}
