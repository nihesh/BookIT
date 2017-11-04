package HelperClasses;

import java.io.File;
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
import java.util.PriorityQueue;

public class Admin extends User{
	private static Integer a=0;
	private static Integer s=0;
	private static Integer f=0;
	private static final long serialVersionUID = 1L;
	public Admin(String name, String password, Email emailID, String userType) {
		super(name,password,emailID,userType);
	}
	public String generateJoincode(String type) {
		try {
		StringBuilder sb=new StringBuilder();
		if(type.equals("Admin")) {
			sb.append("A");
			if(a<10) {
				sb.append("0");
				sb.append("0");
				sb.append(a.toString());
			}
			else if(a<100) {
				sb.append("0");
				sb.append(a.toString());
			}
			else if(a<1000) {
				sb.append(a.toString());
			}
			a++;
			if(a>=1000) {
				return "failed";
			}
		}
		else if(type.equals("Student")) {
			sb.append("S");
			if(s<10) {
				sb.append("0");
				sb.append("0");
				sb.append(s.toString());
			}
			else if(s<100) {
				sb.append("0");
				sb.append(s.toString());
			}
			else if(s<1000) {
				sb.append(s.toString());
			}
			s++;
			if(s>=1000) {
				return "failed";
			}
		}
		else {
			sb.append("F");
			if(f<10) {
				sb.append("0");
				sb.append("0");
				sb.append(f.toString());
			}
			else if(f<100) {
				sb.append("0");
				sb.append(f.toString());
			}
			else if(f<1000) {
				sb.append(f.toString());
			}
			f++;
			if(f>=1000) {
				return "failed";
			}
		}
		String temp=sb.toString();
		File file=new File("./src/AppData/JoinCodes/"+temp+".txt");
		file.createNewFile();
		return temp;}
		catch(IOException e) {
			System.out.println(e);
			return "failed";
		}
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
		temp.deleteReservation(queryDate, slotID);
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
