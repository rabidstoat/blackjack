package drexel.edu.blackjack.client.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import drexel.edu.blackjack.client.BlackjackCLClient;
import drexel.edu.blackjack.client.MessageFrame;
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

	// Multiple listeners
	private Set<MessagesFromServerListener> listeners = null;
	
	// Our main client, who we notify if the socket connection closes
	private BlackjackCLClient blackjackClient = null;
	
	// Multi-line messages get built up her
	private StringBuilder multilineMessage = null;
	
	// We set this true if we're processing a multi-line message
	private boolean processingMultilineMessage = false;

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
		
		// A listener for message traffic; need to create a synchronized set for our
		// multithreaded environment
		listeners = Collections.synchronizedSet(new HashSet<MessagesFromServerListener>());
		addListener( MessageFrame.getDefaultMessageFrame() );
		
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
					
					// What we due depends on if we're processing a multiline message or not
					if( this.processingMultilineMessage ) {
						// If we ARE, then we keep appending to the multi-line message
						// until we hit a newline all by itself. Then we deliver it
						if( isEndOfMultilineMessage(inputLine) ) {
							ResponseCode code = ResponseCode.getCodeFromString( multilineMessage.toString() );
							processingMultilineMessage = false;
							multilineMessage = null;
							deliverMessageFromServer( code );
						} else {
							multilineMessage.append( "\n" );
							multilineMessage.append( inputLine );
						}
					} else {
						// If it's a singleline message, we deliver it right away
						// Otherwise we go into multiline mode
						ResponseCode code = ResponseCode.getCodeFromString( inputLine );
						if( code == null ) {
							LOGGER.severe( "Expected to receive a new response code but instead received something else, so ignoring." );
							LOGGER.severe( "Received: " + inputLine );
						} else if( code.isMultilineCode() ) {
							multilineMessage = new StringBuilder( inputLine );
							processingMultilineMessage = true;
						} else {
							deliverMessageFromServer( code );
						}
					}
					
					// And read the next line
					inputLine = reader.readLine();
		       }	
				
				LOGGER.info( "Apparently the client just disconnected." );
			} catch (IOException e) {
				LOGGER.info( "Apparently the server just shut down." );
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
	 * A multiline message is terminated with an empty line,
	 * basically.
	 * 
	 * @param inputLine Is this line terminating the multiline
	 * message?
	 * @return True if it is, else false
	 */
	private boolean isEndOfMultilineMessage(String inputLine) {
		return inputLine != null && inputLine.trim().length() == 0;
	}

	/**
	 * This takes a a response code from the server, and then sends it to 
	 * to the default listener.
	 * 
	 * @param code The response formatted as a response code
	 * @return True if delivered properly to at least one recipient,
	 * false otherwise
	 */
	private boolean deliverMessageFromServer(ResponseCode code) {

		if( listeners != null && listeners.size() > 0 ) {
			// Now, create the response code from the string
			if( code == null || code.getCode() == null ) {
				// This is a problem! It means the input wasn't valid
				LOGGER.severe( "Received unrecognized input from the server: " + code );
				return false;
			} else {
				// And send to the listeners -- when written as a for loop it was causing a
				// ConcurrentModificationException. Since the javadocs explicitly show
				// an iterator I will use the iterator explicitly. Sigh. It still
				// didn't help!
				synchronized( listeners ) {
					Iterator<MessagesFromServerListener> i = listeners.iterator(); // Must be in the synchronized block
				    while (i.hasNext()) {
						i.next().receivedMessage( code );
				    }				    
				}
			}
		}
		
		// If we get this far, it must have worked
		return true;
	}

	/**
	 * Register a listener
	 * @param listener Who to register
	 * @return True if added successfully, false otherwise
	 */
	public boolean addListener(MessagesFromServerListener listener) {
		
		return listeners.add( listener );
		
	}

	/**
	 * Unregister a listener
	 * @param listener Who to unregister
	 * @return True if removed successfully, false otherwise
	 */
	public boolean removeListener(MessagesFromServerListener listener) {

		return listeners.remove( listener );

	}

}
