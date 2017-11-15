
import AdminReservation.AdminReservationGUI;
import FacultyReservation.FacultyReservationGUI;
import HelperClasses.BookITconstants;
import HelperClasses.User;
import LoginSignup.LoginSignupGUI;
import StudentReservation.StudentReservationGUI;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
/**
 * The BookIT class for launching the application
 * @author Nihesh
 *
 */
public class BookIT extends Application{
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
    public void start(Stage primaryStage){
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
