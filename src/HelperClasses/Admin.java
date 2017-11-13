package HelperClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
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
	public static  HashMap<String, Integer> deserializeJoinCodes(Boolean lock) throws FileNotFoundException, IOException, ClassNotFoundException {
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("ReadJoinCode");
			out.flush();
			HashMap<String, Integer> c = (HashMap<String, Integer>) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch (IOException e){
			System.out.println("IO exception occurred while writing to server");
		}
		catch (ClassNotFoundException x){
			System.out.println("ClassNotFound exception occurred while reading from server");
		}
		return null;
	}
	
	public static void serializeJoinCode(HashMap<String, Integer> r, Boolean lock) {
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("WriteJoinCode");
			out.flush();
			out.writeObject(r);
			out.close();
			in.close();
			server.close();
		}
		catch (IOException e){
			System.out.println("IO exception occured while writing to server");
		}
	}
	public String generateJoincode(String type){
		try {
			type = type.substring(0, 1).toUpperCase();
			Random rnd = new Random();
			StringBuilder sb = new StringBuilder();
			HashMap<String, Integer> codes = deserializeJoinCodes(true);
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
			serializeJoinCode(codes,false);
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
	public static PriorityQueue<ArrayList<Reservation>> deserializeRequestsQueue(Boolean lock) throws FileNotFoundException, IOException, ClassNotFoundException {
		Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
		ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(server.getInputStream());
		if(lock){
			out.writeObject("Hold");
		}
		else{
			out.writeObject("Pass");
		}
		out.flush();
		out.writeObject("ReadRequest");
		out.flush();
		PriorityQueue<ArrayList<Reservation>> c = (PriorityQueue<ArrayList<Reservation>>) in.readObject();
		out.close();
		in.close();
		server.close();
		return c;
	}
	
	public static void serializeRequestsQueue(PriorityQueue<ArrayList<Reservation>> r, Boolean lock) {
		try {
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if(lock){
				out.writeObject("Hold");
			}
			else{
				out.writeObject("Pass");
			}
			out.flush();
			out.writeObject("WriteRequest");
			out.flush();
			out.writeObject(r);
			out.close();
			in.close();
			server.close();
		}
		catch (IOException e){
			System.out.println("IO exception occured while writing to server");
		}
	}
	public Boolean checkSpam(String message) {
		try{
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			out.writeObject("Pass");
			out.flush();
			out.writeObject("SpamCheck");
			out.flush();
			out.writeObject(message);
			out.flush();
			Boolean c = (Boolean) in.readObject();
			out.close();
			in.close();
			server.close();
			return c;
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while checking spam");
		}
		catch (ClassNotFoundException c){
			System.out.println("ClassNotFound exception occurred while checking spam");
		}
		return true;
	}
	public ArrayList<Reservation> getRequest(){
		try {
			PriorityQueue<ArrayList<Reservation>> p = deserializeRequestsQueue(false);
			ArrayList<Reservation> r = p.peek();
			while (r != null && (checkSpam(r.get(0).getMessageWithoutVenue()) || r.get(0).getCreationDate().plusDays(5).isBefore(LocalDateTime.now()))) {
				p.poll();
				r = p.peek();
			}
			while (r != null && (checkSpam(r.get(0).getMessageWithoutVenue()) || r.get(0).getTargetDate().isBefore(LocalDate.now()))) {
				p.poll();
				r = p.peek();
			}
			if(r==null){
				serializeRequestsQueue(p, false);
				return null;
			}
			int flag=0;
			Room temp = Room.deserializeRoom(r.get(0).getRoomName(),false);
			Course ctemp = Course.deserializeCourse(r.get(0).getCourseName(), false);
			while(r!=null) {
				if(checkSpam(r.get(0).getMessageWithoutVenue())){
					p.poll();
					r = p.peek();
					continue;
				}
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
			serializeRequestsQueue(p, false);
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
			PriorityQueue<ArrayList<Reservation>> p = deserializeRequestsQueue(false);
			ArrayList<Reservation> r = p.peek();
			if (r == null) {
				serializeRequestsQueue(p,false);
				return false;
			}
			p.poll();
			int flag=0;
			Room temp = Room.deserializeRoom(r.get(0).getRoomName(), false);
			Course ctemp = Course.deserializeCourse(r.get(0).getCourseName(), false);
			System.out.println("hi");
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
			serializeRequestsQueue(p,false);
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
			PriorityQueue<ArrayList<Reservation>> p = deserializeRequestsQueue(false);
			if (p.size() == 0) {
				serializeRequestsQueue(p,false);
				return false;
			}
			p.poll();
			serializeRequestsQueue(p,false);
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
		Room temp=Room.deserializeRoom(RoomID, false);
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
