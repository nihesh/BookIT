package HelperClasses;

public class LoggedOutException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String message="User has been logged out.";
	public LoggedOutException() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
