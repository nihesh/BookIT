import HelperClasses.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.ECField;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * the server class for back-end development
 */
public class server {
	/**
	 * spam filter object for detecting spam messages
	 */
	public static final double BookITversion = 1.1;
    public static SpamFilter spm;
    public static int noOfConnections = 0;
    public static ExecutorService mailpool = Executors.newFixedThreadPool(2);
    public static HashMap<String, Integer> studhash=null;
    public static HashMap<String, Integer> faculthash=null;
    public static HashMap<String, Integer> adminhash=null;
    public static HashMap<String, Integer> connectedIPs=null;
    public static ArrayList<String> freeCourses = null;

    public static void loadFreeCourses(){
        System.out.println("Loading non registered courses");
        freeCourses = new ArrayList<>();
        ArrayList<String> allCourses = ConnectionHandler.getAllCourses();       // Static helper in connection handler
        for(int i=0;i<allCourses.size();i++){
            String email = ConnectionHandler.course_getFaculty(allCourses.get(i));
            if(email.equals("")){
                freeCourses.add(allCourses.get(i));
            }
        }
        System.out.println("Loaded non registered courses");
        System.out.println();
    }

    public static void loadHashMaps(){

        try {
            System.out.println("Loading allowed user database");
            setup.serialiseFacultyHashMap();
            setup.serialiseStudentHashMap();
            setup.serialiseAdminHashMap();
            System.out.println("Allowed user database loaded");
            System.out.println();
        }
        catch (Exception e){
            BookITconstants.writeLog("IOException occurred while serialising student/faculty/admin hashmaps");
        }

        ObjectInputStream in=null;
        ObjectInputStream in2=null;
        ObjectInputStream in3=null;
        
        try
        {
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Server/StudentEmails.dat"));
            studhash = (HashMap<String, Integer>)in.readObject();
            in2 = new ObjectInputStream(new FileInputStream("./src/AppData/Server/FacultyEmails.dat"));
            faculthash = (HashMap<String, Integer>)in2.readObject();
            in3 = new ObjectInputStream(new FileInputStream("./src/AppData/Server/AdminEmails.dat"));
            adminhash = (HashMap<String, Integer>)in3.readObject();
        }
        catch(Exception e){
            ;
        }
        finally {
            if(in!=null) {
                try {
                    in.close();
                }
                catch (Exception e){
                    ;
                }
            }
            if(in2!=null) {
                try {
                    in.close();
                }
                catch (Exception e){
                    ;
                }
            }
            if(in3!=null) {
                try {
                    in.close();
                }
                catch (Exception e){
                    ;
                }
            }
        }
    }
    public static void main(String[] args)throws IOException{
        BookITconstants b = new BookITconstants("Server");
        BookITconstants.log = new FileWriter(new File("./src/AppData/Server/ServerBugs.txt"), true);
        loadHashMaps();
        loadFreeCourses();
        ServerSocket s = new ServerSocket(BookITconstants.serverPort);
        ConnectionHandler.lock = new ReentrantLock();
        spm = new SpamFilter();
        ExecutorService threads = Executors.newFixedThreadPool(3);
        while(true){
            Socket connection = s.accept();
            threads.execute(new ConnectionHandler(connection));
        }
    }
}
/**
 * Connection Handler class to communicate with clients
 * @author Nihesh
 *
 */
class ConnectionHandler implements Runnable{
	private Socket connection;
    public static ReentrantLock lock;
    private static String JoinString="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public ConnectionHandler(Socket connection){
        this.connection = connection;
    }
    /**
     * 
     * @return list of all courses
     */
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
    /**
     * serialise a user object
     * @param u the user(could be faculty,admin or student)
     */
    public void serializeUser(User u){
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/User/"+u.getEmail().getEmailID()+".txt", false));
                out.writeObject(u);
            }
            finally {
                if(out!=null){
                    out.close();
                }
            }

        }
        catch (IOException e){
            BookITconstants.writeLog("file not found");
        }
    }
    /**
     * deserialise a user given user email
     * @param email email of user
     * @return user object see also{@link User}
     */
    public User getUser(String email){
        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream("./src/AppData/User/"+email+".txt"));
            return (User)in.readObject();
        }
        catch (Exception e){
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
    /**
     * serialise the joincode hashmap to local database
     * @param r the join code hashmap
     */
    public void serializeJoinCode(HashMap<String, Integer> r) {
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/JoinCodes/Codes.txt", false));
                out.writeObject(r);
            }
            finally {
                if(out!=null){
                    out.close();
                }
            }
        }
        catch (IOException e){
            BookITconstants.writeLog("file not found");
        }
    }
    /**
     * deserialise the join codes from its local database
     * @return the join codes in form of Hashmap
     */
    public HashMap<String, Integer> deserializeJoinCodes(){
        HashMap<String, Integer> p=null;
        ObjectInputStream in=null;
        try
        {
            in = new ObjectInputStream(new FileInputStream("./src/AppData/JoinCodes/Codes.txt"));
            p = (HashMap<String, Integer>)in.readObject();
        }
        catch(Exception e){
            ;
        }
        finally {
            if(in!=null) {
                try {
                    in.close();
                }
                catch (Exception e){
                    ;
                }
            }
        }
        return p;
    }
    /**
     * deserialize the requests queue from the database
     * @return priority queue of requests
     */
    public PriorityQueue<ArrayList<Reservation>> deserializeRequests(){
        PriorityQueue<ArrayList<Reservation>> p=null;
        ObjectInputStream in=null;
        try
        {
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Requests/requests.txt"));
            p = ((PriorityQueue<ArrayList<Reservation>>)in.readObject());
        }
        catch(Exception e){
            ;
        }
        finally {
            if(in!=null) {
                try {
                    in.close();
                }
                catch(Exception e){
                    ;
                }
            }
        }
        return p;
    }
    /**
     * serialize requests queue to the server database
     * @param r the priority queue 
     */
    public void serializeRequests(PriorityQueue<ArrayList<Reservation>> r) {
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Requests/requests.txt"));
                out.writeObject(r);
            }
            finally {
                if(out!=null){
                    out.close();
                }
            }

        }
        catch (IOException e){
            BookITconstants.writeLog("file not found");
        }
    }
    public ArrayList<LocalDate> fetchSemDate(){
        try {
            ArrayList<LocalDate> data = new ArrayList<>();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("./src/AppData/Server/StartDate.dat"));
            data.add((LocalDate) in.readObject());

            in = new ObjectInputStream(new FileInputStream("./src/AppData/Server/EndDate.dat"));
            data.add((LocalDate) in.readObject());
            return data;
        }
        catch (Exception e){
            return null;
        }
    }
    public void BookingCancellationNotifier(LocalDate queryDate, int slotID, String RoomID, String cancellationMessage){
    	HashMap<String, Integer> h = new HashMap<>();
        Room temp=Room.deserializeRoom(RoomID);
        Reservation r=temp.getSchedule(queryDate)[slotID];
        String recipient = r.getReserverEmail();
        if(recipient != null) {
        h.put(recipient, 1);}
        if(r.getCourseName() != null && (!r.getCourseName().equals(""))) {
        	String facultyemail = course_getFaculty(r.getCourseName());
        	if(facultyemail != null) {
        	h.put(facultyemail, 1);
        	}
        }
        h.put(Mail.from, 1);
        temp.deleteReservation(queryDate, slotID);
        temp.serialize();
        Course c=Course.deserializeCourse(r.getCourseName());
        if(c!=null) {
            c.deleteReservation(queryDate, slotID,r.getTopGroup());
            c.serialize();
        }
        for(String email : h.keySet()) {
        	if(!email.equals("")) {
        		
        		User x=getUser(email);
        		ArrayList<Integer> t = new ArrayList<>();
        		t.add(r.getReservationSlot());
        		Notification n = new Notification("Room Booking", "Cancelled", r.getMessage(), r.getCourseName(), r.getTargetDate(), r.getRoomName(), r.getReserverEmail(),t);
                String GreetText="Hello User";
                if(x != null) {
                	GreetText = "Hello" + x.getName();
                	x.addNotification(n);
                }
                server.mailpool.execute(new Mail(email,"BooKIT - Room booking cancelled", GreetText+","+"\n\nThe following booking of yours have been cancelled by the admin:\n\n"+"Room: "+RoomID+"\nDate: "+queryDate.getDayOfMonth()+"/"+queryDate.getMonthValue()+"/"+queryDate.getYear()+"\nTime: "+ Reservation.getSlotRange(slotID)+" \nReason: "+cancellationMessage+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                serializeUser(x);
        	}
        }
    }
    public String generateJoincode(String type){
        type = type.substring(0, 1).toUpperCase();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        HashMap<String, Integer> codes = deserializeJoinCodes();
        sb.append(type);
        while (true) {
            while (sb.length() != 7) {
                sb.append(JoinString.charAt(((int)(rnd.nextFloat() * JoinString.length()))));
            }
            if (codes.containsKey(sb.toString()) && codes.get(sb.toString()) == 1) {
                sb = new StringBuilder();
                sb.append(type);
            } else {
                break;
            }
        }
        codes.put(sb.toString(), 1);
        serializeJoinCode(codes);
        return sb.toString();
    }
    public Boolean containsJoinCode(String code){
        HashMap<String, Integer> temp = deserializeJoinCodes();
        if(temp.containsKey(code)){
            return true;
        }
        else{
            return false;
        }
    }
    public void removeJoinCode(String code){
        HashMap<String, Integer> temp = deserializeJoinCodes();
        temp.remove(code);
        serializeJoinCode(temp);
    }
    public ArrayList<Reservation> getRequest(){
        PriorityQueue<ArrayList<Reservation>> p = deserializeRequests();
        ArrayList<Reservation> r = p.peek();
        while (r != null && (SpamFilter.Predict(r.get(0).getMessageWithoutVenue()) || r.get(0).getCreationDate().plusDays(5).isBefore(LocalDateTime.now()))) {
            p.poll();
            r = p.peek();
        }
        while (r != null && (SpamFilter.Predict(r.get(0).getMessageWithoutVenue()) || r.get(0).getTargetDate().isBefore(LocalDate.now()))) {
            p.poll();
            r = p.peek();
        }
        if(r==null){
            serializeRequests(p);
            return null;
        }
        int flag=0;
        Room temp = Room.deserializeRoom(r.get(0).getRoomName());
        Course ctemp = Course.deserializeCourse(r.get(0).getCourseName());
        while(r!=null) {
            if(SpamFilter.Predict(r.get(0).getMessageWithoutVenue())){
                p.poll();
                r = p.peek();
                continue;
            }
            Room room = Room.deserializeRoom(r.get(0).getRoomName());
            Reservation[] pending = room.getPendingReservations(r.get(0).getReserverEmail(),r.get(0).getTargetDate());
            for(int i=r.size()-1;i>=0;i--){
                if(pending[r.get(i).getReservationSlot()] == null){
                    r.remove(i);
                }
            }
            if(r.size()==0){
                p.poll();
                r=p.peek();
                continue;
            }
            for (Reservation reservation : r) {
                if (!temp.checkReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation)) {
                    p.poll();
                    flag=1;
                    r=p.peek();
                    break;
                }
                if(ctemp!=null) {
                    if (ctemp.checkInternalCollision(reservation)) {
                        p.poll();
                        flag=1;
                        r=p.peek();
                        break;
                    }
                }
                flag=0;


            }
            if(flag==0) {
                break;
            }
        }
        serializeRequests(p);
        return r;
    }
    public boolean acceptRequest(ArrayList<Integer> data){
        PriorityQueue<ArrayList<Reservation>> p = deserializeRequests();
        ArrayList<Reservation> r = p.peek();
        if (r == null) {
            serializeRequests(p);
            return false;
        }
        p.poll();
        int flag=0;
        Room temp = Room.deserializeRoom(r.get(0).getRoomName());
        Course ctemp = Course.deserializeCourse(r.get(0).getCourseName());
        while(r!=null) {
            for (int i=0;i<data.size();i++) {
                Reservation reservation = r.get(data.get(i));
                if(!temp.checkReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation)) {
                    p.poll();
                    flag=1;
                    r=p.peek();
                    break;
                }
                if(ctemp!=null) {
                    if (ctemp.checkInternalCollision(reservation)) {
                        p.poll();
                        flag=1;
                        r=p.peek();
                        break;
                    }
                }
                flag=0;
            }
            if(flag==0) {
                break;
            }
        }
        serializeRequests(p);
        for(int i=0;i<r.size();i++){
            temp.deleteRequest(r.get(0).getReserverEmail(), r.get(0).getTargetDate(), r.get(i).getReservationSlot());
        }
        ArrayList<Integer> x=new ArrayList<Integer>();
        if(r!=null) {
        	String recipient = r.get(0).getReserverEmail();
            String GreetText = "Hello " + getUser(recipient).getName();
            String TimeSlots="";
            
            for (int i=0;i<data.size();i++) {
                Reservation reservation = r.get(data.get(i));
                reservation.removeRequestFlag();
                x.add(reservation.getReservationSlot());
                TimeSlots+=Reservation.getSlotRange(reservation.getReservationSlot())+"\n";
                temp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation);
                temp.deleteRequest(r.get(0).getReserverEmail(), r.get(0).getTargetDate(), r.get(i).getReservationSlot());
                if(ctemp!=null) {
                    ctemp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation);
                }
            }
            Notification n = new Notification("Room Reservation Request", "Accepted", r.get(0).getMessage(), r.get(0).getCourseName(), r.get(0).getTargetDate(), r.get(0).getRoomName(), r.get(0).getReserverEmail(), x);
            Student Stud = (Student)getUser(recipient);
            Admin a = (Admin) getUser(Mail.from);
            Stud.addNotification(n);
            a.addNotification(n);
            serializeUser(Stud);
            serializeUser(a);
            server.mailpool.execute(new Mail(recipient,"BooKIT - Room reservation request accepted", GreetText+","+"\n\nThe following request of yours have been accepted by the admin:\n\n"+"Room: "+r.get(0).getVenueName()+"\nDate: "+r.get(0).getTargetDate().getDayOfMonth()+"/"+r.get(0).getTargetDate().getMonthValue()+"/"+r.get(0).getTargetDate().getYear()+"\nTime:\n"+TimeSlots+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
            if(ctemp != null) {
            	if(ctemp.getInstructorEmail() != null && (!ctemp.getInstructorEmail().equals(""))) {
            		Faculty ft = (Faculty)getUser(ctemp.getInstructorEmail());
            		ft.addNotification(n);
            		serializeUser(ft);
            		GreetText = "Hello" + ft.getName();
            		server.mailpool.execute(new Mail(ctemp.getInstructorEmail(),"BooKIT - Room reservation request accepted", GreetText+","+"\n\nThe following request of yours have been accepted by the admin:\n\n"+"Room: "+r.get(0).getVenueName()+"\nDate: "+r.get(0).getTargetDate().getDayOfMonth()+"/"+r.get(0).getTargetDate().getMonthValue()+"/"+r.get(0).getTargetDate().getYear()+"\nTime:\n"+TimeSlots+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                    
            	}
            }
        }
        if(ctemp!=null) {
            ctemp.serialize();
        }
        temp.serialize();
        return true;
    }
    public boolean rejectRequest(){
        PriorityQueue<ArrayList<Reservation>> p = deserializeRequests();
        if (p.size() == 0) {
            serializeRequests(p);
            return false;
        }
        ArrayList<Reservation> r = p.peek();
        String recipient = r.get(0).getReserverEmail();
        String GreetText = getUser(recipient).getName();
        String TimeSlots="";
        ArrayList<Integer> x=new ArrayList<Integer>();
        for (Reservation reservation : r) {
        	x.add(reservation.getReservationSlot());
			TimeSlots+=Reservation.getSlotRange(reservation.getReservationSlot())+"\n";
		}
        Notification n = new Notification("Room Reservation Request", "Declined", r.get(0).getMessage(), r.get(0).getCourseName(), r.get(0).getTargetDate(), r.get(0).getRoomName(), r.get(0).getReserverEmail(), x);
        Student Stud = (Student)getUser(recipient);
        Stud.addNotification(n);
        Admin a = (Admin) getUser(Mail.from);
        a.addNotification(n);
        serializeUser(a);
        server.mailpool.execute(new Mail(recipient,"BooKIT - Room booking request rejected", GreetText+","+"\n\nThe following request of yours have been rejected by the admin:\n\n"+"Room: "+r.get(0).getVenueName()+"\nDate: "+r.get(0).getTargetDate().getDayOfMonth()+"/"+r.get(0).getTargetDate().getMonthValue()+"/"+r.get(0).getTargetDate().getYear()+"\nTime:\n"+TimeSlots+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
        p.poll();
        serializeUser(Stud);
        serializeRequests(p);
        Room temp = Room.deserializeRoom(r.get(0).getRoomName());
        for(int i=0;i<r.size();i++){
            temp.deleteRequest(r.get(i).getReserverEmail(), r.get(i).getTargetDate(), r.get(i).getReservationSlot());
        }
        temp.serialize();
        return true;
    }
    public boolean adminandfaculty_bookRoom(ArrayList<LocalDate> date, int slot, Reservation r) {
        HashMap<String, Integer> h = new HashMap<>();
    	h.put(Mail.from, 1);
        Room room=Room.deserializeRoom(r.getRoomName());
        Boolean addToCourse = true;
        if(r.getCourseName().equals("")){
            addToCourse = false;
        }
        Course course;
        course = Course.deserializeCourse(r.getCourseName());
        for(LocalDate temp2 : date){
            if(addToCourse){
                if (!(course.checkReservation(temp2, slot, r) == true && room.checkReservation(temp2, slot, r) == true)){
                    return false;
                }
            }
            else{
                if (!(room.checkReservation(temp2, slot, r) == true)) {
                    return false;
                }
            }
        }
        User temp = getUser(r.getReserverEmail());
        h.put(r.getReserverEmail(), 1);
        User tempAdmin = null;
        course = Course.deserializeCourse(r.getCourseName());
        if(r.getCourseName()!= null && !r.getCourseName().equals("")) {
        	h.put(r.getCourseName(), 1);
        }
        for(LocalDate start : date){
            if (addToCourse) {
                course.addReservation(start, slot, r);
                room.addReservation(start, slot, r);
                
                for(String email : h.keySet()) {
                ArrayList<Integer> x= new ArrayList<Integer>();
                x.add(r.getReservationSlot());
                Notification n = new Notification("Classroom Booking", "Done", r.getMessage(), r.getCourseName(), r.getTargetDate(), r.getRoomName(), r.getReserverEmail(),x );
                temp.addNotification(n);
                String GreetText="Hello User";
                User xy=getUser(email);
                if(xy!=null) {
                    GreetText = "Hello "+xy.getName();
                }
                server.mailpool.execute(new Mail(email,"BooKIT - Room booking completed", GreetText+","+"\n\nThe following booking of yours have been confirmed:\n\n"+"Room: "+r.getRoomName()+"\nDate: "+r.getTargetDate().getDayOfMonth()+"/"+r.getTargetDate().getMonthValue()+"/"+r.getTargetDate().getYear()+"\nTime: "+ Reservation.getSlotRange(r.getReservationSlot())+" \nReason: "+r.getMessageWithoutVenue()+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                serializeUser(xy);	
                }
            } else {
                room.addReservation(start, slot, r);
                for(String email : h.keySet()) {
                    ArrayList<Integer> x= new ArrayList<Integer>();
                    x.add(r.getReservationSlot());
                    Notification n = new Notification("Classroom Booking", "Done", r.getMessage(), r.getCourseName(), r.getTargetDate(), r.getRoomName(), r.getReserverEmail(),x );
                    temp.addNotification(n);
                    String GreetText="Hello User";
                    User xy=getUser(email);
                    if(xy!=null) {
                        GreetText = "Hello "+xy.getName();
                    }
                    server.mailpool.execute(new Mail(email,"BooKIT - Room booking completed", GreetText+","+"\n\nThe following booking of yours have been confirmed:\n\n"+"Room: "+r.getRoomName()+"\nDate: "+r.getTargetDate().getDayOfMonth()+"/"+r.getTargetDate().getMonthValue()+"/"+r.getTargetDate().getYear()+"\nTime: "+ Reservation.getSlotRange(r.getReservationSlot())+" \nReason: "+r.getMessageWithoutVenue()+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                    serializeUser(xy);	
                	}
            }
        }
        room.serialize();
        room.serialize();
        if(addToCourse) {
            course.serialize();
        }
        serializeUser(temp);
        return true;
    }
    public void faculty_addCourse(String email, String course){
        Faculty f = (Faculty) getUser(email);
        f.getCourses().add(course);
        serializeUser(f);
    }
    public boolean sendReservationRequest(ArrayList<Reservation> r) {
        PriorityQueue<ArrayList<Reservation>> p = null;
        Course c = Course.deserializeCourse(r.get(0).getCourseName());
        Room room = Room.deserializeRoom(r.get(0).getRoomName());
        if(c!=null){
            for(int i=0;i<r.size();i++){
                if(!c.checkReservation(r.get(i).getTargetDate(), r.get(i).getReservationSlot(),r.get(i))){
                    return false;
                }
            }
        }
        for(int i=0;i<r.size();i++){
            if(!room.checkReservation(r.get(i).getTargetDate(), r.get(i).getReservationSlot(),r.get(i))){
                return false;
            }
        }
        Student tempStudent=(Student)getUser(r.get(0).getReserverEmail());
        ArrayList<Integer> x= new ArrayList<Integer>();
        Admin tempAd = (Admin)getUser(Mail.from);
        p = deserializeRequests();
        p.add(r);
        serializeRequests(p);
        Room temp = Room.deserializeRoom(r.get(0).getRoomName());
        for(int i=0;i<r.size();i++){
            Reservation t=r.get(i);
        	temp.addRequest(t);
            x.add(t.getReservationSlot());
        }
        Notification n = new Notification("Room Reservation Request", "Pending", r.get(0).getMessage(), r.get(0).getCourseName(), r.get(0).getTargetDate(), r.get(0).getRoomName(), r.get(0).getReserverEmail(), x);
        tempAd.addNotification(n);
        tempStudent.addNotification(n);
        serializeUser(tempStudent);
        serializeUser(tempAd);
        temp.serialize();
        return true;
    }
    public ArrayList<String> searchCourse(ArrayList<String> keyword){
        int flag=1;
        for(int i=0;i<keyword.size();i++){
            if(!keyword.get(i).equals("")){
                flag=0;
                break;
            }
        }
        if(flag==1){
            return getAllCourses();
        }
        ArrayList<ArrayList<String>> arr=new ArrayList<ArrayList<String>>();
        for (int i=0;i<300;i++) {
            arr.add(new ArrayList<String>());
        }

        ArrayList<String> temp2=new ArrayList<String>();

        ArrayList<String> courseFiles=getAllCourses();
        for(int i=0;i<courseFiles.size();i++) {
            String courseName = courseFiles.get(i).substring(0,courseFiles.get(i).length());
            Course temp=Course.deserializeCourse(courseName);
            int match=temp.keyMatch(keyword);
            if(match > 0) {
                arr.get(match).add(courseFiles.get(i).substring(0,courseFiles.get(i).length()));
            }
        }
        for(int i=arr.size()-1;i>=0;i--) {
            for (String str : arr.get(i)) {
                temp2.add(str);
            }
        }
        return temp2;
    }
    public boolean student_addCourse(String c, String email) {
        Course c2=Course.deserializeCourse(c);
        Student user = (Student) getUser(email);
        for (String string : user.getMyCourses()) {
            Course temp=Course.deserializeCourse(string);
            if(c2.checkCollision(temp)) {
                return false; //cannot add course since there is a collision
            }
        }
        user.getMyCourses().add(c);
        serializeUser(user);
        return true;
    }
    public boolean studentAndFaculty_cancelBooking(LocalDate queryDate, int slotID, String RoomID) {
    	HashMap<String, Integer> h = new HashMap<>();
        h.put(Mail.from, 1);
        
    	Room temp=Room.deserializeRoom(RoomID);
        Reservation r=temp.getSchedule(queryDate)[slotID];
        if(r.getReserverEmail() != null) {
        	h.put(r.getReserverEmail(), 1);
        }
        temp.deleteReservation(queryDate, slotID);
        temp.serialize();
        Course c=Course.deserializeCourse(r.getCourseName());
        if(c!=null) {
        	if(c.getInstructorEmail() != null) {
        	h.put(c.getInstructorEmail(), 1);}
            c.deleteReservation(queryDate, slotID,r.getTopGroup());
            c.serialize();
        }
        for(String email : h.keySet()) {
        	if(!email.equals("")) {
        		String GreetText = "Hello User";
        		User x = getUser(email);
        		ArrayList<Integer> slot= new ArrayList<Integer>();
        		slot.add(r.getReservationSlot());
        		Notification n = new Notification("Room Booking", "Cancelled", r.getMessage(), r.getCourseName(), r.getTargetDate(), r.getRoomName(), r.getReserverEmail(), slot);
                if(x != null) {
        			GreetText = "Hello " + x.getName();
        			x.addNotification(n);
        		}
        		server.mailpool.execute(new Mail(email,"BooKIT - Room booking cancelled", GreetText+","+"\n\nThe following booking of yours have been cancelled by the admin:\n\n"+"Room: "+RoomID+"\nDate: "+queryDate.getDayOfMonth()+"/"+queryDate.getMonthValue()+"/"+queryDate.getYear()+"\nTime: "+ Reservation.getSlotRange(slotID) +"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
        		serializeUser(x);
        	}
        	}
        return true;
    }
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User u = getUser(email);
        if(u.authenticate(oldPassword)) {
            if(newPassword.length()!=0) {
                boolean b=newPassword.matches("[A-Za-z0-9]+");
                if(b) {
                	Notification n = new Notification("Password Changed", "Done", null, null, null, null, null, null);
                    u.addNotification(n);
                	u.setPassword(newPassword);
                    serializeUser(u);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean validateLogin(String y) {
        User temp=getUser(y);
        if(temp==null) {
            return false;
        }
        return true;
    }
    public void setInstructor(String f, String course){
        Course c = Course.deserializeCourse(course);
        c.instructorEmail = f;
        c.serialize();
        server.loadFreeCourses();
    }
    public String reservation_facultyEmail(String course) {
        Course c = Course.deserializeCourse(course);
        if (c == null) {
            return "";
        }
        return c.getInstructorEmail();
    }
    public Reservation[] getRoomDailySchedule(LocalDate queryDate, String room){
        Room r = Room.deserializeRoom(room);
        if(r==null){
            return null;
        }
        return r.getSchedule(queryDate);
    }
    public int getRoomCapacity(String room){
        Room r = Room.deserializeRoom(room);
        return r.getCapacity();
    }
    public Boolean RoomExists(String room){
        Room r = Room.deserializeRoom(room);
        if(r==null){
            return false;
        }
        else{
            return true;
        }
    }
    public static String course_getFaculty(String course){
        Course c = Course.deserializeCourse(course);
        if(c==null){
            return "";
        }
        return c.getInstructorEmail();
    }
    public String course_getAcronym(String course){
        Course c = Course.deserializeCourse(course);
        return c.getAcronym();
    }
    public Reservation[] course_getStudentTT(LocalDate activeDate, ArrayList<String> myCourses){
        ArrayList<Course> courseObjects = new ArrayList<>();
        for(int i=0;i<myCourses.size();i++){
            courseObjects.add(Course.deserializeCourse(myCourses.get(i)));
        }
        Reservation[] listOfReservations = new Reservation[30];
        for(int j=0;j<28;j++) {
            listOfReservations[j] = null;
            for (int i = 0; i < courseObjects.size(); i++) {
                Course c = courseObjects.get(i);
                if (c.getSchedule(activeDate)[j] != null) {
                    if (listOfReservations[j] == null) {
                        listOfReservations[j] = c.getSchedule(activeDate)[j];
                    }
                    else if (c.getSchedule(activeDate)[j].getType().equals("Lecture")) {
                        listOfReservations[j] = c.getSchedule(activeDate)[j];
                    }
                    else if (!listOfReservations[j].getType().equals("Lecture") && c.getSchedule(activeDate)[j].getType().equals("Lab")) {
                        listOfReservations[j] = c.getSchedule(activeDate)[j];
                    }
                }
            }
        }
        return listOfReservations;
    }
    public Reservation course_getReservation(String course, LocalDate queryDate, int slotID){
        Course c = Course.deserializeCourse(course);
        return c.getSchedule(queryDate)[slotID];
    }
    public void mailPass(String email){
    	User temp=getUser(email);
    	server.mailpool.execute(new Mail(temp.getEmail().getEmailID(),"BooKIT - Your Account Password is here", "Hello "+temp.getName()+","+"\n\nThank you for signing up with us.\n\nYour account password is as follows:"+"\n\nPassword - "+temp.getPassword()+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
    	
    }
    public void generatePass(String mail){
    	 Random rnd = new Random();
         StringBuilder sb = new StringBuilder();
         User temp=getUser(mail);
         while(sb.length()!=10) {
        	 sb.append(JoinString.charAt(((int)(rnd.nextFloat() * JoinString.length()))));
         }
         temp.setPassword(sb.toString());
         serializeUser(temp);
    }
    public String getUserType(String email){
        String ans=null;
        if(server.studhash!=null && server.studhash.containsKey(email)){
            ans="Student";
        }
        else if(server.faculthash!=null && server.faculthash.containsKey(email)) {
            ans="Faculty";
        }
        else if(server.adminhash!=null && server.adminhash.containsKey(email)) {
        	ans = "Admin";
        }
       return ans;
        // write code here
    	// write code here
    	
    }
    public Boolean isCompatible(double version){
        if(server.BookITversion == version){
            return true;
        }
        else{
            return false;
        }
    }
    public ArrayList<Notification> GetNotifications(String email){
        User x  = getUser(email);
        ArrayList<Notification> temp = x.getterNotification();
        while(temp.size()>100) {
            temp.remove(0);

        }
        x.setNotification(temp);
        serializeUser(x);
        return temp;
    }
    public Boolean checkBulkBooking(String room, ArrayList<Integer> slots, ArrayList<LocalDate> date){
        Room r = Room.deserializeRoom(room);
        for(LocalDate start: date){
            Reservation[] temp = r.getSchedule(start);
            for(int i=0;i<slots.size();i++){
                if(temp[slots.get(i)]!=null){
                    return false;
                }
            }
        }
        return true;
    }
    public void run(){
        server.noOfConnections++;
        ObjectInputStream in=null;
        ObjectOutputStream out=null;
        String status="";
        try {
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());
            status = (String)in.readObject();
            if(status.equals("Pass")){      // Inverted logic. Parameter lock in other functions = false => take lock and vice versa. This is done so that client code is not modified
                status = "Hold";
            }
            else{
                status = "Pass";
            }
        }
        catch (IOException m){
            ;
        }
        catch (ClassNotFoundException c){
            ;
        }
        try {
            if(!status.equals("Pass")){
                System.out.print("[ "+LocalDateTime.now()+" ] ");
                System.out.println(connection.getInetAddress().toString() + " | ServerLock Taken - Pending connections: "+server.noOfConnections);
            }
            try {
                String request = (String) in.readObject();
                System.out.print("[ "+LocalDateTime.now()+" ] ");
                System.out.println(connection.getInetAddress().toString() + " | Performing " + request);
                Course c;
                User u;
                Room r;
                Reservation res;
                HashMap<String, Integer> joinCode;
                String code, email, course, room;
                LocalDate queryDate;
                Boolean result;
                int slotID;
                LocalDate start;
                Boolean ans;
                LocalDate end;
                PriorityQueue<ArrayList<Reservation>> req;
                switch (request) {
                    case "GetUser":
                        email = (String) in.readObject();
                        u = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        u = getUser(email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(u);
                        out.flush();
                        break;
                    case "WriteUser":
                        u = (User) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        serializeUser(u);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "getNotifications":
                        email = (String)in.readObject();
                        ArrayList<Notification> array=null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        array = GetNotifications(email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(array);
                        out.flush();
                        break;
                    case "AllCourses":
                        ArrayList<String> arr=null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        arr = getAllCourses();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(arr);
                        out.flush();
                        break;
                    case "WriteJoinCode":
                        joinCode = (HashMap<String, Integer>) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        serializeJoinCode(joinCode);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "ReadJoinCode":
                        joinCode = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        joinCode = deserializeJoinCodes();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(joinCode);
                        out.flush();
                        break;
                    case "WriteRequest":
                        req = (PriorityQueue<ArrayList<Reservation>>) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        serializeRequests(req);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "ReadRequest":
                        req=null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        req = deserializeRequests();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(req);
                        out.flush();
                        break;
                    case "SpamCheck":
                        String message = (String) in.readObject();
                        Boolean spamStatus = true;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        spamStatus = SpamFilter.Predict(message);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(spamStatus);
                        out.flush();
                        break;
                    case "GetStartEndDate":
                        ArrayList<LocalDate> StartEndDate=null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        StartEndDate = fetchSemDate();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(StartEndDate);
                        out.flush();
                        break;
                    case "admin_BookingCancelNotification":
                        queryDate = (LocalDate) in.readObject();
                        slotID = (int) in.readObject();
                        String RoomID = (String) in.readObject();
                        String cancellationMessage = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        BookingCancellationNotifier(queryDate, slotID, RoomID, cancellationMessage);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "generateJoinCode":
                        String type = (String) in.readObject();
                        String temp = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        temp = generateJoincode(type);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(temp);
                        out.flush();
                        break;
                    case "containsJoinCode":
                        code = (String) in.readObject();
                        Boolean ret = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ret = containsJoinCode(code);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(ret);
                        out.flush();
                        break;
                    case "removeJoinCode":
                        code = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        removeJoinCode(code);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "getRequest":
                        ArrayList<Reservation> requ = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        requ = getRequest();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(requ);
                        out.flush();
                        break;
                    case "acceptRequest":
                        ArrayList<Integer> data = (ArrayList<Integer>) in.readObject();
                        ans = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ans = acceptRequest(data);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(ans);
                        out.flush();
                        break;
                    case "rejectRequest":
                        ans = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ans=rejectRequest();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(ans);
                        out.flush();
                        break;
                    case "adminandfaculty_bookroom":
                        ArrayList<LocalDate> date = (ArrayList<LocalDate>) in.readObject();
                        slotID = (int) in.readObject();
                        res = (Reservation) in.readObject();
                        ans = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ans = adminandfaculty_bookRoom(date, slotID, res);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(ans);
                        out.flush();
                        break;
                    case "faculty_addCourse":
                        email = (String) in.readObject();
                        course = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        faculty_addCourse(email, course);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "student_sendReservationRequest":
                        ArrayList<Reservation> reservations = (ArrayList<Reservation>) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = sendReservationRequest(reservations);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "student_searchCourse":
                        ArrayList<String> keyword = (ArrayList<String>) in.readObject();
                        ArrayList<String> shortlist = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        shortlist = searchCourse(keyword);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(shortlist);
                        out.flush();
                        break;
                    case "student_addCourse":
                        course = (String) in.readObject();
                        email = (String) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = student_addCourse(course, email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "studentandfaculty_cancelBooking":
                        queryDate = (LocalDate) in.readObject();
                        slotID = (int) in.readObject();
                        RoomID = (String) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = studentAndFaculty_cancelBooking(queryDate, slotID, RoomID);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "changePassword":
                        email = (String) in.readObject();
                        String oldPass = (String) in.readObject();
                        String newPass = (String) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = changePassword(email, oldPass, newPass);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "validateLogin":
                        email = (String) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = validateLogin(email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "setCourseInstructor":
                        course = (String) in.readObject();
                        email = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        setInstructor(email, course);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "reservation_facultyEmail":
                        course = (String) in.readObject();
                        email = "";
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        email = reservation_facultyEmail(course);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(email);
                        out.flush();
                        break;
                    case "getRoomDailySchedule":
                        queryDate = (LocalDate) in.readObject();
                        room = (String) in.readObject();
                        Reservation[] tempReservation = null;
                        if(!status.equals("Pass")){
                            lock.lockInterruptibly();
                        }
                        tempReservation = getRoomDailySchedule(queryDate, room);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(tempReservation);
                        out.flush();
                        break;
                    case "getRoomCapacity":
                        room = (String) in.readObject();
                        int roomsize = 0;
                        if(!status.equals("Pass")){
                            lock.lockInterruptibly();
                        }
                        roomsize = getRoomCapacity(room);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(roomsize);
                        out.flush();
                        break;
                    case "checkRoomExistence":
                        room = (String) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = RoomExists(room);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "course_getFaculty":
                        course = (String) in.readObject();
                        email="";
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        email = course_getFaculty(course);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(email);
                        out.flush();
                        break;
                    case "course_getStudentTT":
                        queryDate = (LocalDate) in.readObject();
                        ArrayList<String> myCourses = (ArrayList<String>) in.readObject();
                        Reservation[] tempSchedule = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        tempSchedule = course_getStudentTT(queryDate, myCourses);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(tempSchedule);
                        out.flush();
                        break;
                    case "course_getAcronym":
                        course = (String) in.readObject();
                        String acronym = "";
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        acronym = course_getAcronym(course);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(acronym);
                        out.flush();
                        break;
                    case "course_getReservation":
                        course = (String) in.readObject();
                        queryDate = (LocalDate) in.readObject();
                        slotID = (int) in.readObject();
                        res = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        res = course_getReservation(course, queryDate, slotID);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(res);
                        out.flush();
                        break;
                    case "mailPass":
                        email = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        mailPass(email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "generatePass":
                        email = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        generatePass(email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "getUserType":
                        email = (String) in.readObject();
                        String usertype = "";
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        usertype = getUserType(email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(usertype);
                        out.flush();
                        break;
                    case "CompatibilityCheck":
                        double version = (double) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = isCompatible(version);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "admin_getRequestsQueue":
                        PriorityQueue<ArrayList<Reservation>> pqReq = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        pqReq = deserializeRequests();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(pqReq);
                        out.flush();
                        break;
                    case "studentDeleteReservationRequest":
                        email = (String) in.readObject();
                        queryDate = (LocalDate) in.readObject();
                        slotID = (int) in.readObject();
                        room = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        r = Room.deserializeRoom(room);
                        r.deleteRequest(email, queryDate, slotID);
                        r.serialize();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "studentGetReservationRequest":
                        email = (String) in.readObject();
                        queryDate = (LocalDate) in.readObject();
                        slotID = (int) in.readObject();
                        room = (String) in.readObject();
                        res = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        r = Room.deserializeRoom(room);
                        res = r.fetchRequest(email, queryDate, slotID);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(res);
                        out.flush();
                        break;
                    case "studentGetPendingReservations":
                        email = (String) in.readObject();
                        queryDate = (LocalDate) in.readObject();
                        room = (String) in.readObject();
                        Reservation[] pending = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        r = Room.deserializeRoom(room);
                        pending = r.getPendingReservations(email, queryDate);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(pending);
                        out.flush();
                        break;
                    case "checkBulkBooking":
                        room = (String) in.readObject();
                        ArrayList<Integer> slots = (ArrayList<Integer>) in.readObject();
                        ArrayList<LocalDate> d = (ArrayList<LocalDate>) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = checkBulkBooking(room, slots, d);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
                        break;
                    case "softResetServer":
                        if(!status.equals("Pass")){         // Must take lock to ensure consistency
                            lock.lockInterruptibly();
                        }
                        server.loadHashMaps();
                        server.loadFreeCourses();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        break;
                    case "faculty_freeCourses":
                        out.writeObject(server.freeCourses);
                        out.flush();
                        System.out.print("[ "+LocalDateTime.now()+" ] ");
                        System.out.println(connection.getInetAddress().toString() + " | ServerLock request cancelled");
                        break;
                    case "adminEmail":
                        out.writeObject(BookITconstants.NoReplyEmail);
                        out.flush();
                        System.out.print("[ "+LocalDateTime.now()+" ] ");
                        System.out.println(connection.getInetAddress().toString() + " | ServerLock request cancelled");
                }
                in.close();
                out.close();
            } catch (IOException e) {
                BookITconstants.writeLog("Error Occured while handling connection");
            } catch (ClassNotFoundException e) {
                BookITconstants.writeLog("Error Occured while handling connection");
            }
        }
        catch(Exception l){
            ;
        }
        server.noOfConnections--;
    }
}
class Mail implements Runnable{
    String recipient,Subject,Body;
    static String from = HelperClasses.BookITconstants.NoReplyEmail;//change accordingly
    final static String username = HelperClasses.BookITconstants.NoReplyUsername;//change accordingly
    final static String password = HelperClasses.BookITconstants.NoReplyPassword;//change accordingly
    public Mail(String target,String subject,String body) {
        recipient=target;Subject=subject;Body=body;
    }
    public void sendMail(){
          String host = "smtp.gmail.com";
          Properties props = new Properties();
          props.put("mail.smtp.auth", "true");
          props.put("mail.smtp.starttls.enable", "true");
          props.put("mail.smtp.host", host);
          props.put("mail.smtp.port", "587");
          // Get the Session object.
          Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
             protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
             }
          });
          try {
             // Create a default MimeMessage object.
             Message message = new MimeMessage(session);
             // Set From: header field of the header.
             message.setFrom(new InternetAddress(from));
             // Set To: header field of the header.
             message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(recipient));
             // Set Subject: header field
             message.setSubject(Subject);
             // Now set the actual message
             message.setText(Body);
             // Send message
             Transport.send(message);
          } catch (MessagingException e) {
                throw new RuntimeException(e);
          }
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        sendMail();
    }
}

