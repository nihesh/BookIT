package HelperClasses;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.PriorityQueue;

public class Admin extends User{
	private static final long serialVersionUID = 1L;
	public Admin(String name, String password, Email emailID, String userType) {
		super(name,password,emailID,userType);
	}
	@SuppressWarnings("unchecked")
	public static PriorityQueue<ArrayList<Reservation>> deserializeRequestsQueue() throws FileNotFoundException, IOException, ClassNotFoundException {
		PriorityQueue<ArrayList<Reservation>> p=null;
		ObjectInputStream in=null;
		try
	        {   
			 in = new ObjectInputStream(new FileInputStream("./src/AppData/Requests/requests.txt"));
	         p = ((PriorityQueue<ArrayList<Reservation>>)in.readObject());  
	         }
		
		 finally {
			 if(in!=null) {
				 in.close();
				 }
			 
		 }
		return p;
	
	}
	
	public static void serializeRequestsQueue(PriorityQueue<ArrayList<Reservation>> r) {
		try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Requests/requests.txt"));
                out.writeObject(r);
            }
            finally {
                if(out!=null){
                    out.close();
                }
            }

        }
        catch (IOException e){
            System.out.println("file not found");
        }
	}

	public ArrayList<Reservation> getRequest() throws FileNotFoundException, ClassNotFoundException, IOException{
		PriorityQueue<ArrayList<Reservation>> p=deserializeRequestsQueue();
		ArrayList<Reservation> temp=p.poll();
		while(temp!=null && temp.get(0).getCreationDate().plusDays(5).isBefore(LocalDateTime.now())) {
			temp=p.poll();
		}
		while(temp!=null && temp.get(0).getTargetDate().isBefore(LocalDate.now())) {
			temp=p.poll();
		}
		return temp;
	}
	public boolean acceptRequest(ArrayList<Reservation> r){
		if(r!=null) {
			
		};
	}
	public boolean rejectRequest(ArrayList<Reservation> r) {
		return true;
	}
	public boolean cancelBooking(LocalDate queryDate,int slotID,String RoomID) {
		return true;
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
