/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - TimeoutDefinition.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Defines a timeout definition for the idle timeout daemon to use.
 * It maps a state, to the appropriate timeout duration.
 ******************************************************************************/
package drexel.edu.blackjack.server.timeouts;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;

/**
 * <b>STATEFUL:</b> This class is a POJO that holds information 
 * about idle timeouts, which are a way to keep the client
 * from tying up a socket connection indefinitely if they
 * aren't sending any information throught it. The only things 
 * that's needed to know is what state the timeout applies to, 
 * and what the timeout duration.
 * 
 * @author Jennifer
 */
public class TimeoutDefinition {
	
	// STATEFUL: The state in which the timeout applies. Defaults to
	// null, but it's required that this be set.
	private BlackjackProtocol.STATE	applicableState = null;
	
	/**
	 * There are two types of timers. One is based on when the last command
	 * of any type was received, the other on when a timer was set.
	 */
	enum TYPE {
		/**
		 * This one goes off the {@link drexel.edu.blackjack.server.BlackjackProtocol#getLastCommand()}
		 * value 
		 */
		LAST_COMMAND,
		
		/**
		 * This one goes of the {@link drexel.edu.blackjack.server.BlackjackProtocol#getTimer()}
		 * value
		 */
		TIMER
	}
	
	// And here is where we track the type
	private TYPE type = null;
	
	// This is the length of the timeout, in milliseconds. Defaults
	// to 15 minutes (time 60 seconds times 1000 milliseconds)
	private long timeoutInMilliseconds = 15 * 60 * 1000;
	
	/**************************************************************
	 * Some constructors go here.
	 *************************************************************/

	public TimeoutDefinition(TYPE type, STATE applicableState, long timeoutInMilliseconds ) {
		super();
		this.type = type;
		this.applicableState = applicableState;
		this.timeoutInMilliseconds = timeoutInMilliseconds;
	}

	/**************************************************************
	 * Getters and setters
	 *************************************************************/

	
	/**
	 * What state does this timeout correspond to?
	 * @return the applicableState
	 */
	public BlackjackProtocol.STATE getApplicableState() {
		return applicableState;
	}

	/**
	 * What state does this timeout correspond to?
	 * @param applicableState the applicableState to set
	 */
	public void setApplicableState(BlackjackProtocol.STATE applicableState) {
		this.applicableState = applicableState;
	}

	/**
	 * @return the type
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TYPE type) {
		this.type = type;
	}

	/**
	 * New state to transition to.
	 * @return the newState
	 */
	public BlackjackProtocol.STATE getNewState() {
		return BlackjackProtocol.STATE.DISCONNECTED;
	}

	/**
	 * How many milliseconds until a timeout occurs?
	 * @return the timeoutInMilliseconds
	 */
	public long getTimeoutInMilliseconds() {
		return timeoutInMilliseconds;
	}

	/**
	 * How many milliseconds until a timeout occurs?
	 * @param timeoutInMilliseconds the timeoutInMilliseconds to set
	 */
	protected void setTimeoutInMilliseconds(long timeoutInMilliseconds) {
		this.timeoutInMilliseconds = timeoutInMilliseconds;
	}
	
	/**************************************************************
	 * Other public methods
	 *************************************************************/

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((applicableState == null) ? 0 : applicableState.hashCode());
		result = prime
				* result
				+ (int) (timeoutInMilliseconds ^ (timeoutInMilliseconds >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeoutDefinition other = (TimeoutDefinition) obj;
		if (applicableState != other.applicableState)
			return false;
		if (timeoutInMilliseconds != other.timeoutInMilliseconds)
			return false;
		return true;
	}

}
