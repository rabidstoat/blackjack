package drexel.edu.blackjack.client.out;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

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
	
	// Need to keep track of what we're writing output to
	private PrintWriter writer = null;

	// And a logger for errors
	private final static Logger LOGGER = BlackjackLogger.createLogger(ClientOutputToServerHelper.class.getName()); 

	/**********************************************************
	 * Constructor goes here
	 *********************************************************/

	public ClientOutputToServerHelper( Socket socket ) {
		super( "ClientOutputToServerThread" );
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

}
