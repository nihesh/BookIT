// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package StudentReservation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class StudentReservationGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("StudentReservation.fxml"));
        File file = new File("./src/BookIT_icon.jpg");
        primaryStage.getIcons().add(new Image(file.toURI().toString()));
        primaryStage.setTitle("BookIT");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1920, 1000));
        primaryStage.show();
    }

}
