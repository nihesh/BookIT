package HelperClasses;

import java.io.*;
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
    public static Course deserializeCourse(String name){
        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Course/"+name+".dat"));
            return (Course)in.readObject();
        }
        catch (Exception e){
            System.out.println("Exception occured while deserialising Course");
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
   
    public Course(String name, String instructorEmail, ArrayList<String> postCondition, HashMap<LocalDate, Reservation[]> Schedule, String acronym){
        this.name = name;
        this.instructorEmail = instructorEmail;
        this.postCondition = postCondition;
        this.Schedule = Schedule;
        this.acronym = acronym;
        serialize();
    }
    public static ArrayList<String> getAllCourses(){
        File directory= new File("./src/AppData/Course");
        ArrayList<String> courses = new ArrayList<>();
        File[] courseFiles=directory.listFiles();
        for(int i=0;i<courseFiles.length;i++) {
            String courseName = courseFiles[i].getName().substring(0, courseFiles[i].getName().length() - 4);
            courses.add(courseName);
        }
        return courses;
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
    public void serialize(){
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Course/"+this.name+".dat", false));
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
    public void setInstructor(String f){
        this.instructorEmail = f;
    }
    public Boolean addReservation(LocalDate date, int slot, Reservation r, Boolean serialize){
        if(Schedule.get(date)[slot] == null){
            r.setTargetDate(date);
            Schedule.get(date)[slot] = r;
            if(serialize)
                serialize();
            return true;
        }
        else{
            if(Schedule.get(date)[slot].getCourseName().equals(r.getCourseName())){
                r.setTargetDate(date);
                Schedule.get(date)[slot].addGroup(r.getTopGroup(),r.getVenueName(),r.getMessageWithoutVenue());
                if(serialize)
                    serialize();
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
        serialize();
    }
    
}
