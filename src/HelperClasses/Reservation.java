package HelperClasses;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** The Reservation object class for modeling schedule of room and course objects
 * Created by nihesh on 27/10/17.
 */
public class Reservation implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    public ArrayList<String> message;  // cloned
    public String course;
    public String facultyEmail;
    public String type;
    public ArrayList<String> groups;
    public ArrayList<String> groupPurpose;
    public ArrayList<String> groupVenue;
    public ArrayList<LocalDateTime> groupTimeStamp;
    public LocalDateTime creationDate;
    public LocalDate targetDate;
    public String room;
    public String reserverEmail;
    public int slotID;
    public Boolean isRequest;

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        for(int i=0;i<groupTimeStamp.size();i++){
            groupTimeStamp.set(i, creationDate);
        }
    }

    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    public Reservation clone(){
        Reservation r = new Reservation();
        r.message = (ArrayList<String>)this.message.clone();
        r.course = this.course;
        r.facultyEmail = this.facultyEmail;
        r.type = this.type;
        r.groups = (ArrayList<String>)this.groups.clone();
        r.groupPurpose = (ArrayList<String>)this.groupPurpose.clone();
        r.groupVenue = (ArrayList<String>)this.groupVenue.clone();
        r.groupTimeStamp = (ArrayList<LocalDateTime>)this.groupTimeStamp.clone();
        r.creationDate = this.creationDate;
        r.targetDate = this.targetDate;
        r.room = this.room;
        r.reserverEmail = this.reserverEmail;
        r.slotID = this.slotID;
        r.isRequest = this.isRequest;
        return r;
    }
    public Reservation(){       // Default constructor for cloning
        ;
    }
    /**
     * constructor for reservation class
     * @param Message the message describing purpose for reservation
     * @param group the group(1,2,3,4) for which reservation is made.Blank Group refers to the whole audience of a course
     * @param course the course for which reservation is meant
     * @param facultyEmail the email of the instructor who teaches the course
     * @param room the room for which reservation is made
     * @param type lecture/lab/tutorial. Can be left blank as well
     * @param slotID the time slot in Integer
     */
    public Reservation(String Message, ArrayList<String> group, String course, String facultyEmail, String room, String type, int slotID){
        this.reserverEmail = "";
        this.slotID = slotID;
        this.message = new ArrayList<String>();
        this.type = type;
        this.course = course;
        this.facultyEmail = facultyEmail;
        this.room = room;
        this.creationDate = LocalDateTime.now();
        this.groups = new ArrayList<>();
        this.groupVenue = new ArrayList<>();
        this.groupPurpose = new ArrayList<>();
        this.groupTimeStamp = new ArrayList<>();
        for(int i=0;i<group.size();i++) {
            this.groups.add(group.get(i));
            this.groupVenue.add(room);
            this.groupPurpose.add(type);
            this.message.add(Message);
            this.groupTimeStamp.add(this.creationDate);
        }
        this.isRequest = false;
    }
    public String getPurpose(){
        return this.type;
    }
    public void removeRequestFlag(){
        this.isRequest = false;
    }
    public void requestAdmin(){
        this.isRequest = true;
    }
    public Boolean isRequest(){
        return this.isRequest;
    }
    /**
     * getter for getting groups from reservation
     * @return ArryList of string
     */
    public ArrayList<String> getGroups(){
        return this.groups;
    }
    /**
     * getter, returns message without the venue
     * @return String describing the message
     */
    public String getMessageWithoutVenue(){
        if(message.size()!=0){
            return message.get(0);
        }
        else{
            return "";
        }
    }
    /**
     * setter for Reserver email
     * @param email requires an email string 
     */
    public void setReserverEmail(String email){
        this.reserverEmail = email;
    }
    /**
     * getter for getting reserver email
     * @return String
     */
    public String getReserverEmail(){
        return this.reserverEmail;
    }
    /**
     * get Faculty email from reservation
     * @return String
     */
    public String getFacultyEmail(Boolean lock){

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
            out.writeObject("reservation_facultyEmail");
            out.flush();
            out.writeObject(course);
            out.flush();
            String c = (String) in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch(IOException e){
            System.out.println("IO Exception occurred while booking room");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class not found exception occurred while booking room");
        }
        return "";
    }
    /**
     * returns slot id of reservation
     * @return Integer
     */
    public int getReservationSlot(){
        return this.slotID;
    }
    /**
     * returns the date for which reservation is intended
     * @return LocalDate 
     */
    public LocalDate getTargetDate(){
        return this.targetDate;
    }
    /**
     * delete a group from a reservation. Results in removing a student group from that reservation
     * @param g a String that describes the group
     */
    public void deleteGroup(String g, LocalDateTime creationTimeStamp){
        int i=0;
        for(;i<groups.size();i++){
            if(groups.get(i).equals(g) && groupTimeStamp.get(i).equals(creationTimeStamp)){
                groups.remove(i);
                groupVenue.remove(i);
                message.remove(i);
                groupPurpose.remove(i);
                groupTimeStamp.remove(i);
            }
        }
    }
    /**
     * returns the top group for which reservation is made
     * @return String
     */
    public String getTopGroup(){
        if(this.groups.size() == 0){
            return "";
        }
        return this.groups.get(0);
    }
    /**
     * setter for setting the reservation date
     * @param date LocalDate object
     */
    public void setTargetDate(LocalDate date){
        this.targetDate = date;
    }
    /**
     * returns Venue
     * @return String
     */
    public String getVenueName(){
        return this.room;
    }
    /**
     * returns type of reservation - none/lab/lecture/tutorial
     * @return String
     */
    public String getType(){
        return this.type;
    }
    /**
     * returns the message to be displayed in the gui
     * @return String
     */
    public String getMessage(){
        String actualMessage="";
        for(int i=0;i<message.size();i++){
            if(groups.get(i).equals("0"))
                actualMessage+="Group: All groups\n";
            else
                actualMessage+="Group   : "+groups.get(i)+"\n";
            actualMessage+="Venue   : "+groupVenue.get(i)+"\n";
            actualMessage+="Purpose : "+groupPurpose.get(i)+"\n";
            actualMessage+=message.get(i)+"\n";
        }
        return actualMessage;
    }
    public String getMessageWOpurpose() {
        String actualMessage = "";
        for (int i = 0; i < message.size(); i++) {
            String reservationPurpose = this.type;
            if (this.type.equals("")) {
                reservationPurpose = "N/A";
            }
            if (groups.get(i).equals("0"))
                actualMessage += "Group: All groups\n";
            else
                actualMessage += "Group   : " + groups.get(i) + "\n";
            actualMessage += "Venue   : " + groupVenue.get(i) + "\n";
            actualMessage += "Purpose : " + reservationPurpose + "\n";
        }
        return actualMessage;
    }

    /**
     * returns name of the Course, empty string if blank
     * @return String
     */
    public String getCourseName(){
        return this.course;
    }
    /**
     * add a group to a reservation object
     * @param group the group to be added
     * @param venue the venue of the group
     * @param message message describing the reservation
     */
    public void addGroup(ArrayList<String> group, String venue, String message, Reservation r){     // r contains first 3 parameters, but it's being passed for clarity
        for(int i=0; i<group.size();i++) {
            this.groups.add(group.get(i));
            this.groupVenue.add(venue);
            this.message.add(message);
            this.groupPurpose.add(r.getPurpose());
            this.groupTimeStamp.add(r.getCreationDate());
        }
    }
    /**
     * returns name of the room
     * @return String
     */
    public String getRoomName(){
        return this.room;
    }
    /**
     * returns the date when the reservation was created
     * @return LocalDateTime object
     */
    public LocalDateTime getCreationDate(){
        return this.creationDate;
    }
    /**
     * returns the time range corresponding to a slotID. Example for slotID 1 it returns "0800AM - 0830AM"
     * @param slotID the slotID for the time range
     * @return String
     */
    public static String getSlotRange(int slotID){
        String[] data = {"0800AM - 0830AM","0830AM - 0900AM","0900AM - 0930AM","0930AM - 1000AM","1000AM - 1030AM","1030AM - 1100AM","1100AM - 1130AM","1130AM - 1200PM","1200PM - 1230PM","1230PM - 0100PM","0100PM - 0130PM","0130PM - 0200PM","0200PM - 0230PM","0230PM - 0300PM","0300PM - 0330PM","0330PM - 0400PM","0400PM - 0430PM","0430PM - 0500PM","0500PM - 0530PM","0530PM - 0600PM","0600PM - 0630PM","0630PM - 0700PM","0700PM - 0730PM","0730PM - 0800PM","0800PM - 0830PM","0830PM - 0900PM","0900PM - 0930PM","0930PM - 1000PM"};
        return data[slotID];
    }
    /**
     * get Slot ID corresponding to a buttonID
     * @param buttonID a String describing the time range ("0800AM - 0830AM")
     * @return Integer
     */
    public static int getSlotID(String buttonID){
        switch(buttonID){
            case "0800AM - 0830AM":
                return 0;
            case "0830AM - 0900AM":
                return 1;
            case "0900AM - 0930AM":
                return 2;
            case "0930AM - 1000AM":
                return 3;
            case "1000AM - 1030AM":
                return 4;
            case "1030AM - 1100AM":
                return 5;
            case "1100AM - 1130AM":
                return 6;
            case "1130AM - 1200PM":
                return 7;
            case "1200PM - 1230PM":
                return 8;
            case "1230PM - 0100PM":
                return 9;
            case "0100PM - 0130PM":
                return 10;
            case "0130PM - 0200PM":
                return 11;
            case "0200PM - 0230PM":
                return 12;
            case "0230PM - 0300PM":
                return 13;
            case "0300PM - 0330PM":
                return 14;
            case "0330PM - 0400PM":
                return 15;
            case "0400PM - 0430PM":
                return 16;
            case "0430PM - 0500PM":
                return 17;
            case "0500PM - 0530PM":
                return 18;
            case "0530PM - 0600PM":
                return 19;
            case "0600PM - 0630PM":
                return 20;
            case "0630PM - 0700PM":
                return 21;
            case "0700PM - 0730PM":
                return 22;
            case "0730PM - 0800PM":
                return 23;
            case "0800PM - 0830PM":
                return 24;
            case "0830PM - 0900PM":
                return 25;
            case "0900PM - 0930PM":
                return 26;
            case "0930PM - 1000PM":
                return 27;
        }
        return 50;          // never returned. Just a placeholder
    }
}
