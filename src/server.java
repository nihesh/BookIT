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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * the server class for back-end development
 */
public class server {
	/**
	 * spam filter object for detecting spam messages
	 */
    public static SpamFilter spm;
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
     * deserialise a course
     * @param name name of course
     * @return Course object see also {@link Course} 
     */
    public Course deserializeCourse(String name){
        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Course/"+name+".dat"));
            return (Course)in.readObject();
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
     * serialise a course object
     * @param c the course to be serialised
     */
    public void serializeCourse(Course c){
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Course/"+c.getName()+".dat", false));
                out.writeObject(c);
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
     * deserialise a room object given its name
     * @param name name of room
     * @return Room object
     */
    public Room deserializeRoom(String name){
        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream("./src/AppData/Room/"+name+".dat"));
            return (Room)in.readObject();
        }
        catch (Exception e){
            System.out.println("Exception occured while deserialising Room");
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
     * serialise a room object to local database
     * @param r the room object to be serialised
     */
    public void serializeRoom(Room r){
        try{
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Room/"+r.getRoomID()+".dat", false));
                out.writeObject(r);
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
        Room temp = deserializeRoom(r.get(0).getRoomName());
        Course ctemp = deserializeCourse(r.get(0).getCourseName());
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
                        HashMap<String, Integer> joinCode;
                        String code;
                        PriorityQueue<ArrayList<Reservation>> req;
                        switch (request) {
                            case "ReadCourse":
                                String courseName = (String) in.readObject();
                                c = deserializeCourse(courseName);
                                out.writeObject(c);
                                out.flush();
                                break;
                            case "AllCourses":
                                ArrayList<String> arr = getAllCourses();
                                out.writeObject(arr);
                                out.flush();
                                break;
                            case "WriteCourse":
                                c = (Course) in.readObject();
                                serializeCourse(c);
                                break;
                            case "GetUser":
                                String email = (String) in.readObject();
                                u = getUser(email);
                                out.writeObject(u);
                                out.flush();
                                break;
                            case "WriteUser":
                                u = (User) in.readObject();
                                serializeUser(u);
                                break;
                            case "ReadRoom":
                                String roomName = (String) in.readObject();
                                r = deserializeRoom(roomName);
                                out.writeObject(r);
                                out.flush();
                                break;
                            case "WriteRoom":
                                r = (Room) in.readObject();
                                serializeRoom(r);
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
                            case "BookingCancelNotification":
                                LocalDate queryDate = (LocalDate) in.readObject();
                                int slotID = (int) in.readObject();
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
                        }
                        if(lock.isLocked() && lock.isHeldByCurrentThread()){
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
