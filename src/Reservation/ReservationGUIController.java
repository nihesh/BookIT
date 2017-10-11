// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package Reservation;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;


public class ReservationGUIController {
    int appearAfter_HoverPane = 200;
    @FXML
    private StackPane HoverPane;
    @FXML
    private Label RoomNo;
    private void induceDelay(long time){
        try {
            Thread.sleep(time);
        }
        catch(Exception e){
            System.out.println("Error in ReservationGUIController: InduceDelay");
        }
    }
    public void flyRight(){
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        int location=1;
        while(location<2000) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(HoverPane);
            translate.setToX(location);
            translate.setDuration(Duration.millis(10));
            step++;
            location+=step;
            sequence.getChildren().add(translate);
        }
        sequence.play();
        sequence.setOnFinished(e->{
            HoverPane.setTranslateX(0);
            exitReadOnlyBookings();
        });
    }
    public void openBooking(Event action){
        Button current = (Button) action.getSource();
        RoomNo.setText(current.getText());
        induceDelay(appearAfter_HoverPane);
        HoverPane.setVisible(true);
        HoverPane.setDisable(false);
        FadeTransition appear = new FadeTransition(Duration.millis(1000), HoverPane);
        appear.setToValue(1);
        appear.play();
    }
    public void showReadOnlyBookings(Event action){
        double opacitySaturation = 0.92;
        Button current = (Button) action.getSource();
        RoomNo.setText(current.getText());
        induceDelay(appearAfter_HoverPane);
        HoverPane.setVisible(true);
        HoverPane.setDisable(true);
        FadeTransition appear = new FadeTransition(Duration.millis(700), HoverPane);
        if(HoverPane.getOpacity()==opacitySaturation){
            appear.setFromValue(0.6);
        }
        else {
            appear.setFromValue(0);
        }
        appear.setToValue(opacitySaturation);
        appear.play();
    }
    public void exitReadOnlyBookings(){
        induceDelay(appearAfter_HoverPane);
        HoverPane.setVisible(false);
        HoverPane.setDisable(false);
        HoverPane.setOpacity(1);
        RoomNo.setText("Not Set");
    }
}
