/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - MessageFrame.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This frame shows all message traffic, and is used in the client. 
 * It can be toggled on and off, and is for debugging and demo purposes only.
 * It uses the Observer design pattern to register interest in both incoming
 * and outgoing server messages, and the Singleton design pattern to ensure
 * only one instance is instantiated.
 ******************************************************************************/
package drexel.edu.blackjack.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import lpcn.xbee.LimitLinesDocumentListener;

import drexel.edu.blackjack.client.in.MessagesFromServerListener;
import drexel.edu.blackjack.client.out.MessagesToServerListener;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This is a GUI window that, for debug and development
 * purposes, displays all incoming and outgoing messages
 * related to the client. It registers interest in these
 * messages through listeners, and then uses a JTextPane to
 * display them, color-coded, on the screen. Pretty much 
 * everything in this class has to do with the UI, but it's
 * not an end user UI, it's a developer UI. <strong>The end user
 * UI would never show these raw protocol messages.</strong>
 * 
 * @author Jennifer
 *
 */
public class MessageFrame extends JFrame implements MessagesFromServerListener, MessagesToServerListener {

	// Some random serializable ID
	private static final long serialVersionUID = -4226392218324274800L;

	// If we're showing message traffic, do so in this document....
	private StyledDocument doc = null;
	
	// Styling
	private SimpleAttributeSet inputStyle = null;
	private SimpleAttributeSet outputStyle = null;
	
	// End of line
	private static String EOL = "\n";
	
	// For the singleton instance
	private static MessageFrame messageFrame = null;
	
	/**
	 * Default constructor
	 */
	private MessageFrame() {
		
		// Need to add text to a document, which we limit to 1000 lines
		doc = new DefaultStyledDocument();
		doc.addDocumentListener(new LimitLinesDocumentListener(1000));
		
		// Document goes in a text pane
		JTextPane textPane = new JTextPane(doc)
		{
			private static final long serialVersionUID = -6439520365442219830L;

			public boolean getScrollableTracksViewportWidth()
		    {
		        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
		    }
		};
		textPane.setEditable(false);
		
		// Which goes in a JScrollPane
		JScrollPane pane = new JScrollPane( textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		pane.setViewportView(textPane);
		DefaultCaret caret = (DefaultCaret)textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// Which goes in the frame
		setPreferredSize( new Dimension(600,400 ) );
		getContentPane().setLayout( new BorderLayout(5,5) );
		getContentPane().add( pane );
		setLocationRelativeTo( null );
		setTitle( "Client Message Monitor" );
		pack();
		
		// Oh, and some font stuff
		inputStyle = new SimpleAttributeSet();
		StyleConstants.setFontFamily(inputStyle , "Courier New Bold");
		StyleConstants.setFontSize(inputStyle , 16 );
		StyleConstants.setForeground(inputStyle , Color.RED);

		outputStyle = new SimpleAttributeSet();
		StyleConstants.setFontFamily(outputStyle , "Courier New Bold");
		StyleConstants.setFontSize(outputStyle , 16 );
		StyleConstants.setForeground(outputStyle , Color.BLUE);

	}
	
	/**
	 * Returns the singleton instance of the message frame.
	 * May or may not be visible
	 * @return A singleton instance of the message frame
	 */
	public static MessageFrame getDefaultMessageFrame() {
		if( messageFrame == null ) {
			messageFrame = new MessageFrame();
		}
		return messageFrame;
	}
	
	@Override
	public void receivedMessage(ResponseCode code) {

		synchronized( doc ) {
			// Build up the exact string to display
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss");
			StringBuilder str = new StringBuilder( "<<<<<< [" );
			str.append( sdf.format( new Date() ) );
			str.append( "] " );
			str.append( code.toString() );
			str.append( EOL );
			
			// Display in the input color
			try {
				doc.insertString(doc.getLength(), str.toString(), inputStyle );
			} catch (BadLocationException badLocationException) {
				System.err.println( "Having trouble with our message frame, sorry." );
			}
		}
	}

	@Override
	public void sendingToServer(String message) {

		synchronized( doc ) {
			// Build up the exact string to display
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss");
			StringBuilder str = new StringBuilder( ">>>>>> [" );
			str.append( sdf.format( new Date() ) );
			str.append( "] " );
			str.append( message );
			str.append( EOL );

			// Display in the output color
			try {
				doc.insertString(doc.getLength(), str.toString(), outputStyle );
			} catch (BadLocationException badLocationException) {
				System.err.println( "Having trouble with our message frame, sorry." );
			}
		}
	}

}
