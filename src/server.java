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
    public static SpamFilter spm;
    public static ExecutorService mailpool = Executors.newFixedThreadPool(2);
    public static void main(String[] args)throws IOException{
        BookITconstants b = new BookITconstants();
        ServerSocket s = new ServerSocket(BookITconstants.serverPort);
        ConnectionHandler.lock = new ReentrantLock();
        spm = new SpamFilter();
        ExecutorService threads = Executors.newFixedThreadPool(4);
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
        // Insert mailing module here

        Room temp=Room.deserializeRoom(RoomID);
        Reservation r=temp.getSchedule(queryDate)[slotID];
        String recipient = r.getReserverEmail();
        String GreetText="Hello User";
        User x=User.getUser(recipient);
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
                System.out.println();
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
    public boolean acceptRequest(){
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
        if(r!=null) {
            for (Reservation reservation : r) {
                temp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation);
                temp.serialize();
                if(ctemp!=null) {
                    ctemp.addReservation(r.get(0).getTargetDate(), reservation.getReservationSlot(), reservation);
                    ctemp.serialize();
                }
            }
        }
        return true;
    }
    public boolean rejectRequest(){
        PriorityQueue<ArrayList<Reservation>> p = deserializeRequests();
        if (p.size() == 0) {
            serializeRequests(p);
            return false;
        }
        p.poll();
        serializeRequests(p);
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
        Faculty f = (Faculty) User.getUser(email);
        f.getCourses().add(course);
        f.serialize();
    }
    public boolean sendReservationRequest(ArrayList<Reservation> r) {
        PriorityQueue<ArrayList<Reservation>> p = null;
        p = deserializeRequests();
        p.add(r);
        serializeRequests(p);
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
        Student user = (Student) User.getUser(email);
        for (String string : user.getMyCourses()) {
            Course temp=Course.deserializeCourse(string);
            if(c2.checkCollision(temp)) {
                return false; //cannot add course since there is a collision
            }
        }
        user.getMyCourses().add(c);
        user.serialize();
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
        User u = User.getUser(email);
        if(u.authenticate(oldPassword)) {
            if(newPassword.length()!=0) {
                boolean b=newPassword.matches("[A-Za-z0-9]+");
                if(b) {
                    u.setPassword(newPassword);
                    u.serialize();
                    return true;
                }
            }
        }
        return false;
    }
    public boolean validateLogin(String y) {
        User temp=User.getUser(y);
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
                                out.writeObject(acceptRequest());
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
                                faculty_addCourse(email,course);
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
                                String oldPass = (String)  in.readObject();
                                String newPass = (String)  in.readObject();
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

