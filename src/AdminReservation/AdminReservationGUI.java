// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package AdminReservation;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import HelperClasses.BookITconstants;
import HelperClasses.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class AdminReservationGUI extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminReservation.fxml"));
            File file = new File("./src/BookIT_icon.jpg");
            primaryStage.getIcons().add(new Image(file.toURI().toString()));
            User activeUser = (User) User.getActiveUser();
            if(AdminReservationGUIController.admin_email_used != null){
                primaryStage.setTitle("BookIT - " + AdminReservationGUIController.admin_email_used);
            }
            else {
                primaryStage.setTitle("BookIT - " + activeUser.getEmail().getEmailID());
            }
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            double width = visualBounds.getWidth();
            double height = visualBounds.getHeight();
            height = height*(1000.0/1037);
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root, width, height, Color.BLACK));
            primaryStage.showAndWait();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
