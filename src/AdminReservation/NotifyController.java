package AdminReservation;

import HelperClasses.Admin;
import HelperClasses.BookITconstants;
//import com.sun.jmx.remote.security.NotificationAccessController;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import HelperClasses.Notification;
import HelperClasses.User;
import javafx.stage.Screen;
import javafx.stage.Stage;

import static HelperClasses.Notification.throwConfirmation;

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

        notificationPane.getChildren().clear();
    	ArrayList<String> data = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        for (int i=myList.size()-1;i>=0;i--) {
			Notification notifi = myList.get(i);

        	if(notifi!=null) {
                    data.add(notifi.toString());
                    LocalDateTime t = notifi.getNotificationDateTime();
                    String hour = "";
                    String minute = "";
                    if (t.getHour() == 0) {
                        hour += "00";
                    } else {
                        hour += t.getHour();
                    }
                    if (t.getMinute() < 10) {
                        minute += "0" + t.getMinute();
                    } else {
                        minute += t.getMinute();
                    }
                    time.add(hour + ":" + minute + ", " + t.getDayOfMonth() + "/" + t.getMonthValue() + "/" + t.getYear());

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
            TextField reason_delete = new TextField();
            reason_delete.setPromptText("Reason for cancellation(Mandatory)");
            Button but = new Button();
            but.setText("Delete Booking");
            if(!((data.get(i).contains("Classroom Booking") && data.get(i).contains("Done")) || (data.get(i).contains("Room Reservation Request") && data.get(i).contains("Accepted")))){
                but.setDisable(true);
                reason_delete.setPromptText("");
                reason_delete.setDisable(true);

            }
            if(AdminReservationGUIController.admin_email_used != null) {
                notificationPane.getChildren().add(reason_delete);
            }
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
        TextField Reason = null;
        String str = null;
        if(AdminReservationGUIController.admin_email_used != null){
            for (int i = 0; i < nodes.size(); i++) {
                if(nodes.get(i) == but){
                    label = (Label)nodes.get(i - 3);
                    Reason = (TextField)nodes.get(i - 1);
                }
            }
            str = Reason.getText().trim();
            if(str.equals("")){
                Notification.throwAlert("Error", "Please specify a reason for deletion");
                return;
            }
        }
        else{
            for (int i = 0; i < nodes.size(); i++) {
                if(nodes.get(i) == but){
                    label = (Label)nodes.get(i - 2);
                }
            }
        }
        Boolean answer = Notification.throwConfirmation("Warning", "Are you sure you want to delete the booking?");
        if(answer == false){
            return;
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
        if(del_notification != null){

            if(!deleteNotificationCallToServer(del_notification, str,false)){
                Notification.throwAlert("Error","Cannot delete the bookings as they have been modified");
            }
            else{
                Notification.throwAlert("Success","Deletion Successful");
            }
        }
        User x=User.getActiveUser();
        loadNotifications(x.getNotifications(false));
    }
    public boolean deleteNotificationCallToServer(Notification notification,String reason_delete ,boolean lock){
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
            out.writeObject(reason_delete);
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
