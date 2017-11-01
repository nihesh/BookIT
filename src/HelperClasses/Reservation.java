package HelperClasses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by nihesh on 27/10/17.
 */
public class Reservation implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String Message;
    private String course;
    private String facultyEmail;
    private String type;
    private ArrayList<String> groups;
    private ArrayList<String> groupVenue;
    private LocalDateTime creationDate;
    private String targetGroup;
    private LocalDate targetDate;
    private String room;
    public Reservation(String Message, String group, String course, String facultyEmail, String room, String type){
        this.type = type;
        this.Message = Message;
        this.course = course;
        this.facultyEmail = facultyEmail;
        this.room = room;
        creationDate = LocalDateTime.now();
        this.groups = new ArrayList<String>();
        this.groupVenue = new ArrayList<String>();
        this.targetGroup = group;
    }
    public LocalDate getTargetDate(){
        return this.targetDate;
    }
    public void setTargetDate(LocalDate date){
        this.targetDate = date;
    }
    public String getTargetGroup(){
        return this.targetGroup;
    }
    public String getVenueName(){
        return this.room;
    }
    public String getType(){
        return this.type;
    }
    public String getMessage(){
        return this.Message;
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
        return User.getUser(this.facultyEmail);
    }
    public void processForCourse(){
        addGroup(targetGroup, room, Message);
    }
    public Room getRoom(){
        if(room.equals("")){
            return null;
        }
        return Room.deserializeRoom(room);
    }
    public ArrayList<String> getGroups(){
        return this.groups;
    }
    public ArrayList<String> getGroupVenue(){
        return this.groupVenue;
    }
    public void addGroup(String group, String venue, String message){
        if(group.equals("0")){
            this.Message = message;
        }
        else {
            groups.add(group);
            groupVenue.add(venue);
            this.Message += message + "\n" + "Group: " + group + "\nVenue: " + venue + "\n";
        }
    }
    public LocalDateTime getCreationDate(){
        return this.creationDate;
    }
}
