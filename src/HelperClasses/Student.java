package HelperClasses;

import java.util.ArrayList;

public class Student extends User{
	protected String Batch;
	private ArrayList<Course> myCourses;
	public Student(String name, String password, Email emailID, String userType, String batch,
			ArrayList<Course> myCourses) {
		super(name, password, emailID, userType);
		Batch = batch;
		this.myCourses = myCourses;
	}
	public boolean sendReservationRequest(ArrayList<Reservation> r) {

	}
	public ArrayList<Course> searchCourse(String keyword){

	}
	public void addCourse(Course c) {

	}
	public String getBatch() {
		return Batch;
	}
	public ArrayList<Course> getMyCourses() {
		return myCourses;
	}
	
	
}

