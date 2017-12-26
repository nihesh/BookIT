package AdminReservation;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import HelperClasses.Notification;
import HelperClasses.User;

public class NotifyController {
    @FXML
    private VBox notificationPane;
    @FXML
    public void initialize(){
        User x=User.getActiveUser();
        loadNotifications(x.getNotifications(false));
        
    }
    private void loadNotifications(ArrayList<Notification> myList){ // assert: data.size() == time.size()
    	if(myList.size()==0) {
    		Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("There are no new notifications");
			alert.showAndWait();
			
    	}
    	ArrayList<String> data = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        for (Notification notifi : myList) {
			if(notifi!=null) {
        	data.add(notifi.toString());
        	LocalDateTime t=notifi.getNotificationDateTime();
        	String hour="";
        	String minute="";
        	if(t.getHour()==0) {
        		hour+="00";
        	}
        	else {
        		hour+=t.getHour();
        	}
        	if(t.getMinute()<10) {
        		minute+="0"+t.getMinute();
        	}
        	else {
        		minute+=t.getMinute();
        	}
        	time.add(hour+":"+minute+", "+t.getDayOfMonth()+"/"+t.getMonthValue()+"/"+t.getYear());
			}
			else {
				break;
			}
			}
        
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
