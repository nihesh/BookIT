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
import java.util.HashMap;
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
    @FXML
    private StackPane roomGridPane;
    @FXML
    private StackPane classStatus;
    @FXML
    private ImageView classStatusBG;
    @FXML
    private Label statusRoomID;
    @FXML
    private StackPane topPane;
    private int pullDownPaneInitial = 650;
    private HashMap<Button,Integer> selection = new HashMap<Button,Integer>();
    @Override
    public void initialize(URL location, ResourceBundle resources){
        File file = new File("./src/BookIT_logo.jpg");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        file = new File("./src/Reservation/classStatusBG.jpg");
        image = new Image(file.toURI().toString());
        classStatusBG.setImage(image);
        pullDownPane.setTranslateY(pullDownPaneInitial);
        pullDownPane.setVisible(true);
    }
    private void updateClassStatus(Event e){
        hideLogo();
        Button current = (Button) e.getSource();
        statusRoomID.setText(current.getText());
        FadeTransition appear = new FadeTransition(Duration.millis(1000), classStatus);
        classStatus.setOpacity(0);
        classStatus.setVisible(true);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }
    private void closeClassStatus(){
        if(classStatus.isVisible()) {
            classStatus.setVisible(false);
            showLogo();
        }
    }
    private void showLogo(){
        FadeTransition appear = new FadeTransition(Duration.millis(1000), logo);
        logo.setOpacity(0);
        logo.setVisible(true);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }
    private void hideLogo(){
        logo.setVisible(false);
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
            selection.put(currentBtn,1);
            currentBtn.setText("");
            currentBtn.setStyle("-fx-background-color:  linear-gradient(#229954,#27AE60,#229954)");
        }
    }
    public void closeReservationPane(){
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        double location=pullDownPane.getTranslateY();
        while(location<pullDownPaneInitial+20) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(pullDownPane);
            translate.setToY(location);
            translate.setDuration(Duration.millis(15));
            step++;
            location+=step;
            sequence.getChildren().add(translate);
        }
        sequence.play();
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(1000), BookBtn);
        appearBookBtn.setToValue(1);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(1000), BackBtn);
        appearBackBtn.setToValue(1);
        ParallelTransition inParallel = new ParallelTransition(appearBookBtn, appearBackBtn);
        inParallel.play();
        sequence.setOnFinished(e->{
            pullDownPane.setTranslateY(pullDownPaneInitial);
            topPane.setDisable(false);
            HoverPane.setDisable(false);
            roomGridPane.setDisable(false);
            roomGridPane.setVisible(true);
            pullDownPane.setVisible(false);
        });
    }
    public void pullDownReservationPane(){
        HoverPane.setDisable(true);
        topPane.setDisable(true);
        roomGridPane.setDisable(true);
        roomGridPane.setVisible(false);
        pullDownPane.setVisible(true);
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        int location=pullDownPaneInitial;
        while(location>40) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(pullDownPane);
            translate.setToY(location);
            translate.setDuration(Duration.millis(15));
            step++;
            location-=step;
            sequence.getChildren().add(translate);
        }
        sequence.play();
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(1000), BookBtn);
        appearBookBtn.setToValue(0);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(1000), BackBtn);
        appearBackBtn.setToValue(0);
        ParallelTransition inParallel = new ParallelTransition(appearBookBtn, appearBackBtn);
        inParallel.play();
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
        closeClassStatus();
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
        updateClassStatus(action);
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
        closeClassStatus();
        selection.forEach((currentBtn, val)->{
            currentBtn.setText("Free");
            currentBtn.setStyle("-fx-background-color:  #424949");
            selection = new HashMap<Button,Integer>();
        });
        appear.setOnFinished(e->{
            HoverPane.setVisible(false);
            HoverPane.setDisable(false);
            HoverPane.setOpacity(1);
            RoomNo.setText("Not Set");
        });
    }
}
