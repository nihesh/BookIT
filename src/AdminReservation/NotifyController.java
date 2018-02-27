package AdminReservation;

import HelperClasses.Admin;
import HelperClasses.BookITconstants;
import com.sun.jmx.remote.security.NotificationAccessController;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import HelperClasses.Notification;
import HelperClasses.User;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class NotifyController {
    @FXML
    private VBox notificationPane;
    @FXML
    private StackPane rootPane;
    @FXML
    public void initialize(){

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = visualBounds.getWidth();
        double height = visualBounds.getHeight();
        double scaleWidth = (width)/1920;
        double scaleHeight = (height)/1037;

        rootPane.setScaleX(scaleWidth);
        rootPane.setScaleY(scaleHeight);

        User x=User.getActiveUser();
        loadNotifications(x.getNotifications(false));

    }
    private void loadNotifications(ArrayList<Notification> myList){ // assert: data.size() == time.size()

    	ArrayList<String> data = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        for (int i=myList.size()-1;i>=0;i--) {
			Notification notifi = myList.get(i);
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
            Button but = new Button();
            but.setText("Delete");
            but.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    pressDelButton(event);
                }
            });
            notificationPane.getChildren().add(but);

        }
        for(int i=0;i<1;i++) {
            StackPane s = new StackPane();
            s.setPrefHeight(10);
            s.setStyle("-fx-background-color: #1B2631;");
            notificationPane.getChildren().add(s);
        }
    }
    @FXML
    private void pressDelButton(MouseEvent e){
        User u = User.getActiveUser();
        Button but  = (Button)e.getSource();
        VBox vbox = (VBox) but.getParent();
        ObservableList<Node> nodes = vbox.getChildren();
        Label label = null;
        for (int i = 0; i < nodes.size(); i++) {
            if(nodes.get(i) == but){
                label = (Label)nodes.get(i - 2);
            }
        }
        //System.out.println(label.getText());
        Notification del_notification = null;
        ArrayList<Notification> notifications = u.getNotifications(false);
        for (Notification nots: notifications) {
           // System.out.println(nots.toString());
            if(nots.toString().equals(label.getText())){
                del_notification = nots;
            }
        }
        System.out.println("hello world");
        if(del_notification != null){
            System.out.println(del_notification.toString());
            if(!deleteNotificationCallToServer(del_notification, false)){
                Notification.throwAlert("Error","Cannot delete the bookings as they have been modified");
            }
            else{
                Notification.throwAlert("Success","Deletion Successful");
                but.setDisable(true);
            }
        }


    }
    public boolean deleteNotificationCallToServer(Notification notification, boolean lock){
        try{
            User user = User.getActiveUser();
            Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
            ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            if(lock){
                out.writeObject("Hold");
            }
            else{
                out.writeObject("Pass");
            }
            out.flush();
            out.writeObject("BulkDeleteUseNotification");
            out.flush();
            out.writeObject(notification);
            out.flush();
            if(AdminReservationGUIController.admin_email_used == null) {
                out.writeObject(user.getEmail().getEmailID());
            }
            else{
                out.writeObject(AdminReservationGUIController.admin_email_used);
            }
            out.flush();
            Boolean c = (Boolean) in.readObject();
            out.close();
            in.close();
            server.close();
            return c;
        }
        catch(IOException e){
            System.out.println("IO Exception occurred while booking room");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class not found exception occurred while booking room");
        }
        return false;
    }

}
