package drexel.edu.blackjack.server.timeouts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackServerThread;

/**
 * This class keeps track of all client connections. Every 5 seconds
 * it steps through them, looks at their last activity time, and sees
 * if they've hit a timeout.
 * 
 * @author Jennifer
 */
public class IdleTimeoutDaemon extends Thread {

	/***********************************************************
	 * Local variables go here.
	 **********************************************************/

	// These are some static definitions of times
	private static final int SECOND_IN_MILLISECONDS	= 1000;
	
	// And how long we pause between checking for idle connections
	private static final int SWEEP_DELAY			= 5 * SECOND_IN_MILLISECONDS;
	
	// Since we only have one timer per state, we'll store them
	// in a map for ease of lookup
	private Map<BlackjackProtocol.STATE,TimeoutDefinition> timeoutMap;
	
	// Here is where we keep a list of all the server threads that
	// we are monitoring
	private volatile Set<BlackjackServerThread> clientThreads;
	
	// Our singleton instance
	private static IdleTimeoutDaemon daemon = null;
	
	// And a logger for errors
	private final static Logger LOGGER = Logger.getLogger(IdleTimeoutDaemon.class.getName()); 

	
	/***********************************************************
	 * Constructor goes here
	 **********************************************************/
	
	private IdleTimeoutDaemon() {
	
		// Need somewhere to store the threads
		clientThreads = new HashSet<BlackjackServerThread>();
		
		// And need to establish the timeouts that we are monitoring
		etsablishTimeoutMap();
	}
	
	/***********************************************************
	 * Public methods go here
	 **********************************************************/
	
	/**
	 * This gets the one and only copy of the idle timeout daemon,
	 * which it ensures is started.
	 * 
	 * @return The one and only idle timeout daemon
	 */
	public static IdleTimeoutDaemon getDefaultIdleTimeoutDaemon() {
		if( daemon == null ) {
			daemon = new IdleTimeoutDaemon();
			daemon.start();
		}
		return daemon;
	}
	
	/**
	 * Adds a thread that represents a connection to a client for the
	 * timeout idler daemon to monitor. This should be done once a
	 * connection is established.
	 * 
	 * @param thread What to monitor
	 * @return true if added successfully, else false
	 */
	public boolean addBlackjackServerThread( BlackjackServerThread thread ) {
		LOGGER.info( "Adding a server thread to the existing " + clientThreads.size() + " threads." );
		return clientThreads.add(thread);
	}
	
	/**
	 * Removes a thread from the list to be monitored. This will be done
	 * automatically if it ends up being in a disconnected state (that the
	 * monitor can detect). But it should be done whenever a session ends
	 * and the thread is no longer active.
	 * 
	 * @param thread What to stop monitoring
	 * @return true if removed successfully, else false
	 */
	public boolean removeBlackjackServerThread( BlackjackServerThread thread ) {
		LOGGER.info( "Removing a server thread from the existing " + clientThreads.size() + " threads." );
		return clientThreads.remove(thread);
	}
	
	/**********************************************************
	 * This is the meat of the thread, the run() method
	 *********************************************************/
	@Override
	public void run() {
		
		// Loop forever. Every 5 seconds, check on the status of
		// the connections
		while( true ) {
			
			try {
				Thread.sleep( SWEEP_DELAY );
			} catch (InterruptedException e) {
				// Nothing to see here, move along....
			}

			
			LOGGER.info( "About to sweep the connections." );
			
			// Keep a list of threads that need to be removed. We can't
			// remove them while we're iterating through them, though
			Set<BlackjackServerThread> threadsToReap = new HashSet<BlackjackServerThread>();
			
			// Cycle through all of the connections
			for( BlackjackServerThread thread : clientThreads ) {
				
				// First looked for closed sockets that we need to remove
				if( !thread.isAlive() || ( thread.getSocket() != null && thread.getSocket().isClosed()) ) {
					LOGGER.info( "Found a dead thread to remove." );
					threadsToReap.add(thread);
				} else {
					// Then look for a relevant timer based on state
					TimeoutDefinition timeout = this.timeoutMap.get( thread.getState() );
					if( timeout != null ) {
						
						// Figure out how much time has passed since the relevant timer
						long currentTime = System.currentTimeMillis();
						long delta = 0;
						if( timeout.getType() == TimeoutDefinition.TYPE.LAST_COMMAND &&
								thread.getProtocol() != null &&
								thread.getProtocol().getLastCommand() != null ) {
							delta = currentTime - thread.getProtocol().getLastCommand();
						} else if( timeout.getType() == TimeoutDefinition.TYPE.TIMER &&
								thread.getProtocol() != null &&
								thread.getProtocol().getTimer() != null ) {
							delta = currentTime - thread.getProtocol().getTimer();
						}
						
						// If it's more than the timeout, we have to do something
						if( delta > timeout.getTimeoutInMilliseconds() ) {
							thread.handleTimeout( timeout.getNewState(), timeout.getResponseCode() );
						}
					}
				}				
			}
			
			// Now remove the threads we marked to reap
			for( BlackjackServerThread threadToReap : threadsToReap ) {
				clientThreads.remove(threadToReap);
			}
		}
	}
	
	/***********************************************************
	 * Private methods go here
	 **********************************************************/

	/**
	 * Sets up the map that controls the timeout behavior
	 * in various states. For this simple implementation, 
	 * it's hard-coded.
	 */
	private void etsablishTimeoutMap() {
		
		timeoutMap = new HashMap<BlackjackProtocol.STATE,TimeoutDefinition>();
		
		// They get disconnected after 45 seconds of no activity when waiting for a username
		timeoutMap.put( BlackjackProtocol.STATE.WAITING_FOR_USERNAME, new TimeoutDefinition(
				BlackjackProtocol.STATE.WAITING_FOR_USERNAME,
				BlackjackProtocol.STATE.DISCONNECTED,
				null,
				45 * SECOND_IN_MILLISECONDS,
				TimeoutDefinition.TYPE.LAST_COMMAND) );
		
		// Same is true when waiting for a password
		timeoutMap.put( BlackjackProtocol.STATE.WAITING_FOR_PASSWORD, new TimeoutDefinition(
				BlackjackProtocol.STATE.WAITING_FOR_PASSWORD,
				BlackjackProtocol.STATE.DISCONNECTED,
				null,
				45 * SECOND_IN_MILLISECONDS,
				TimeoutDefinition.TYPE.LAST_COMMAND) );

		// When authenticated but not in a game, they get 900 seconds
		timeoutMap.put( BlackjackProtocol.STATE.NOT_IN_SESSION, new TimeoutDefinition(
				BlackjackProtocol.STATE.NOT_IN_SESSION,
				BlackjackProtocol.STATE.DISCONNECTED,
				null,
				900 * SECOND_IN_MILLISECONDS,
				TimeoutDefinition.TYPE.LAST_COMMAND) );

		// They get 60 seconds to place a bet
		timeoutMap.put( BlackjackProtocol.STATE.IN_SESSION_AWAITING_BETS, new TimeoutDefinition(
				BlackjackProtocol.STATE.IN_SESSION_AWAITING_BETS,
				BlackjackProtocol.STATE.IN_SESSION_AS_OBSERVER,
				null,
				60 * SECOND_IN_MILLISECONDS,
				TimeoutDefinition.TYPE.TIMER) );

		// And 60 to play their hand
		timeoutMap.put( BlackjackProtocol.STATE.IN_SESSION_AND_YOUR_TURN, new TimeoutDefinition(
				BlackjackProtocol.STATE.IN_SESSION_AND_YOUR_TURN,
				BlackjackProtocol.STATE.IN_SESSION_AS_OBSERVER,
				null,
				60 * SECOND_IN_MILLISECONDS,
				TimeoutDefinition.TYPE.TIMER) );
	}

}
