import HelperClasses.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.ECField;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	public static final double BookITversion = 1.0;
    public static SpamFilter spm;
    public static ExecutorService mailpool = Executors.newFixedThreadPool(2);
    public static HashMap<String, Integer> studhash=null;
    public static HashMap<String, Integer> faculthash=null;
    public static HashMap<String, Integer> connectedIPs=null;

    public static void loadHashMaps(){
        ObjectInputStream in=null;
        ObjectInputStream in2=null;
        try
        {
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Server/StudentEmails.dat"));
            studhash = (HashMap<String, Integer>)in.readObject();
            in2 = new ObjectInputStream(new FileInputStream("./src/AppData/Server/FacultyEmails.dat"));
            faculthash = (HashMap<String, Integer>)in.readObject();
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
        }
    }
    public static void main(String[] args)throws IOException{
        BookITconstants b = new BookITconstants();
        connectedIPs = new HashMap<>();
        loadHashMaps();
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
    public ArrayList<String> getAllCourses(){
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
            System.out.println("file not found");
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
            System.out.println("file not found");
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
            System.out.println("file not found");
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

        Room temp=Room.deserializeRoom(RoomID);
        Reservation r=temp.getSchedule(queryDate)[slotID];
        String recipient = r.getReserverEmail();
        String GreetText="Hello User";
        User x=getUser(recipient);
        if(x!=null) {
            GreetText = "Hello "+x.getName();
        }
        server.mailpool.execute(new Mail(recipient,"BooKIT - Room booking cancelled", GreetText+","+"\n\nThe following booking of yours have been cancelled by the admin:\n\n"+"Room: "+RoomID+"\nDate: "+queryDate.getDayOfMonth()+"/"+queryDate.getMonthValue()+"/"+queryDate.getYear()+"\nTime: "+ Reservation.getSlotRange(slotID)+" \nReason: "+cancellationMessage+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
        
        temp.deleteReservation(queryDate, slotID);
        temp.serialize();
        Course c=Course.deserializeCourse(r.getCourseName());
        if(c!=null) {
            c.deleteReservation(queryDate, slotID,r.getTopGroup());
            c.serialize();
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
        for(int i=0;i<r.size();i++){
            temp.deleteRequest(r.get(0).getReserverEmail(), r.get(0).getTargetDate(), r.get(i).getReservationSlot());
        }
        if(r!=null) {
            for (int i=0;i<data.size();i++) {
                Reservation reservation = r.get(data.get(i));
                temp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation);
                temp.deleteRequest(r.get(0).getReserverEmail(), r.get(0).getTargetDate(), r.get(i).getReservationSlot());
                if(ctemp!=null) {
                    ctemp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation);
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
        p.poll();
        serializeRequests(p);
        Room temp = Room.deserializeRoom(r.get(0).getRoomName());
        for(int i=0;i<r.size();i++){
            temp.deleteRequest(r.get(i).getReserverEmail(), r.get(i).getTargetDate(), r.get(i).getReservationSlot());
        }
        temp.serialize();
        return true;
    }
    public boolean adminandfaculty_bookRoom(LocalDate queryDate,int slot, Reservation r) {
        Room room=Room.deserializeRoom(r.getRoomName());
        Boolean addToCourse = true;
        if(r.getCourseName().equals("")){
            addToCourse = false;
        }
        Course course;
        if(addToCourse) {
            course = Course.deserializeCourse(r.getCourseName());
            if(course.checkReservation(queryDate,slot,r)==true && room.checkReservation(queryDate,slot,r)==true) {
                course.addReservation(queryDate,slot,r);
                course.serialize();
                room.addReservation(queryDate,slot,r);
                room.serialize();
                return true;
            }
        }
        else{
            if(room.checkReservation(queryDate,slot,r)==true){
                room.addReservation(queryDate,slot,r);
                room.serialize();
                return true;
            }
        }
        return false;
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
        p = deserializeRequests();
        p.add(r);
        serializeRequests(p);
        Room temp = Room.deserializeRoom(r.get(0).getRoomName());
        for(int i=0;i<r.size();i++){
            temp.addRequest(r.get(i));
        }
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
        Room temp=Room.deserializeRoom(RoomID);
        Reservation r=temp.getSchedule(queryDate)[slotID];
        temp.deleteReservation(queryDate, slotID);
        temp.serialize();
        Course c=Course.deserializeCourse(r.getCourseName());
        if(c!=null) {
            c.deleteReservation(queryDate, slotID,r.getTopGroup());
            c.serialize();
        }
        return true;
    }
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User u = getUser(email);
        if(u.authenticate(oldPassword)) {
            if(newPassword.length()!=0) {
                boolean b=newPassword.matches("[A-Za-z0-9]+");
                if(b) {
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
    public String course_getFaculty(String course){
        Course c = Course.deserializeCourse(course);
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
        if(server.studhash.containsKey(email)){
            ans="Student";
        }
        if(server.faculthash.containsKey(email)) {
            ans="Faculty";
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
    public void run(){
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
        do {
            try {
                if(status.equals("Pass") || lock.tryLock(10, TimeUnit.SECONDS)) {
                    if(!status.equals("Pass")){
                        System.out.print("[ "+LocalDateTime.now()+" ] ");
                        System.out.println(connection.getInetAddress().toString() + " | ServerLock Taken");
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
                        PriorityQueue<ArrayList<Reservation>> req;
                        switch (request) {
                            case "GetUser":
                                email = (String) in.readObject();
                                u = getUser(email);
                                out.writeObject(u);
                                out.flush();
                                break;
                            case "WriteUser":
                                u = (User) in.readObject();
                                serializeUser(u);
                                break;
                            case "AllCourses":
                                ArrayList<String> arr = getAllCourses();
                                out.writeObject(arr);
                                out.flush();
                                break;
                            case "WriteJoinCode":
                                joinCode = (HashMap<String, Integer>) in.readObject();
                                serializeJoinCode(joinCode);
                                break;
                            case "ReadJoinCode":
                                joinCode = deserializeJoinCodes();
                                out.writeObject(joinCode);
                                out.flush();
                                break;
                            case "WriteRequest":
                                req = (PriorityQueue<ArrayList<Reservation>>) in.readObject();
                                serializeRequests(req);
                                break;
                            case "ReadRequest":
                                req = deserializeRequests();
                                out.writeObject(req);
                                out.flush();
                                break;
                            case "SpamCheck":
                                String message = (String) in.readObject();
                                Boolean spamStatus = SpamFilter.Predict(message);
                                out.writeObject(spamStatus);
                                out.flush();
                                break;
                            case "GetStartEndDate":
                                ArrayList<LocalDate> StartEndDate;
                                StartEndDate = fetchSemDate();
                                out.writeObject(StartEndDate);
                                out.flush();
                                break;
                            case "admin_BookingCancelNotification":
                                queryDate = (LocalDate) in.readObject();
                                slotID = (int) in.readObject();
                                String RoomID = (String) in.readObject();
                                String cancellationMessage = (String) in.readObject();
                                BookingCancellationNotifier(queryDate, slotID, RoomID, cancellationMessage);
                                break;
                            case "generateJoinCode":
                                String type = (String) in.readObject();
                                out.writeObject(generateJoincode(type));
                                out.flush();
                                break;
                            case "containsJoinCode":
                                code = (String) in.readObject();
                                out.writeObject(containsJoinCode(code));
                                out.flush();
                                break;
                            case "removeJoinCode":
                                code = (String) in.readObject();
                                removeJoinCode(code);
                                break;
                            case "getRequest":
                                out.writeObject(getRequest());
                                out.flush();
                                break;
                            case "acceptRequest":
                                ArrayList<Integer> data = (ArrayList<Integer>) in.readObject();
                                out.writeObject(acceptRequest(data));
                                out.flush();
                                break;
                            case "rejectRequest":
                                out.writeObject(rejectRequest());
                                out.flush();
                                break;
                            case "adminandfaculty_bookroom":
                                queryDate = (LocalDate) in.readObject();
                                slotID = (int) in.readObject();
                                res = (Reservation) in.readObject();
                                out.writeObject(adminandfaculty_bookRoom(queryDate, slotID, res));
                                out.flush();
                                break;
                            case "faculty_addCourse":
                                email = (String) in.readObject();
                                course = (String) in.readObject();
                                faculty_addCourse(email, course);
                                break;
                            case "student_sendReservationRequest":
                                ArrayList<Reservation> reservations = (ArrayList<Reservation>) in.readObject();
                                result = sendReservationRequest(reservations);
                                out.writeObject(result);
                                out.flush();
                                break;
                            case "student_searchCourse":
                                ArrayList<String> keyword = (ArrayList<String>) in.readObject();
                                ArrayList<String> shortlist = searchCourse(keyword);
                                out.writeObject(shortlist);
                                out.flush();
                                break;
                            case "student_addCourse":
                                course = (String) in.readObject();
                                email = (String) in.readObject();
                                result = student_addCourse(course, email);
                                out.writeObject(result);
                                out.flush();
                                break;
                            case "studentandfaculty_cancelBooking":
                                queryDate = (LocalDate) in.readObject();
                                slotID = (int) in.readObject();
                                RoomID = (String) in.readObject();
                                result = studentAndFaculty_cancelBooking(queryDate, slotID, RoomID);
                                out.writeObject(result);
                                out.flush();
                                break;
                            case "changePassword":
                                email = (String) in.readObject();
                                String oldPass = (String) in.readObject();
                                String newPass = (String) in.readObject();
                                result = changePassword(email, oldPass, newPass);
                                out.writeObject(result);
                                out.flush();
                                break;
                            case "validateLogin":
                                email = (String) in.readObject();
                                out.writeObject(validateLogin(email));
                                out.flush();
                                break;
                            case "setCourseInstructor":
                                course = (String) in.readObject();
                                email = (String) in.readObject();
                                setInstructor(email, course);
                                break;
                            case "reservation_facultyEmail":
                                course = (String) in.readObject();
                                out.writeObject(reservation_facultyEmail(course));
                                out.flush();
                                break;
                            case "getRoomDailySchedule":
                                queryDate = (LocalDate) in.readObject();
                                room = (String) in.readObject();
                                out.writeObject(getRoomDailySchedule(queryDate, room));
                                out.flush();
                                break;
                            case "getRoomCapacity":
                                room = (String) in.readObject();
                                out.writeObject(getRoomCapacity(room));
                                out.flush();
                                break;
                            case "checkRoomExistence":
                                room = (String) in.readObject();
                                out.writeObject(RoomExists(room));
                                out.flush();
                                break;
                            case "course_getFaculty":
                                course = (String) in.readObject();
                                out.writeObject(course_getFaculty(course));
                                out.flush();
                                break;
                            case "course_getStudentTT":
                                queryDate = (LocalDate) in.readObject();
                                ArrayList<String> myCourses = (ArrayList<String>) in.readObject();
                                out.writeObject(course_getStudentTT(queryDate, myCourses));
                                out.flush();
                                break;
                            case "course_getAcronym":
                                course = (String) in.readObject();
                                out.writeObject(course_getAcronym(course));
                                out.flush();
                                break;
                            case "course_getReservation":
                                course = (String) in.readObject();
                                queryDate = (LocalDate) in.readObject();
                                slotID = (int) in.readObject();
                                out.writeObject(course_getReservation(course, queryDate, slotID));
                                out.flush();
                                break;
                            case "mailPass":
                                email = (String) in.readObject();
                                mailPass(email);
                                break;
                            case "generatePass":
                                email = (String) in.readObject();
                                generatePass(email);
                                break;
                            case "getUserType":
                                email = (String) in.readObject();
                                out.writeObject(getUserType(email));
                                out.flush();
                                break;
                            case "CompatibilityCheck":
                                double version = (double) in.readObject();
                                out.writeObject(isCompatible(version));
                                out.flush();
                                break;
                            case "admin_getRequestsQueue":
                                out.writeObject(deserializeRequests());
                                out.flush();
                                break;
                            case "studentGetReservationRequest":
                                email = (String) in.readObject();
                                queryDate = (LocalDate) in.readObject();
                                slotID = (int) in.readObject();
                                room = (String) in.readObject();
                                r = Room.deserializeRoom(room);
                                out.writeObject(r.fetchRequest(email, queryDate, slotID));
                                out.flush();
                                break;
                            case "studentGetPendingReservations":
                                email = (String) in.readObject();
                                queryDate = (LocalDate) in.readObject();
                                room = (String) in.readObject();
                                r = Room.deserializeRoom(room);
                                out.writeObject(r.getPendingReservations(email, queryDate));
                                out.flush();
                        }
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        System.out.println("Error Occured while handling connection");
                    } catch (ClassNotFoundException e) {
                        System.out.println("Error Occured while handling connection");
                    }
                    break;
                }
            }
            catch(InterruptedException l){
                ;
            }
        }while(true);
    }
}
class Mail implements Runnable{
    String recipient,Subject,Body;
    static String from = "harsh.pathak.temp@gmail.com";//change accordingly
    final static String username = "harsh.pathak.temp";//change accordingly
    final static String password = "1a2bb3ccc";//change accordingly
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
             message.setRecipients(Message.RecipientType.TO,
             InternetAddress.parse("harsh16041@iiitd.ac.in"));
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

