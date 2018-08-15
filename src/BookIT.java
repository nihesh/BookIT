
import AdminReservation.AdminReservationGUI;
import FacultyReservation.FacultyReservationGUI;
import HelperClasses.BookITconstants;
import HelperClasses.Notification;
import HelperClasses.User;
import LoginSignup.LoginSignupGUI;
import StudentReservation.StudentReservationGUI;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * The BookIT class for launching the application
 * @author Nihesh
 *
 */
public class BookIT extends Application{
    public static final double BookITversion = 1.4;
	/**
	 * launches the login/signup gui
	 * @param primaryStage stage object
	 */
    public static void launchLoginGUI(Stage primaryStage){
        LoginSignupGUI l = new LoginSignupGUI();
        l.start(primaryStage);
    }
    /**
     * launches the user gui depending on the user type in Active User database
     * @param primaryStage stage object
     */
    public static void reservationGUI(Stage primaryStage){
        User activeUser = User.getActiveUser();
        if(activeUser.getUsertype().equals("Student")){
            StudentReservationGUI student = new StudentReservationGUI();
            student.start(primaryStage);
        }
        else if(activeUser.getUsertype().equals("Faculty")){
            FacultyReservationGUI faculty = new FacultyReservationGUI();
            faculty.start(primaryStage);
        }
        else{
            File file1=new File("./src/AppData/GeneratedJoinCode");
            if(!file1.exists()){
                file1.mkdir();
            }
            File file=new File("./src/AppData/GeneratedJoinCode/list.txt");
            try{
                file.createNewFile();
            }
            catch(Exception e){
                ;
            }
            AdminReservationGUI admin = new AdminReservationGUI();
            admin.start(primaryStage);
        }
    }
    public boolean TestInstance() {
    	Socket s = null;
    	try {
    		s = new Socket("localhost", 9999);
    		return false; //not available
    	}
    	catch(IOException e) {
    		return true;
    	}
    	finally {
    		if(s != null) {
    			try {
    				s.close();
    			}
    			catch(IOException e) {
    				System.out.println("close socket error");
    			}
    		}
    	}
    }
    public boolean TestClientPort() {
    	Socket s = null;
    	try {
    		s = new Socket("localhost", 9004);
    		return false; //not available
    	}
    	catch(IOException e) {
    		return true;
    	}
    	finally {
    		if(s != null) {
    			try {
    				s.close();
    			}
    			catch(IOException e) {
    				System.out.println("close socket error");
    			}
    		}
    	}
    }
    public int CheckCompatibility(double version, Boolean lock){
        try{
            Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
            ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            if(lock){
                out.writeObject("Hold");
            }
            else{
                out.writeObject("Pass");
            }
            out.flush();
            out.writeObject("CompatibilityCheck");
            out.flush();
            out.writeObject(version);
            out.flush();
            Boolean c = (Boolean) in.readObject();
            out.close();
            in.close();
            server.close();
            int result;
            if(c){
                result = 1;
            }
            else{
                result = 0;
            }
            return result;
        }
        catch(IOException e){
            ;
        }
        catch (ClassNotFoundException c){
            ;
        }
        return 2;
    }
    public void start(Stage primaryStage){
        File fileex = new File("./src/AppData/ActiveUser/ActiveUser.txt");
        if(fileex.exists()) {
        	fileex.delete();
        }
    	ServerSocket ss = null;
    	int compatibilityCheck = CheckCompatibility(BookITversion, false);
        if(compatibilityCheck == 0){
            Notification.throwAlert("Version Outdated Error", "You are currently using BookIT v"+BookITversion+". Kindly download the latest version of BookIT");
            return;
        }
        else if(compatibilityCheck == 2){
            Notification.throwAlert("Network Error", "Unable to reach BookIT server");
            return;
        }
        if(TestInstance() == false) {
        	Notification.throwAlert("Multiple Client Error", "One instance of the app is already running");
			return;
        
        }
        else {
        	try {
				ss = new ServerSocket(9999);
			} catch (IOException e) {
				Notification.throwAlert("Multiple Client Error", "One instance of the app is already running");
				// TODO Auto-generated catch block
			}
        }
        if(TestClientPort() == false) {
        	Notification.throwAlert("Port Error", "Are you running two instances of the app?");
        	return;
        }
        File file2 = new File("./src/AppData/ActiveUser");
        if(!file2.exists()){
            file2.mkdir();
        }
        while(true) {
            Stage stage = new Stage();
            File file = new File("./src/AppData/ActiveUser/ActiveUser.txt");
            if (!file.exists()) {
                launchLoginGUI(stage);
            }
            file = new File("./src/AppData/ActiveUser/ActiveUser.txt");
            if(!file.exists()){
                break;
            }
            else{
                try {
                    User u = User.getActiveUser();
                    User.getUser(u.getEmail().getEmailID(), false).setActiveUser();
                }
                catch (Exception e){
                    file.delete();
                    continue;
                }
            }
            reservationGUI(stage);
            file = new File("./src/AppData/ActiveUser/ActiveUser.txt");
            if(file.exists()){
                break;
            }
        }
        if(ss != null) {
        try {
			ss.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
    }
    public static void main(String[] args) throws IOException{
        BookITconstants b = new BookITconstants("Client");
        launch(args);
    }
}
