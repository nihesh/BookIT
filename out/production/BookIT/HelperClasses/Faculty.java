package HelperClasses;

import java.io.File;
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
	public void addCourse(String course){
		myCourses.add(course);
		serialize(false);
		this.setActiveUser();
	}
	/**
	 * cancel a booking done by faculty
	 * @param queryDate date on which to cancel booking
	 * @param slotID time slot
	 * @param RoomID room name
	 * @return true if booking cancels, false otherwise
	 */
	public boolean cancelBooking(LocalDate queryDate,int slotID, String RoomID) {
		Room temp=Room.deserializeRoom(RoomID, false);
		Reservation r=temp.getSchedule(queryDate)[slotID];
		
		Course c=r.getCourse();
		if(c!=null) {
			if(myCourses.contains(r.getCourseName())) {
				temp.deleteReservation(queryDate, slotID);
				 c.deleteReservation(queryDate, slotID,r.getTopGroup());
				 return true;
			}
			}
		return false;
	}
	/**
	 * book a room on a date and time
	 * @param queryDate date on which booking needs to be done
	 * @param slot time slot 
	 * @param r reservation object describing details of reservation
	 * @return true if booking successful, false otherwise
	 */
	public boolean bookRoom(LocalDate queryDate,int slot, Reservation r) {
		Room room=Room.deserializeRoom(r.getRoomName(), false);
		Boolean addToCourse = true;
		if(r.getCourseName().equals("")){
			addToCourse = false;
		}
		Course course;
		if(addToCourse) {
			course = Course.deserializeCourse(r.getCourseName(), false);
			if(course.checkReservation(queryDate,slot,r)==true && room.checkReservation(queryDate,slot,r)==true) {
				course.addReservation(queryDate,slot,r,true);
				room.addReservation(queryDate,slot,r,true);
				return true;
			}
		}
		else{
			if(room.checkReservation(queryDate,slot,r)==true){
				room.addReservation(queryDate,slot,r,true);
				return true;
			}
		}
		return false;
	}
}
