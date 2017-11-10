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
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

public class Admin extends User{
	private static String JoinString="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final long serialVersionUID = 1L;
	public Admin(String name, String password, Email emailID, String userType) {
		super(name,password,emailID,userType);
	}
	@SuppressWarnings("unchecked")
	public static  HashMap<String, Integer> deserializeJoinCodes() throws FileNotFoundException, IOException, ClassNotFoundException {
		HashMap<String, Integer> p=null;
		ObjectInputStream in=null;
		try
		{
			in = new ObjectInputStream(new FileInputStream("./src/AppData/JoinCodes/Codes.txt"));
			p = (HashMap<String, Integer>)in.readObject();
		}
		finally {
			if(in!=null) {
				in.close();
			}
		}
		return p;
	}
	
	public static void serializeJoinCode(HashMap<String, Integer> r) {
		try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/JoinCodes/Codes.txt", false));
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
	public String generateJoincode(String type){
		try {
			type = type.substring(0, 1).toUpperCase();
			Random rnd = new Random();
			StringBuilder sb = new StringBuilder();
			HashMap<String, Integer> codes = deserializeJoinCodes();
			sb.append(type);
			while (true) {
				while (sb.length() != 7) {
					System.out.println();
					sb.append(JoinString.charAt(((int)(rnd.nextFloat() * JoinString.length()))));
				}
				if (codes.containsKey(sb.toString()) && codes.get(sb.toString()) == 1) {
					sb = new StringBuilder();
					sb.append(type);
				} else {
					break;
				}
			}
			codes.put(sb.toString(), 1);
			serializeJoinCode(codes);
			return sb.toString();
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while getting request");
		}
		catch (ClassNotFoundException ce){
			System.out.println("Class not found exception occured while getting request");
		}
		catch (IOException ie){
			System.out.println("IOException occured while getting request");
		}
		return null;
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

	public ArrayList<Reservation> getRequest(){
		try {
			PriorityQueue<ArrayList<Reservation>> p = deserializeRequestsQueue();
			ArrayList<Reservation> r = p.peek();
			while (r != null && r.get(0).getCreationDate().plusDays(5).isBefore(LocalDateTime.now())) {
				p.poll();
				r = p.peek();
			}
			while (r != null && r.get(0).getTargetDate().isBefore(LocalDate.now())) {
				p.poll();
				r = p.peek();
			}
			int flag=0;
			Room temp = Room.deserializeRoom(r.get(0).getRoomName());
			Course ctemp = Course.deserializeCourse(r.get(0).getCourseName());
			while(r!=null) {
			for (Reservation reservation : r) {
				if (!temp.checkReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation)) {
					p.poll();
					flag=1;
					r=p.peek();
					break;
				}
				if(ctemp!=null) {
					if (ctemp.checkInternalCollision(reservation)) {
						p.poll();
						flag=1;
						r=p.peek();
						break;
						}
				}
				flag=0;
					
				
			}
			if(flag==0) {
				break;
			}
			}
			
			serializeRequestsQueue(p);
			return r;
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while getting request");
		}
		catch (ClassNotFoundException ce){
			System.out.println("Class not found exception occured while getting request");
		}
		catch (IOException ie){
			System.out.println("IOException occured while getting request");
		}
		return null;
	}
	public boolean acceptRequest(){
		try {
			PriorityQueue<ArrayList<Reservation>> p = deserializeRequestsQueue();
			ArrayList<Reservation> r = p.peek();
			if (r == null) {
				return false;
			}
			p.poll();
			serializeRequestsQueue(p);
			int flag=0;
			Room temp = Room.deserializeRoom(r.get(0).getRoomName());
			Course ctemp = Course.deserializeCourse(r.get(0).getCourseName());
			while(r!=null) {
			for (Reservation reservation : r) {
				if (!temp.checkReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation)) {
					p.poll();
					flag=1;
					r=p.peek();
					break;
				}
				if(ctemp!=null) {
					if (ctemp.checkInternalCollision(reservation)) {
						p.poll();
						flag=1;
						r=p.peek();
						break;
						}
				}
				flag=0;
					
				
			}
			if(flag==0) {
				break;
			}
			}
			if(r!=null) {
				for (Reservation reservation : r) {
					temp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation, true);
					if(ctemp!=null) {
						ctemp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation, true);
					}
				}
			}
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while accepting request");
		}
		catch (ClassNotFoundException ce){
			System.out.println("Class not found exception occured while accepting request");
		}
		catch(IOException ie){
			System.out.println("IO exception occured while accepting request");
		}
		return false;
	}
	public boolean rejectRequest(){
		try{
			PriorityQueue<ArrayList<Reservation>> p = deserializeRequestsQueue();
			if (p.size() == 0) {
				return false;
			}
			p.poll();
			serializeRequestsQueue(p);
			return true;
		}
		catch (FileNotFoundException fe){
			System.out.println("File not found exception occured while rejecting request");
		}
		catch (ClassNotFoundException ce){
			System.out.println("Class not found exception occured while rejecting request");
		}
		catch(IOException ie){
			System.out.println("IO exception occured while rejecting request");
		}
		return false;
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
		Boolean addToCourse = true;
		if(r.getCourseName().equals("")){
			addToCourse = false;
		}
		Course course;
		if(addToCourse) {
			course = Course.deserializeCourse(r.getCourseName());
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
	public static void main(String[] args) {
		
	}
	
}
