// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package StudentReservation;

import HelperClasses.Student;
import HelperClasses.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

public class StudentReservationGUI extends Application {

    @Override
    public void start(Stage primaryStage){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("StudentReservation.fxml"));
            File file = new File("./src/BookIT_icon.jpg");
            primaryStage.getIcons().add(new Image(file.toURI().toString()));
            User activeUser = (User) User.getActiveUser();
            primaryStage.setTitle("BookIT - "+activeUser.getEmail().getEmailID());
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
            primaryStage.setScene(new Scene(root, Color.BLACK));
            primaryStage.showAndWait();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
