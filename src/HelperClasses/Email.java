package HelperClasses;

import java.io.Serializable;

public class Email implements Serializable{
	private static final long serialVersionUID = 1L;
	private static  String domain="@iiitd.ac.in";
	private static String check="@@iiitd.ac.in";
	private String emailID;
	public Email(String x) {
		emailID=x;
	}
	public boolean validate() {
		StringBuilder x=new StringBuilder();
		for(int i=0;i<emailID.length();i++) {
			if(!(emailID.charAt(i)==' ')) {
				x.append(emailID.charAt(i));
			}
		}
		String y=x.toString();
		
		if(y.contains(domain) && 
				!y.contains(check) && 
				(y.indexOf(domain)+12==y.length())) {
			String[] temp=y.split("@");
			if(temp[1].equals("iiitd.ac.in") && temp[0].length()>1) {
				if(Character.isLetter(temp[0].charAt(0))){
					if(temp[0].matches("[A-Za-z0-9]+")) {
						this.emailID=y;
						return true;
					}
			}
			
		}
			
	}
		return false;
	}
	public String getEmailID() {
		return emailID;
	}
	
	
}
