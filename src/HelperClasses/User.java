package HelperClasses;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	protected String Name;
	private String Password; 	//User account password
	protected Email emailID; 		//Email of user
	protected String userType;	//Student, Faculty and Admin
	public User(String name, String password, Email emailID, String userType) {
		Name = name;
		Password = password;
		this.emailID = emailID;
		this.userType = userType;
	}
	public boolean authenticate(String password) {
		if(this.Password.equals(password)) {
			return true;
		}
		return false;
	}
	public boolean changePassword(String oldPassword, String newPassword) {
		if(authenticate(oldPassword)) {
			if(newPassword.length()!=0) {
					boolean b=newPassword.matches("[A-Za-z0-9]+");
					if(b) {
						Password=newPassword;
						serialize();
						return true;
					}
		}
		}
		return false;
	}
	public void serialize() {
		try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./AppData/User/"+this.emailID+".txt"));
                out.writeObject(this);
            }
            finally {
                if(out!=null){
                    out.close();
                }
            }

        }
        catch (IOException e){
            System.out.println("file not found");
        }
	}
	public void logout() {
		
	}
	public String getName() {
		return Name;
	}
	public String getPassword() {
		return Password;
	}
	public Email getEmail() {
		return emailID;
	}
	public String getUsertype() {
		return userType;
	}
}
