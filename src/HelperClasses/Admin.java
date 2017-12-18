package HelperClasses;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <h1> The Admin User class derived from user class</h1>
 * <p>
 * class for admin user type
 * </p>
 * @author Harsh Pathak
 * @author Nihesh Anderson
 * @version 1.0
 * @since 29-10-2017
 *  	
 */
public class Admin extends User{
	/**
	 * Joincode String used to generate JoinCodes
	 */
	private static String JoinString="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final long serialVersionUID = 1L;
	/**
	 * Constructor for admin class
	 * @param name name of the admin 
	 * @param password password of the admin
	 * @param emailID the email class 
	 * @param userType denotes whether user is Admin/Faculty/Student. Here this is Admin
	 */
	public Admin(String name, String password, Email emailID, String userType) {
		super(name,password,emailID,userType);
	}
	/**
	 * used to access all the joincodes of the users. 
	 * Joincodes qre used to determine the usertype of a user who is signing up. 
	 * joincode is similar to license key here
	 * @param lock takes a lock on server if set to true
	 * @return returns hashmap of all joincodes. The value(Int) refers to 2 values, 0 or 1 
	 * @throws FileNotFoundException file doesn't exist
	 * @throws IOException Io exception
	 * @throws ClassNotFoundException de-serialize issue 
	 */
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
	/**
	 * Serialize a Hashmap of Joincodes 
	 * @param r the hashmap to be serialised
	 * @param lock takes a lock on server if set to true
	 */
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
	/**
	 * 
	 * @param type denotes the type(Admin/Faculty/Student) 
	 * for which join code is to be generated
	 * @return returns the randomly generated join code
	 */
	public String generateJoincode(String type){
		try {
			File file = new File("./src/AppData/GeneratedJoinCode/list.txt");
			type = type.substring(0, 1).toUpperCase();
			Random rnd = new Random();
			StringBuilder sb = new StringBuilder();
			HashMap<String, Integer> codes = deserializeJoinCodes(false);
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
			FileWriter sc=new FileWriter(file,true);
			sc.write("["+LocalDateTime.now()+"]\t"+sb.toString()+"\n");
			sc.flush();
			sc.close();
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
	/**
	 * de-serializes all the requests from the database.
	 * @param lock takes a lock on server if set to true
	 * @return  priority queue of requests
	 * @throws FileNotFoundException file not found 
	 * @throws IOException IO exception 
	 * @throws ClassNotFoundException de-serialize issue
	 */
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
	/**
	 * Serialize the requests queue
	 * @param r The priority queue to be serialised
	 * @param lock takes lock on server if true
	 */
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
	/**
	 * checks whether a message string is spam or not 
	 * @param message to be checked
	 * @return true if it is
	 */
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
	/**
	 * returns the top request in the priority queue
	 * see also {@link #deserializeRequestsQueue(Boolean)}
	 * @return The top request; an array list of reservation objects
	 */
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
	/**
	 * accepts the top request in the request queue
	 *see also {@link #deserializeRequestsQueue(Boolean)}
	 *see also {@link #serializeRequestsQueue(PriorityQueue, Boolean)} 
	 * @return true if accepted and false if not able to accept because of time table clashes
	 */
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
	/**
	 * rejects the request at top of the queue
	 * see also{@link #deserializeRequestsQueue(Boolean)}
	 * see also{@link #serializeRequestsQueue(PriorityQueue, Boolean)} 
	 * @return true if requests gets requested, false for handling empty parameters
	 */
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
	/**
	 * allows admin to cancel a reservation 
	 * @param queryDate the date on which reservation is to be cancelled
	 * @param slotID the time slot for the reservation to be cancelled
	 * @param RoomID the room for which reservation has to be cancelled
	 * @return true if it is cancelled false otherwise
	 */
	public boolean cancelBooking(LocalDate queryDate,int slotID,String RoomID, String cancellationMessage) {
		try{
			Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			out.writeObject("Pass");
			out.flush();
			out.writeObject("BookingCancelNotification");
			out.flush();
			out.writeObject(queryDate);
			out.flush();
			out.writeObject(slotID);
			out.flush();
			out.writeObject(RoomID);
			out.flush();
			out.writeObject(cancellationMessage);
			out.flush();
			out.close();
			in.close();
			server.close();
		}
		catch(IOException e){
			System.out.println("IO Exception occurred while checking spam");
		}
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
	/**
	 * allows admin to book a free room on a date in a room
	 * @param queryDate the date
	 * @param slot the time(30 minute slot)
	 * @param r the reservation object see also {@link Reservation} class
	 * @return true if booked false otherwise
	 */
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
