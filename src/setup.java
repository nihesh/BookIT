import HelperClasses.Course;
import HelperClasses.Reservation;
import HelperClasses.Room;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize;

/**
 * Created by nihesh on 28/10/17.
 */
public class setup {
    public static LocalDate StartDate = LocalDate.of(2017,8,1);
    public static LocalDate EndDate = LocalDate.of(2017,12,16);
    public static ArrayList<Integer> getSlots(String startTime, String endTime){
        String[] slots = {"0800AM","0830AM","0900AM","0930AM","1000AM","1030AM","1100AM","1130AM","1200PM","1230PM","0100PM","0130PM","0200PM","0230PM","0300PM","0330PM","0400PM","0430PM","0500PM","0530PM","0600PM","0630PM","0700PM","0730PM","0800PM","0830PM","0900PM","0930PM","1000PM"};
        int counter=0;
        while(!startTime.equals(slots[counter])){
            counter++;
            continue;
        }
        ArrayList<Integer> listOfSlots = new ArrayList<Integer>();
        listOfSlots.add(counter);
        counter++;
        while(!endTime.equals(slots[counter])){
            listOfSlots.add(counter);
            counter++;
        }
        return listOfSlots;
    }
    public static void loadRoomAndCourseObjects() throws IOException,FileNotFoundException{
        Scanner file = new Scanner(new FileReader("./src/AppData/StaticTimeTable/TimeTable.csv"));
        HashMap<String, Room > roomData = new HashMap<String, Room >();
        HashMap<String, Course> courseData = new HashMap<String, Course>();
        file.useDelimiter(",|\\n");
        int flag=0;
        while(file.hasNext()){
            String type,name,code,instructor,credits,acronym,day,startTime,endTime,group,message,venue;
            type = file.next();
            name = file.next();
            code = file.next();
            instructor = file.next();
            credits = file.next();
            acronym = file.next();
            day = file.next().toLowerCase();
            startTime = file.next();
            endTime = file.next();
            group = file.next();
            message = file.next();
            venue = file.next();
            if(flag==0){
                flag=1;
                continue;
            }
            if(!courseData.containsKey(name)){
                ArrayList<String> postCondition = new ArrayList<String>();          // Fill this
                HashMap<LocalDate, Reservation[]> Schedule = new HashMap<LocalDate, Reservation[]>();
                LocalDate currentDate = StartDate;
                LocalDate endDate = EndDate;
                while(!currentDate.equals(endDate)){
                    Reservation[] r = new Reservation[30];
                    Schedule.put(currentDate, r);
                    currentDate = currentDate.plusDays(1);
                }
                Course newCourse = new Course(name, "", postCondition, Schedule);
                courseData.put(name,newCourse);
            }
            if(!roomData.containsKey(venue)){
                HashMap<LocalDate, Reservation[]> Schedule = new HashMap<LocalDate, Reservation[]>();
                LocalDate currentDate = StartDate;
                LocalDate endDate = EndDate;
                while(!currentDate.equals(endDate)){
                    Reservation[] r = new Reservation[30];
                    Schedule.put(currentDate, r);
                    currentDate = currentDate.plusDays(1);
                }
                Room newRoom = new Room(venue,Schedule,40);
                roomData.put(venue,newRoom);
            }
            ArrayList<Integer> listOfSlots = getSlots(startTime, endTime);
            for(int i=0;i<listOfSlots.size();i++){
                int currentSlot = listOfSlots.get(i);
                LocalDate currentDate = StartDate;
                while(currentDate.isBefore(EndDate))
                {
                    if(day.equals(currentDate.getDayOfWeek().toString().toLowerCase())) {
                        Reservation r = new Reservation(message, group, name, "", venue, message);
                        courseData.get(name).addReservation(currentDate, currentSlot, r, false);
                        currentDate = currentDate.plusDays(7);
                    }
                    else {
                        currentDate = currentDate.plusDays(1);
                    }
                }
            }
            for(int i=0;i<listOfSlots.size();i++){
                int currentSlot = listOfSlots.get(i);
                LocalDate currentDate = StartDate;
                while(currentDate.isBefore(EndDate)) {
                    if(day.equals(currentDate.getDayOfWeek().toString().toLowerCase())) {
                        Reservation r = new Reservation(message, group, name, "", venue, message);
                        roomData.get(venue).addReservation(currentDate, currentSlot, r, false);
                        currentDate = currentDate.plusDays(7);
                    }
                    else {
                        currentDate = currentDate.plusDays(1);
                    }
                }
            }
        }
        roomData.forEach((name, room)->{
            room.serialize();
        });
        courseData.forEach((name, course)->{
            course.serialize();
        });
    }
    public static void main(String[] args)throws IOException,FileNotFoundException{
        loadRoomAndCourseObjects();                    // Creates Room and Course Objects for all rooms and courses in AppData. This should be used for initialisation only
    }
}
