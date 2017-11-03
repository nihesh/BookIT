package HelperClasses;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
		ArrayList<Reservation> temp=p.peek();
		while(temp!=null && temp.get(0).getCreationDate().plusDays(5).isBefore(LocalDateTime.now())) {
			p.poll();
			temp=p.peek();
		}
		while(temp!=null && temp.get(0).getTargetDate().isBefore(LocalDate.now())) {
			p.poll();
			temp=p.peek();
		}
		serializeRequestsQueue(p);
		return temp;
	}
	public boolean acceptRequest(ArrayList<Reservation> r) throws FileNotFoundException, ClassNotFoundException, IOException{
		if(r==null) {
			return false;
		}
		PriorityQueue<ArrayList<Reservation>> p=deserializeRequestsQueue();
		p.poll();
		serializeRequestsQueue(p);
		Room temp = Room.deserializeRoom(r.get(0).getRoomName());
		Course ctemp=Course.deserializeCourse(r.get(0).getCourseName());
		for (Reservation reservation : r) {
			if(!temp.checkReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation)) {
				return false;
			}
			
		}
		for (Reservation reservation : r) {
			temp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation, true);
			if(ctemp!=null) {
				ctemp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation, true);
			}
			
		}
		
		return true;
	}
	public boolean rejectRequest(ArrayList<Reservation> r) throws FileNotFoundException, ClassNotFoundException, IOException {
		if(r==null) {
			return false;
		}
		PriorityQueue<ArrayList<Reservation>> p=deserializeRequestsQueue();
		p.poll();
		serializeRequestsQueue(p);
		return true;
	}
	public boolean cancelBooking(LocalDate queryDate,int slotID,String RoomID) {
		Room temp=Room.deserializeRoom(RoomID);
		Reservation r=temp.getSchedule(queryDate)[slotID];
		temp.getSchedule(queryDate)[slotID]=null;
		Course c=r.getCourse();
		if(c!=null) {
			c.deleteReservation(queryDate, slotID,r.getTopGroup());
			return true;
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
