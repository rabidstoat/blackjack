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
	 * @param userame the username to set
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

}
