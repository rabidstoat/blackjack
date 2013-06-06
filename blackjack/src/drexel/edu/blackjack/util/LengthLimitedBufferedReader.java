/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackServerThread.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Overrides the readLine() method on the BufferedReader so that, if
 * more characters than the limit specified are read before encountering the
 * line delimiter specified, then an exception is raised. Otherwise the string
 * (stripped of the delimiter) is returned.
 ******************************************************************************/
package drexel.edu.blackjack.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Overrides the readLine() method on the BufferedReader so that, if
 * more characters than the limit specified are read before encountering the
 * line delimiter specified, then an exception is raised. Otherwise the string
 * (stripped of the delimiter) is returned. 
 **/
public class LengthLimitedBufferedReader extends BufferedReader {

	/**********************************************************************************
	 * Local variables here
	 *********************************************************************************/
	
	private int byteLimit = 0;
	private String EOL = null;

	/**********************************************************************************
	 * Constructor here
	 *********************************************************************************/
	
	/**
	 * Creates a reader that handles not only buffering, but limiting the number
	 * of bytes that are read before the EOL character(s) are encountered.
	 * 
	 * @param byteLimit How many bytes, at most, can be read, inclusive of the EOL
	 * @param EOL What string delimits what a line is
	 */
	public LengthLimitedBufferedReader( Reader in, int byteLimit, String EOL ) {
		super(in);
		this.byteLimit = byteLimit;
		this.EOL = EOL;
		
		// TODO: Not handling multiple characters yet
		if( EOL == null || EOL.length() > 1 ) {
			throw new IllegalArgumentException( "EOL string must be exactly 1 character long." );
		}
	}

	/**********************************************************************************
	 * Overridden method here
	 *********************************************************************************/
	
	@Override
	public String readLine() throws IOException {
		
		// Track position in the line
		int index=0;

		// Store the string to return
		StringBuilder str = new StringBuilder();

		// Keep reading until you hit the delimiter OR end of stream OR you exceed the line length
		char ch = (char)read();
		while( !isDelimiter(ch) && ch != -1 && index < byteLimit ) {
			str.append(ch);
			ch = (char)read();
		}
		
		// Okay, now, see if it wasn't the end of the stream (-1) we have
		// to figure out if we read too many bytes, or if we're good
		String result = str.toString();
		if( ch != -1 ) {
			
			// If the string does NOT end with the delimiter, we read too much
			if( !str.toString().endsWith(EOL) ) {
				throw new IOException( "Byte limit of " + byteLimit + " exceeded while reading." );
			}
			
			// Otherwise, we have to strip off the delimiter before returning
			int length = str.toString().length();
			result = str.toString().substring(0,length-EOL.length());
		}
		
		// And finally we can return it!
		return result;

	}
	
	/**
	 * Is this character a delimiter for a new line? Currently
	 * only implemented for single-character delimiters. Multi-
	 * character delimiters would involve lookahead with mark
	 * and restore.
	 * 
	 * @param ch The character read
	 * @return True if it's a delimiter, false otherwise. Not that
	 * a null or 0-length delimiter always returns true.
	 */
	private boolean isDelimiter(char ch) {
		return EOL == null || EOL.length() == 0 || EOL.charAt(0) == ch;
	}
}
