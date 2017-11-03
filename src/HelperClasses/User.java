package HelperClasses;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
	public static User getUser(String email) {
		ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream("./src/AppData/User/"+email+".txt"));
            return (User)in.readObject();
        }
        catch (Exception e){
            System.out.println("Exception occured while deserialising Course");
            return null;
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch(IOException f){
                ;
            }
        }
	}
	public boolean authenticate(String password) {   //login version
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
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/User/"+this.emailID.getEmailID()+".txt"));
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
	public static void main(String[] args) {
		Email e=new Email("ha rsh1  6041 @iiit d.ac.in");
		User u=new User("Harsh Pathak", "abcd", e, "Admin");
		System.out.println(u.changePassword("abcd", "abcd"));
	}
}
