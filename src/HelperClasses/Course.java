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
    public String getName(){
        return this.name;
    }
    public Faculty getInstructor(){
        if(this.instructorEmail.equals("")){
            return null;
        }
        return (Faculty)User.getUser(this.instructorEmail);
    }
    public int keyMatch(ArrayList<String> query){
        int matchQuotient=0;
        for(int i=0;i<query.size();i++){
            String keyword = query.get(i).toLowerCase();
            for(int j=0;j<postCondition.size();j++){
                if(postCondition.get(i).toLowerCase().equals(keyword)){
                    matchQuotient++;
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
            r.addGroup(r.getTopGroup(),r.getRoomName(),r.getMessage());
            r.setTargetDate(date);
            Schedule.get(date)[slot] = r;
            if(serialize)
                serialize();
            return true;
        }
        else{
            if(Schedule.get(date)[slot].getCourseName().equals(r.getCourseName())){
                r.setTargetDate(date);
                Schedule.get(date)[slot].addGroup(r.getTopGroup(),r.getVenueName(),r.getMessage());
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
