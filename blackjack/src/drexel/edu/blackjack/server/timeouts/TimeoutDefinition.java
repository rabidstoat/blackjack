package drexel.edu.blackjack.server.timeouts;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;

/**
 * This class is a POJO that holds information about timeouts.
 * 
 * @author Jennifer
 */
public class TimeoutDefinition {
	
	// The state in which the timeout applies. Defaults to
	// null, but it's required that this be set.
	private BlackjackProtocol.STATE	applicableState = null;
	
	// This is the length of the timeout, in milliseconds. Defaults
	// to 15 minutes (time 60 seconds times 1000 milliseconds)
	private long timeoutInMilliseconds = 15 * 60 * 1000;
	
	/**************************************************************
	 * Some constructors go here.
	 *************************************************************/

	public TimeoutDefinition(STATE applicableState, long timeoutInMilliseconds ) {
		super();
		this.applicableState = applicableState;
		this.timeoutInMilliseconds = timeoutInMilliseconds;
	}

	/**************************************************************
	 * Getters and setters
	 *************************************************************/

	/**
	 * @return the applicableState
	 */
	public BlackjackProtocol.STATE getApplicableState() {
		return applicableState;
	}

	/**
	 * @param applicableState the applicableState to set
	 */
	public void setApplicableState(BlackjackProtocol.STATE applicableState) {
		this.applicableState = applicableState;
	}

	/**
	 * @return the newState
	 */
	public BlackjackProtocol.STATE getNewState() {
		return BlackjackProtocol.STATE.DISCONNECTED;
	}

	/**
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return null;
	}

	/**
	 * @return the timeoutInMilliseconds
	 */
	public long getTimeoutInMilliseconds() {
		return timeoutInMilliseconds;
	}

	/**
	 * @param timeoutInMilliseconds the timeoutInMilliseconds to set
	 */
	public void setTimeoutInMilliseconds(long timeoutInMilliseconds) {
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
