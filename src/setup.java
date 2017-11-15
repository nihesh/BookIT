import HelperClasses.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;


/**
 * request comparator for the priority queue
 * @author Harsh
 */
class RequestCompare implements Comparator<ArrayList<Reservation>>,java.io.Serializable{

    @Override
    public int compare(ArrayList<Reservation> o1, ArrayList<Reservation> o2) {
        // TODO Auto-generated method stub
        if(o1.get(0).getCreationDate().isBefore(o2.get(0).getCreationDate())) {
            return -1;
        }
        else if(o1.get(0).getCreationDate().isAfter(o2.get(0).getCreationDate())) {
            return 1;
        }
        if(o1.get(0).getTargetDate().isBefore(o2.get(0).getTargetDate())) {
            return -1;
        }
        else if(o1.get(0).getTargetDate().isAfter(o2.get(0).getTargetDate())){
            return 1;
        }
        return 0;

    }

}
/**
 * setup class to initialize database and create default users
 * @author Nihesh
 *
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
    public static ArrayList<String> fillPostConditions(String CourseName){
    	ArrayList<String> temp=new ArrayList<>();
    	String[] splitCourse = CourseName.split("\\s+");
    	for(int i=0;i<splitCourse.length;i++){
    	    if(!splitCourse[i].equals("")){
    	        temp.add(splitCourse[i]);
            }
        }
    	try {
			Scanner x=new Scanner(new BufferedReader(new FileReader("src/AppData/PostCondition/"+CourseName+".txt")));
            while(x.hasNext()) {
                temp.add(x.next());
            }
			x.close();
			
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
    }
    public static void loadRoomAndCourseObjects() throws IOException,FileNotFoundException{
        File file2 = new File("./src/AppData/ActiveUser");
        if(!file2.exists()){
            file2.mkdir();
        }
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
                ArrayList<String> postCondition = fillPostConditions(name); 
                HashMap<LocalDate, Reservation[]> Schedule = new HashMap<LocalDate, Reservation[]>();
                LocalDate currentDate = StartDate;
                LocalDate endDate = EndDate;
                while(!currentDate.equals(endDate)){
                    Reservation[] r = new Reservation[30];
                    Schedule.put(currentDate, r);
                    currentDate = currentDate.plusDays(1);
                }
                Course newCourse = new Course(name, "", postCondition, Schedule, acronym);
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
                        Reservation r = new Reservation(message, group, name, "", venue, message, currentSlot);
                        r.setTargetDate(currentDate);
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
                        Reservation r = new Reservation(message, group, name, "", venue, message, currentSlot);
                        r.setTargetDate(currentDate);
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
            room.serialize(true);
        });
        courseData.forEach((name, course)->{
            course.serialize(true);
        });
    }
    public static void createFirstAdmin(){
        Email masterEmail = new Email("admin@iiitd.ac.in");
        Admin master = new Admin("IIITDadmin","adminiiitd",masterEmail,"Admin");
        master.setActiveUser();
    }
    public static void createFirstStudent(){
        Email masterEmail = new Email("student@iiitd.ac.in");
        ArrayList<String> courses = new ArrayList<>();
        courses.add("Discrete Mathematics");
        Student master = new Student("IIITDStudent","studentiiitd",masterEmail,"Student", "BT2017",courses  );
        master.setActiveUser();
    }
    public static void createFirstFaculty(){
        Email masterEmail = new Email("faculty@iiitd.ac.in");
        ArrayList<String> courses = new ArrayList<>();
        Faculty master = new Faculty("IIITDFaculty","facultyiiitd",masterEmail,"Faculty", courses);
        master.setActiveUser();
    }
    public static void serialiseEmptyPriorityQueue() throws IOException, ClassNotFoundException{
        PriorityQueue<ArrayList<Reservation>> p = new PriorityQueue<>(new RequestCompare());
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Requests/requests.txt", false));
            out.writeObject(p);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    public static void serialiseEmptyJoinCodeMap() throws IOException, ClassNotFoundException{
        HashMap<String, Integer> p = new HashMap<String, Integer>();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("./src/AppData/JoinCodes/Codes.txt", false));
            out.writeObject(p);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static void main(String[] args){
        BookITconstants b = new BookITconstants();
        try {
            loadRoomAndCourseObjects();                    // Creates Room and Course Objects for all rooms and courses in AppData. This should be used for initialisation only
            serialiseEmptyPriorityQueue();
            serialiseEmptyJoinCodeMap();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

