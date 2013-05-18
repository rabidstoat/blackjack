package drexel.edu.blackjack.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles creating nicely formatted loggers.
 * 
 * @author Jennifer
 */
public class BlackjackLogger {
	
	private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

	/**
	 * Makes a logger for the name. Include a nice formatter
	 * that overrides the default ugly one.
	 * 
	 * @param name Should be like a class name
	 * @return A nice logger
	 */
	public static Logger createLogger( String name ) {
		
		// Add our new log handler
		Logger LOGGER = Logger.getLogger(name); 
		LOGGER.setUseParentHandlers(false);
		LOGGER.setLevel( DEFAULT_LOG_LEVEL );		
		LOGGER.addHandler( new BlackjackLogHandler() );
		
		// And return
		return LOGGER;
	}
}
