package HelperClasses;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	public boolean sendReservationRequest(ArrayList<Reservation> r){
		try {
			PriorityQueue<ArrayList<Reservation>> p = null;
				p = Admin.deserializeRequestsQueue(false);
				p.add(r);
				Admin.serializeRequestsQueue(p, false);
			
		}
		catch(IOException e){
			System.out.println("IO Exception while deserialising priority queue");
		}
		catch (ClassNotFoundException f){
			System.out.println("Class not found exception while deserialising priority queue");
		}
		return true;
	}
	//marker need to check
	/**
	 * searches for a course on basis of a search string
	 * @param keyword the search string
	 * @return ArrayList of string that refer to courses whose post conditions match with search string
	 */
	public static ArrayList<String> searchCourse(ArrayList<String> keyword){
		int flag=1;
		for(int i=0;i<keyword.size();i++){
			if(!keyword.get(i).equals("")){
				flag=0;
				break;
			}
		}
		if(flag==1){
			return Course.getAllCourses();
		}
		ArrayList<ArrayList<String>> arr=new ArrayList<ArrayList<String>>();
		for (int i=0;i<300;i++) {
			arr.add(new ArrayList<String>());
		}
		
		ArrayList<String> temp2=new ArrayList<String>();
		
		ArrayList<String> courseFiles=Course.getAllCourses();
		for(int i=0;i<courseFiles.size();i++) {
			String courseName = courseFiles.get(i).substring(0,courseFiles.get(i).length());
			Course temp=Course.deserializeCourse(courseName, false);
			int match=temp.keyMatch(keyword);
			if(match > 0) {
				arr.get(match).add(courseFiles.get(i).substring(0,courseFiles.get(i).length()));
			}
		}
		for(int i=arr.size()-1;i>=0;i--) {
			for (String str : arr.get(i)) {
				temp2.add(str);
			}
		}
		return temp2;
	}
	/**
	 * adds a course to timetable of a student
	 * @param c the course to be added
	 * @return true if successful false otherwise
	 */
	public boolean addCourse(String c) {
		Course c2=Course.deserializeCourse(c, false);
		for (String string : myCourses) {
			Course temp=Course.deserializeCourse(string, false);
			if(c2.checkCollision(temp)) {
				return false; //cannot add course since there is a collision
			}
		}
		myCourses.add(c);
		this.serialize(false);
		this.setActiveUser();
		return true;
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
