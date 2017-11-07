package HelperClasses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by nihesh on 27/10/17.
 */
public class Reservation implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<String> message;
    private String course;
    private String facultyEmail;
    private String type;
    private ArrayList<String> groups;
    private ArrayList<String> groupVenue;
    private LocalDateTime creationDate;
    private LocalDate targetDate;
    private String room;
    private String reserverEmail;
    private int slotID;
    public Reservation(String Message, String group, String course, String facultyEmail, String room, String type, int slotID){
        this.reserverEmail = "";
        this.slotID = slotID;
        this.message = new ArrayList<String>();
        this.type = type;
        if(!Message.equals(""))
            this.message.add(Message);
        this.course = course;
        this.facultyEmail = facultyEmail;
        this.room = room;
        this.creationDate = LocalDateTime.now();
        this.groups = new ArrayList<String>();
        this.groupVenue = new ArrayList<String>();
        this.groups.add(group);
        this.groupVenue.add(room);
    }
    public void setReserverEmail(String email){
        this.reserverEmail = email;
    }
    public String getReserverEmail(){
        return this.reserverEmail;
    }
    public String getFacultyEmail(){
        return this.facultyEmail;
    }
    public int getReservationSlot(){
        return this.slotID;
    }
    public LocalDate getTargetDate(){
        return this.targetDate;
    }
    public void deleteGroup(String g){
        int i=0;
        for(;i<groups.size();i++){
            if(groups.get(i).equals(g)){
                break;
            }
        }
        groups.remove(i);
        groupVenue.remove(i);
    }
    public String getTopGroup(){
        return this.groups.get(0);
    }
    public void setTargetDate(LocalDate date){
        this.targetDate = date;
    }
    public String getVenueName(){
        return this.room;
    }
    public String getType(){
        return this.type;
    }
    public String getMessage(){
        String actualMessage="";
        for(int i=0;i<message.size();i++){
            String reservationPurpose = this.type;
            if(this.type.equals("")){
                reservationPurpose = "N/A";
            }
            if(groups.get(i).equals("0"))
                actualMessage+="Group: All groups\n";
            else
                actualMessage+="Group   : "+groups.get(i)+"\n";
            actualMessage+="Venue   : "+groupVenue.get(i)+"\n";
            actualMessage+="Purpose : "+reservationPurpose+"\n";
            actualMessage+=message.get(i)+"\n";
        }
        return actualMessage;
    }
    public String getCourseName(){
        return this.course;
    }
    public Course getCourse(){
        if(course.equals("")){
            return null;
        }
        return Course.deserializeCourse(course);
    }
    public Faculty getFaculty(){
        if(facultyEmail.equals("")){
            return null;
        }
        return (Faculty)User.getUser(this.facultyEmail);
    }
    public Room getRoom(){
        if(room.equals("")){
            return null;
        }
        return Room.deserializeRoom(room);
    }
    public void addGroup(String group, String venue, String message){
        this.groups.add(group);
        this.groupVenue.add(venue);
        this.message.add(message);
    }
    public String getRoomName(){
        return this.room;
    }
    public LocalDateTime getCreationDate(){
        return this.creationDate;
    }
    public static String getSlotRange(int slotID){
        String[] data = {"0800AM - 0830AM","0830AM - 0900AM","0900AM - 0930AM","0930AM - 1000AM","1000AM - 1030AM","1030AM - 1100AM","1100AM - 1130AM","1130AM - 1200PM","1200PM - 1230PM","1230PM - 0100PM","0100PM - 0130PM","0130PM - 0200PM","0200PM - 0230PM","0230PM - 0300PM","0300PM - 0330PM","0330PM - 0400PM","0400PM - 0430PM","0430PM - 0500PM","0500PM - 0530PM","0530PM - 0600PM","0600PM - 0630PM","0630PM - 0700PM","0700PM - 0730PM","0730PM - 0800PM","0800PM - 0830PM","0830PM - 0900PM","0900PM - 0930PM","0930PM - 1000PM"};
        return data[slotID];
    }
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
