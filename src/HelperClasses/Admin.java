package HelperClasses;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <h1> The Admin User class derived from user class</h1>
 * <p>
 * class for admin user type
 * </p>
 * @author Harsh Pathak
 * @author Nihesh Anderson
 * @version 1.0
 * @since 29-10-2017
 *  	
 */
public class Admin extends User{
	/**
	 * Joincode String used to generate JoinCodes
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constructor for admin class
	 * @param name name of the admin 
	 * @param password password of the admin
	 * @param emailID the email class 
	 * @param userType denotes whether user is Admin/Faculty/Student. Here this is Admin
	 */
	public Admin(String name, String password, Email emailID, String userType) {
		super(name,password,emailID,userType);
	}
	/**
	 * used to access all the joincodes of the users. 
	 * Joincodes qre used to determine the usertype of a user who is signing up. 
	 * joincode is similar to license key here
	 * @param lock takes a lock on server if set to true
	 * @return returns hashmap of all joincodes. The value(Int) refers to 2 values, 0 or 1 
	 * @throws FileNotFoundException file doesn't exist
	 * @throws IOException Io exception
	 * @throws ClassNotFoundException de-serialize issue 
	 */
	public static Boolean containsJoinCode(String joinCode, Boolean lock){
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
			out.writeObject("containsJoinCode");
			out.flush();
			out.writeObject(joinCode);
			out.flush();
			Boolean result = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			return  result;
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while getting request");
		}
		catch (ClassNotFoundException ce){
			System.out.println("Class not found exception occured while getting request");
		}
		catch (IOException ie){
			System.out.println(ie.getMessage());
			System.out.println("IOException occured while getting request");
		}
		return null;
	}
	public static void removeJoinCode(String joinCode, Boolean lock){
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
			out.writeObject("removeJoinCode");
			out.flush();
			out.writeObject(joinCode);
			out.flush();
			out.close();
			in.close();
			server.close();
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while getting request");
		}
		catch (IOException ie){
			System.out.println("IOException occured while getting request");
		}
	}
	public void softResetServer(Boolean lock){
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
			out.writeObject("softResetServer");
			out.flush();
			out.close();
			in.close();
			server.close();
		}
		catch (IOException ie){
			System.out.println("IOException occured while soft resetting the server");
		}
	}
	public static Boolean checkBulkBooking(String room, ArrayList<Integer> slots, ArrayList<LocalDate> date, Boolean lock){
		try {
			File file = new File("./src/AppData/GeneratedJoinCode/list.txt");
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
			out.writeObject("checkBulkBooking");
			out.flush();
			out.writeObject(room);
			out.flush();
			out.writeObject(slots);
			out.flush();
			out.writeObject(date);
			out.flush();
			Boolean c = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while checking bulk booking");
		}
		catch (ClassNotFoundException ce){
			System.out.println("Class not found exception occured while checking bulk booking");
		}
		catch (IOException ie){
			System.out.println("IOException occured while checking bulk booking");
		}
		return false;
	}
	/**
	 * 
	 * @param type denotes the type(Admin/Faculty/Student) 
	 * for which join code is to be generated
	 * @return returns the randomly generated join code
	 */
	public String generateJoincode(String type, Boolean lock){
		try {
			File file = new File("./src/AppData/GeneratedJoinCode/list.txt");
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
			out.writeObject("generateJoinCode");
			out.flush();
			out.writeObject(type);
			out.flush();
			String sb = (String) in.readObject();
			out.close();
			in.close();
			server.close();
			FileWriter sc=new FileWriter(file,true);
			sc.write("["+LocalDateTime.now()+"]\t"+sb.toString()+"\n");
			sc.flush();
			sc.close();
			return sb.toString();
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while generating join code");
		}
		catch (ClassNotFoundException ce){
			System.out.println("Class not found exception occured while generating join code");
		}
		catch (IOException ie){
			System.out.println(ie.getMessage());
			System.out.println("IOException occured while generating join code");
		}
		return null;
	}
	/**
	 * returns the top request in the priority queue
	 * @return The top request; an array list of reservation objects
	 */
	public ArrayList<Reservation> getRequest(Boolean lock){
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
			out.writeObject("getRequest");
			out.flush();
			ArrayList<Reservation> c = (ArrayList<Reservation>) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while getting request");
		}
		catch (ClassNotFoundException c){
			System.out.println("ClassNotFound exception occurred while getting request");
		}
		return null;
	}
	/**
	 * accepts the top request in the request queue
	 * @return true if accepted and false if not able to accept because of time table clashes
	 */
	public boolean acceptRequest(ArrayList<Integer> data, Boolean lock){
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
			out.writeObject("acceptRequest");
			out.flush();
			out.writeObject(data);
			out.flush();
			Boolean c = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while accepting request");
		}
		catch (ClassNotFoundException c){
			System.out.println("ClassNotFound exception occurred while accepting request");
		}
		return false;
	}
	/**
	 * rejects the request at top of the queue
	 * @return true if requests gets requested, false for handling empty parameters
	 */
	public boolean rejectRequest(Boolean lock){
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
			out.writeObject("rejectRequest");
			out.flush();
			Boolean c = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while rejecting request");
		}
		catch (ClassNotFoundException c){
			System.out.println("ClassNotFound exception occurred while rejecting request");
		}
		return false;
	}
	/**
	 * allows admin to cancel a reservation 
	 * @param queryDate the date on which reservation is to be cancelled
	 * @param slotID the time slot for the reservation to be cancelled
	 * @param RoomID the room for which reservation has to be cancelled
	 * @return true if it is cancelled false otherwise
	 */
	public boolean cancelBooking(LocalDate queryDate,int slotID,String RoomID, String cancellationMessage, Boolean lock) {
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
			out.writeObject("admin_BookingCancelNotification");
			out.flush();
			out.writeObject(queryDate);
			out.flush();
			out.writeObject(slotID);
			out.flush();
			out.writeObject(RoomID);
			out.flush();
			out.writeObject(cancellationMessage);
			out.flush();
			out.writeObject(emailID.getEmailID());
			out.flush();
			out.close();
			in.close();
			server.close();
			return true;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while cancelling room");
		}
		return false;
	}
	/**
	 * allows admin to book a free room on a date in a room
	 * @param slot the time(30 minute slot)
	 * @param r the reservation object see also {@link Reservation} class
	 * @return true if booked false otherwise
	 */
	public boolean bookRoom(ArrayList<LocalDate> date, int slot, Reservation r, Boolean lock) {
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
			out.writeObject("adminandfaculty_bookroom");
			out.flush();
			out.writeObject(date);
			out.flush();
			out.writeObject(slot);
			out.flush();
			out.writeObject(r);
			out.flush();
			Boolean c = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
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
}
