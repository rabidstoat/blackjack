package drexel.edu.blackjack.client.out;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import drexel.edu.blackjack.client.MessageFrame;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This class is used to write output to the server.
 * 
 * @author Jennifer
 */
public class ClientOutputToServerHelper extends Thread {

	/**********************************************************
	 * Local variables go here
	 *********************************************************/

	// For listeners
	private Set<MessagesToServerListener> listeners = null;
	
	// Need to keep track of what we're writing output to
	private PrintWriter writer = null;

	// And a logger for errors
	private final static Logger LOGGER = BlackjackLogger.createLogger(ClientOutputToServerHelper.class.getName()); 

	/**********************************************************
	 * Constructor goes here
	 *********************************************************/

	public ClientOutputToServerHelper( Socket socket ) {
		super( "ClientOutputToServerThread" );
		
		// Add the message frame as a listener; needs to be explicitly made synchronized
		listeners = Collections.synchronizedSet(new HashSet<MessagesToServerListener>());
		addListener( MessageFrame.getDefaultMessageFrame() );
		
		try {
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			LOGGER.severe( "Had an error trying to open a writer to our established socket." );
			e.printStackTrace();
		}		
	}

	/**********************************************************
	 * Public methods go here
	 *********************************************************/
	public boolean sendRawText( String text ) {
		
		// In case we're debugging
		LOGGER.fine( ">>>> " + text );
		
		// Any listeners?
		if( listeners != null ) {
			Object[] copy;
			synchronized( this ) {
				copy = listeners.toArray();
			}
			for( int i = 0; i < copy.length; i++ ) {
				((MessagesToServerListener)copy[i]).sendingToServer( text );
			}
		}
		
		// Can't send a message if we didn't open the writer
		if( writer == null ) {
			return false;
		}
		
		// Otherwise, try to do what we can do
		try {
			writer.println( text );
			writer.flush();
		} catch( Exception e ) {
			LOGGER.severe( "Had a problem writing to the socket." );
			e.printStackTrace();
			return false;
		}
		
		// If we got this far, assume success!
		return true;
	}
	
	/**
	 * given a username, formulate the proper command
	 * string for passing this to the server.
	 * @param username The username
	 * @return True if sent correctly, false otherwise
	 */
	public boolean sendUsername( String username ) {
		
		if( username == null ) {
			return false;
		}
		
		return this.sendRawText( "USERNAME " + username );
	}

	/**
	 * given a password, formulate the proper command
	 * string for passing this to the server.
	 * @param password The password
	 * @return True if sent correctly, false otherwise
	 */
	public boolean sendPassword( String password ) {
		
		if( password == null ) {
			return false;
		}
		
		return this.sendRawText( "PASSWORD " + password );
	}

	/**
	 * Sends a request to the server asking for the version
	 */
	public boolean sendVersionRequest() {
		return this.sendRawText( "VERSION" );
	}

	/**
	 * Sends a request to the server asking for the capabilities
	 */
	public boolean sendCapabilitiesRequest() {
		return this.sendRawText( "CAPABILITIES" );
	}

	/**
	 * Sends a request to the server asking for the list of games
	 */
	public boolean sendListGamesRequest() {
		return this.sendRawText( "LISTGAMES" );
	}

	/**
	 * Sends a request to the server asking for the account balance
	 */
	public boolean sendAccountRequest() {
		return this.sendRawText( "ACCOUNT" );
	}

	/**
	 * Sends a request to the server asking to join a session
	 */
	public boolean sendJoinSessionRequest( String sessionName ) {
		if( sessionName == null ) {
			return false;
		}
		return this.sendRawText( "JOINSESSION " + sessionName );
	}
	
	/**
	 * Sends a request to the server asking to leave a session
	 */
	public boolean sendLeaveSessionRequest() {
		return this.sendRawText( "LEAVESESSION" );
	}
	
	/**
	 * Sends a request to the server asking to quit
	 */
	public boolean sendQuitRequest() {
		return this.sendRawText( "QUIT" );
	}
	
	/**
	 * Add a listener for messages going out to the server
	 * @param listener The listener
	 * @return True if added okay, false otherwise
	 */
	synchronized public boolean addListener( MessagesToServerListener listener ) {
		return listeners.add(listener);
	}
	
	/**
	 * Remove a listener for messages going out to the server
	 * @param listener The listener
	 * @return True if removed okay, false otherwise
	 */
	synchronized public boolean removeListener( MessagesToServerListener listener ) {
		return listeners.remove(listener);
	}
	
}
