package HelperClasses;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;
/**
 * The user class for modeling faculty,student and admin objects
 * @author Harsh
 * @author Nihesh
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	protected String Name;
	private String Password; 	//User account password
	protected Email emailID; 		//Email of user
	protected String userType;	//Student, Faculty and Admin
	/**
	 * constructor for the user class
	 * @param name name of the user
	 * @param password password of user account
	 * @param emailID emailID of the user
	 * @param userType user type - choices are faculty, admin and student
	 */
	public User(String name, String password, Email emailID, String userType) {
		Name = name;
		Password = password;
		this.emailID = emailID;
		this.userType = userType;
		
	}
	/**
	 * sets the user object as the active user in the databse and opens the account of the user
	 */
	public void setActiveUser() {
		try{
			File file=new File("./src/AppData/ActiveUser/ActiveUser.txt");
			file.createNewFile();
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
	/**
	 * return the active user from the database
	 * @return User object
	 */
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
	/**
	 * returns a user object from the database by using the user email
	 * @param email email of the user
	 * @return User class object
	 */
	public static User getUser(String email) {
		ObjectInputStream in = null;
		try{
			in = new ObjectInputStream(new FileInputStream("./src/AppData/User/"+email+".txt"));
			return (User)in.readObject();
		}
		catch (Exception e){
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
	/**
	 * authenticates a user by checking the password typed and the real password of the 
	 * user in the database during login
	 * @param password password typed during login
	 * @return true if validated false otherwise
	 */
	public boolean authenticate(String password) {   //login version
		if(this.Password.equals(password)) {
			return true;
		}
		return false;
	}
	/**
	 * change password of a user
	 * @param oldPassword the existing password
	 * @param newPassword the new password
	 * @return true if successful, false otherwise
	 */
	public boolean changePassword(String oldPassword, String newPassword) {
		if(authenticate(oldPassword)) {
			if(newPassword.length()!=0) {
					boolean b=newPassword.matches("[A-Za-z0-9]+");
					if(b) {
						Password=newPassword;
						serialize();
						this.setActiveUser();
						return true;
					}
		}
		}
		return false;
	}
	/**
	 * serializes a user back to the server database
	 */
	public void serialize(){
		try{
			ObjectOutputStream out = null;
			try{
				out = new ObjectOutputStream(new FileOutputStream("./src/AppData/User/"+this.getEmail().getEmailID()+".txt", false));
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
	/**
	 * used to logout a user currently logged in
	 * @throws LoggedOutException exception generated when the user is logged out
	 */
	public void logout() throws LoggedOutException{
		File file=new File("./src/AppData/ActiveUser/ActiveUser.txt");
		file.delete();
		throw new LoggedOutException();
	}
	/**
	 * 
	 * @return name of the user
	 */
	public String getName() {
		return Name;
	}
	/**
	 * 
	 * @return password of the user
	 */
	public String getPassword() {
		return Password;
	}
	/**
	 * 
	 * @return email of the user
	 */
	public Email getEmail() {
		return emailID;
	}
	/**
	 * 
	 * @return email type of the user
	 */
	public String getUsertype() {
		return userType;
	}
	
}
