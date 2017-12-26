package AdminReservation;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NotifyController {
    @FXML
    private VBox notificationPane;
    @FXML
    public void initialize(){
        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        for(int i=0;i<4;i++) {             // this is just a temp code to illustrate working of this controller
            data.add("nihesh16059@iiitd.ac.in has cancelled his booking");
            time.add("26-12-2017 | 16:24");
        }
        loadNotifications(data, time);
    }
    private void loadNotifications(ArrayList<String> data, ArrayList<String> time){ // assert: data.size() == time.size()
        int labelSize = 993;
        for(int i=0;i<data.size();i++){
            StackPane s = new StackPane();
            s.setPrefHeight(10);
            s.setStyle("-fx-background-color: #1B2631;");
            notificationPane.getChildren().add(s);
            Label l = new Label(data.get(i));
            l.setWrapText(true);
            l.setTextAlignment(TextAlignment.JUSTIFY);
            l.setFont(new Font(18));
            l.setStyle("-fx-background-color: lightgrey;");
            l.setPrefWidth(labelSize);
            l.setPadding(new Insets(4,5,4,5));
            notificationPane.getChildren().add(l);
            l = new Label(time.get(i));
            l.setWrapText(true);
            l.setTextAlignment(TextAlignment.LEFT);
            l.setPrefWidth(labelSize);
            l.setPadding(new Insets(3,5,3,5));
            l.setStyle("-fx-background-color: grey;");
            notificationPane.getChildren().add(l);
        }
        for(int i=0;i<1;i++) {
            StackPane s = new StackPane();
            s.setPrefHeight(10);
            s.setStyle("-fx-background-color: #1B2631;");
            notificationPane.getChildren().add(s);
        }
    }
}
