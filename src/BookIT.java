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

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class BookIT {
    public static void launchLoginGUI(){
        javafx.application.Application.launch(LoginSignupGUI.class);
    }
    public static void reservationGUI(){
        User activeUser = User.getActiveUser();
        if(activeUser.getUsertype().equals("Student")){
            javafx.application.Application.launch(StudentReservationGUI.class);
        }
        else if(activeUser.getUsertype().equals("Faculty")){
            javafx.application.Application.launch(FacultyReservationGUI.class);
        }
        else{
            javafx.application.Application.launch(StudentReservationGUI.class);
        }
    }
    public static void main(String[] args){
        while(true) {
            File file = new File("./src/AppData/ActiveUser/ActiveUser.txt");
            if (!file.exists()) {
                launchLoginGUI();
            }
            file = new File("./src/AppData/ActiveUser/ActiveUser.txt");
            if(!file.exists()){
                break;
            }
            reservationGUI();
        }
    }
}
