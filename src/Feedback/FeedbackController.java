package Feedback;

import AdminReservation.AdminReservationGUIController;
import HelperClasses.BookITconstants;
import HelperClasses.Notification;
import HelperClasses.User;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.util.Duration;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static java.lang.Math.min;

public class FeedbackController {
    @FXML
    private StackPane rootPane;
    @FXML
    private TextArea comments;
    @FXML
    private TextField rating;
    @FXML
    private Button submit_btn;
    @FXML
    private javafx.scene.image.ImageView rocket;
    @FXML
    private Label Success;

    @FXML
    public void initialize(){
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = visualBounds.getWidth();
        double height = visualBounds.getHeight();
        double scaleWidth = (width)/1366;
        double scaleHeight = (height)/768;
        rootPane.setScaleX(scaleWidth);
        rootPane.setScaleY(scaleHeight);
        User x=User.getActiveUser();
    }
    @FXML
    private void submit(){
        try{
            double rate = Double.parseDouble(rating.getText().substring(0,min(4,rating.getText().length())));
            if(rate < 0 || rate > 10){
                throw new Exception();
            }
            if(sendRating(rate, comments.getText().trim().substring(0,min(4000,comments.getText().trim().length())), false)){
                TranslateTransition rocket_up = new TranslateTransition(Duration.millis(1000), rocket);
                rocket_up.setByY(rootPane.getHeight());
                rocket_up.setCycleCount(2);
                rocket_up.play();
                submit_btn.setDisable(true);
                rating.setDisable(true);
                comments.setDisable(true);
                Success.setVisible(true);
            }
            else{
                Notification.throwAlert("Error", "Unable to contact Server");
            }
        }
        catch(Exception e){
            Notification.throwAlert("Error", "Rating should be an number between 0 to 10");
        }
        finally {
            rating.clear();
        }

    }
    private boolean sendRating(double rating, String message, boolean lock){
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
            out.writeObject("sendFeedback");
            out.flush();
            out.writeObject(rating);
            out.flush();
            out.writeObject(message);
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
            System.out.println("IO Exception occurred while sending feedback");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class not found exception occurred while sending feedback");
        }
        return false;
    }

}
