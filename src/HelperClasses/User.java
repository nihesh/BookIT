package HelperClasses;
import java.io.File;
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
	public void setActiveUser() {
		try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/ActiveUser/ActiveUser.txt", false));
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
	public static User getActiveUser() {
		ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream("./src/AppData/ActiveUser/ActiveUser.txt"));
            return (User)in.readObject();
        }
        catch (Exception e){
            System.out.println("Exception occured while deserialising User");
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
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/User/"+this.emailID.getEmailID()+".txt", false));
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
	public void logout() throws LoggedOutException{
		try {
		File file=new File("./src/AppData/ActiveUser/ActiveUser.txt");
		file.delete();
		file.createNewFile();
		throw new LoggedOutException();
		}
		catch(IOException e) {
			System.out.println("file not found");
		}
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
