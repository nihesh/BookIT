// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package Reservation;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class ReservationGUIController implements Initializable{
    private int appearAfter_HoverPane = 200;
    @FXML
    private StackPane HoverPane;
    @FXML
    private Label RoomNo;
    @FXML
    private Button BackBtn;
    @FXML
    private Button BookBtn;
    @FXML
    private ImageView logo;
    @FXML
    private StackPane pullDownPane;
    @FXML
    private GridPane roomGrid;
    private int pullDownPaneInitial = -955;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        File file = new File("./src/BookIT_logo.jpg");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        pullDownPane.setTranslateY(pullDownPaneInitial);
    }
    private void induceDelay(long time){
        try {
            Thread.sleep(time);
        }
        catch(Exception e){
            System.out.println("Error in ReservationGUIController: InduceDelay");
        }
    }
    public void addSlotToBookQueue(Event e){
        Button currentBtn = (Button) e.getSource();
        if(currentBtn.getText().equals("")){
            currentBtn.setText("Free");
            currentBtn.setStyle("-fx-background-color:  #424949");
        }
        else{
            currentBtn.setText("");
            currentBtn.setStyle("-fx-background-color:  linear-gradient(#229954,#27AE60,#229954)");
        }
    }
    public void pullDownReservationPane(){
        pullDownPane.setVisible(true);
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        int location=pullDownPaneInitial;
        while(location<-350) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(pullDownPane);
            translate.setToY(location);
            translate.setDuration(Duration.millis(15));
            step++;
            location+=step;
            sequence.getChildren().add(translate);
        }
        sequence.play();
        HoverPane.setDisable(true);
        roomGrid.setVisible(false);
    }
    public void flyRight(){
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        int location=1;
        while(location<2000) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(HoverPane);
            translate.setToX(location);
            translate.setDuration(Duration.millis(15));
            step++;
            location+=step;
            sequence.getChildren().add(translate);
        }
        sequence.play();
        sequence.setOnFinished(e->{
            exitReadOnlyBookings();
        });
    }
    public void openBooking(Event action){
        HoverPane.setTranslateX(0);
        BackBtn.setVisible(true);
        BookBtn.setVisible(true);
        BookBtn.setOpacity(0);
        BackBtn.setOpacity(0);
        Button current = (Button) action.getSource();
        RoomNo.setText(current.getText());
        induceDelay(appearAfter_HoverPane);
        HoverPane.setVisible(true);
        HoverPane.setDisable(false);
        FadeTransition appear = new FadeTransition(Duration.millis(1000), HoverPane);
        appear.setToValue(1);
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(1000), BookBtn);
        appearBookBtn.setToValue(1);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(1000), BackBtn);
        appearBackBtn.setToValue(1);
        ParallelTransition inParallel = new ParallelTransition(appear, appearBookBtn, appearBackBtn);
        inParallel.play();
    }
    public void showReadOnlyBookings(Event action){
        HoverPane.setTranslateX(0);
        BackBtn.setVisible(false);
        BookBtn.setVisible(false);
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
        FadeTransition appear = new FadeTransition(Duration.millis(700), HoverPane);
        appear.setToValue(0);
        appear.play();
        appear.setOnFinished(e->{
            HoverPane.setVisible(false);
            HoverPane.setDisable(false);
            HoverPane.setOpacity(1);
            RoomNo.setText("Not Set");
        });
    }
}
