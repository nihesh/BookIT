package HelperClasses;

import java.time.LocalDate;

/**
 * Created by nihesh on 27/10/17.
 */
public class Reservation implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String Message;
    private Course course;
    private Faculty faculty;
    private LocalDate creationDate;
    private Room room;
    public Reservation(String Message, Course course, Faculty faculty, Room room){
        this.Message = Message;
        this.course = course;
        this.faculty = faculty;
        this.room = room;
        creationDate = LocalDate.now();
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
    public LocalDate getCreationDate(){
        return this.creationDate;
    }
}
