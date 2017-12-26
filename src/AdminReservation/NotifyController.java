package AdminReservation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class NotifyController {
    @FXML
    private StackPane notificationPane;
    public void initialize(URL location, ResourceBundle resources){
        System.out.println("hi");
        Label l = new Label();
        notificationPane.getChildren().add(l);
    }
}
