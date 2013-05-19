package drexel.edu.blackjack.client.in;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

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
	
	// A property that is set to "true" if we should display a window with message traffic
	private final String SHOW_MESSAGE_TRAFFIC 	= "ShowMessages";
	
	// If we're showing message traffic, do so in this window....
	private JTextArea textArea = null;
	
	// Need to keep track of what we're reading input from
	private BufferedReader reader = null;

	// Only one listener at a time
	private MessagesFromServerListener defaultListener = null;
	
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
		
		// If they set a certain system property to true, put up a window that
		// show all message traffic
		String showMessageTraffic = System.getProperty( SHOW_MESSAGE_TRAFFIC );
		if( showMessageTraffic != null && showMessageTraffic.equalsIgnoreCase("true") ) {
			createMessageTrafficWindow();
		}
		
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
					
					// If we're logging
					if( textArea != null ) {
						SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss");
						textArea.append( "[" + sdf.format( new Date() ) + "] " + inputLine );
						textArea.append("\n" );
					}

					// What we due depends on if we're processing a multiline message or not
					if( this.processingMultilineMessage ) {
						// If we ARE, then we keep appending to the multi-line message
						// until we hit a newline all by itself. Then we deliver it
						if( isEndOfMultilineMessage(inputLine) ) {
							ResponseCode code = ResponseCode.getCodeFromString( multilineMessage.toString() );
							processingMultilineMessage = false;
							multilineMessage = null;
							deliverMessage( code );
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
							deliverMessage( code );
						}
					}
					
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
	private boolean deliverMessage(ResponseCode code) {
		
		// And that someone is listening
		if( defaultListener == null ) {
			LOGGER.severe( "Internal error, no current default listener in ClientInputFromServerThread." );
			return false;
		}
		
		// Now, create the response code from the string
		if( code == null || code.getCode() == null ) {
			// This is a problem! It means the input wasn't valid
			LOGGER.severe( "Received unrecognized input from the server: " + code );
			return false;
		} else if( defaultListener != null ) {
			// And send to the non-null listener
			defaultListener.receivedMessage( code );
		}
		
		// If we get this far, it must have worked
		return true;
	}

	/**
	 * Creates a JFrame for printing user messages
	 */
	private void createMessageTrafficWindow() {
		
		JFrame frame = new JFrame( "Incoming Client Messages" );
		frame.setPreferredSize( new Dimension(600,400 ) );
		frame.setLayout( new BorderLayout(5,5) );
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane pane = new JScrollPane( textArea );
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		frame.add( pane );
		frame.setLocationRelativeTo( null );
		frame.pack();
		frame.setVisible( true );
		
	}

	/**
	 * @param defaultListener the defaultListener to set
	 */
	public void setDefaultListener(MessagesFromServerListener defaultListener) {
		this.defaultListener = defaultListener;
	}

}
