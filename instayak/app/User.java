/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 3
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

/**
 * Represents an InstaYak user
 * 
 * @author Kevin Aud
 * @version 1.0
 */
public class User {
	
	// user id
	private String id;
	
	// user password
	private String password;
	
	// number of times user has posted an InstaYakSLMD message
	private int slmdCount = 1;
	
	/**
	 * Constructs a new user object
	 * @param id user id
	 * @param password user password
	 */
	public User (String id, String password) {
		this.id = id;
		this.password = password;
	}

	/**
	 * returns user id
	 * @return user id
	 */
	public String getId() {
		return id;
	}

	/**
	 * sets a new user id
	 * @param id new user id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * returns user password
	 * @return user password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * sets a new user password
	 * @param password new user password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * increments slmdCount by 1
	 */
	public void incrementSlmdCount() {
		slmdCount++;
	}
	
	/**
	 * returns slmdCount
	 * @return slmdCount
	 */
	public int getSlmdCount() {
		return slmdCount;
	}
	
}












