/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackServerThread.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: When a client establishes a connection with the server, one of these
 * threads is created to handle its input and output. It reads both single-line
 * and multi-line messages, and sends messages formatted elsewhere. Input from
 * a client is sent to the associated protocol object for processing, this is
 * mostly just concerned with the actual I/O.
 ******************************************************************************/
package drexel.edu.blackjack.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import drexel.edu.blackjack.db.user.FlatfileUserManager;
import drexel.edu.blackjack.db.user.UserManagerInterface;
import drexel.edu.blackjack.server.timeouts.IdleTimeoutDaemon;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * <b>CONCURRENT:</b> The whole purpose of this code and, thus,
 * all the comments, have to do with concurrency in the system.
 * Each client that is connected gets its own thread, to allow
 * for multiple clients to be handled at a time. The thread has
 * references to the socket connection, for purposes of I/O,
 * and to a protocol state, so the state can be maintained.
 * 
 * @author Jennifer
 */
public class BlackjackServerThread extends Thread {

	/**********************************************************
	 * Local variables go here
	 *********************************************************/
	
	// Need to keep track of client's socket for writing responses
	private Socket socket 		= null;
	
	// Input and output
	private PrintWriter out		= null;
	private BufferedReader in	= null;
	
	// There's a protocol state that goes with it
	private BlackjackProtocol protocol = null;
	
	// And a logger for errors
	private final static Logger LOGGER = BlackjackLogger.createLogger(BlackjackServerThread.class.getName()); 

	/**********1************************************************
	 * Constructor goes here
	 *********************************************************/

	/**
	 * Create a thread that is centered around processing input from,
	 * and providing output to, a client that is connected through
	 * this socket.
	 * 
	 * @param socket The secure socket for the connection to the
	 * client. It must already be open.
	 */
	public BlackjackServerThread( Socket socket ) {
		super( "BlackjackServerThread" );
		this.socket = socket;
		this.protocol = new BlackjackProtocol(this);
		LOGGER.finer( "Inside a blackjack server thread constructor." );
	}

	/**********************************************************
	 * This is the meat of the thread, the run() method. It
	 * starts by registering the thread with the 
	 * {@link drexel.edu.blackjack.server.timeouts.IdleTimeoutDaemon},
	 * then opens an input Reader and output Writer for the
	 * socket. Finally, it goes into a loop where it waits for
	 * client I/O, takes the message (presumably a valid command
	 * in the protocol specification), passes it to the protocol
	 * state for processing, then takes the response from the
	 * protocol object (should be a response code and parameters
	 * as specified by the protocol) and sends it to the 
	 * client. It's basically a request-response loop that
	 * ends when the client connection is severed.
	 *********************************************************/
	@Override
	public void run() {
		
		LOGGER.finer( "Inside a blackjack server thread run() method." );

		// We need to register the thread with the idle timeout daemon
		IdleTimeoutDaemon daemon = IdleTimeoutDaemon.getDefaultIdleTimeoutDaemon();
		if( daemon != null ) {
			LOGGER.finer( "Inside a blackjack server thread, about to register this thread" );
			daemon.addBlackjackServerThread(this);
		} else {
			LOGGER.warning( "Cannot register a blackjack server thread with the timeout daemon." );
		}
		
		try {
			// This is used to write responses to the client
			out = new PrintWriter(socket.getOutputStream(), true);
			
			// And this is how responses are read from the client
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
			// Keep reading single-line commands as long as we can
			LOGGER.finer( "Inside a blackjack server thread, about to block for the first read" );
			String inputLine = in.readLine();
			while ( inputLine != null ) {
				
				// We pass it to our protcol to figure out what to do
				LOGGER.finer( "Inside a blackjack server thread, about to process some input" );
				String outputLine = protocol.processInput(inputLine);
				
				// They give us the response to send back
				LOGGER.finer( "Inside a blackjack server thread, about to write some output" );
				out.println(outputLine);
				out.flush();
				
				// Was it a code that requires us to disconnect them?
				ResponseCode code = ResponseCode.getCodeFromString( outputLine );
				if( code != null && code.requiresDisconnect() ) {
					break;
				}
				
				// And we read another line
				LOGGER.finer( "Inside a blackjack server thread, about to block for another read" );
				inputLine = in.readLine();
	       }	
		} catch (IOException e) {
			// This happens when the socket is closed, for whatever reason
			LOGGER.finer( "Socket was closed for a connection." );
		} finally {
			LOGGER.finer( "Inside a blackjack server thread, in the finally clause" );
			// Always nice to clean up after ourselves
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				// At this point we're about to end anyway, so ignore it
			}
		}
		
		LOGGER.info( "Inside a client connection thread, about to shut down the connection" );
		if( daemon != null ) {
			daemon.removeBlackjackServerThread(this);
		}
		
		// Don't forget to log out the user
		if( protocol != null && protocol.getUser() != null ) {
			UserManagerInterface um = FlatfileUserManager.getDefaultUserManager();
			String username = (protocol.getUser().getUserMetadata() == null 
					? null 
					: protocol.getUser().getUserMetadata().getUsername() );
			if( um != null && username != null ) {
				um.logoutUser(username);
			}
		}
	}

	/**
	 * Get the (open) socket reference
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Get the protocol instance associated with this thread.
	 * @return the protocol
	 */
	public BlackjackProtocol getProtocol() {
		return protocol;
	}

	/**
	 * This method is called when the connection is closed. It should handle
	 * shutting down the connection, closing any readers or writers, closing
	 * the socket, stopping the thread, and unregistering the thread with the
	 * idle monitor.
	 */	
	public void forceDisconnectDueToTimeout() {
		
		LOGGER.info( "Inside a client connection thread, about to force a timeout disconnect!" );
		// Well, if we close the input reader, then the thread
		// should (hopefully stop!)
		try {
			LOGGER.finer( "Inside a blackjack server thread, about to call socket.close()" );
			socket.close();
		} catch (IOException e) {
			LOGGER.warning( "Unable to close the socket in a blackjack server thread." );
		}
		
	}

	/**
	 * Sends a response code through the socket by using the
	 * writer, and sending the toString() of the response
	 * code.
	 * 
	 * @param code What to send
	 */
	public void sendMessage(ResponseCode code) {
		
		if( out == null ) {
			LOGGER.severe( "Wanted to send a message to some user but the out writer was null." );
		} else {
			out.println( code.toString() );
			out.flush();
		}
	}
	
}
