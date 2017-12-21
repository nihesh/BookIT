package HelperClasses;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.HashMap;

/** The Room class for modeling schedule of various venue of courses
 * Created by nihesh on 27/10/17.
 */
public class Room implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String RoomID;
    private HashMap<LocalDate, Reservation[]> Schedule;
    private HashMap<LocalDate, HashMap<String,Reservation>[]> studentRequests = new HashMap<>();
    private int Capacity;
    public Reservation[] getPendingReservations(String email, LocalDate date){
        Reservation[] output = new Reservation[30];
        if(!studentRequests.containsKey(date)){
            return output;
        }
        HashMap<String, Reservation>[] temp = studentRequests.get(date);
        for(int i=0;i<30;i++){
            if(temp[i].containsKey(email)){
                output[i] = temp[i].get(email);
            }
        }
        return output;
    }
    public static Reservation[] getPendingReservations(String email, LocalDate date, String room, Boolean lock){
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
            out.writeObject("studentGetPendingReservations");
            out.flush();
            out.writeObject(email);
            out.flush();
            out.writeObject(date);
            out.flush();
            out.writeObject(room);
            out.flush();
            Reservation[] c = (Reservation[]) in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch (FileNotFoundException fe){
            System.out.println("File not found exception occured while getting pending reservations");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class not found exception while getting pending reservations");
        }
        catch (IOException ie){
            System.out.println("IOException occured while getting pending reservations");
        }
        return null;
    }

    public static Reservation serverFetchRequest(String email, LocalDate date, int slot, String room, Boolean lock){
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
            out.writeObject("studentGetReservationRequest");
            out.flush();
            out.writeObject(email);
            out.flush();
            out.writeObject(date);
            out.flush();
            out.writeObject(slot);
            out.flush();
            out.writeObject(room);
            out.flush();
            Reservation c = (Reservation) in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch (FileNotFoundException fe){
            System.out.println("File not found exception occured while getting student reservation request");
        }
        catch (ClassNotFoundException e){
            System.out.println("Class not found while getting student reservation request");
        }
        catch (IOException ie){
            System.out.println("IOException occured while getting student reservation request");
        }
        return null;
    }
    public static void serverDeleteRequest(String email, LocalDate date, int slot, String room, Boolean lock){
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
            out.writeObject("studentDeleteReservationRequest");
            out.flush();
            out.writeObject(email);
            out.flush();
            out.writeObject(date);
            out.flush();
            out.writeObject(slot);
            out.flush();
            out.writeObject(room);
            out.flush();
            out.close();
            in.close();
            server.close();
        }
        catch (FileNotFoundException fe){
            System.out.println("File not found exception occurred while deleting student reservation request");
        }
        catch (IOException ie){
            System.out.println("IOException occurred while deleting student reservation request");
        }
    }
    /**
     * deserialise a room object from the server room database
     * @return Room object see alse {@link Room}
     */
    public void addRequest(Reservation r){
        if(!r.isRequest()){
            return;
        }
        else{
            if(!studentRequests.containsKey(r.getTargetDate())){
                HashMap<String,Reservation>[] temp = new HashMap[30];
                for(int i=0;i<30;i++){
                    temp[i] = new HashMap<>();
                }
                studentRequests.put(r.getTargetDate(), temp);
            }
            studentRequests.get(r.getTargetDate())[(r.getReservationSlot())].put(r.getReserverEmail(), r);
        }
    }
    public void deleteRequest(String email, LocalDate queryDate, int slot){
        if(!studentRequests.containsKey(queryDate)){
            return;
        }
        if(!studentRequests.get(queryDate)[slot].containsKey(email)){
            return;
        }
        studentRequests.get(queryDate)[slot].remove(email);
    }
    public Reservation fetchRequest(String email, LocalDate queryDate, int slot){
        if(!studentRequests.containsKey(queryDate)){
            return null;
        }
        if(!studentRequests.get(queryDate)[slot].containsKey(email)){
            return null;
        }
        return studentRequests.get(queryDate)[slot].get(email);
    }
    public static Reservation[] getDailySchedule(LocalDate queryDate, String room, Boolean lock){
        try{
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
            out.writeObject("getRoomDailySchedule");
            out.flush();
            out.writeObject(queryDate);
            out.flush();
            out.writeObject(room);
            out.flush();
            Reservation[] c = (Reservation[]) in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch(IOException e){
            System.out.println("IO Exception occurred while getting daily schedule");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class not found exception occurred while getting daily schedule");
        }
        return null;
    }
    public static int getCapacity(String room, Boolean lock){
        try{
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
            out.writeObject("getRoomCapacity");
            out.flush();
            out.writeObject(room);
            out.flush();
            int c = (int) in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch(IOException e){
            System.out.println("IO Exception occurred while getting daily schedule");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class not found exception occurred while getting daily schedule");
        }
        return 0;
    }
    public static Boolean exists(String room, Boolean lock){
        try{
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
            out.writeObject("checkRoomExistence");
            out.flush();
            out.writeObject(room);
            out.flush();
            Boolean c = (Boolean) in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch(IOException e){
            System.out.println("IO Exception occurred while getting daily schedule");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class not found exception occurred while getting daily schedule");
        }
        return false;
    }
    public static Room deserializeRoom(String name){
        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Room/"+name+".dat"));
            return (Room)in.readObject();
        }
        catch (Exception e){
            System.out.println("Exception occured while deserialising Room");
            return null;
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch(IOException f){
                ;
            }
        }
    }
    /**
     * constructor for room class
     * @param RoomID name of Room
     * @param Schedule schedule of room's occupancy semester long
     * @param Capacity capacity of room
     */
    public Room(String RoomID, HashMap<LocalDate, Reservation[]> Schedule, int Capacity){
        this.RoomID = RoomID;
        this.Schedule = Schedule;
        this.Capacity = Capacity;
        serialize();
    }
    /**
     * returns schedule of a room on a date
     * @param queryDate the date
     * @return array of Reservation objects {@link Reservation}
     */
    public Reservation[] getSchedule(LocalDate queryDate){
        return Schedule.get(queryDate);
    }
    /**
     * returns name of the room
     * @return String
     */
    public String getRoomID(){
        return this.RoomID;
    }
    /**
     * returns capcaity of room
     * @return Integer
     */
    public int getCapacity(){
        return this.Capacity;
    }
    /**
     * serialize a room in the database
     */
    public void serialize(){
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Room/"+this.getRoomID()+".dat", false));
                out.writeObject(this);
            }
            finally {
                if(out!=null){
                    out.close();
                }
            }
        }
        catch (IOException e){
            ;
        }
    }
    /**
     * add a reservation to the room schedule specified by a date and a time slot
     * @param date the date of reservation
     * @param slot the time slot
     * @param r the reservation object
     * @return true if successful, false otherwise
     */
    public Boolean addReservation(LocalDate date, int slot, Reservation r){
        if(Schedule.get(date)[slot] == null){
            r.setTargetDate(date);
            Schedule.get(date)[slot] = r;
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * check reservation checks whether there is a reservation opening at a given date and time slot
     * @param date the date to check reservation for
     * @param slot the time slot
     * @param r the reservation object
     * @return true if available, false otherwise
     */
    public Boolean checkReservation(LocalDate date, int slot, Reservation r){
        if(Schedule.get(date)[slot] == null){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * deletes a room reservation at a specified date and time slot
     * @param date the date
     * @param slot the time slot
     */
    public void deleteReservation(LocalDate date, int slot){
        Schedule.get(date)[slot] = null;
    }
}
