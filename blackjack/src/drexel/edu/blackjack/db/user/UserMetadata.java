package drexel.edu.blackjack.db.user;

/**
 * Persistent information about users to store in the database.
 * 
 * @author DAN
 */
public class UserMetadata {
	
	private String userame;
	private String password;
	private String fullname;
	private int balance;		// Integer because we only deal in whole dollars
	
	// TODO: Maybe constructor? 

	// Automated getters/setters below
	
	/**
	 * @return the userame
	 */
	public String getUserame() {
		return userame;
	}
	/**
	 * @param userame the userame to set
	 */
	protected void setUserame(String userame) {
		this.userame = userame;
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
