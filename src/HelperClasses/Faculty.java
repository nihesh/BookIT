package HelperClasses;

import java.util.ArrayList;
import java.util.Date;

public class Faculty extends User{
	private static final long serialVersionUID = 1L;
	private ArrayList<Course> myCourses;
	public Faculty(String name, String password, Email emailID, String userType, ArrayList<Course> myCourses) {
		super(name, password, emailID, userType);
		this.myCourses = myCourses;
	}
	public ArrayList<Course> getCourses() {
		return myCourses;
	}
	public boolean cancelBooking(Date queryDate,int slotID) {
		
	}
	public boolean bookRoom(Date queryDate,int slot, Reservation r) {
		
	}
}
