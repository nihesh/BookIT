// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package Reservation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReservationGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Reservation.fxml"));
        primaryStage.setTitle("BookIT");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1920, 1000));
        primaryStage.show();
    }

}
