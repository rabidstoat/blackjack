/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackLogHandler.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Custom log handler class, needed as per java.util.logging API
 ******************************************************************************/
package drexel.edu.blackjack.util;

import java.util.logging.ConsoleHandler;

/**
 * We need to override the standard console output
 * handler with our new, custom formatter.
 * 
 * @author Jennifer
 */
public class BlackjackLogHandler extends ConsoleHandler {

	public BlackjackLogHandler() {
		super();
		setFormatter( new BlackjackLogFormatter() );
	}
}
