package drexel.edu.blackjack.client.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.util.BlackjackLogHandler;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This threaded class is designed to read input from the 
 * server. It doesn't actually do anything with it. Instead,
 * it passes it off to listeners who have registered interest
 * in particular response codes.
 * 
 * @author Jennifer
 */
public class ClientInputFromServerThread extends Thread {

	/**********************************************************
	 * Local variables go here
	 *********************************************************/
	
	// Need to keep track of what we're reading input from
	private BufferedReader reader = null;

	// Only one listener at a time
	private MessagesFromServerListener defaultListener = null;

	// And a logger for errors
	private final static Logger LOGGER = BlackjackLogger.createLogger(ClientInputFromServerThread.class.getName()); 
	
	/**********************************************************
	 * Constructor goes here
	 *********************************************************/

	public ClientInputFromServerThread( Socket socket, MessagesFromServerListener defaultListener ) {
		
		super( "ClientInputFromServerThread" );
		
		// Record the listener
		this.defaultListener = defaultListener;
		
		// Create a reader for the socket
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			LOGGER.severe( "Had an error trying to open a reader from our established socket." );
			e.printStackTrace();
		}
	}

	/**********************************************************
	 * This is the meat of the thread, the run() method
	 *********************************************************/
	
	// TODO: Handle multi-line messages
	@Override
	public void run() {
		
		if( reader == null ) {
			LOGGER.severe( "Was unable to open a reader on the socket, so I can't run!" );
		} else {
			try {

				// Keep reading responses as long as we can, and not told to stop
				String inputLine = reader.readLine();
				
				while ( inputLine != null ) {

					// Send it out to any listeners
					deliverMessage( inputLine );
					
					// And read the next line
					inputLine = reader.readLine();
		       }	
			} catch (IOException e) {
				LOGGER.severe( "Something went wrong in our ClientInputFromServerThread for a client" );
				e.printStackTrace();
			} finally {
				// Always nice to clean up after ourselves
				try {
					reader.close();
				} catch (IOException e) {
					// At this point we're about to end anyway, so ignore it
				}
			}
		}
		
	}

	/**
	 * This takes a string that is an input line, creates a response code
	 * out of it, and then sends it to any listeners who have registered
	 * interest. If no one has registered interest, send it to the default
	 * listener.
	 * 
	 * @param inputLine Some text line
	 * @return True if delivered properly to at least one recipient,
	 * false otherwise
	 */
	private boolean deliverMessage(String inputLine) {
		
		// Make sure it's not null
		if( inputLine == null ) {
			LOGGER.severe( "Got a null input line. This shouldn't happen! " );
			return false;
		}
		
		// And that someone is listening
		if( defaultListener == null ) {
			LOGGER.severe( "Internal error, no current default listener in ClientInputFromServerThread." );
			return false;
		}
		
		// Now, create the response code from the string
		ResponseCode code = ResponseCode.getCodeFromString( inputLine );
		if( code == null || code.getCode() == null ) {
			// This is a problem! It means the input wasn't valid
			LOGGER.severe( "Received unrecognized input from the server: " + inputLine );
			return false;
		} else {
			// And send to the non-null listener
			defaultListener.receivedMessage( code );
		}
		
		// If we get this far, it must have worked
		return true;
	}

}
