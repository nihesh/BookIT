package HelperClasses;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The faculty class derived from user class 
 * @author Harsh
 *
 */
public class Faculty extends User{
	private static final long serialVersionUID = 1L;
	/**
	 * list of all the courses being taught by a faculty member
	 */
	private ArrayList<String> myCourses;
	/**
	 * constructor for faculty class
	 * @param name name of faculty
	 * @param password password for faculty account
	 * @param emailID emailID of instructor/faculty
	 * @param userType user type(Faculty here)
	 * @param myCourses list of courses
	 */
	public Faculty(String name, String password, Email emailID, String userType, ArrayList<String> myCourses) {
		super(name, password, emailID, userType);
		this.myCourses = myCourses;
	}
	/**
	 * getter function 
	 * @return list of courses instructor teaches
	 */
	public ArrayList<String> getCourses() {
		return myCourses;
	}
	/**
	 * add Course to list of courses a faculty teaches
	 * @param course course to be added
	 */
	public void addCourse(String course, Boolean lock){
		myCourses.add(course);
		this.setActiveUser();
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if (lock) {
				out.writeObject("Hold");
			} else {
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("faculty_addCourse");
			out.flush();
			out.writeObject(this.getEmail().getEmailID());
			out.flush();
			out.writeObject(course);
			out.flush();
			out.close();
			in.close();
			server.close();
		}
		catch (IOException e){
			System.out.println("IO Exception occurred while adding course");
		}
		this.setActiveUser();
	}
	/**
	 * cancel a booking done by faculty
	 * @param queryDate date on which to cancel booking
	 * @param slotID time slot
	 * @param RoomID room name
	 * @return true if booking cancels, false otherwise
	 */
	public boolean cancelBooking(LocalDate queryDate, int slotID, String RoomID, Boolean lock) {
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
			out.writeObject("studentandfaculty_cancelBooking");
			out.flush();
			out.writeObject(queryDate);
			out.flush();
			out.writeObject(slotID);
			out.flush();
			out.writeObject(RoomID);
			out.flush();
			out.writeObject(emailID.getEmailID());
			out.flush();
			Boolean c = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while sending reservation request");
		}
		catch (ClassNotFoundException c){
			System.out.println("ClassNotFound exception occurred while sending reservation request");
		}
		return false;
	}
	/**
	 * book a room on a date and time
	 * @param slot time slot 
	 * @param r reservation object describing details of reservation
	 * @return true if booking successful, false otherwise
	 */
	public boolean bookRoom(ArrayList<LocalDate> date, ArrayList<Integer> slot, Reservation r, Boolean lock) {
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
			out.writeObject(null);
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
