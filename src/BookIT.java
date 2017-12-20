
import AdminReservation.AdminReservationGUI;
import FacultyReservation.FacultyReservationGUI;
import HelperClasses.BookITconstants;
import HelperClasses.User;
import LoginSignup.LoginSignupGUI;
import StudentReservation.StudentReservationGUI;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
/**
 * The BookIT class for launching the application
 * @author Nihesh
 *
 */
public class BookIT extends Application{
    public static final double BookITversion = 1.0;
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
    public Boolean CheckCompatibility(double version, Boolean lock){
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
            return c;
        }
        catch(IOException e){
            System.out.println("IO Exception occurred while checking compatibility");
        }
        catch (ClassNotFoundException c){
            System.out.println("ClassNotFound exception occurred while checking compatibility");
        }
        return false;
    }
    public void start(Stage primaryStage){
        if(!CheckCompatibility(BookITversion, false)){
            JOptionPane.showMessageDialog(null, "You are currently using BookIT v"+BookITversion+". Please download the latest version of BookIT to continue using the application.", "Launch Error", JOptionPane.ERROR_MESSAGE);
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
                User u = User.getActiveUser();
                User.getUser(u.getEmail().getEmailID(), false).setActiveUser();
            }
            reservationGUI(stage);
            file = new File("./src/AppData/ActiveUser/ActiveUser.txt");
            if(file.exists()){
                break;
            }
        }
    }
    public static void main(String[] args) throws IOException{
        BookITconstants b = new BookITconstants();
        launch(args);
    }
}
