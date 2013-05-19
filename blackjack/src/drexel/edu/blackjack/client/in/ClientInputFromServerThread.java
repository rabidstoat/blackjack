package drexel.edu.blackjack.client.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.server.ResponseCode;
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
	
	// Our main client, who we notify if the socket connection closes
	private BlackjackCLClient blackjackClient = null;

	// And a logger for errors
	private final static Logger LOGGER = BlackjackLogger.createLogger(ClientInputFromServerThread.class.getName()); 
	
	/**********************************************************
	 * Constructor goes here
	 *********************************************************/

	/**
	 * Creates the thread that monitors the socket for data.
	 * 
	 * @param blackjackClient Pointer to the main client class
	 * @param socket The socket to listen from
	 */
	public ClientInputFromServerThread( BlackjackCLClient blackjackClient,
			Socket socket ) {
		
		super( "ClientInputFromServerThread" );
		
		// Record the listener and client
		this.blackjackClient = blackjackClient;
		
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
					
					// If we're debuging
					LOGGER.info( "<<<< " + inputLine );

					// Send it out to any listeners
					deliverMessage( inputLine );
					
					// And read the next line
					inputLine = reader.readLine();
		       }	
				
				LOGGER.info( "Apparently the client just disconnected." );
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
		
		// If we get here it's because the socket connection was closed
		blackjackClient.notifyOfShutdown();
		
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
		} else if( defaultListener != null ) {
			// And send to the non-null listener
			defaultListener.receivedMessage( code );
		}
		
		// If we get this far, it must have worked
		return true;
	}

	/**
	 * @param defaultListener the defaultListener to set
	 */
	public void setDefaultListener(MessagesFromServerListener defaultListener) {
		this.defaultListener = defaultListener;
	}

}
