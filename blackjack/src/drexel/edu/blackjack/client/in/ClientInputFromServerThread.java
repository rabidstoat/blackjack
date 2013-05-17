package drexel.edu.blackjack.client.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import drexel.edu.blackjack.server.ResponseCode;

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
	
	// This listener is automatically registered for any messages that don't
	// have anyone else registering for them
	private MessagesFromServerListener defaultListener = null;

	// And a logger for errors
	private final static Logger LOGGER = Logger.getLogger(ClientInputFromServerThread.class.getName()); 
	
	// Listeners are done on a per-response-code basis. So, instead of just a set of
	// listeners, there needs to be a set per response code. This will be done in a Map,
	// where the key is the response code integer, and the value is the list of listeners
	// interested in that response code (possibly null if no one is interested).
	private Map<Integer,Set<MessagesFromServerListener>> listeners = null;

	/**********************************************************
	 * Constructor goes here
	 *********************************************************/

	public ClientInputFromServerThread( Socket socket, MessagesFromServerListener defaultListener ) {
		
		super( "ClientInputFromServerThread" );
		
		// Record the listener
		this.defaultListener = defaultListener;
		
		// Need something to hold the listeners
		listeners = new HashMap<Integer,Set<MessagesFromServerListener>>();
		
		// Just sets the logging level
		LOGGER.setLevel( Level.INFO );
		
		// Create a reader for the socket
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			LOGGER.severe( "Had an error trying to open a reader from our established socket." );
			e.printStackTrace();
		}
	}

	/**********************************************************
	 * Public methods for adding and removing oneself as a
	 * listener.
	 *********************************************************/
	
	/**
	 * Register for interest in a particular response code. Here, the
	 * response code is given as an integer. An example of how one
	 * would get the integer value of a response code of interest is:
	 * ResponseCode.USER_BUSTED.getCode()
	 * 
	 * @param l The listener who is registering for the messages
	 * @param responseCode The response code, as an integer 
	 * @return true if interest was successfully registered,
	 * false if there was some sort of problem
	 */
	public boolean registerForMessages( MessagesFromServerListener l, int responseCode ) {
		
		// First, get the set for this response code
		Set<MessagesFromServerListener> listenersForCode = listeners.get( Integer.valueOf(responseCode) );
		
		// If the set is null, create it and store it in the map
		if( listenersForCode == null ) {
			listenersForCode = new HashSet<MessagesFromServerListener>();
			listeners.put( Integer.valueOf(responseCode), listenersForCode );
		}
		
		// And add them 
		return listenersForCode.add( l );
	}

	/**
	 * Register for interest in a particular response code. Here, the
	 * response code is given as an object. An example of how one
	 * would get the integer value of a response code of interest is:
	 * ResponseCode.USER_BUSTED
	 * 
	 * @param l The listener who is registering for the messages
	 * @param responseCode The response code, as an object. Just the
	 * code value is used, and the text is ignored.
	 * @return true if interest was successfully registered,
	 * false if there was some sort of problem
	 */
	public boolean registerForMessages( MessagesFromServerListener l, ResponseCode responseCode ) {
		
		// If it's null, we have a problem
		if( responseCode == null || responseCode.getCode() == null ) {
			return false;
		}
		
		// Otherwise just pass it to the above method
		return registerForMessages( l, responseCode.getCode().intValue() );
	}
	
	/**
	 * Register for interest in a set of response codec. Here, the
	 * response codes are given as objects. An example of how one
	 * would get the integer value of a response code of interest is:
	 * ResponseCode.USER_BUSTED
	 * 
	 * @param l The listener who is registering for the messages
	 * @param responseCodes The set of response codes, as an object
	 * @return true if all response codes of interest were successfully 
	 * registered, false if one or more had some sort of problem
	 */
	public boolean registerForMessages( MessagesFromServerListener l, Set<ResponseCode> responseCodes ) {
		
		// If it's null, we have a problem
		if( responseCodes == null ) {
			return false;
		}

		// Otherwise step through and add them, one by one
		boolean success = true;
		for( ResponseCode code : responseCodes ) {
			success = success && registerForMessages( l, code );
		}
		
		return success;
	}

	/**
	 * Unregister for interest in a particular response code. Here, the
	 * response code is given as an integer. An example of how one
	 * would get the integer value of a response code of interest is:
	 * ResponseCode.USER_BUSTED.getCode()
	 * 
	 * @param l The listener who is unregistering for the messages
	 * @param responseCode The response code, as an integer 
	 * @return true if interest was successfully unregistered,
	 * false if there was some sort of problem (like if it wasn't
	 * registered in the first place)
	 */
	public boolean unregisterForMessages( MessagesFromServerListener l, int responseCode ) {
		
		// First, get the set for this response code
		Set<MessagesFromServerListener> listenersForCode = listeners.get( Integer.valueOf(responseCode) );
		
		// If the set is null, then there's a problem, obviously we never registered
		if( listenersForCode == null ) {
			return false;
		}
		
		// otherwise, remove them if possible
		return listenersForCode.remove( l );
	}

	/**
	 * Unregister for interest in a particular response code. Here, the
	 * response code is given as an object. An example of how one
	 * would get the value of a response code of interest is:
	 * ResponseCode.USER_BUSTED
	 * 
	 * @param l The listener who is unregistering for the messages
	 * @param responseCode The response code, as an object. Just the
	 * code value is used, and the text is ignored.
	 * @return true if interest was successfully unregistered,
	 * false if there was some sort of problem
	 */
	public boolean unregisterForMessages( MessagesFromServerListener l, ResponseCode responseCode ) {
		
		// If it's null, we have a problem
		if( responseCode == null || responseCode.getCode() == null ) {
			return false;
		}
		
		// Otherwise just pass it to the above method
		return unregisterForMessages( l, responseCode.getCode().intValue() );
	}
	
	/**
	 * Unregister for interest in a set of response codes. Here, the
	 * response codes are given as objects. An example of how one
	 * would get the value of a response code of interest is:
	 * ResponseCode.USER_BUSTED
	 * 
	 * @param l The listener who is unregistering for the messages
	 * @param responseCodes The set of response codes, as an object
	 * @return true if all response codes of interest were successfully 
	 * unregistered, false if one or more had some sort of problem
	 */
	public boolean unregisterForMessages( MessagesFromServerListener l, Set<ResponseCode> responseCodes ) {
		
		// If it's null, we have a problem
		if( responseCodes == null ) {
			return false;
		}

		// Otherwise step through and add them, one by one
		boolean success = true;
		for( ResponseCode code : responseCodes ) {
			success = success && unregisterForMessages( l, code );
		}
		
		return success;
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
		} else if( listeners == null ) {
			LOGGER.severe( "Internal error, null listeners list in ClientInputFromServerThread." );
			return false;
		}
		
		// Now, create the response code from the string
		ResponseCode code = ResponseCode.getCodeFromString( inputLine );
		if( code == null || code.getCode() == null ) {
			// This is a problem! It means the input wasn't valid
			LOGGER.severe( "Received unrecognized input from the server: " + inputLine );
			return false;
		} else {
			// Get the set of listeners it needs to get delivered to
			Set<MessagesFromServerListener> recipients = listeners.get( code.getCode() );
			
			// If it's null or empty, we send to the default listener
			if( recipients == null || recipients.size() == 0 ) {
				defaultListener.receivedMessage( code );
			} else {
				// Otherwise, send it to each listener
				for( MessagesFromServerListener recipient : recipients ) {
					recipient.receivedMessage( code );
				}
			}
		}
		
		// If we get this far, it must have worked
		return true;
	}

}
