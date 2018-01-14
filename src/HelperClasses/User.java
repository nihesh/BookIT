package HelperClasses;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
/**
 * The user class for modeling faculty,student and admin objects
 * @author Harsh
 * @author Nihesh
 */
public class User implements Serializable{
	ArrayList<Notification> notifications = new ArrayList<Notification>();
	private static final long serialVersionUID = 1L;
	protected String Name;
	private String Password; 	//User account password
	protected Email emailID; 	//Email of user
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
	public void clearNotifications(){
		notifications.clear();
	}
	public ArrayList<Notification> getterNotification(){
		return notifications;
	}
	public void addNotification(Notification r) {
		this.notifications.add(r);
		while(this.notifications.size()>100) {
			this.notifications.remove(0);
    		
    	}
	}
	public void setNotification(ArrayList<Notification> x) {
		notifications = x;
	}
	public ArrayList<Notification> getNotifications(boolean lock){
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("getNotifications");
			out.flush();
			out.writeObject(this.emailID.getEmailID());
			out.flush();
			ArrayList<Notification> c = (ArrayList<Notification>) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;	}
		catch (IOException e){
			System.out.println("IO exception occurred while writing to server");
		}
		catch (ClassNotFoundException c){
			System.out.println("Class not found exception occurred while getting user type");
		}
		return null;
	}
	public void setPassword(String password){
		Password = password;
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
            System.out.println("Exception occured while deserialising ActiveUser");
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
	public void mailPass(Boolean lock){
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("mailPass");
			out.flush();
			out.writeObject(this.emailID.getEmailID());
			out.flush();
			out.close();
			in.close();
			server.close();
		}
		catch (IOException e){
			System.out.println("IO exception occurred while writing to server");
		}
	}
	public static String getUserType(String email, Boolean lock){
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("getUserType");
			out.flush();
			out.writeObject(email);
			out.flush();
			String c = (String) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch (IOException e){
			System.out.println("IO exception occurred while writing to server");
		}
		catch (ClassNotFoundException c){
			System.out.println("Class not found exception occurred while getting user type");
		}
		return "";
	}
	public void generatePass(Boolean lock){
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("generatePass");
			out.flush();
			out.writeObject(this.emailID.getEmailID());
			out.flush();
			out.close();
			in.close();
			server.close();
		}
		catch (IOException e){
			System.out.println("IO exception occurred while writing to server");
		}
	}
	/**
	 * returns a user object from the database by using the user email
	 * @param email email of the user
	 * @return User class object
	 */
	public static User getUser(String email,boolean lock) {
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("GetUser");
			out.flush();
			out.writeObject(email);
			out.flush();
			User c = (User) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch (IOException e){
			System.out.println("IO exception occurred while writing to server");
		}
		catch (ClassNotFoundException x){
			System.out.println("ClassNotFound exception occurred while reading from server");
		}
		return null;
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
	public boolean changePassword(String oldPassword, String newPassword, Boolean lock) {
		try{
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("changePassword");
			out.flush();
			out.writeObject(this.getEmail().getEmailID());
			out.flush();
			out.writeObject(oldPassword);
			out.flush();
			out.writeObject(newPassword);
			out.flush();
			Boolean c = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			if(c){
				this.setPassword(newPassword);
				this.setActiveUser();
			}
			return c;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while booking room");
		}
		catch (ClassNotFoundException c){
			System.out.println("Class not found exception occurred while booking room");
		}
		return false;
	}
	/**
	 * serializes a user back to the server database
	 */
	public void serialize(Boolean lock) {
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("WriteUser");
			out.flush();
			out.writeObject(this);
			out.close();
			in.close();
			server.close();
		}
		catch (IOException e){
			System.out.println("IO exception occured while writing to server");
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
