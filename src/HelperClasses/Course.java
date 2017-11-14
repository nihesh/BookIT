package HelperClasses;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;

/**
 * describes the course class
 * @author Nihesh Anderson
 * Created by nihesh on 27/10/17.
 * @since 27/10/2017
 */
public class Course implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String acronym;
    private String instructorEmail;
    private ArrayList<String> postCondition;
    private HashMap<LocalDate, Reservation[]> Schedule;
    /**
     * static method to get Course object corresponding to name of the course
     * @param name of the course to deserialise
     * @param lock takes lock on server if set true
     * @return the course object
     */
    public static Course deserializeCourse(String name, Boolean lock){
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
            out.writeObject("ReadCourse");
            out.flush();
            out.writeObject(name);
            out.flush();
            Course c = (Course)in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch (IOException e){
            System.out.println("IO exception occured while writing to server");
        }
        catch (ClassNotFoundException x){
            System.out.println("ClassNotFound exception occured while reading from server");
        }
        return null;
    }
   /**
    * constructor for the course class
    * @param name name of course
    * @param instructorEmail email
    * @param postCondition string
    * @param Schedule schedule of a course in a semester
    * @param acronym short form of course
    */
    public Course(String name, String instructorEmail, ArrayList<String> postCondition, HashMap<LocalDate, Reservation[]> Schedule, String acronym){
        this.name = name;
        this.instructorEmail = instructorEmail;
        this.postCondition = postCondition;
        this.Schedule = Schedule;
        this.acronym = acronym;
        serialize(true);
    }
    /**
     * static method returns name of all courses
     * @return Array List of all course names available
     */
    public static ArrayList<String> getAllCourses(){
        try {
            Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
            ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            out.writeObject("Pass");
            out.flush();
            out.writeObject("AllCourses");
            out.flush();
            ArrayList<String> c = (ArrayList<String>)in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch (IOException e){
            System.out.println("IO exception occured while writing to server");
        }
        catch (ClassNotFoundException x){
            System.out.println("ClassNotFound exception occured while reading from server");
        }
        return null;
    }
    /**
     * detects a collision inside a course object while processing a reservation by admin
     * {@link Admin#acceptRequest()}
     * {@link Admin#getRequest()}
     * @param r the reservation object
     * @return true if collison else no 
     */
    public boolean checkInternalCollision(Reservation r){
        if(!name.equals(r.getCourseName())){
            return false;
        }
        if(Schedule.get(r.getTargetDate())[r.getReservationSlot()] == null){
            return false;
        }
        else{
            Reservation old = Schedule.get(r.getTargetDate())[r.getReservationSlot()];
            if(old.getType().equals("Lecture")){
                return true;
            }
            else if(r.getType().equals("Lecture")){
                return true;
            }
            else if(r.getType().equals("Tutorial") || r.getType().equals("Lab")){
                if(old.getGroups().contains(r.getTopGroup())){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 
     * @return name of course
     */
    public String getName(){
        return this.name;
    }
    /**
     * 
     * @return instructor email
     */
    public String getInstructorEmail(){
        return this.instructorEmail;
    }
    /**
     * finds the number of matches between the search text
     * and the post condition string of a course
     * @param query the keyword specified
     * @return integer as number of matches
     */
    public int keyMatch(ArrayList<String> query){
        int matchQuotient=0;
        for(int i=0;i<query.size();i++){
            String keyword = query.get(i).toLowerCase();
            if(postCondition!=null) {
                for (int j = 0; j < postCondition.size(); j++) {
                    if (postCondition.get(j).toLowerCase().equals(keyword)) {
                        matchQuotient++;
                    }
                }
            }
        }
        return matchQuotient;
    }
    /**
     * returns schedule of a course on a date
     * @param queryDate the concerned date
     * @return array of reservation object
     */
    public Reservation[] getSchedule(LocalDate queryDate){
        return Schedule.get(queryDate);
    }
    /**
     * checks collisions in timings of 2 course objects
     * @param b Course object
     * @return true if collision else false
     */
    public Boolean checkCollision(Course b){
        for(int i=0;i<7;i++){
            Reservation[] s1 = this.getSchedule(LocalDate.now().plusDays(i+1));
            Reservation[] s2 = b.getSchedule(LocalDate.now().plusDays(i+1));
            for(int j=0;j<28;j++){
                if(s1[j]!=null && s2[j]!=null){
                    if(s1[j].getType().equals("Lecture") && s2[j].getType().equals("Lecture"))
                        return true;
                }
            }
        }
        return false;
    }
    /**
     * returns the shortform of a course 
     * @return String 
     */
    public String getAcronym(){
        return this.acronym;
    }
    /**
     * serialize a course object to the server database of courses
     * @param lock takes a lock on the server if set to true
     */
    public void serialize(Boolean lock){
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
            out.writeObject("WriteCourse");
            out.flush();
            out.writeObject(this);
            out.close();
            in.close();
            server.close();
        }
        catch (IOException e){
            System.out.println("IO exception occured while writing to server");
        }
    }
    /**
     * setter method for setting instructor of a course
     * @param f String describing the email of instructor
     */
    public void setInstructor(String f){
        this.instructorEmail = f;
        serialize(true);
    }
    /**
     * Adds reservation to a course on a particular date and time(30 minute slot)
     * @param date the date on which reservation is to be added
     * @param slot the time slot(integer) 
     * @param r reservation object. See also{@link Reservation}
     * @param serialize if serialize is set to true, the object is serialized
     * @return true if added else false
     */
    public Boolean addReservation(LocalDate date, int slot, Reservation r, Boolean serialize){
        if(Schedule.get(date)[slot] == null){
            r.setTargetDate(date);
            Schedule.get(date)[slot] = r;
            if(serialize)
                serialize(false);
            return true;
        }
        else{
            if(Schedule.get(date)[slot].getCourseName().equals(r.getCourseName())){
                r.setTargetDate(date);
                Schedule.get(date)[slot].addGroup(r.getTopGroup(),r.getVenueName(),r.getMessageWithoutVenue());
                if(serialize)
                    serialize(false);
                return true;
            }
            else {
                return false;
            }
        }
    }
    /**
     * checks if a particular time slot is reserved for a course object on a date
     * @param date the date on which reservation has to be checked
     * @param slot the time slot
     * @param r the reservation object. see also {@link Reservation}
     * @return true if the slot is empty else false
     */
    public Boolean checkReservation(LocalDate date, int slot, Reservation r){
        if(Schedule.get(date)[slot] == null){
            return true;
        }
        else{
            if(Schedule.get(date)[slot].getCourseName().equals(r.getCourseName())){
                return true;
            }
            else {
                return false;
            }
        }
    }
    /**
     * deletes a reservation for a student group on specified date and slot
     * @param date the specified date on which reservation has to be cancelled
     * @param slot the specified time slot(Integer) for which reservation should be cancelled.
     * @param group the group for which reservation should be cancelled
     */
    public void deleteReservation(LocalDate date, int slot, String group){
        if(group.equals("0")){
            Schedule.get(date)[slot] = null;
        }
        else{
            Reservation r = Schedule.get(date)[slot];
            r.deleteGroup(group);
            Schedule.get(date)[slot] = r;
        }
        serialize(false);
    }
    
}
