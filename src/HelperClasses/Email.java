package HelperClasses;
import java.io.File;
import java.io.Serializable;

public class Email implements Serializable{
	private static final long serialVersionUID = 1L;
	private static  String domain="@iiitd.ac.in";
	private static String check="@@iiitd.ac.in";
	private String emailID;
	public Email(String x) {
		StringBuilder y=new StringBuilder();
		for(int i=0;i<x.length();i++) {
			if(!(x.charAt(i)==' ')) {
				y.append(x.charAt(i));
			}
		}
		emailID=y.toString();
	}
	public int validateSignup() {
		StringBuilder x=new StringBuilder();
		for(int i=0;i<emailID.length();i++) {
			if(!(emailID.charAt(i)==' ')) {
				x.append(emailID.charAt(i));
			}
		}
		String y=x.toString();
		User temp100=User.getUser(y, false);
		boolean exists=true;
		if(temp100==null) {
			exists=false;
		}
		if(exists) {
			return 1; //user already exists
		}
		if(y.contains(domain) && 
				!y.contains(check) && 
				(y.indexOf(domain)+12==y.length())) {
			String[] temp=y.split("@");
			if(temp[1].equals("iiitd.ac.in") && temp[0].length()>1) {
				if(Character.isLetter(temp[0].charAt(0))){
					if(temp[0].matches("[A-Za-z0-9]+")) {
						this.emailID=y;
						return 0; //okay proceed
					}}
		}
		}
		return 2; //bad email including special characters or missing @
	}
	public boolean validateLogin() {
		StringBuilder x=new StringBuilder();
		for(int i=0;i<emailID.length();i++) {
			if(!(emailID.charAt(i)==' ')) {
				x.append(emailID.charAt(i));
			}
		}
		String y=x.toString();
		
		User temp=User.getUser(y, false);
		if(temp==null) {
			return false;
		}
		return true;
	}	
	public String getEmailID() {
		return emailID;
	}
	
	
}
