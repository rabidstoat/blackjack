/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackLogFormatter.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Defines a custom format for displaying logger messages
 ******************************************************************************/
package drexel.edu.blackjack.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * The Java logging package uses formatters for determining
 * how to output a logging message. This override the default
 * format to one more like log4j default output, which I'm
 * used to looking at.
 * 
 * @author Jennifer *
 */
public class BlackjackLogFormatter extends Formatter {

	// Technically we shouldn't do this static, it can lead to occasional subtle 
	// errors or so they say, but eh.
    private static final DateFormat df = new SimpleDateFormat("hh:mm:ss.S");

    @Override
	public synchronized String format(LogRecord record) {	
    	StringBuilder builder = new StringBuilder(1000);
    	builder.append("[").append(record.getLevel()).append("] ");
    	builder.append(formatMessage(record));
    	builder.append(" (").append(record.getSourceClassName()).append(".");
    	builder.append(record.getSourceMethodName()).append(") [");
    	builder.append(df.format(new Date(record.getMillis()))).append("]");
    	builder.append("\n");
    	return builder.toString();
	}
}
