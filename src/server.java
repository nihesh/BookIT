
import HelperClasses.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.print.DocFlavor;

/**
 * the server class for back-end development
 */
public class server {
    /**
     * spam filter object for detecting spam messages
     */
    public static final double BookITversion = 1.3;
    public static SpamFilter spm;
    public static int noOfConnections = 0;
    public static ExecutorService mailpool = Executors.newFixedThreadPool(2);
    public static HashMap<String, Integer> studhash=null;
    public static HashMap<String, Integer> faculthash=null;
    public static HashMap<String, Integer> adminhash=null;
    public static ArrayList<String> freeCourses = null;
    public static HashMap<LocalDate, Boolean> HolidaysList = null;
    public static HashMap<LocalDate, Boolean> BlockedDaysList = null;

    public static void loadFreeCourses(){
        freeCourses = new ArrayList<>();
        ArrayList<String> allCourses = ConnectionHandler.getAllCourses();       // Static helper in connection handler
        for(int i=0;i<allCourses.size();i++){
            String email = ConnectionHandler.course_getFaculty(allCourses.get(i));
            if(email.equals("")){
                freeCourses.add(allCourses.get(i));
            }
        }
    }
    public static void loadBlockedDaysList(){
        try {
            BlockedDaysList = new HashMap<>();
            Scanner blockeddays = new Scanner(new BufferedReader(new FileReader("./src/AppData/StaticTimeTable/blocked_days.txt")));
            blockeddays.useDelimiter("-|\\n");
            while (blockeddays.hasNext()) {
                int date = Integer.parseInt(blockeddays.next().trim());
                int month = Integer.parseInt(blockeddays.next().trim());
                int year = Integer.parseInt(blockeddays.next().trim());
                BlockedDaysList.put(LocalDate.of(year, month, date), true);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception occurred while loading blocked days list");
        }
    }
    public static void loadHolidaysList(){
        try {
            HolidaysList = new HashMap<>();
            Scanner holidays = new Scanner(new BufferedReader(new FileReader("./src/AppData/StaticTimeTable/holidays.txt")));
            holidays.useDelimiter("-|\\n");
            while (holidays.hasNext()) {
                int date = Integer.parseInt(holidays.next().trim());
                int month = Integer.parseInt(holidays.next().trim());
                int year = Integer.parseInt(holidays.next().trim());
                HolidaysList.put(LocalDate.of(year, month, date), true);
            }
        }
        catch (Exception e){
            System.out.println("Exception occurred while loading holidays list");
        }
    }

    public static void loadUserHashMaps(){

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
        BookITconstants.transactions = new FileWriter(new File("./src/AppData/Server/transactions.txt"), true);
        loadUserHashMaps();
        loadFreeCourses();
        loadHolidaysList();
        loadBlockedDaysList();
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
    public LinkedList<ArrayList<Reservation>> deserializeRequests(){
        LinkedList<ArrayList<Reservation>> p=null;
        ObjectInputStream in=null;
        try
        {
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Requests/requests.txt"));
            p = ((LinkedList<ArrayList<Reservation>>)in.readObject());
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
    public void serializeRequests(LinkedList<ArrayList<Reservation>> r) {
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
    public void BookingCancellationNotifier(LocalDate queryDate, int slotID, String RoomID, String cancellationMessage, String cancelledBy){
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
        temp.deleteReservation(queryDate, slotID, cancelledBy);
        temp.serialize();
        Course c=Course.deserializeCourse(r.getCourseName());
        if(c!=null) {
            c.deleteReservation(queryDate, slotID,r.getTopGroup(), r.getCreationDate());
            c.serialize();
        }
        ArrayList<LocalDate> target_date = new ArrayList<>();
        target_date.add(r.getTargetDate());
        for(String email : h.keySet()) {
            if(!email.equals("")) {

                User x=getUser(email);
                ArrayList<Integer> t = new ArrayList<>();
                t.add(r.getReservationSlot());
                Notification n = new Notification("Classroom Booking", "Cancelled", r.getMessage(), r.getCourseName(), target_date, r.getRoomName(), r.getReserverEmail(),t);
                n.setCancelledBy(cancelledBy);
                String GreetText="Hello User";
                if(x != null) {
                    GreetText = "Hello" + x.getName();
                    x.addNotification(n);
                }
                server.mailpool.execute(new Mail(email,"BooKIT - Room booking cancelled", GreetText+","+"\n\nThe following booking of yours have been cancelled:\n\n"+ "Cancelled By: " +  cancelledBy + "\n" + r.getMessage() + "\nBooked By: " + r.getReserverEmail() + "\n"  +"\nDate: "+queryDate.getDayOfMonth()+"/"+queryDate.getMonthValue()+"/"+queryDate.getYear()+"\nTime: "+ Reservation.getSlotRange(slotID)+" \nReason: "+cancellationMessage+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
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
    public ArrayList<Reservation> FilterInvalidSlots(ArrayList<Reservation> r){     // Returns null if request expired or the user deleted the request
        if(r == null || r.size()==0){
            return null;
        }
        if(SpamFilter.Predict(r.get(0).getMessageWithoutVenue()) || r.get(0).getCreationDate().plusDays(10).isBefore(LocalDateTime.now()) || r.get(0).getTargetDate().isBefore(LocalDate.now())){
            return null;
        }
        else{
            Room temp = Room.deserializeRoom(r.get(0).getRoomName());
            Course ctemp = Course.deserializeCourse(r.get(0).getCourseName());
            Reservation[] pending = temp.getPendingReservations(r.get(0).getReserverEmail(),r.get(0).getTargetDate());
            int flag=0;
            for(int i=r.size()-1;i>=0;i--){
                if(pending[r.get(i).getReservationSlot()] == null || !pending[r.get(i).getReservationSlot()].getCreationDate().equals(r.get(i).getCreationDate())){
                    r.remove(i);
                }
            }
            for(int i=r.size()-1;i>=0;i--){
                if (!temp.checkReservation(r.get(0).getTargetDate(), r.get(i).getReservationSlot(), r.get(i))) {
                    r.remove(i);
                }
                if(ctemp!=null) {
                    if (ctemp.checkInternalCollision(r.get(i))) {
                        r.remove(i);
                    }
                }
            }
            if(r.size()==0){
                return null;
            }
            return r;
        }
    }
    public LinkedList<ArrayList<Reservation>> getRequest(){
        LinkedList<ArrayList<Reservation> > requests = deserializeRequests();
        LinkedList<ArrayList<Reservation> > filteredRequests = new LinkedList<>();
        for(int i=0;i<requests.size();i++){
            ArrayList<Reservation> temp = FilterInvalidSlots(requests.get(i));
            if(temp!=null) {
                filteredRequests.add(temp);
            }
        }
        serializeRequests(filteredRequests);
        return filteredRequests;
    }
    public boolean acceptRequest(ArrayList<Reservation> acceptList, ArrayList<Reservation> rejectList){
        int flag=0;
        String room_Name;
        String course_Name;
        if(acceptList.size()>0){
            room_Name = acceptList.get(0).getRoomName();
            course_Name = acceptList.get(0).getCourseName();
        }
        else if(rejectList.size()>0){
            room_Name = rejectList.get(0).getRoomName();
            course_Name = rejectList.get(0).getCourseName();
        }
        else{
            return false;
        }
        Room temp = Room.deserializeRoom(room_Name);
        Course ctemp = Course.deserializeCourse(course_Name);
        for(int i=0;i<rejectList.size();i++){
            temp.deleteRequest(rejectList.get(0).getReserverEmail(), rejectList.get(0).getTargetDate(), rejectList.get(i).getReservationSlot());
        }
        ArrayList<Integer> x=new ArrayList<Integer>();
        if(acceptList.size()>0) {
            String recipient = acceptList.get(0).getReserverEmail();
            String GreetText = "Hello " + getUser(recipient).getName();
            String TimeSlots="";
            LocalDateTime creat = LocalDateTime.now();
            for (int i=0;i<acceptList.size();i++) {
                Reservation reservation = acceptList.get(i);
                reservation.setCreationDate(creat);
                reservation.removeRequestFlag();
                x.add(reservation.getReservationSlot());
                TimeSlots += Reservation.getSlotRange(reservation.getReservationSlot())+"\n";
                temp.addReservation(reservation.getTargetDate(), reservation.getReservationSlot(), reservation, null);
                temp.deleteRequest(reservation.getReserverEmail(), reservation.getTargetDate(), reservation.getReservationSlot());
                if(ctemp!=null) {
                    ctemp.addReservation(reservation.getTargetDate(), reservation.getReservationSlot(), reservation);
                }
            }
            ArrayList<LocalDate> target_dateAList = new ArrayList<>();
            target_dateAList.add(acceptList.get(0).getTargetDate());
            Notification n = new Notification("Room Reservation Request", "Accepted", acceptList.get(0).getMessage(), acceptList.get(0).getCourseName(), target_dateAList, acceptList.get(0).getRoomName(), acceptList.get(0).getReserverEmail(), x, acceptList.get(0).getCreationDate());
            Student Stud = (Student)getUser(recipient);
            Admin a = (Admin) getUser(Mail.from);
            Stud.addNotification(n);
            a.addNotification(n);
            serializeUser(Stud);
            serializeUser(a);
            server.mailpool.execute(new Mail(recipient,"BooKIT - Room reservation request accepted", GreetText+","+"\n\nThe following request of yours have been accepted by the admin:\n\n"+"Room: "+acceptList.get(0).getVenueName()+"\nDate: "+acceptList.get(0).getTargetDate().getDayOfMonth()+"/"+acceptList.get(0).getTargetDate().getMonthValue()+"/"+acceptList.get(0).getTargetDate().getYear()+"\nTime:\n"+TimeSlots+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
            if(ctemp != null) {
                if(ctemp.getInstructorEmail() != null && (!ctemp.getInstructorEmail().equals(""))) {
                    Faculty ft = (Faculty)getUser(ctemp.getInstructorEmail());
                    if(ft!=null) {
                        ft.addNotification(n);
                        serializeUser(ft);
                        GreetText = "Hello" + ft.getName();
                        server.mailpool.execute(new Mail(ctemp.getInstructorEmail(), "BooKIT - Room reservation request accepted", GreetText + "," + "\n\nThe following request of yours have been accepted by the admin:\n\n" + "Room: " + acceptList.get(0).getVenueName() + "\nDate: " + acceptList.get(0).getTargetDate().getDayOfMonth() + "/" + acceptList.get(0).getTargetDate().getMonthValue() + "/" + acceptList.get(0).getTargetDate().getYear() + "\nTime:\n" + TimeSlots + "\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                    }
                }
            }
        }
        if(ctemp!=null) {
            ctemp.serialize();
        }
        temp.serialize();
        return true;
    }
    public boolean rejectRequest(ArrayList<Reservation> r){
        if(r==null || r.size()==0){
            return false;
        }
        Room temp = Room.deserializeRoom(r.get(0).getRoomName());
        for(int i=0;i<r.size();i++){
            temp.deleteRequest(r.get(i).getReserverEmail(), r.get(i).getTargetDate(), r.get(i).getReservationSlot());
        }
        temp.serialize();
        String recipient = r.get(0).getReserverEmail();
        String GreetText = getUser(recipient).getName();
        String TimeSlots="";
        ArrayList<Integer> x=new ArrayList<Integer>();
        for (Reservation reservation : r) {
            x.add(reservation.getReservationSlot());
            TimeSlots+=Reservation.getSlotRange(reservation.getReservationSlot())+"\n";
        }
        ArrayList<LocalDate> t_date = new ArrayList<>();
        t_date.add(r.get(0).getTargetDate());
        Notification n = new Notification("Room Reservation Request", "Declined", r.get(0).getMessage(), r.get(0).getCourseName(), t_date, r.get(0).getRoomName(), r.get(0).getReserverEmail(), x);
        Student Stud = (Student)getUser(recipient);
        Stud.addNotification(n);
        Admin a = (Admin) getUser(Mail.from);
        a.addNotification(n);
        serializeUser(a);
        server.mailpool.execute(new Mail(recipient,"BooKIT - Room booking request rejected", GreetText+","+"\n\nThe following request of yours have been rejected by the admin:\n\n"+"Room: "+r.get(0).getVenueName()+"\nDate: "+r.get(0).getTargetDate().getDayOfMonth()+"/"+r.get(0).getTargetDate().getMonthValue()+"/"+r.get(0).getTargetDate().getYear()+"\nTime:\n"+TimeSlots+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
        serializeUser(Stud);
        return true;
    }
    public boolean adminandfaculty_bookRoom(ArrayList<LocalDate> date, ArrayList<Integer> time_slots, Reservation reservation, String admin_email) {
        HashMap<String, Integer> h = new HashMap<>();
        h.put(Mail.from, 1);
        Room room = Room.deserializeRoom(reservation.getRoomName());
        Boolean addToCourse = true;
        if(reservation.getCourseName().equals("")){
            addToCourse = false;
        }
        Course course;
        course = Course.deserializeCourse(reservation.getCourseName());
        for(LocalDate temp2 : date){
            for (int i = 0; i < time_slots.size(); i++) {
                if (addToCourse) {
                    if ((course.checkInternalCollision(reservation) == true && room.checkReservation(temp2, time_slots.get(i), reservation) == true)) {
                        return false;
                    }
                } else {
                    if (!(room.checkReservation(temp2, time_slots.get(i), reservation) == true)) {
                        return false;
                    }
                }
            }
        }
        h.put(reservation.getReserverEmail(), 1);
        course = Course.deserializeCourse(reservation.getCourseName());
        if(reservation.getCourseName()!= null && !reservation.getCourseName().equals("")) {

            h.put(course.getInstructorEmail(), 1);
        }
        String slots = "";
        for (Integer slot: time_slots) {
            slots += Reservation.getSlotRange(slot) + "\n";
        }
        LocalDateTime creat_time = LocalDateTime.now();
        reservation.setCreationDate(creat_time);
        for(LocalDate start : date) {
            Reservation r1 = reservation.clone();
            r1.setCreationDate(creat_time);
            r1.setTargetDate(start);
            for (int i = 0; i < time_slots.size(); i++) {
                Reservation r2 = r1.clone();
                r2.setSlotID(time_slots.get(i));
                r2.setCreationDate(creat_time);
                if (addToCourse) {
                    course.addReservation(start, time_slots.get(i), r2);
                }
                if(admin_email != null){
                room.addReservation(start, time_slots.get(i), r2, admin_email);}
                else{
                    room.addReservation(start, time_slots.get(i), r2, null);
                }
            }
        }
        Notification n = new Notification("Classroom Booking", "Done", reservation.getMessage(), reservation.getCourseName(), date, reservation.getRoomName(), reservation.getReserverEmail(),time_slots, reservation.getCreationDate());
        if(admin_email != null){
            n.setReserverEmail(admin_email);
        }
        String target_date = "";
        for (LocalDate d : date) {
            target_date = target_date + d.getDayOfMonth()+"/"+d.getMonthValue()+ "/"+ d.getYear()+"\n";
        }
        for(String email : h.keySet()) {
            String GreetText="Hello User";
            User xy=getUser(email);
            if(xy!=null) {
                xy.addNotification(n);
                GreetText = "Hello "+xy.getName();
                server.mailpool.execute(new Mail(email,"BooKIT - Room booking completed", GreetText+","+"\n\nThe following booking of yours have been confirmed:\n\n" + "Booked By: " + n.getReserverEmail() + "\n" +reservation.getMessageWOpurpose()+"\nCourse: "+reservation.getCourseName() +"\nDate: "+ target_date +"\nTime: "+ slots +" \nReason: "+reservation.getMessageWithoutVenue()+ "  \n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                serializeUser(xy);

            }
        }
        if(admin_email != null && !h.containsKey(admin_email)) {
            server.mailpool.execute(new Mail(admin_email,"BooKIT - Room booking completed", "Hello User"+","+"\n\nThe following booking of yours have been confirmed:\n\n"+ "Booked By: " + n.getReserverEmail() + "\n"  + reservation.getMessageWOpurpose()+"\nCourse: "+reservation.getCourseName() +"\nDate: "+ target_date +"\nTime: "+ slots+" \nReason: "+reservation.getMessageWithoutVenue()+"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
        }
        room.serialize();
        if(addToCourse) {
            course.serialize();
        }
        return true;
    }
    public void faculty_addCourse(String email, String course){
        Faculty f = (Faculty) getUser(email);
        f.getCourses().add(course);
        serializeUser(f);
    }
    public boolean sendReservationRequest(ArrayList<Reservation> r) {
        LinkedList<ArrayList<Reservation>> p = null;
        Course c = Course.deserializeCourse(r.get(0).getCourseName());
        Room room = Room.deserializeRoom(r.get(0).getRoomName());
        if(c!=null){
            for(int i=0;i<r.size();i++){
                if(c.checkInternalCollision(r.get(i))){
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
        p.addLast(r);
        serializeRequests(p);
        for(int i=0;i<r.size();i++){
            Reservation t=r.get(i);
            room.addRequest(t);
            x.add(t.getReservationSlot());
        }
        ArrayList<LocalDate> date = new ArrayList<>();
        date.add(r.get(0).getTargetDate());
        Notification n = new Notification("Room Reservation Request", "Pending", r.get(0).getMessage(), r.get(0).getCourseName(), date, r.get(0).getRoomName(), r.get(0).getReserverEmail(), x);
        tempAd.addNotification(n);
        tempStudent.addNotification(n);
        serializeUser(tempStudent);
        serializeUser(tempAd);
        room.serialize();
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
    public boolean studentAndFaculty_cancelBooking(LocalDate queryDate, int slotID, String RoomID, String cancelledBy) {
        HashMap<String, Integer> h = new HashMap<>();
        h.put(Mail.from, 1);

        Room temp=Room.deserializeRoom(RoomID);
        Reservation r=temp.getSchedule(queryDate)[slotID];
        if(r.getReserverEmail() != null) {
            h.put(r.getReserverEmail(), 1);
        }
        temp.deleteReservation(queryDate, slotID, cancelledBy);
        temp.serialize();
        Course c=Course.deserializeCourse(r.getCourseName());
        if(c!=null) {
            if(c.getInstructorEmail() != null) {
                h.put(c.getInstructorEmail(), 1);}
            c.deleteReservation(queryDate, slotID,r.getTopGroup(), r.getCreationDate());
            c.serialize();
        }
        ArrayList<Integer> slot= new ArrayList<Integer>();
        slot.add(r.getReservationSlot());
        ArrayList<LocalDate> date= new ArrayList<LocalDate>();
        date.add(r.getTargetDate());
        for(String email : h.keySet()) {
            if(!email.equals("")) {
                String GreetText = "Hello User";
                User x = getUser(email);
                Notification n = new Notification("Classroom Booking", "Cancelled", r.getMessage(), r.getCourseName(),date, r.getRoomName(), r.getReserverEmail(), slot);
                n.setCancelledBy(cancelledBy);
                if(x != null) {
                    GreetText = "Hello " + x.getName();
                    x.addNotification(n);
                }
                server.mailpool.execute(new Mail(email,"BooKIT - Room booking cancelled", GreetText+","+"\n\nThe following booking(s) have been cancelled:\n\n"+ "Cancelled By: " + cancelledBy + "\n" + r.getMessage()+  "\nBooked By: " + r.getReserverEmail() + "\n"  +"\nDate: "+queryDate.getDayOfMonth()+"/"+queryDate.getMonthValue()+"/"+queryDate.getYear()+"\nTime: "+ Reservation.getSlotRange(slotID) +"\n\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
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
        int index = 0;
        ArrayList<Notification> temp2 = new ArrayList<>();
        while(index < temp.size()){
            if(temp.get(index).getMax_targetdate().isAfter(LocalDate.now().minusDays(1))){
                temp2.add(temp.get(index));
            }
            index++;
        }
        x.setNotification(temp2);
        serializeUser(x);
        return temp2;
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
    public Boolean faculty_leaveCourse(ArrayList<String> courses, String email){
        if(courses == null || courses.size() == 0){
            return false;
        }
        Faculty f = (Faculty) getUser(email);
        for(int i=0;i<courses.size();i++){
            Course c = Course.deserializeCourse(courses.get(i));
            if(c.getInstructorEmail().equals(email)){
                f.getCourses().remove(courses.get(i));
                setInstructor("", courses.get(i));
            }
        }
        serializeUser(f);
        return true;
    }
    public Boolean BulkDeleteUserNotification(Notification notification,String reason_delete ,String cancelledBy){
        HashMap<String, Integer> mailList = new HashMap<>();
        mailList.put(cancelledBy, 1);
        mailList.put(notification.getReserverEmail(), 1);
        mailList.put(Mail.from, 1);
        ArrayList<LocalDate> targetDates = notification.getTargetDate();
        ArrayList<Integer> time_slots = notification.getSlotIDs();
        String room_id = notification.getRoom();
        Room room = Room.deserializeRoom(room_id);
        String course_id  = notification.getCourse();
        Course course = Course.deserializeCourse(course_id);
        for (LocalDate date: targetDates) {
            for (Integer slot: time_slots) {
              if(room.checkReservation(date, slot, null)){
                 return false;
              }
              if(course != null){
                  mailList.put(course.getInstructorEmail(), 1);
                  if(course.getSchedule(date)[slot] == null){
                      return false;
                  }
              }
            }
        }
        for (LocalDate date: targetDates) {
            for (Integer slot: time_slots) {
                    Reservation room_res = room.getSchedule(date)[slot];
                    if(!(notification.getReservationStamp().equals(room_res.getCreationDate()))){
                        return false;
                    }
            }
        }
        String group = null;
        LocalDateTime creationDate = null;
        for (LocalDate date: targetDates) {
            for (Integer slot: time_slots) {
                Reservation room_res = room.getSchedule(date)[slot];
                if(room_res!=null){
                    group = room_res.getTopGroup();
                    creationDate = room_res.getCreationDate();
                }
                room.deleteReservation(date, slot, cancelledBy);
                if(course != null && room_res!=null){
                    course.deleteReservation(date, slot, group, creationDate);
                }
            }
        }
        room.serialize();
        if(course!=null) {
            course.serialize();
        }
        String dates = "";
        String slots = "";
        for (LocalDate dt: targetDates) {
            dates += dt.getDayOfMonth() + "/" + dt.getMonthValue() + "/" + dt.getYear() + ",\n";
        }
        for (Integer sl: time_slots) {
            slots += Reservation.getSlotRange(sl) + ",\n";
        }
        Notification newNotification = new Notification("Classroom Booking", "Cancelled", notification.getMessage(), notification.getCourse(), targetDates, notification.getRoom(), notification.getReserverEmail(), notification.getSlotIDs());
        newNotification.setCancelledBy(cancelledBy);
        newNotification.setReason_cancel(reason_delete);
        for (String email: mailList.keySet()) {
            if(!email.equals("")) {
                User user = getUser(email);
                if (user != null) {
                    user.addNotification(newNotification);
                    serializeUser(user);
                }
                if (reason_delete != null) {
                    server.mailpool.execute(new Mail(email, "BooKIT - Room booking cancelled", "Hello User" + "," + "\n\nThe following booking(s) have been cancelled\n\n" + notification.getMessage() + "\nBooked By: " + notification.getReserverEmail() + "\n" + "\nCourse: " + notification.getCourse() + "\nCancelled By: " + cancelledBy + "\nDate: " + dates + "\nTime: " + slots + "Reason for cancellation: " + reason_delete + "\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                }
                else{
                    server.mailpool.execute(new Mail(email, "BooKIT - Room booking cancelled", "Hello User" + "," + "\n\nThe following booking(s) have been cancelled\n\n" + "Cancelled By: " + cancelledBy + "\n" + notification.getMessage() + "\nBooked By: " + notification.getReserverEmail() + "\n"  + "\nCourse: " + notification.getCourse() + "\nDate: " + dates + "\nTime: " + slots + "\nIf you think this is a mistake, please contact admin.\n\nRegards,\nBookIT Team"));
                }
            }
        }
        return true;
    }
    public ArrayList<String> getBookingReport(){
        ArrayList<String> data = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/Server/transactions.txt")));
            while(sc.hasNextLine()){
                data.add(sc.nextLine());
            }
        }
        catch(Exception e){
            BookITconstants.writeLog("Exception occurred while getting booking report");
            BookITconstants.writeLog(e.getMessage());
        }
        return data;
    }
    public boolean sendFeedback(Double rating, String message, String email){
        server.mailpool.execute(new Mail("harsh16041@iiitd.ac.in", "Feedback BookIT", "Hey Harsh,\n" + email + " sent a rating." + "\nRating is " + rating + " out of 10\n" + "Comments :\n" + message + "\n"));
        server.mailpool.execute(new Mail("nihesh16059@iiitd.ac.in", "Feedback BookIT", "Hey Nihesh,\n" + email + " sent a rating." + "\nRating is " + rating + " out of 10\n" + "Comments :\n" + message + "\n"));
        return true;
    }
    public void softResetServer(){
        server.loadUserHashMaps();
        server.loadFreeCourses();
        server.loadHolidaysList();
        server.loadBlockedDaysList();
    }
    ArrayList<String> sendRoomList(){
        File roomfile = new File("./src/AppData/StaticTimeTable/RoomData.csv");
        ArrayList<String> roomlist = new ArrayList<>();
        try {
            Scanner readroom = new Scanner(new BufferedReader(new FileReader(roomfile)));
            readroom.useDelimiter(",|\\n");
            readroom.nextLine();
            while(readroom.hasNext()){
                roomlist.add(readroom.next().trim());
                readroom.next();
            }
            return roomlist;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
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
                LinkedList<ArrayList<Reservation> > req;
                String cancelledBy;
                switch (request) {
                    case "getRoomList":
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ArrayList<String> roomlist = sendRoomList();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(roomlist);
                        out.flush();
                        break;

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
                    case "sendFeedback":
                        double rating = (Double)in.readObject();
                        String message_comment = (String)in.readObject();
                        String email_from = (String)in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        boolean answ = sendFeedback(rating, message_comment, email_from);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(answ);
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
                        cancelledBy = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        BookingCancellationNotifier(queryDate, slotID, RoomID, cancellationMessage, cancelledBy);
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
                        req = null;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        req = getRequest();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(req);
                        out.flush();
                        break;
                    case "acceptRequest":
                        ArrayList<Reservation> acceptList = null;
                        ArrayList<Reservation> rejectList = null;
                        acceptList = (ArrayList<Reservation>) in.readObject();
                        rejectList = (ArrayList<Reservation>) in.readObject();
                        ans = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ans = acceptRequest(acceptList, rejectList);
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
                        ArrayList<Reservation> rejList = null;
                        rejList = (ArrayList<Reservation>) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ans=rejectRequest(rejList);
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
                        ArrayList<Integer> slotIDs = (ArrayList<Integer>) in.readObject();
                        Reservation reservation = (Reservation) in.readObject();
                        String admin_email = (String) in.readObject();
                        ans = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ans = adminandfaculty_bookRoom(date, slotIDs, reservation, admin_email);
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
                    case "BulkDeleteUseNotification":
                        Notification notification = (Notification) in.readObject();
                        String reason_delete = (String) in.readObject();
                        String cancelled_by = (String) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = BulkDeleteUserNotification(notification,reason_delete,cancelled_by);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
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
                        cancelledBy = (String) in.readObject();
                        result = false;
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = studentAndFaculty_cancelBooking(queryDate, slotID, RoomID, cancelledBy);
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
                        LinkedList<ArrayList<Reservation>> pqReq = null;
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
                        softResetServer();
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
                        break;
                    case "admin_getBookingReport":
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        ArrayList<String> bookingReport = getBookingReport();
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(bookingReport);
                        out.flush();
                        break;
                    case "checkHoliday":
                        Boolean holiday;
                        queryDate = (LocalDate) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        if(server.HolidaysList.containsKey(queryDate)){
                            holiday = true;
                        }
                        else{
                            holiday = false;
                        }
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(holiday);
                        out.flush();
                        break;
                    case "checkBlockedDay":
                        Boolean blockedDay;
                        queryDate = (LocalDate) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        if(server.BlockedDaysList.containsKey(queryDate)){
                            blockedDay = true;
                        }
                        else{
                            blockedDay = false;
                        }
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(blockedDay);
                        out.flush();
                        break;
                    case "faculty_removeCourses":
                        ArrayList<String> coursesToBeRemoved = (ArrayList<String>) in.readObject();
                        email = (String) in.readObject();
                        if(!status.equals("Pass")) {
                            lock.lockInterruptibly();
                        }
                        result = faculty_leaveCourse(coursesToBeRemoved, email);
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
                            System.out.print("[ "+LocalDateTime.now()+" ] ");
                            System.out.println(connection.getInetAddress().toString() + " | ServerLock Released");
                            lock.unlock();
                        }
                        out.writeObject(result);
                        out.flush();
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
