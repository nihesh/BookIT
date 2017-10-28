package HelperClasses;

import java.util.ArrayList;
import java.util.Date;

public class Admin extends User{
	private static final long serialVersionUID = 1L;
	public Admin(String name, String password, Email emailID, String userType) {
		super(name,password,emailID,userType);
	}
	public ArrayList<Reservation> getRequest(){
		
	}
	public boolean acceptRequest(ArrayList<Reservation> r){
		return true;
	}
	public boolean rejectRequest(ArrayList<Reservation> r) {
		return true;
	}
	public boolean cancelBooking(Date queryDte) {
		return true;
	}
	public boolean bookRoom() {
		return true;
	}
}
