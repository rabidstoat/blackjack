package drexel.edu.blackjack.client.out;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
	
	// A property that is set to "true" if we should display a window with message traffic
	private final String SHOW_MESSAGE_TRAFFIC 	= "ShowMessages";
	
	// If we're showing message traffic, do so in this window....
	private JTextArea textArea = null;
	
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
		
		// If they set a certain system property to true, put up a window that
		// show all message traffic
		String showMessageTraffic = System.getProperty( SHOW_MESSAGE_TRAFFIC );
		if( showMessageTraffic != null && showMessageTraffic.equalsIgnoreCase("true") ) {
			createMessageTrafficWindow();
		}
		
	}

	/**********************************************************
	 * Public methods go here
	 *********************************************************/
	public boolean sendRawText( String text ) {
		
		// In case we're debugging
		LOGGER.info( ">>>> " + text );
		
		// If we're logging
		if( textArea != null ) {
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss");
			textArea.append( "[" + sdf.format( new Date() ) + "] " + text );
			textArea.append("\n" );
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
	 * Creates a JFrame for printing user messages
	 */
	private void createMessageTrafficWindow() {
		
		JFrame frame = new JFrame( "Outgoing Client Messages" );
		frame.setPreferredSize( new Dimension(600,400 ) );
		frame.setLayout( new BorderLayout(5,5) );
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane pane = new JScrollPane( textArea );
		frame.add( pane );
		frame.setLocationRelativeTo( null );
		frame.pack();
		frame.setVisible( true );
		
	}


}
