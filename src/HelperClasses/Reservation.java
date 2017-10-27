package HelperClasses;

/**
 * Created by nihesh on 27/10/17.
 */
public class Reservation implements java.io.Serializable{
    private String Message;
    private Course course;
    private Faculty faculty;
    private Room room;
    public Reservation(String Message, Course course, Faculty faculty, Room room){
        this.Message = Message;
        this.course = course;
        this.faculty = faculty;
        this.room = room;
    }
    public String getMessage(){
        return this.Message;
    }
    public Course getCourse(){
        return this.course;
    }
    public Faculty getFaculty(){
        return this.faculty;
    }
    public Room getRoom(){
        return this.room;
    }
}
