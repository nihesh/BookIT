// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package AdminReservation;

import HelperClasses.*;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.Event;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

import static java.lang.Math.max;

public class AdminReservationGUIController implements Initializable{
    private int appearAfter_HoverPane = 200;
    @FXML
    private StackPane HoverPane;
    @FXML
    private Label RoomNo, joiningCodeMessage, AccRejCourseName, AccRejDate, AccRejVenue, AccRejMessage;
    @FXML
    private Button BackBtn, cancelSlotBooking;
    @FXML
    private Button BookBtn;
    @FXML
    private ImageView logo;
    @FXML
    private StackPane pullDownPane;
    @FXML
    private StackPane roomGridPane, topPane, cancelMessagePane;
    @FXML
    private StackPane classStatus, slotInfoPane, changePasswordPane, joiningCodePane;
    @FXML
    private ImageView classStatusBG, slotStatusBG, changePasswordBG, cancelSlotBookingImage, joiningCodeBG, cancelMessageBG;
    @FXML
    private Label statusRoomID, slotInfo,statusClassSize, statusFreeSlots;
    @FXML
    private StackPane leftPane,rightPane,mainPane, pullDownPane2;
    @FXML
    private AnchorPane selectedSlotsScrollPane, requestedSlotsScrollPane;
    @FXML
    private Label error1, AccRejSource;
    @FXML
    private ComboBox courseDropDown;
    @FXML
    private DatePicker datePicker, startDate, endDate;
    @FXML
    private Label curDate,curMon,curYear;
    @FXML
    private ChoiceBox joinCodeDropDown;
    @FXML
    private ArrayList<Button> slotButtons;
    @FXML
    private Label slotInfoFaculty, slotInfoCourse, slotInfoMessage;
    @FXML
    private PasswordField oldPass, newPass, renewPass;
    @FXML
    private ComboBox groupDropDown, optionDropDown;
    @FXML
    private TextArea requestMessage, requestMessage2, cancelMessageText;

    @FXML
    private VBox rootPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private SplitPane sp3;
    @FXML
    private ComboBox purposeDropDown;
    @FXML
    private StackPane preBooking, courseBooking, otherBooking;
    @FXML
    private TextField purposeBox;

    private String currentPurpose;
    private LocalDate activeDate;
    private String activeRoom;
    private String currentlyShowingSlot;
    private ArrayList<Integer> chosenSlots;
    private Admin activeUser;
    private int pullDownPaneInitial = 650;
    private HashMap<Button,Integer> selection = new HashMap<Button,Integer>();
    private Boolean isActiveReservation,requestProcessing,changepassProcessing, joinCodeProcessing, cancelBookingProcessing;
    private Event classEvent;
    private LocalDate StartDate;
    private LocalDate EndDate;
    private Button[] b1;
    private static int animation = 200;

    /**
     * Constructor for setting up Faculty Reservation GUI. It includes the adaptor code to suit any dimensional screen
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){

        // Scaling elements
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = visualBounds.getWidth();
        double height = visualBounds.getHeight();
        double scaleWidth = (width)/1920;
        double scaleHeight = (height)/1037;

        rootPane.setScaleX(scaleWidth);
        rootPane.setScaleY(scaleHeight);

        try {
            Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
            ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            out.writeObject("Pass");        // Takes lock
            out.flush();
            out.writeObject("GetStartEndDate");
            out.flush();
            ArrayList<LocalDate> temp = (ArrayList<LocalDate>) in.readObject();
            StartDate = temp.get(0);
            EndDate = temp.get(1);
            out.close();
            in.close();
            server.close();
        }
        catch (IOException e){
            System.out.println("IO exception occurred while writing to server");
        }
        catch (ClassNotFoundException c){
            System.out.println("Class Not found exception occurred while getting Start and End date of semester");
        }

        activeUser = (Admin) User.getActiveUser();
        joinCodeProcessing = false;
        isActiveReservation = false;
        requestProcessing = false;
        changepassProcessing = false;
        cancelBookingProcessing = false;
        File file = new File("./src/BookIT_logo.jpg");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        file = new File("./src/AdminReservation/classStatusBG.jpg");
        image = new Image(file.toURI().toString());
        classStatusBG.setImage(image);
        slotStatusBG.setImage(image);
        changePasswordBG.setImage(image);
        joiningCodeBG.setImage(image);
        cancelMessageBG.setImage(image);
        pullDownPane.setTranslateY(pullDownPaneInitial);
        pullDownPane.setVisible(true);

        optionDropDown.getItems().clear();
        optionDropDown.getItems().add("Course");
        optionDropDown.getItems().add("Other");
        optionDropDown.setValue("Course");

        datePicker.setEditable(false);
        startDate.setEditable(false);
        endDate.setEditable(false);
        datePicker.setValue(LocalDate.now());
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell()
        {
            @Override
            public void updateItem(LocalDate item, boolean empty)
            {
                super.updateItem(item, empty);

                if(item.isBefore(LocalDate.now()) || item.isBefore(StartDate) || item.isAfter(EndDate))
                {
                    setStyle("-fx-background-color: #ffc0cb;");
                    Platform.runLater(() -> setDisable(true));
                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
        datePicker.setValue(LocalDate.now());
        startDate.setDayCellFactory(dayCellFactory);
        endDate.setDayCellFactory(dayCellFactory);
        activeDate=LocalDate.now();
        setDate(activeDate);
        file = new File("./src/AdminReservation/cancel.png");
        image = new Image(file.toURI().toString());
        cancelSlotBookingImage.setImage(image);
        joinCodeDropDown.getItems().add("Student");
        joinCodeDropDown.getItems().add("Faculty");
        // joinCodeDropDown.getItems().add("Admin"); // Only one admin user
        joinCodeDropDown.getSelectionModel().selectFirst();
        joinCodeDropDown.setStyle("-fx-font-size : 13pt;-fx-background-color: #922B21;");
        loadDate();
    }
    public void downloadRequests(){
        try{
            Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
            ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            if(true){
                out.writeObject("Hold");
            }
            else{
                out.writeObject("Pass");
            }
            out.flush();
            out.writeObject("admin_getRequestsQueue");
            out.flush();
            PriorityQueue<ArrayList<Reservation>> c = (PriorityQueue<ArrayList<Reservation>>) in.readObject();
            FileWriter file = new FileWriter(new File("./src/AppData/Downloads/Requests.txt"), false);
            int i=1;
            while(!c.isEmpty()){
                ArrayList<Reservation> r = c.peek();
                c.poll();
                file.write("Reservation "+i+" - "+r.get(0).getCreationDate().toString()+"\n");
                file.flush();
                file.write("Sender: "+r.get(0).getReserverEmail()+"\n");
                file.flush();
                file.write("Target Date: "+r.get(0).getTargetDate()+"\n");
                file.flush();
                file.write("Room: "+r.get(0).getRoomName()+"\n");
                file.flush();
                file.write("Course: "+r.get(0).getCourseName()+"\n");
                file.flush();
                file.write("Message: "+r.get(0).getMessageWithoutVenue()+"\n");
                file.flush();
                file.write("Purpose: "+r.get(0).getType()+"\n");
                file.flush();
                file.write("Requested Slots: ");
                file.flush();
                r.forEach(items->{
                    try {
                        file.write(Reservation.getSlotRange(items.getReservationSlot()) + ", ");
                        file.flush();
                    }
                    catch (Exception e){
                        ;
                    }
                });
                file.write("\n\n");
                file.flush();
                i++;
            }
            file.close();
            out.close();
            in.close();
            server.close();
            Notification.throwAlert("Notification","Download Complete! The downloaded file is located in ./src/AppData/Downloads");
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("IO Exception occurred while downloading requests");
        }
        catch (ClassNotFoundException c){
            System.out.println("ClassNotFound exception occurred while downloading requests");
        }
    }
    public void OpenNotifications() {
    	try {
    		User x=User.getActiveUser();
    		if(x.getNotifications(false).size()==0) {
    		    Notification.throwAlert("Information Dialog", "There are no new notifications");
			    return;
    	    }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Notify.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            File file = new File("./src/BookIT_icon.jpg");
            stage.getIcons().add(new Image(file.toURI().toString()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            //stage.initStyle(StageStyle.UNDECORATED);
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            double width = visualBounds.getWidth();
            double height = visualBounds.getHeight();
            double scaleWidth = (width)/1920;
            double scaleHeight = (height)/1037;
            stage.setTitle("Notification");
            stage.setWidth(1000*scaleWidth);
            stage.setHeight(666*scaleHeight);
            stage.setScene(new Scene(root1,1000*scaleWidth, 666*scaleHeight));
            stage.show();
    	}
    	catch(Exception e) {
            System.out.println("Error occurred while opening notifications");
        }
    }
    public void restartServer(){
        rootPane.setDisable(true);
        activeUser.softResetServer(false);
        Notification.throwAlert("Notification","Server has been successfully restarted");
        rootPane.setDisable(false);
    }
    public void exitCancelBooking(){
        cancelBookingProcessing = false;
        leftPane.setDisable(false);
        rightPane.setDisable(false);
        mainPane.setDisable(false);
        cancelMessagePane.setVisible(false);
    }
    /**
     * Event handler to cancel a booked slot
     */
    public void cancelSlotBooking(){
        if(cancelMessageText.getText().equals("")){
            return;
        }
        cancelBookingProcessing = false;
        leftPane.setDisable(false);
        rightPane.setDisable(false);
        mainPane.setDisable(false);
        activeUser.cancelBooking(activeDate,Reservation.getSlotID(currentlyShowingSlot),activeRoom, cancelMessageText.getText(), false);
        Button current = slotButtons.get(Reservation.getSlotID(currentlyShowingSlot));
        current.setDisable(false);
        current.setText("Free");
        updateClassStatus(classEvent);
        cancelMessagePane.setVisible(false);
    }

    /**
     * Event handler to generate a new joining code
     */
    public void generateCode(){
        try {
            String type = (String) joinCodeDropDown.getSelectionModel().getSelectedItem();
            String joiningCode = activeUser.generateJoincode(type, false);
            joiningCodeMessage.setText(joiningCode);
        }
        catch(Exception e){
            System.out.println("Exception occured in generateCode function");
        }
    }

    /**
     * Displays the pane where joining code can be generated
     */
    public void showJoiningCodePane(){
        joinCodeProcessing = true;
        joiningCodePane.setVisible(true);
        leftPane.setDisable(true);
        rightPane.setDisable(true);
        mainPane.setDisable(true);
        hideLogo();
        FadeTransition appear = new FadeTransition(Duration.millis(animation), joiningCodePane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }

    /**
     * Closes the pane where joining code is generated
     */
    public void hideJoiningCodePane(){
        joiningCodeMessage.setText("");
        joinCodeProcessing = false;
        joiningCodePane.setVisible(false);
        leftPane.setDisable(false);
        rightPane.setDisable(false);
        mainPane.setDisable(false);
        showLogo();
    }
    /**
     * Opening change password pane
     */
    public void openChangePassword(){
        changepassProcessing = true;
        hideLogo();
        leftPane.setDisable(true);
        rightPane.setDisable(true);
        mainPane.setDisable(true);
        FadeTransition appear = new FadeTransition(Duration.millis(animation), changePasswordPane);
        changePasswordPane.setVisible(true);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }
    /**
     * Closes change password pane
     */
    public void cancelChangePassword(){
        oldPass.clear();
        newPass.clear();
        renewPass.clear();
        changepassProcessing = false;
        changePasswordPane.setVisible(false);
        rightPane.setDisable(false);
        leftPane.setDisable(false);
        mainPane.setDisable(false);
        showLogo();
    }
    /**
     * Saves new password after validation and closes change password pane
     */
    public void saveChangePassword(){
        String oldPassString = oldPass.getText();
        String newPassString = newPass.getText();
        String renewPassString = renewPass.getText();
        if(newPassString.equals(renewPassString)) {
            Boolean status = activeUser.changePassword(oldPassString, newPassString, false);
            if(status) {
                changepassProcessing = false;
                leftPane.setDisable(false);
                changePasswordPane.setVisible(false);
                rightPane.setDisable(false);
                mainPane.setDisable(false);
                showLogo();
            }
            else{
                Notification.throwAlert("Error","Either the old password is wrong, or the new passwords don't match");
            }
        }
        oldPass.clear();
        newPass.clear();
        renewPass.clear();
    }
    /**
     * Sets the selected date on the date pane
     * @param d selected date
     */
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
    /**
     * Reads date from the input field and sets the date into the date pane
     */
    public void loadDate(){
        LocalDate date = datePicker.getValue();
        if(!date.isBefore(StartDate) && !date.isAfter(EndDate)){
            activeDate = date;
            datePicker.setValue(activeDate);
            setDate(activeDate);
        }
        else{
            datePicker.setValue(activeDate);
            setDate(activeDate);
            topPane.setDisable(true);
            mainPane.setDisable(true);
            Notification.throwAlert("Server Error","Sorry, BookIT server is down");
        }
    }
    /**
     * Returns the slot that a time range is mapped to
     * @param buttonID Slot id in the form of string
     * @return Slot index corresponding to the string
     */
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

    /**
     * Loads the most prioritized request onto the requests pane
     * @param requests
     */
    private void loadRequest(ArrayList<Reservation> requests){
        Reservation firstRequest = requests.get(0);
        AccRejCourseName.setText(firstRequest.getCourseName());
        String date = Integer.toString(firstRequest.getTargetDate().getDayOfMonth());
        if(date.length()==1){
            date = "0"+date;
        }
        AccRejSource.setText(firstRequest.getReserverEmail());
        AccRejDate.setText(date+"-"+firstRequest.getTargetDate().getMonthValue()+"-"+firstRequest.getTargetDate().getYear());
        AccRejVenue.setText(firstRequest.getVenueName());
        String group = firstRequest.getTopGroup();
        if(group.equals("")){
            group="N/A";
        }
        if(group.equals("0")){
            group.equals("All groups");
        }
        AccRejMessage.setText(firstRequest.getMessage());
        ArrayList<String> items = new ArrayList<String>();
        for(int j=0;j<requests.size();j++){
            items.add(Reservation.getSlotRange(requests.get(j).getReservationSlot()));
        }
        Label[] label = new Label[50];
        b1 = new Button[50];
        int i=0;
        while(i<items.size()){
            label[i] = new Label();
            b1[i] = new Button();
            Label curLabel = label[i];
            Button curButton = b1[i];
            b1[i].getStylesheets().add("./AdminReservation/buttonAccRej.css");
            b1[i].setPrefSize(80,50);
            b1[i].setTranslateY(i*49);
            b1[i].setText("Del");
            curButton.setOnMouseClicked(e->{
                if(curButton.getText().equals("Del")){
                    curButton.setText("Add");
                    curLabel.setStyle("-fx-background-color: red; -fx-border-color:  #2a2a2a; -fx-border-width:3");
                }
                else{
                    curButton.setText("Del");
                    curLabel.setStyle("-fx-background-color: green; -fx-border-color:  #2a2a2a; -fx-border-width:3");
                }
            });
            label[i].setText(items.get(i));
            label[i].setPrefSize(414, 50);
            label[i].setAlignment(Pos.CENTER);
            label[i].setTranslateX(80);
            label[i].setTranslateY(i*49);
            label[i].setStyle("-fx-background-color: green; -fx-border-color:  #2a2a2a; -fx-border-width:3");
            label[i].setFont(new Font(20));
            requestedSlotsScrollPane.getChildren().add(b1[i]);
            requestedSlotsScrollPane.getChildren().add(label[i]);
            i++;
        }
        b1[i] = new Button();
        b1[i].setText("");
        requestedSlotsScrollPane.setPrefSize(494,max(474,49*i));
    }

    /**
     * Accepts a booking requested by the student
     */
    public void acceptRequest(){
        int i=0;
        ArrayList<Integer> data = new ArrayList<Integer>();
        while(!b1[i].getText().equals("")){
            if(b1[i].getText().equals("Del")) {
                data.add(i);
            }
            i++;
        }
        activeUser.acceptRequest(data,false);                                             // Throw not accepted warning...
        ArrayList<Reservation> requests = activeUser.getRequest(false);
        if(requests == null){
            hideRequests();
            return;
        }
        loadRequest(requests);
    }

    /**
     * Rejects a request requested by the student
     */
    public void deleteRequest(){
        activeUser.rejectRequest(false);
        ArrayList<Reservation> requests = activeUser.getRequest(false);
        if(requests == null){
            hideRequests();
            return;
        }
        loadRequest(requests);
    }

    /**
     * Opens requests pane
     */
    public void showRequests(){
        ArrayList<Reservation> requests = activeUser.getRequest(false);              // GUI Integration begins
        if(requests == null){
            Notification.throwAlert("Notification","There are no more pending requests");
            return;
        }
        requestProcessing = true;
        leftPane.setDisable(true);
        rightPane.setDisable(true);
        roomGridPane.setDisable(true);
        requestedSlotsScrollPane.getChildren().clear();
        loadRequest(requests);                                                 // GUI Integration Ends
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        int location=pullDownPaneInitial;
        pullDownPane2.setTranslateY(location);
        pullDownPane2.setVisible(true);
        while(location>40) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(pullDownPane2);
            translate.setToY(location);
            translate.setDuration(Duration.millis(15));
            step++;
            location-=step;
            sequence.getChildren().add(translate);
        }
        sequence.play();
    }

    /**
     * Logs out the user
     */
    public void signout(){
        try {
            activeUser.logout();
        }
        catch (LoggedOutException l){
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Closes the requests pane
     */
    public void hideRequests(){
        requestProcessing = false;
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        double location=pullDownPane2.getTranslateY();
        while(location<pullDownPaneInitial+20) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(pullDownPane2);
            translate.setToY(location);
            translate.setDuration(Duration.millis(15));
            step++;
            location+=step;
            sequence.getChildren().add(translate);
        }
        sequence.play();
        sequence.setOnFinished(e->{
            pullDownPane2.setVisible(false);
            leftPane.setDisable(false);
            rightPane.setDisable(false);
            roomGridPane.setDisable(false);
        });
    }

    /**
     * Displays details attached to the slot on the top center pane
     * @param e Event object
     */
    public void showSlotInfo(Event e){
        slotInfoPane.setVisible(true);
        Label curLabel = (Label) e.getSource();
        slotInfo.setText(curLabel.getText());
        currentlyShowingSlot = curLabel.getText();         // GUI-Helper Integration starts
        Reservation[] bookings = Room.getDailySchedule(activeDate, statusRoomID.getText(), false);
        if(bookings[Reservation.getSlotID(curLabel.getText())]!=null) {
            cancelSlotBooking.setDisable(false);
            String facultyName="~~~~";
            if (!bookings[Reservation.getSlotID(curLabel.getText())].getFacultyEmail(false).equals("")){
                Faculty f = (Faculty)User.getUser(bookings[Reservation.getSlotID(curLabel.getText())].getFacultyEmail(false), false);
                facultyName = f.getName();
            }
            slotInfoFaculty.setText(facultyName);
            if(bookings[Reservation.getSlotID(curLabel.getText())].getCourseName().length()>30) {
                slotInfoCourse.setText(bookings[Reservation.getSlotID(curLabel.getText())].getCourseName().substring(0,15)+"..."+bookings[Reservation.getSlotID(curLabel.getText())].getCourseName().substring(bookings[Reservation.getSlotID(curLabel.getText())].getCourseName().length()-10,bookings[Reservation.getSlotID(curLabel.getText())].getCourseName().length()));
            }
            else{
                slotInfoCourse.setText(bookings[Reservation.getSlotID(curLabel.getText())].getCourseName());
            }
            slotInfoMessage.setText(bookings[Reservation.getSlotID(curLabel.getText())].getCourseName()+"\n"+bookings[Reservation.getSlotID(curLabel.getText())].getMessage());
        }
        else{
            cancelSlotBooking.setDisable(true);
            slotInfoFaculty.setText("N/A");
            slotInfoCourse.setText("N/A");
            slotInfoMessage.setText("N/A");
        }                                                               // GUI-Helper Integration ends
    }

    /**
     * Hides the pane that shows information regarding a slot
     */
    private void hideSlotPane(){
        slotInfoPane.setVisible(false);
    }

    /**
     * Displays the pane describing the class room
     * @param e Event object
     */
    public void updateClassStatus(Event e){
        hideLogo();
        hideSlotPane();
        Button current = (Button) e.getSource();
        statusRoomID.setText(current.getText());
        int capacity = Room.getCapacity(current.getText(),false);                                  // GUI-Helper integration begins here
        statusClassSize.setText("  "+Integer.toString(capacity));
        Reservation[] reservation = Room.getDailySchedule(activeDate,current.getText(), false);
        int freeSlots=0;
        for(int i=0;i<28;i++){
            if(reservation[i] == null){
                freeSlots++;
            }
        }
        statusFreeSlots.setText("  "+Integer.toString(freeSlots));                         // GUI-Helper integration ends here
        FadeTransition appear = new FadeTransition(Duration.millis(animation), classStatus);
        classStatus.setOpacity(0);
        classStatus.setVisible(true);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }

    /**
     * Hides the pane that desplays the description of the class room
     */
    private void closeClassStatus(){
        if(classStatus.isVisible()) {
            hideSlotPane();
            classStatus.setVisible(false);
            showLogo();
        }
    }

    /**
     * Shows BookIT logo
     */
    private void showLogo(){
        FadeTransition appear = new FadeTransition(Duration.millis(animation), logo);
        logo.setOpacity(0);
        logo.setVisible(true);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }

    /**
     * Hides BookIT logo
     */
    private void hideLogo(){
        logo.setVisible(false);
    }

    /**
     * Sleeps for some time in milli seconds
     * @param time
     */
    private void induceDelay(long time){
        try {
            Thread.sleep(time);
        }
        catch(Exception e){
            System.out.println("Error in AdminReservationGUIController: InduceDelay");
        }
    }

    /**
     * Adds the selected slot to selected list so that booking can be performed later
     * @param e
     */
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

    /**
     * Booking confirmation pane disappears
     */
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
            location+=max(20,step);
            sequence.getChildren().add(translate);
        }
        sequence.play();
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(animation), BookBtn);
        appearBookBtn.setToValue(1);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(animation), BackBtn);
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

    /**
     * Booking confirmation pane appears
     */
    public void pullDownReservationPane(){
        startDate.setValue(activeDate);
        endDate.setValue(activeDate);
        courseBooking.setVisible(false);
        otherBooking.setVisible(false);
        preBooking.setVisible(true);
        chosenSlots = new ArrayList<>();
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
            chosenSlots.add(Reservation.getSlotID(getReserveButtonInfo(items.get(i).getId())));
            label[i].setAlignment(Pos.CENTER);
            label[i].setTranslateY(i*50);
            label[i].setStyle("-fx-background-color: white; -fx-border-color:  #2a2a2a; -fx-border-width:3");
            label[i].setFont(new Font(22));
            selectedSlotsScrollPane.getChildren().add(label[i]);
            i++;
        }
        selectedSlotsScrollPane.setPrefSize(494,max(474,50*i));
        ArrayList<String> allCourses = Course.getAllCourses();                  // GUI Integration
        courseDropDown.getItems().clear();
        purposeDropDown.getItems().clear();
        groupDropDown.getItems().clear();
        allCourses.sort(String::compareToIgnoreCase);
        for(int j=0;j<allCourses.size();j++) {
            courseDropDown.getItems().add(allCourses.get(j));
        }
        purposeDropDown.getItems().add("Lecture");
        purposeDropDown.getItems().add("Lab");
        purposeDropDown.getItems().add("Tutorial");
        purposeDropDown.getItems().add("Quiz");
        for(int j=0;j<6;j++) {
            groupDropDown.getItems().add(Integer.toString(j+1));
        }                                                                       // GUI Integration Ends
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
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(animation), BookBtn);
        appearBookBtn.setToValue(0);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(animation), BackBtn);
        appearBackBtn.setToValue(0);
        ParallelTransition inParallel = new ParallelTransition(appearBookBtn, appearBackBtn);
        inParallel.play();
    }

    /**
     * Event handler for confirming booking of a room
     */
    public void bookingCompleted1(){
        String chosenCourse;
        Course courseObject = null;
        try {
            chosenCourse = courseDropDown.getSelectionModel().getSelectedItem().toString();
        }
        catch(NullPointerException e){
            Notification.throwAlert("Error","Course Field can't be empty");
            return;
        }
        ArrayList<String> chosenGroup = new ArrayList<>();
        try {
            chosenGroup.add(groupDropDown.getSelectionModel().getSelectedItem().toString());
        }
        catch(NullPointerException e){
            chosenGroup.add("0");
        }
        String chosenPurpose;
        try {
            chosenPurpose = purposeDropDown.getSelectionModel().getSelectedItem().toString();
        }
        catch(NullPointerException e){
            Notification.throwAlert("Error","Purpose Field can't be empty");
            return;
        }
        String chosenFaculty;
        if(chosenCourse == ""){
            chosenFaculty = "";
        }
        else{
            chosenFaculty = Course.getCourseFaculty(chosenCourse, false);
        }
        String chosenMessage;
        chosenMessage = requestMessage.getText();
        ArrayList<Reservation> listOfReservations = new ArrayList<>();
        for(int i=0;i<chosenSlots.size();i++){              // GUI Integration Begins
            Reservation r;
            r = new Reservation(chosenMessage, chosenGroup, chosenCourse, chosenFaculty, activeRoom, chosenPurpose, chosenSlots.get(i));
            r.setTargetDate(activeDate);
            r.setReserverEmail(activeUser.getEmail().getEmailID());
            listOfReservations.add(r);
        }                                                   // GUI Integration Ends
        if(!Admin.checkBulkBooking(activeRoom, chosenSlots, startDate.getValue(), endDate.getValue(), false)){
            Notification.throwAlert("Error","Cannot complete booking as there is some other confirmed booking in one of the slots that you are trying to book");
            return;
        }
        Boolean failure = false;
        for(int i=0;i<listOfReservations.size();i++){
            if(!activeUser.bookRoom(startDate.getValue(), endDate.getValue(), listOfReservations.get(i).getReservationSlot(), listOfReservations.get(i), false)){
                failure = true;
            }
        }
        if(!failure){
            Notification.throwAlert("Success", "Your booking has been completed. Kindly verify");
        }
        else{
            Notification.throwAlert("Booking Failed", "Some bookings couldn't be completed. Kindly check notifications for successful bookings");
        }
        closeReservationPane();
        flyRight();
        requestMessage.clear();
    }

    public void bookingCompleted2(){
        String chosenCourse="";
        ArrayList<String> chosenGroup = new ArrayList<>();
        chosenGroup.add("0");
        String chosenPurpose;
        chosenPurpose = purposeBox.getText();
        if(chosenPurpose.equals("")){
            Notification.throwAlert("Error","Purpose field can't be empty");
            return;
        }
        String chosenFaculty="";
        String chosenMessage;
        chosenMessage = requestMessage2.getText();
        ArrayList<Reservation> listOfReservations = new ArrayList<>();
        for(int i=0;i<chosenSlots.size();i++){              // GUI Integration Begins
            Reservation r;
            r = new Reservation(chosenMessage, chosenGroup, chosenCourse, chosenFaculty, activeRoom, chosenPurpose, chosenSlots.get(i));
            r.setTargetDate(activeDate);
            r.setReserverEmail(activeUser.getEmail().getEmailID());
            listOfReservations.add(r);
        }                                                   // GUI Integration Ends
        if(!Admin.checkBulkBooking(activeRoom, chosenSlots, startDate.getValue(), endDate.getValue(), false)){
            Notification.throwAlert("Cannot complete booking. Please close this session and try again", "Error");
            return;
        }
        for(int i=0;i<listOfReservations.size();i++){
            if(!activeUser.bookRoom(startDate.getValue(), endDate.getValue(), listOfReservations.get(i).getReservationSlot(), listOfReservations.get(i), false)){
                Notification.throwAlert("Booking Error","The booking couldn't be completed as one of the slots you've chosen has been booked. Please refresh the page and try a different slot");
            }
        }
        closeReservationPane();
        flyRight();
        purposeBox.clear();
        requestMessage2.clear();
    }
    public void cancelBookingMessage(){
        cancelBookingProcessing = true;
        leftPane.setDisable(true);
        rightPane.setDisable(true);
        mainPane.setDisable(true);
        slotInfoPane.setVisible(false);
        FadeTransition appear = new FadeTransition(Duration.millis(animation), cancelMessagePane);
        cancelMessagePane.setOpacity(0);
        cancelMessagePane.setVisible(true);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }
    /**
     * Reservation pane flys right
     */
    public void flyRight(){
        FadeTransition sequence = new FadeTransition(Duration.millis(animation), HoverPane);
        sequence.setToValue(0);
        sequence.play();
        closeClassStatus();
        rightPane.setDisable(false);
        leftPane.setDisable(false);
        sequence.setOnFinished(e->{
            exitReadOnlyBookings();
        });
    }

    /**
     * Resercation pane appears
     * @param action Event object
     */
    public void openBooking(Event action){
        Button current = (Button) action.getSource();
        Boolean check = Room.exists(current.getText(),false);                               // Loading buttons
        if(check==false){
            return;
        }
        classEvent = action;
        HoverPane.setTranslateX(0);
        error1.setVisible(true);
        BookBtn.setDisable(true);
        BackBtn.setVisible(true);
        BookBtn.setVisible(true);
        BookBtn.setOpacity(0);
        BackBtn.setOpacity(0);
        RoomNo.setText(current.getText());
        activeRoom = current.getText();
        Reservation[] reservation = Room.getDailySchedule(activeDate, current.getText(), false);
        for(int i=0;i<28;i++){
            if(reservation[i] != null){
                slotButtons.get(i).setText("Booked");
                slotButtons.get(i).setDisable(true);
            }
            else{
                slotButtons.get(i).setText("Free");
                slotButtons.get(i).setDisable(false);
            }
        }                                                                               // Loading ends
        induceDelay(appearAfter_HoverPane);
        HoverPane.setVisible(true);
        HoverPane.setDisable(false);
        FadeTransition appear = new FadeTransition(Duration.millis(animation), HoverPane);
        appear.setToValue(1);
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(animation), BookBtn);
        appearBookBtn.setToValue(1);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(animation), BackBtn);
        appearBackBtn.setToValue(1);
        ParallelTransition inParallel = new ParallelTransition(appear, appearBookBtn, appearBackBtn);
        inParallel.play();
    }
    public void preBookingProceed(){
        try {
            if(startDate.getValue().isAfter(endDate.getValue())){
                Notification.throwAlert("Error","Start Date is after End Date");
                return;
            }
            if (!Admin.checkBulkBooking(activeRoom,chosenSlots, startDate.getValue(), endDate.getValue(), false)) {
                Notification.throwAlert("Error","The requested slots on some of the requested days can't be completed as there is some other confirmed booking in this range");
                return;
            }
            currentPurpose = optionDropDown.getSelectionModel().getSelectedItem().toString();
            preBooking.setVisible(false);
            if(currentPurpose.equals("Course")){
                courseBooking.setVisible(true);
            }
            else{
                otherBooking.setVisible(true);
            }
        }
        catch (Exception e){
            System.out.println("No option has been selected case in preBookingProceed function");
            return;
        }
    }
    /**
     * Reservation pane appears, but it remains disabled
     * @param action Event object
     */
    public void showReadOnlyBookings(Event action){
        Button current = (Button) action.getSource();
        Boolean check = Room.exists(current.getText(),false);                               // Loading buttons
        if(check==false){
            return;
        }
        updateClassStatus(action);
        HoverPane.setTranslateX(0);
        BackBtn.setVisible(false);
        BookBtn.setVisible(false);
        double opacitySaturation = 0.92;
        RoomNo.setText(current.getText());
        Reservation[] reservation = Room.getDailySchedule(activeDate, current.getText(), false);
        for(int i=0;i<28;i++){
            if(reservation[i] != null){
                slotButtons.get(i).setText("Booked");
                slotButtons.get(i).setDisable(true);
            }
            else{
                slotButtons.get(i).setText("Free");
                slotButtons.get(i).setDisable(false);
            }
        }                                                                               // Loading ends
        induceDelay(appearAfter_HoverPane);
        HoverPane.setVisible(true);
        HoverPane.setDisable(true);
        FadeTransition appear = new FadeTransition(Duration.millis(animation), HoverPane);
        if(HoverPane.getOpacity()==opacitySaturation){
            appear.setFromValue(0.6);
        }
        else {
            appear.setFromValue(0);
        }
        appear.setToValue(opacitySaturation);
        appear.play();
    }

    /**
     * Closes disabled reservation pane
     */
    public void exitReadOnlyBookings(){
        if(!isActiveReservation && !requestProcessing) {
            induceDelay(appearAfter_HoverPane);
            FadeTransition appear = new FadeTransition(Duration.millis(animation), HoverPane);
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
                rightPane.setDisable(false);
                leftPane.setDisable(false);
                RoomNo.setText("Not Set");
                if(changepassProcessing){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                }
                if(requestProcessing){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                    pullDownPane2.setVisible(true);
                }
                if(joinCodeProcessing){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                    mainPane.setDisable(true);
                }
                if(cancelBookingProcessing){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                    mainPane.setDisable(true);
                }
            });
        }
    }
}
