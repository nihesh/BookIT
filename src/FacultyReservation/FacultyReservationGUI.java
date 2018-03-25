// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package FacultyReservation;

import HelperClasses.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;

public class FacultyReservationGUI extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyReservation.fxml"));
            Parent root = loader.load();
            File file = new File("./src/BookIT_icon.jpg");
            primaryStage.getIcons().add(new Image(file.toURI().toString()));
            User activeUser = (User) User.getActiveUser();
            primaryStage.setTitle("BookIT - "+activeUser.getEmail().getEmailID());
            FacultyReservationGUIController controller = loader.getController();
            controller.setHostservices(getHostServices());
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            double width = visualBounds.getWidth();
            double height = visualBounds.getHeight();
            height = height*(1000.0/1037);
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root, width, height, Color.BLACK));
            primaryStage.showAndWait();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
