package HelperClasses;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by nihesh on 27/10/17.
 */
public class Course implements java.io.Serializable{
    private String name;
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
        return Schedule[queryDate];
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
}
