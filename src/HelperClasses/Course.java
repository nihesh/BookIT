package HelperClasses;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nihesh on 27/10/17.
 */
public class Course implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private String acronym;
    private Faculty instructor;
    private ArrayList<String> postCondition;
    private HashMap<LocalDate, Reservation[]> Schedule;
    public Course(String name, Faculty instructor, ArrayList<String> postCondition, HashMap<LocalDate, Reservation[]> Schedule){
        this.name = name;
        this.instructor = instructor;
        this.postCondition = postCondition;
        this.Schedule = Schedule;
    }
    public String getName(){
        return this.name;
    }
    public Faculty getInstructor(){
        return this.instructor;
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
                    return false;
                }
            }
        }
        return true;
    }
    public String getAcronym(){
        return this.acronym;
    }
    public void serialize(){
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Course/"+this.name+".dat"));
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
    public void setInstructor(Faculty f){
        this.instructor = f;
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
