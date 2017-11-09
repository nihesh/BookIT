// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

// Packages

import AdminReservation.AdminReservationGUI;
import FacultyReservation.FacultyReservationGUI;
import HelperClasses.Admin;
import HelperClasses.Faculty;
import HelperClasses.Student;
import HelperClasses.User;
import LoginSignup.LoginSignupGUI;
import StudentReservation.StudentReservationGUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class BookIT extends Application{
    public static void launchLoginGUI(Stage primaryStage){
        LoginSignupGUI l = new LoginSignupGUI();
        l.start(primaryStage);
    }
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
            AdminReservationGUI admin = new AdminReservationGUI();
            admin.start(primaryStage);
        }
    }
    public void start(Stage primaryStage){
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
    public static void main(String[] args){
        launch(args);
    }
}
