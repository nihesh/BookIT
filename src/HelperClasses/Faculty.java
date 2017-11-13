package HelperClasses;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;


public class Faculty extends User{
	private static final long serialVersionUID = 1L;
	private ArrayList<String> myCourses;
	public Faculty(String name, String password, Email emailID, String userType, ArrayList<String> myCourses) {
		super(name, password, emailID, userType);
		this.myCourses = myCourses;
	}
	public ArrayList<String> getCourses() {
		return myCourses;
	}
	
	public void addCourse(String course){
		myCourses.add(course);
		serialize(true);
		this.setActiveUser();
	}
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
