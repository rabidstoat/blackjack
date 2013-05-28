/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - UserMetadata.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is an object-oriented representation of the metadata
 * associated with a user.
 ******************************************************************************/
package drexel.edu.blackjack.db.user;

import java.io.Serializable;

/**
 * Persistent information about users to store in the database.
 * 
 * @author DAN
 */
public class UserMetadata implements Serializable {
	
	/**
	 * For serializable purpose 
	 */
	private static final long serialVersionUID = 2724507393682991711L;
	
	private String username;
	private String password;
	private String fullname;
	private int balance;		// Integer because we only deal in whole dollars
	
	public static class Builder {
		private String username, password, fullname;
		private int balance;
		
		public UserMetadata build() {
			if (balance < 0) return null;
			if (fullname == null || password == null || username == null) return null;
			UserMetadata u = new UserMetadata();
			u.setBalance(balance);
			u.setFullname(fullname);
			u.setPassword(password);
			u.setUsername(username);
			return u;
		}
		
		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}
		
		public Builder setPassword(String password) {
			this.password = password;
			return this;
		}
		
		public Builder setFullname(String name) {
			this.fullname = name;
			return this;
		}
		
		public Builder setBalance(int balance) {
			this.balance = balance;
			return this;
		}
	}
	
	private UserMetadata() {
		this.username = "";
		this.password = "";
		this.fullname = "";
		this.balance = 0;
	}

	// Automated getters/setters below
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	protected void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	protected void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}
	/**
	 * @param fullname the fullname to set
	 */
	protected void setFullname(String fullname) {
		this.fullname = fullname;
	}
	/**
	 * @return the balance
	 */
	public int getBalance() {
		return balance;
	}
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(int balance) {
		this.balance = balance;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + balance;
		result = prime * result
				+ ((fullname == null) ? 0 : fullname.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		UserMetadata other = (UserMetadata) obj;
		if (balance != other.balance)
			return false;
		if (fullname == null) {
			if (other.fullname != null)
				return false;
		} else if (!fullname.equals(other.fullname))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/**
	 * return a nicely formatted presentation of UserMetadata
	 */
	public String toString() {
		StringBuilder b=  new StringBuilder();
		b.append(username).append(": password=").append(password).append(" name=");
		b.append(fullname).append(" balance=").append(balance);
		return b.toString();
	}
}
