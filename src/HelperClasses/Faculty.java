package HelperClasses;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

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
	public boolean cancelBooking(LocalDate queryDate,int slotID, String RoomID) {
		Room temp=Room.deserializeRoom(RoomID);
		Reservation r=temp.getSchedule(queryDate)[slotID];
		
		Course c=r.getCourse();
		if(c!=null) {
			if(myCourses.contains(r.getCourseName())) {
				temp.getSchedule(queryDate)[slotID]=null;
				 c.deleteReservation(queryDate, slotID,r.getTopGroup());
				 return true;
			}
			}
		return false;
	}
	public boolean bookRoom(LocalDate queryDate,int slot, Reservation r) {
		Room room=Room.deserializeRoom(r.getRoomName());
		Course course=Course.deserializeCourse(r.getCourseName()+".dat");
		if(course.checkReservation(queryDate,slot,r)==true && room.checkReservation(queryDate,slot,r)==true) {
			course.addReservation(queryDate,slot,r,true);
			room.addReservation(queryDate,slot,r,true);
			return true;
		}
		return false;
	}
}
