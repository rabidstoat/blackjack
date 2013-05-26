/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackLogger.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Convenience method for creating a logger. Sets the custom format,
 * and if a system variable is set, the logging level.
 ******************************************************************************/
package drexel.edu.blackjack.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles creating nicely formatted loggers.
 * 
 * @author Jennifer
 */
public class BlackjackLogger {
	
	// By default, info messages and above get displayed
	private static final Level DEFAULT_LOG_LEVEL = Level.INFO;
	
	// The user can override this level by setting a 'loglevel'
	// environment variable, a string which is either: 
	// "FINEST", "FINER", "FINE", "INFO", "WARNING", "SEVERE"
	// (in order of criticality)
	private static final String LOG_LEVEL_PROPERTY	= "loglevel";
	private static final String FINEST	= "FINEST";
	private static final String FINER	= "FINER";
	private static final String FINE	= "FINE";
	private static final String INFO	= "INFO";
	private static final String WARNING	= "WARNING";
	private static final String SEVERE	= "SEVERE";
	
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
		
		// See if an environment variable overrode the log level
		String customLogLevel = System.getProperty( LOG_LEVEL_PROPERTY );
		if( customLogLevel != null ) {
			if( customLogLevel.equalsIgnoreCase(FINEST) ) {
				LOGGER.setLevel(Level.FINEST);
			} else if( customLogLevel.equalsIgnoreCase(FINER) ) {
				LOGGER.setLevel(Level.FINER);
			} else if( customLogLevel.equalsIgnoreCase(FINE) ) {
				LOGGER.setLevel(Level.FINE);
			} else if( customLogLevel.equalsIgnoreCase(INFO) ) {
				LOGGER.setLevel(Level.INFO);
			} else if( customLogLevel.equalsIgnoreCase(WARNING) ) {
				LOGGER.setLevel(Level.WARNING);
			} else if( customLogLevel.equalsIgnoreCase(SEVERE) ) {
				LOGGER.setLevel(Level.SEVERE);
			} else {
				LOGGER.warning( "Unknonwn logger level of '" + customLogLevel + "' ignored." );
			}
		}

		// And return
		return LOGGER;
	}
}
