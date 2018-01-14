// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package AdminReservation;

import HelperClasses.BookITconstants;
import HelperClasses.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AdminReservationGUI extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminReservation.fxml"));
            File file = new File("./src/BookIT_icon.jpg");
            primaryStage.getIcons().add(new Image(file.toURI().toString()));
            User activeUser = (User) User.getActiveUser();
            primaryStage.setTitle("BookIT - "+activeUser.getEmail().getEmailID());
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
            primaryStage.setScene(new Scene(root));
            primaryStage.showAndWait();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
