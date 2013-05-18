package drexel.edu.blackjack.util;

import java.util.logging.ConsoleHandler;

public class BlackjackLogHandler extends ConsoleHandler {

	public BlackjackLogHandler() {
		super();
		setFormatter( new BlackjackLogFormatter() );
	}
}
