package HelperClasses;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;

/**
 * Created by nihesh on 27/10/17.
 */
public class Course implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private String acronym;
    private String instructorEmail;
    private ArrayList<String> postCondition;
    private HashMap<LocalDate, Reservation[]> Schedule;
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
   
    public Course(String name, String instructorEmail, ArrayList<String> postCondition, HashMap<LocalDate, Reservation[]> Schedule, String acronym){
        this.name = name;
        this.instructorEmail = instructorEmail;
        this.postCondition = postCondition;
        this.Schedule = Schedule;
        this.acronym = acronym;
        serialize(true);
    }
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
    public String getName(){
        return this.name;
    }
    public String getInstructorEmail(){
        return this.instructorEmail;
    }
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
    public Reservation[] getSchedule(LocalDate queryDate){
        return Schedule.get(queryDate);
    }
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
    public String getAcronym(){
        return this.acronym;
    }
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
    public void setInstructor(String f){
        this.instructorEmail = f;
        serialize(true);
    }
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
