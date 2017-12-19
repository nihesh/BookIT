package HelperClasses;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import HelperClasses.Course;
/**
 * The student class derived from user class
 * @author Harsh
 * @author Nihesh
 *
 */
public class Student extends User{
	private static final long serialVersionUID = 1L;
	protected String Batch;
	private ArrayList<String> myCourses;
	/**
	 * Constructor for the student class
	 * @param name name of the student
	 * @param password password for student account
	 * @param emailID emailID of student
	 * @param userType userType - student here
	 * @param batch batch of student
	 * @param myCourses courses of a student
	 */
	public Student(String name, String password, Email emailID, String userType, String batch,
			ArrayList<String> myCourses) {
		super(name, password, emailID, userType);
		Batch = batch;
		this.myCourses = myCourses;
	}
	/**
	 * sends a reservation request to the Request queue for the admins to see
	 * @param r ArrayList of reservation objects corresponding to different time slots 
	 * @return true if successful false otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean sendReservationRequest(ArrayList<Reservation> r, Boolean lock){
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
			out.writeObject("student_sendReservationRequest");
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
			System.out.println("IO Exception occurred while sending reservation request");
		}
		catch (ClassNotFoundException c){
			System.out.println("ClassNotFound exception occurred while sending reservation request");
		}
		return false;
	}
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
	 * searches for a course on basis of a search string
	 * @param keyword the search string
	 * @return ArrayList of string that refer to courses whose post conditions match with search string
	 */
	public static ArrayList<String> searchCourse(ArrayList<String> keyword, Boolean lock){
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
			out.writeObject("student_searchCourse");
			out.flush();
			out.writeObject(keyword);
			out.flush();
			ArrayList<String> c = (ArrayList<String>) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while searching course");
		}
		catch (ClassNotFoundException c){
			System.out.println("ClassNotFound exception occurred while searching course");
		}
		return null;
	}
	/**
	 * adds a course to timetable of a student
	 * @param c the course to be added
	 * @return true if successful false otherwise
	 */
	public boolean addCourse(String c, Boolean lock) {
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
			out.writeObject("student_addCourse");
			out.flush();
			out.writeObject(c);
			out.flush();
			out.writeObject(this.getEmail().getEmailID());
			out.flush();
			Boolean res = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			if(res){
				myCourses.add(c);
				this.setActiveUser();
			}
			return res;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while adding course");
		}
		catch (ClassNotFoundException x){
			System.out.println("ClassNotFound exception occurred while adding course");
		}
		return false;
	}
	/**
	 * getter for returning batch of student
	 * @return String
	 */
	public String getBatch() {
		return Batch;
	}
	/**
	 * returns list of student's courses
	 * @return ArrayList of String
	 */
	public ArrayList<String> getMyCourses() {
		return myCourses;
	}
	
	
}
