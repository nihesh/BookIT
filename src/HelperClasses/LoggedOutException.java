package HelperClasses;
/**
 * exception class LoggedOutException
 * used to logout a user
 * @author Harsh
 *
 */
public class LoggedOutException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String message="User has been logged out.";
	/**
	 * constructor for the class
	 */
	public LoggedOutException() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
