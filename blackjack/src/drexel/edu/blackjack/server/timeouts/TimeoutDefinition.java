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
	
	// This is used to specify which timestamp to use when
	// calculating the timeout interval
	enum TYPE {
		// This uses BlackjackProtocol.lastCommand
		LAST_COMMAND,
		
		// This uses BlackjackProtocol.timer
		TIMER
	}

	// The state in which the timeout applies. Defaults to
	// null, but it's required that this be set.
	private BlackjackProtocol.STATE	applicableState = null;
	
	// The new state that the connection moves to. Defaults to
	// null, but it's required that this is set.
	private BlackjackProtocol.STATE	newState = null;
	
	// If the timeout is exceeded, this response code should
	// get sent to the relevant client. If it's null, no
	// response code is sent. Defaults to null
	private ResponseCode responseCode = null;
	
	// This is the length of the timeout, in milliseconds. Defaults
	// to 15 minutes (time 60 seconds times 1000 milliseconds)
	private long timeoutInMilliseconds = 15 * 60 * 1000;
	
	// And which timer to go off. Defaults to last command
	private TYPE type = TYPE.LAST_COMMAND;
	
	/**************************************************************
	 * Some constructors go here.
	 *************************************************************/

	public TimeoutDefinition(STATE applicableState, STATE newState,
			ResponseCode responseCode, long timeoutInMilliseconds,
			TYPE type) {
		super();
		this.applicableState = applicableState;
		this.newState = newState;
		this.responseCode = responseCode;
		this.timeoutInMilliseconds = timeoutInMilliseconds;
		this.type = type;
	}

	public TimeoutDefinition(STATE applicableState, STATE newState,
			long timeoutInMilliseconds, TYPE type) {
		super();
		this.applicableState = applicableState;
		this.newState = newState;
		this.timeoutInMilliseconds = timeoutInMilliseconds;
		this.type = type;
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
		return newState;
	}

	/**
	 * @param newState the newState to set
	 */
	public void setNewState(BlackjackProtocol.STATE newState) {
		this.newState = newState;
	}

	/**
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
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
		result = prime * result
				+ ((newState == null) ? 0 : newState.hashCode());
		result = prime * result
				+ ((responseCode == null) ? 0 : responseCode.hashCode());
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
		if (newState != other.newState)
			return false;
		if (responseCode == null) {
			if (other.responseCode != null)
				return false;
		} else if (!responseCode.equals(other.responseCode))
			return false;
		if (timeoutInMilliseconds != other.timeoutInMilliseconds)
			return false;
		return true;
	}

}
