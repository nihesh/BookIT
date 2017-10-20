// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package StudentReservation;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import static java.lang.Math.max;

public class StudentReservationGUIController implements Initializable{
    private int appearAfter_HoverPane = 200;
    @FXML
    private StackPane HoverPane, listCoursesPane;
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
    private StackPane roomGridPane;
    @FXML
    private StackPane classStatus, slotInfoPane, changePasswordPane;
    @FXML
    private ImageView classStatusBG, slotStatusBG, changePasswordBG, addCoursesBG,listCoursesBG;
    @FXML
    private Label statusRoomID, slotInfo;
    @FXML
    private StackPane topPane,leftPane,rightPane,mainPane, fetchCoursesPane, TimeTablePane, TTinfoPane;
    @FXML
    private AnchorPane selectedSlotsScrollPane, myCoursesScrollPane, shortlistedCourses;
    @FXML
    private Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn10,btn11,btn12,btn13,btn14,btn15,btn16,btn17,btn18,btn19,btn20,btn21,btn22,btn23,btn24,btn25,btn26,btn27,btn28;
    @FXML
    private Label error1, slotTTinfo;
    @FXML
    private ComboBox courseDropDown, facultyDropDown;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label curDate,curMon,curYear;

    private LocalDate activeDate;
    private Button[] slotButtons = {btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn10,btn11,btn12,btn13,btn14,btn15,btn16,btn17,btn18,btn19,btn20,btn21,btn22,btn23,btn24,btn25,btn26,btn27,btn28};
    private int pullDownPaneInitial = 650;
    private HashMap<Button,Integer> selection = new HashMap<Button,Integer>();
    private Boolean isActiveReservation, changepassProcessing, fetchCoursesProcessing, listCoursesProcessing,timetableprocessing;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        listCoursesProcessing = false;
        isActiveReservation = false;
        changepassProcessing = false;
        fetchCoursesProcessing = false;
        File file = new File("./src/BookIT_logo.jpg");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        file = new File("./src/AdminReservation/classStatusBG.jpg");
        image = new Image(file.toURI().toString());
        classStatusBG.setImage(image);
        slotStatusBG.setImage(image);
        addCoursesBG.setImage(image);
        changePasswordBG.setImage(image);
        listCoursesBG.setImage(image);
        pullDownPane.setTranslateY(pullDownPaneInitial);
        pullDownPane.setVisible(true);
        datePicker.setValue(LocalDate.now());
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell()
        {
            @Override
            public void updateItem(LocalDate item, boolean empty)
            {
                super.updateItem(item, empty);

                if(item.isBefore(LocalDate.of(2017,8,1)) || item.isAfter(LocalDate.of(2017,12,5)))
                {
                    setStyle("-fx-background-color: #ffc0cb;");
                    Platform.runLater(() -> setDisable(true));
                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
        datePicker.setValue(LocalDate.now());
        activeDate=LocalDate.now();
        setDate(activeDate);
        loadCourses();
    }
    public void openCoursesList(){
        fetchCoursesProcessing = false;
        listCoursesProcessing = true;
        fetchCoursesPane.setVisible(false);
        listCoursesPane.setVisible(true);
        leftPane.setDisable(true);
        rightPane.setDisable(true);
        mainPane.setDisable(true);
        FadeTransition appear = new FadeTransition(Duration.millis(1000), listCoursesPane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }
    public void closeCoursesList(){
        leftPane.setDisable(false);
        rightPane.setDisable(false);
        mainPane.setDisable(false);
        listCoursesProcessing = false;
        listCoursesPane.setVisible(false);
        showLogo();
    }
    public void OpenTimeTable(){
        timetableprocessing = true;
        TimeTablePane.setVisible(true);
        FadeTransition appear = new FadeTransition(Duration.millis(1000), TimeTablePane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
        rightPane.setDisable(true);
        leftPane.setDisable(true);
        roomGridPane.setDisable(true);
    }
    public void showTTslotinfo(Event action){
        hideLogo();
        TTinfoPane.setVisible(true);
        Button current = (Button) action.getSource();
        String id = current.getId();
        String slotDuration = getReserveButtonInfo(id.substring(0,id.length()-1));
        System.out.println(slotDuration);
        slotTTinfo.setText(slotDuration+" | Cxx");
    }
    public void CloseTimeTable(){
        timetableprocessing = false;
        showLogo();
        FadeTransition appear = new FadeTransition(Duration.millis(1000), TimeTablePane);
        appear.setFromValue(1);
        appear.setToValue(0);
        appear.play();
        TTinfoPane.setVisible(false);
        if(!logo.isVisible()){
            showLogo();
        }
        showLogo();
        appear.setOnFinished(e->{
            TimeTablePane.setVisible(false);
            rightPane.setDisable(false);
            leftPane.setDisable(false);
            roomGridPane.setDisable(false);
        });
    }
    public void openFetchCourses(){
        fetchCoursesProcessing = true;
        rightPane.setDisable(true);
        leftPane.setDisable(true);
        mainPane.setDisable(true);
        hideLogo();
        fetchCoursesPane.setVisible(true);
        ArrayList<String> items = new ArrayList<String>();
        items.add("Course 1");
        items.add("Course 2");
        CheckBox[] label = new CheckBox[100];
        int i=0;
        while(i<items.size()){
            label[i] = new CheckBox();
            label[i].setText(items.get(i));
            label[i].setPrefSize(578, 35);
            label[i].setAlignment(Pos.CENTER);
            label[i].setTranslateY(i*35);
            label[i].setStyle("-fx-background-color: #229954; -fx-border-color:  white; -fx-border-width:2");
            label[i].setFont(new Font(16));
            shortlistedCourses.getChildren().add(label[i]);
            i++;
        }
        shortlistedCourses.setPrefSize(578,max(235,34*i));
        FadeTransition appear = new FadeTransition(Duration.millis(1000), fetchCoursesPane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }
    public void closeFetchCourses(){
        fetchCoursesProcessing = false;
        rightPane.setDisable(false);
        leftPane.setDisable(false);
        mainPane.setDisable(false);
        fetchCoursesPane.setVisible(false);
        showLogo();
    }
    public void gobackFetchCourses(){
        fetchCoursesProcessing = false;
        listCoursesPane.setVisible(false);
        openFetchCourses();
    }
    public void openChangePassword(){
        changepassProcessing = true;
        hideLogo();
        leftPane.setDisable(true);
        rightPane.setDisable(true);
        mainPane.setDisable(true);
        FadeTransition appear = new FadeTransition(Duration.millis(1000), changePasswordPane);
        changePasswordPane.setVisible(true);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }
    public void cancelChangePassword(){
        changepassProcessing = false;
        changePasswordPane.setVisible(false);
        rightPane.setDisable(false);
        leftPane.setDisable(false);
        mainPane.setDisable(false);
        showLogo();
    }
    public void saveChangePassword(){
        changepassProcessing = false;
        leftPane.setDisable(false);
        changePasswordPane.setVisible(false);
        rightPane.setDisable(false);
        mainPane.setDisable(false);
        showLogo();
    }
    private void setDate(LocalDate d){
        String date = Integer.toString(d.getDayOfMonth());
        String month = Integer.toString(d.getMonthValue());
        String year = Integer.toString(d.getYear());
        if(date.length() == 1){
            date = "0"+date;
        }
        if(month.length() == 1){
            month = "0"+month;
        }
        curDate.setText(date);
        curMon.setText(month);
        curYear.setText(year);
    }
    public void loadDate(){
        LocalDate date = datePicker.getValue();
        if(date.isAfter(LocalDate.of(2017,8,1)) && date.isBefore(LocalDate.of(2017,12,5))){
            activeDate = date;
            datePicker.setValue(activeDate);
            setDate(activeDate);
        }
        else{
            datePicker.setValue(activeDate);
            setDate(activeDate);
        }
    }
    private String getReserveButtonInfo(String buttonID){
        switch(buttonID){
            case "btn1":
                return "0800AM - 0830AM";
            case "btn2":
                return "0830AM - 0900AM";
            case "btn3":
                return "0900AM - 0930AM";
            case "btn4":
                return "0930AM - 1000AM";
            case "btn5":
                return "1000AM - 1030AM";
            case "btn6":
                return "1030AM - 1100AM";
            case "btn7":
                return "1100AM - 1130AM";
            case "btn8":
                return "1130AM - 1200PM";
            case "btn9":
                return "1200PM - 1230PM";
            case "btn10":
                return "1230PM - 0100PM";
            case "btn11":
                return "0100PM - 0130PM";
            case "btn12":
                return "0130PM - 0200PM";
            case "btn13":
                return "0200PM - 0230PM";
            case "btn14":
                return "0230PM - 0300PM";
            case "btn15":
                return "0300PM - 0330PM";
            case "btn16":
                return "0330PM - 0400PM";
            case "btn17":
                return "0400PM - 0430PM";
            case "btn18":
                return "0430PM - 0500PM";
            case "btn19":
                return "0500PM - 0530PM";
            case "btn20":
                return "0530PM - 0600PM";
            case "btn21":
                return "0600PM - 0630PM";
            case "btn22":
                return "0630PM - 0700PM";
            case "btn23":
                return "0700PM - 0730PM";
            case "btn24":
                return "0730PM - 0800PM";
            case "btn25":
                return "0800PM - 0830PM";
            case "btn26":
                return "0830PM - 0900PM";
            case "btn27":
                return "0900PM - 0930PM";
            case "btn28":
                return "0930PM - 1000PM";
        }
        return "";
    }
    public void loadCourses(){
        myCoursesScrollPane.getChildren().clear();
        Label[] label = new Label[100];
        ArrayList<String> items = new ArrayList<String>();
        items.add("Course 1");
        items.add("Course 2");
        int i=0;
        while(i<items.size()){
            label[i] = new Label();
            label[i].setText(items.get(i));
            label[i].setPrefSize(543, 35);
            label[i].setAlignment(Pos.CENTER);
            label[i].setTranslateY(i*35);
            label[i].setStyle("-fx-background-color: #229954; -fx-border-color:  white; -fx-border-width:2");
            label[i].setFont(new Font(16));
            myCoursesScrollPane.getChildren().add(label[i]);
            i++;
        }
        myCoursesScrollPane.setPrefSize(543,max(170,34*i));
    }
    public void showSlotInfo(Event e){
        slotInfoPane.setVisible(true);
        Label curLabel = (Label) e.getSource();
        slotInfo.setText(curLabel.getText());
    }
    private void hideSlotPane(){
        slotInfoPane.setVisible(false);
    }
    public void updateClassStatus(Event e){
        hideLogo();
        hideSlotPane();
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
            hideSlotPane();
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
            System.out.println("Error in AdminReservationGUIController: InduceDelay");
        }
    }
    public void addSlotToBookQueue(Event e){
        hideSlotPane();
        Button currentBtn = (Button) e.getSource();
        if(currentBtn.getText().equals("")){
            currentBtn.setText("Free");
            currentBtn.setStyle("-fx-background-color:  #424949");
            selection.remove(currentBtn);
        }
        else{
            selection.put(currentBtn,1);
            currentBtn.setText("");
            currentBtn.setStyle("-fx-background-color:  linear-gradient(#229954,#27AE60,#229954)");
        }
        if(selection.size()==0){
            BookBtn.setDisable(true);
            BookBtn.setVisible(true);
            error1.setVisible(true);
        }
        else{
            BookBtn.setVisible(true);
            BookBtn.setDisable(false);
            error1.setVisible(false);
        }
    }
    public void closeReservationPane(){
        isActiveReservation = false;
        hideSlotPane();
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
            rightPane.setDisable(false);
            leftPane.setDisable(false);
            HoverPane.setDisable(false);
            roomGridPane.setDisable(false);
            roomGridPane.setVisible(true);
            pullDownPane.setVisible(false);
        });
    }
    public void pullDownReservationPane(){
        isActiveReservation = true;
        hideSlotPane();
        HoverPane.setDisable(true);
        rightPane.setDisable(true);
        leftPane.setDisable(true);
        roomGridPane.setDisable(true);
        roomGridPane.setVisible(false);
        pullDownPane.setVisible(true);
        Label[] label = new Label[30];
        ArrayList<Button> items = new ArrayList<Button>();
        selection.forEach((btn, num)->{
            items.add(btn);
        });
        selectedSlotsScrollPane.getChildren().clear();
        int i=0;
        while(i<items.size()){
            label[i] = new Label();
            label[i].setText(getReserveButtonInfo(items.get(i).getId()));
            label[i].setPrefSize(494, 50);
            label[i].setAlignment(Pos.CENTER);
            label[i].setTranslateY(i*50);
            label[i].setStyle("-fx-background-color: white; -fx-border-color:  #2a2a2a; -fx-border-width:3");
            label[i].setFont(new Font(22));
            selectedSlotsScrollPane.getChildren().add(label[i]);
            i++;
        }
        selectedSlotsScrollPane.setPrefSize(494,max(474,50*i));
        for(int j=0;j<50;j++) {
            courseDropDown.getItems().add("Choice " + Integer.toString(j));
        }
        for(int j=0;j<50;j++) {
            facultyDropDown.getItems().add("Choice " + Integer.toString(j));
        }
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
    public void bookingCompleted(){
        closeReservationPane();
        flyRight();
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
        rightPane.setDisable(false);
        sequence.setOnFinished(e->{
            exitReadOnlyBookings();
        });
    }
    public void openBooking(Event action){
        HoverPane.setTranslateX(0);
        error1.setVisible(true);
        BookBtn.setDisable(true);
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
        if(!isActiveReservation) {
            induceDelay(appearAfter_HoverPane);
            FadeTransition appear = new FadeTransition(Duration.millis(700), HoverPane);
            appear.setToValue(0);
            appear.play();
            closeClassStatus();
            selection.forEach((currentBtn, val) -> {
                currentBtn.setText("Free");
                currentBtn.setStyle("-fx-background-color:  #424949;");
            });
            selection = new HashMap<Button, Integer>();
            appear.setOnFinished(e -> {
                HoverPane.setVisible(false);
                HoverPane.setDisable(false);
                HoverPane.setOpacity(1);
                leftPane.setDisable(false);
                rightPane.setDisable(false);
                RoomNo.setText("Not Set");
                if(changepassProcessing){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                }
                if(fetchCoursesProcessing){
                    rightPane.setDisable(false);
                    leftPane.setDisable(false);
                    mainPane.setDisable(false);
                }
                if(listCoursesProcessing){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                    mainPane.setDisable(true);
                }
                if(timetableprocessing){
                    rightPane.setDisable(true);
                    leftPane.setDisable(true);
                    roomGridPane.setDisable(true);
                }
            });
        }
    }
}
