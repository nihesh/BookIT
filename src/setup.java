import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import HelperClasses.*;
import com.sun.org.apache.xpath.internal.operations.Bool;


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
    public static LocalDate StartDate;
    public static LocalDate EndDate;
    public static ArrayList<Integer> getSlots(String startTime, String endTime){
        ArrayList<Integer> listOfSlots = new ArrayList<Integer>();;
        String[] slots = {"0800AM","0830AM","0900AM","0930AM","1000AM","1030AM","1100AM","1130AM","1200PM","1230PM","0100PM","0130PM","0200PM","0230PM","0300PM","0330PM","0400PM","0430PM","0500PM","0530PM","0600PM","0630PM","0700PM","0730PM","0800PM","0830PM","0900PM","0930PM","1000PM"};
        int counter=0;
        try {
            while(!startTime.equals(slots[counter])){
                counter++;
                continue;
            }
            listOfSlots.add(counter);
            counter++;
            while (!endTime.equals(slots[counter])) {
                listOfSlots.add(counter);
                counter++;
            }
        }
        catch (Exception e){
            System.out.println(startTime+" to "+endTime+" booking is invalid. Please recheck csv");
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
                temp.add(x.next().trim());
            }
			x.close();
			
    	} catch (Exception e) {
            System.out.println("Course Post Condition list for "+CourseName+" not found");
        }
		return temp;
    }
    public static void loadRoomAndCourseObjects() throws IOException,FileNotFoundException{
        File file2 = new File("./src/AppData/ActiveUser");
        if(!file2.exists()){
            file2.mkdir();
        }
        Scanner file = new Scanner(new FileReader("./src/AppData/StaticTimeTable/RoomData.csv"));
        HashMap<String, Room > roomData = new HashMap<String, Room >();
        HashMap<String, Course> courseData = new HashMap<String, Course>();
        file.useDelimiter(",|\\n");
        int flag=0;
        while(file.hasNext()){
            String venue;
            int capacity;
            if(flag==0){
                flag=1;
                file.next().trim();
                file.next().trim();
                continue;
            }
            venue = file.next().trim();
            capacity = Integer.parseInt(file.next().trim());
            HashMap<LocalDate, Reservation[]> Schedule = new HashMap<LocalDate, Reservation[]>();
            LocalDate currentDate = StartDate;
            LocalDate endDate = EndDate;
            while(!currentDate.isAfter(endDate)){
                Reservation[] r = new Reservation[30];
                Schedule.put(currentDate, r);
                currentDate = currentDate.plusDays(1);
            }
            Room newRoom = new Room(venue,Schedule,capacity);
            roomData.put(venue,newRoom);
        }
        file = new Scanner(new FileReader("./src/AppData/StaticTimeTable/TimeTable.csv"));
        file.useDelimiter(",|\\n");
        flag=0;
        while(file.hasNext()){
            String type,name,code,instructor,credits,acronym,day,startTime,endTime,group,message,venue;
            code = file.next().trim();
            name = file.next().trim();
            instructor = file.next().trim();
            credits = file.next().trim();
            acronym = file.next().trim();
            day = file.next().trim().toLowerCase();
            startTime = file.next().trim();
            endTime = file.next().trim();
            group = file.next().trim();
            message = file.next().trim();
            venue = file.next().trim();
            if(flag==0){
                flag=1;
                continue;
            }
            if(!courseData.containsKey(name)){
                ArrayList<String> postCondition = fillPostConditions(name); 
                HashMap<LocalDate, Reservation[]> Schedule = new HashMap<LocalDate, Reservation[]>();
                LocalDate currentDate = StartDate;
                LocalDate endDate = EndDate;
                while(!currentDate.isAfter(endDate)){
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
                while(!currentDate.isAfter(endDate)){
                    Reservation[] r = new Reservation[30];
                    Schedule.put(currentDate, r);
                    currentDate = currentDate.plusDays(1);
                }
                Room newRoom = new Room(venue,Schedule,-5);
                roomData.put(venue,newRoom);
            }
            ArrayList<Integer> listOfSlots = getSlots(startTime, endTime);
            for(int i=0;i<listOfSlots.size();i++){
                int currentSlot = listOfSlots.get(i);
                LocalDate currentDate = StartDate;
                while(!currentDate.isAfter(EndDate))
                {
                    if(day.equals(currentDate.getDayOfWeek().toString().toLowerCase())) {
                        Reservation r = new Reservation(message, group, name, "", venue, message, currentSlot);
                        r.setTargetDate(currentDate);
                        courseData.get(name).addReservation(currentDate, currentSlot, r);
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
                Boolean reservationSuccessFlag = true;
                while(!currentDate.isAfter(EndDate)) {
                    if(day.equals(currentDate.getDayOfWeek().toString().toLowerCase())) {
                        Reservation r = new Reservation(message, group, name, "", venue, message, currentSlot);
                        r.setTargetDate(currentDate);
                        if(!roomData.get(venue).addReservation(currentDate, currentSlot, r) && reservationSuccessFlag){
                            reservationSuccessFlag = false;
                            System.out.println(name+" "+startTime+" "+endTime+" "+venue+" has a collision with an earlier reservation. Please rectify csv");
                        }
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
    public static void createFirstAdmin(){
        Email masterEmail = new Email("admin@iiitd.ac.in");
        Admin master = new Admin("IIITDadmin","adminiiitd",masterEmail,"Admin");
        master.setActiveUser();
        master.serialize(false);
    }
    public static void createFirstStudent(){
        Email masterEmail = new Email("student@iiitd.ac.in");
        ArrayList<String> courses = new ArrayList<>();
        courses.add("Discrete Mathematics");
        Student master = new Student("IIITDStudent","studentiiitd",masterEmail,"Student", "BT2017",courses  );
        master.setActiveUser();
        master.serialize(false);
    }
    public static void createFirstFaculty(){
        Email masterEmail = new Email("faculty@iiitd.ac.in");
        ArrayList<String> courses = new ArrayList<>();
        Faculty master = new Faculty("IIITDFaculty","facultyiiitd",masterEmail,"Faculty", courses);
        master.setActiveUser();
        master.serialize(false);
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
    public static void serialiseStudentHashMap() throws IOException, ClassNotFoundException{
        HashMap<String,Integer> p = new HashMap<String,Integer>();
        Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/StaticTimeTable/StudentEmails.txt")));
        while(sc.hasNext()){
            p.put(sc.next().trim(),1);
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Server/StudentEmails.dat", false));
            out.writeObject(p);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    public static void serialiseFacultyHashMap() throws IOException, ClassNotFoundException{
        HashMap<String,Integer> p = new HashMap<String,Integer>();
        Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/StaticTimeTable/FacultyEmails.txt")));
        while(sc.hasNext()){
            p.put(sc.next().trim(),1);
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Server/FacultyEmails.dat", false));
            out.writeObject(p);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    public static void serialiseAdminHashMap() throws IOException, ClassNotFoundException{
        HashMap<String,Integer> p = new HashMap<String,Integer>();
        Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/StaticTimeTable/AdminEmails.txt")));
        while(sc.hasNext()){
            p.put(sc.next().trim(),1);
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Server/AdminEmails.dat", false));
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
    public static void clearUserData(){
        File file = new File("./src/AppData/User/");
        File[] users = file.listFiles();
        for(int i=0;i<users.length;i++){
            User u = User.getUser(users[i].getName().substring(0,users[i].getName().length()-4), false);
            if(u.getUsertype().equals("Faculty")){
                Faculty f = (Faculty) u;
                f.getCourses().clear();
                f.serialize(false);
            }
            else if(u.getUsertype().equals("Student")){
                Student s = (Student) u;
                s.getMyCourses().clear();
                s.serialize(false);
            }
        }
    }
    public static void main(String[] args){
        BookITconstants b = new BookITconstants();
        Scanner sc = new Scanner(System.in);
        System.out.print("Semester start date: ");
        int date, month, year;
        date = sc.nextInt();
        System.out.print("Semester start month: ");
        month = sc.nextInt();
        System.out.print("Semester start year: ");
        year = sc.nextInt();
        StartDate = LocalDate.of(year, month, date);
        System.out.print("Semester end date: ");
        date = sc.nextInt();
        System.out.print("Semester end month: ");
        month = sc.nextInt();
        System.out.print("Semester end year: ");
        year = sc.nextInt();
        EndDate = LocalDate.of(year, month, date);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Server/StartDate.dat", false));
            out.writeObject(StartDate);
            out.close();
            out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Server/EndDate.dat", false));
            out.writeObject(EndDate);
            out.close();
        }
        catch(Exception e){
            System.out.println("Error occurred while serialising sem start and end dates");
        }
        try {
            loadRoomAndCourseObjects();                    // Creates Room and Course Objects for all rooms and courses in AppData. This should be used for initialisation only
            clearUserData();
            serialiseEmptyPriorityQueue();
            serialiseEmptyJoinCodeMap();
            serialiseFacultyHashMap();
            serialiseStudentHashMap();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }
}

