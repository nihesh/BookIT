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
    private int Capacity;
    /**
     * deserialise a room object from the server room database
     * @param name room name
     * @return Room object see alse {@link Room}
     */
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
