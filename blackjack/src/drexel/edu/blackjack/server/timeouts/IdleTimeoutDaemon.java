/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - IdleTimeoutDaemon.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This class implements a background thread that runs while a server
 * is up. It contains a reference to all client connections. Every few seconds
 * it checks and sees if they have 'timed out' -- that is, if their last
 * activity is longer ago than the maximum idle time stipulated for the particular
 * state. Not all states have idle timeouts, but those that do have it 
 * handled here.
 ******************************************************************************/
package drexel.edu.blackjack.server.timeouts;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackServerThread;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * <b>STATEFUL:</b> This class keeps track of all client connections. Every 5 seconds
 * it steps through them, looks at their last activity time, and sees
 * if they've hit a timeout. The consideration of how long they are
 * allowed to remain idle before a timeout occurs is dependent on
 * the same of the protocol, as different protocol states have 
 * different timeout duartions.
 * <P>
 * This is ONLY needed for states where timing out disconnects them.
 * If the timeout does something else, it should be handle elsewhere,
 * in the relevant {@link drexel.edu.blackjack.server.game.driver.GameAction}.
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
	
	// STATEFUL: Since we only have one timer per state, we'll store them
	// in a map for ease of lookup
	private Map<BlackjackProtocol.STATE,TimeoutDefinition> timeoutMap;
	
	// Here is where we keep a list of all the server threads that
	// we are monitoring
	private volatile Set<BlackjackServerThread> clientThreads;
	
	// Our singleton instance
	private static IdleTimeoutDaemon daemon = null;
	
	// And a logger for errors
	private final static Logger LOGGER = BlackjackLogger.createLogger(IdleTimeoutDaemon.class.getName()); 

	
	/***********************************************************
	 * Constructor goes here
	 **********************************************************/
	
	private IdleTimeoutDaemon() {
	
		// Need somewhere to store the threads, and it better be synchronized
		clientThreads = Collections.synchronizedSet(new HashSet<BlackjackServerThread>());
		
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
		LOGGER.info( "Adding a client connection to monitor in the idle timeout daemon." );
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
		LOGGER.info( "Removing a client connection to monitor from the idle timeout daemon." );
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

			
			LOGGER.finer( "About to sweep the connections." );
			
			// Keep a list of threads that need to be removed. We can't
			// remove them while we're iterating through them, though
			Set<BlackjackServerThread> threadsToReap = Collections.synchronizedSet(
					new HashSet<BlackjackServerThread>() );
			
			// Cycle through all of the connections
			synchronized( clientThreads ) {
				for( BlackjackServerThread thread : clientThreads ) {
					
					// First looked for closed sockets that we need to remove
					if( !thread.isAlive() || ( thread.getSocket() != null && thread.getSocket().isClosed()) ) {
						LOGGER.info( "Found a dead client connection thread to remove." );
						threadsToReap.add(thread);
					} else if( thread.getProtocol() != null ) {
						// Then look for a relevant timer based on state
						LOGGER.finer( "Thread state is: " + thread.getProtocol().getState() );
						TimeoutDefinition timeout = this.timeoutMap.get( thread.getProtocol().getState() );
						if( timeout != null ) {
							
							LOGGER.finer( "Found a timeout definition for the state." );
							
							// Figure out how much time has passed since the relevant timer
							long currentTime = System.currentTimeMillis();
							long delta = 0;
							if( thread.getProtocol() != null && timeout.getType() != null ) {
								if( timeout.getType().equals( TimeoutDefinition.TYPE.LAST_COMMAND) &&
										thread.getProtocol().getLastCommand() != null ) {
									delta = currentTime - thread.getProtocol().getLastCommand();
								} else if( timeout.getType().equals(TimeoutDefinition.TYPE.TIMER) &&
										thread.getProtocol().getTimer() != null ) {
									delta = currentTime - thread.getProtocol().getTimer();
								}
							}
							
							LOGGER.finer( "The delta on the thread is " + delta );
							LOGGER.finer( "The timeout value is " + timeout.getTimeoutInMilliseconds() );
							
							// If it's more than the timeout, we have to do something
							if( delta > timeout.getTimeoutInMilliseconds() ) {
								// STATEFUL: This will actually force a state change, to the
								// disconnected state. We only have timeouts that disconnect
								// currently implemented through this mechanism.
								thread.forceDisconnectDueToTimeout();
							}
						}
					}				
				}
			}
			
			// Now remove the threads we marked to reap
			synchronized( threadsToReap ) {
				for( BlackjackServerThread threadToReap : threadsToReap ) {
					clientThreads.remove(threadToReap);
				}
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
		
		// STATEFUL: They get disconnected after 45 seconds of no activity when waiting for a username
		timeoutMap.put( BlackjackProtocol.STATE.WAITING_FOR_USERNAME, new TimeoutDefinition(
				TimeoutDefinition.TYPE.TIMER, BlackjackProtocol.STATE.WAITING_FOR_USERNAME,
				45 * SECOND_IN_MILLISECONDS) );
		
		// STATEFUL: Same is true when waiting for a password
		timeoutMap.put( BlackjackProtocol.STATE.WAITING_FOR_PASSWORD, new TimeoutDefinition(
				TimeoutDefinition.TYPE.TIMER, BlackjackProtocol.STATE.WAITING_FOR_PASSWORD,
				45 * SECOND_IN_MILLISECONDS) );

		// STATEFUL: When authenticated but not in a game, they get 900 seconds
		timeoutMap.put( BlackjackProtocol.STATE.NOT_IN_SESSION, new TimeoutDefinition(
				TimeoutDefinition.TYPE.LAST_COMMAND, BlackjackProtocol.STATE.NOT_IN_SESSION,
				900 * SECOND_IN_MILLISECONDS) );

	}

}
