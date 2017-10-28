package HelperClasses;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * Created by nihesh on 27/10/17.
 */
public class Room implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String RoomID;
    private HashMap<LocalDate, Reservation[]> Schedule;
    private int Capacity;
    public Room(String RoomID, HashMap<LocalDate, Reservation[]> Schedule, int Capacity){
        this.RoomID = RoomID;
        this.Schedule = Schedule;
        this.Capacity = Capacity;
    }
    public Reservation[] getSchedule(LocalDate queryDate){
        return Schedule.get(queryDate);
    }
    public String getRoomID(){
        return this.RoomID;
    }
    public int getCapacity(){
        return this.getCapacity();
    }
    public void serialize(){
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Room/"+RoomID+".dat"));
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
    public Boolean addReservation(LocalDate date, int slot, Reservation r){
        if(Schedule.get(date)[slot] == null){
            Schedule.get(date)[slot] = r;
            return true;
        }
        else{
            return false;
        }
    }
}
