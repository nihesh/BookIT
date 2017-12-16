// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package StudentReservation;

import HelperClasses.*;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
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
    private StackPane HoverPane,listCoursesPane;
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
    private Label statusRoomID, slotInfo,statusClassSize, statusFreeSlots, dateLabel;
    @FXML
    private StackPane topPane,leftPane,rightPane,mainPane, fetchCoursesPane, TimeTablePane, TTinfoPane;
    @FXML
    private AnchorPane selectedSlotsScrollPane, myCoursesScrollPane, shortlistedCourses;
    @FXML
    private Label error1, slotTTinfo;
    @FXML
    private ComboBox courseDropDown, purposeDropDown, groupDropDown, optionDropDown;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label curDate,curMon,curYear;
    @FXML
    private ArrayList<Button> slotButtons, courseSlotButtons;
    @FXML
    private Label slotInfoCourse, slotInfoMessage, slotInfoFaculty,batchLabel;
    @FXML
    private PasswordField oldPass, newPass, renewPass;
    @FXML
    private TextField courseKeywordSearch;
    @FXML
    private TextArea requestMessage, requestMessage2;

    @FXML
    private VBox rootPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private SplitPane sp1;
    @FXML
    private StackPane preBooking, courseBooking, otherBooking;
    @FXML
    private TextField purposeBox;

    private String currentPurpose;
    private LocalDate activeDate;
    private ArrayList<Integer> chosenSlots;
    private ArrayList<CheckBox> courseLabels = new ArrayList<>();
    private int pullDownPaneInitial = 650;
    private Student activeUser;
    private String activeRoom;
    private HashMap<Button,Integer> selection = new HashMap<Button,Integer>();
    private Boolean isActiveReservation, changepassProcessing, fetchCoursesProcessing, listCoursesProcessing,timetableprocessing;
    private LocalDate StartDate;
    private LocalDate EndDate;


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
        double scaleHeight = (height)/1005;
        double TTFactor;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double Screenwidth = screenSize.getWidth();
        double Screenheight = screenSize.getHeight();
        double menuFactor;
        double reservationFactor;
        double splitBar;

        if(Screenwidth==1920 && Screenheight==1080){
            menuFactor = 1;
            reservationFactor = 1;
            scaleHeight = (height-44)/1000;
            TTFactor=1;
            splitBar=1;
        }
        else if(Screenwidth==1600 && Screenheight==900){
            menuFactor = 1;
            reservationFactor = 1.14;
            TTFactor=1;
            splitBar=1;
        }
        else if((Screenwidth==1360 || Screenwidth==1368 || Screenwidth==1366) && Screenheight==768){
            scaleHeight = (height+55)/1005;
            menuFactor = 1;
            reservationFactor = 1.33;
            TTFactor=1;
            splitBar = 1.1;
        }
        else{
            menuFactor = 1;
            reservationFactor = 1;
            TTFactor=1;
            splitBar=1;
        }

        rootPane.setScaleX(scaleWidth);
        rootPane.setScaleY(scaleHeight);
        menuBar.setScaleX(1/(menuFactor*scaleWidth));
        mainPane.setScaleX(1/(reservationFactor*scaleWidth));
        TimeTablePane.setScaleX(TTFactor);
        sp1.setScaleX(splitBar*reservationFactor);

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

        activeUser = (Student)User.getActiveUser();
        batchLabel.setText(activeUser.getBatch());
        listCoursesProcessing = false;
        isActiveReservation = false;
        changepassProcessing = false;
        fetchCoursesProcessing = false;
        timetableprocessing = false;
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

        optionDropDown.getItems().clear();
        optionDropDown.getItems().add("Course");
        optionDropDown.getItems().add("Other");
        optionDropDown.setValue("Course");

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
        activeDate=LocalDate.now();
        setDate(activeDate);
        loadDate();
        loadCourses();
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
     * Shows a list of valid courses on clicking Join course option
     */
    public void openCoursesList(){
        courseLabels.clear();
        String keyword = courseKeywordSearch.getText();
        ArrayList<String> keywordTokens = new ArrayList<String>();
        for(String word : keyword.split(" ")) {
            keywordTokens.add(word);
        }
        ArrayList<String> items = Student.searchCourse(keywordTokens);
        int i=0;
        while(i<items.size()){
            courseLabels.add(new CheckBox());
            courseLabels.get(i).setText(items.get(i));
            courseLabels.get(i).setPrefSize(578, 35);
            courseLabels.get(i).setAlignment(Pos.CENTER);
            courseLabels.get(i).setTranslateY(i*35);
            courseLabels.get(i).setStyle("-fx-background-color: #229954; -fx-border-color:  white; -fx-border-width:2");
            courseLabels.get(i).setFont(new Font(16));
            shortlistedCourses.getChildren().add(courseLabels.get(i));
            i++;
        }
        shortlistedCourses.setPrefSize(578,max(235,34*i));
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
    /**
     * Adds selected courses, and closes the pane that shows the list of courses which can be joined
     */
    public void closeCoursesList(){
        ArrayList<String> selectedCourses = new ArrayList<>();
        for(int i=0;i<courseLabels.size();i++){
            if(courseLabels.get(i).isSelected()){
                selectedCourses.add(courseLabels.get(i).getText());
            }
        }
        for(int i=0;i<selectedCourses.size();i++){
            activeUser.addCourse(selectedCourses.get(i));
        }
        loadCourses();
        leftPane.setDisable(false);
        rightPane.setDisable(false);
        mainPane.setDisable(false);
        listCoursesProcessing = false;
        listCoursesPane.setVisible(false);
        showLogo();
    }

    /**
     * Open's the student's custom time table
     */
    public void OpenTimeTable(){
        timetableprocessing = true;
        TimeTablePane.setVisible(true);
        ArrayList<String> myCourses = activeUser.getMyCourses();
        ArrayList<Course> courseObjects = new ArrayList<>();
        FadeTransition appear = new FadeTransition(Duration.millis(1000), TimeTablePane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
        for(int i=0;i<myCourses.size();i++){
            courseObjects.add(Course.deserializeCourse(myCourses.get(i), false));
        }
        Reservation[] listOfReservations = new Reservation[30];
        String date = Integer.toString(activeDate.getDayOfMonth());
        if(date.length() == 1){
            date="0"+date;
        }
        dateLabel.setText(date+"-"+activeDate.getMonthValue()+"-"+activeDate.getYear());
        dateLabel.setFont(new Font(24));
        for(int j=0;j<28;j++) {
            listOfReservations[j] = null;
            for (int i = 0; i < courseObjects.size(); i++) {
                Course c = courseObjects.get(i);
                if (c.getSchedule(activeDate)[j] != null) {
                    if (listOfReservations[j] == null) {
                        listOfReservations[j] = c.getSchedule(activeDate)[j];
                    }
                    else if (c.getSchedule(activeDate)[j].getType().equals("Lecture")) {
                        listOfReservations[j] = c.getSchedule(activeDate)[j];
                    }
                    else if (!listOfReservations[j].getType().equals("Lecture") && c.getSchedule(activeDate)[j].getType().equals("Lab")) {
                        listOfReservations[j] = c.getSchedule(activeDate)[j];
                    }
                }
            }
        }
        for(int i=0;i<courseSlotButtons.size();i++){
            if(listOfReservations[i] != null){
                courseSlotButtons.get(i).setDisable(false);
                Course c = Course.deserializeCourse(listOfReservations[i].getCourseName(), false);
                courseSlotButtons.get(i).setText(c.getAcronym());
            }
            else{
                courseSlotButtons.get(i).setDisable(true);
                courseSlotButtons.get(i).setText("Free");
            }
        }
        rightPane.setDisable(true);
        leftPane.setDisable(true);
        roomGridPane.setDisable(true);
    }

    /**
     * Closes the student's custom time table
     */
    public void CloseTimeTable(){
        timetableprocessing = false;
        showLogo();
        slotInfoPane.setVisible(false);
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

    /**
     * Opens the pane where courses can be searched for
     */
    public void openFetchCourses(){
        courseKeywordSearch.clear();
        fetchCoursesProcessing = true;
        rightPane.setDisable(true);
        leftPane.setDisable(true);
        mainPane.setDisable(true);
        hideLogo();
        fetchCoursesPane.setVisible(true);
        FadeTransition appear = new FadeTransition(Duration.millis(1000), fetchCoursesPane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
    }

    /**
     * Closes the pane where courses can be searched
     */
    public void closeFetchCourses(){
        courseKeywordSearch.clear();
        fetchCoursesProcessing = false;
        rightPane.setDisable(false);
        leftPane.setDisable(false);
        mainPane.setDisable(false);
        fetchCoursesPane.setVisible(false);
        showLogo();
    }

    /**
     * Goes back to the pane where courses can be searched for
     */
    public void gobackFetchCourses(){
        courseKeywordSearch.clear();
        fetchCoursesProcessing = false;
        listCoursesPane.setVisible(false);
        openFetchCourses();
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
        FadeTransition appear = new FadeTransition(Duration.millis(1000), changePasswordPane);
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
            Boolean status = activeUser.changePassword(oldPassString, newPassString);
            if(status) {
                changepassProcessing = false;
                leftPane.setDisable(false);
                changePasswordPane.setVisible(false);
                rightPane.setDisable(false);
                mainPane.setDisable(false);
                showLogo();
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
        if(date.isAfter(StartDate) && date.isBefore(EndDate)){
            activeDate = date;
            datePicker.setValue(activeDate);
            setDate(activeDate);
        }
        else{
            datePicker.setValue(activeDate);
            setDate(activeDate);
            topPane.setDisable(true);
            mainPane.setDisable(true);
            JOptionPane.showMessageDialog(null, "Sorry, BookIT server is down", "Server Error", JOptionPane.ERROR_MESSAGE);
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
     * Loads the list of user's courses onto the courses pane
     */
    public void loadCourses(){
        myCoursesScrollPane.getChildren().clear();
        Label[] label = new Label[100];
        ArrayList<String> items = activeUser.getMyCourses();
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

    /**
     * Shows the information of a reservation in the student Time table
     * @param e
     */
    public void showCourseSlotInfo(Event e){
        hideLogo();
        slotInfoPane.setVisible(true);
        Label curLabel = (Label) e.getSource();
        slotInfo.setText(curLabel.getText());
        ArrayList<String> myCourses = activeUser.getMyCourses();
        Reservation r = null;
        for(int i=0;i<myCourses.size();i++){
            Course c = Course.deserializeCourse(myCourses.get(i), false);
            if(c.getSchedule(activeDate)[Reservation.getSlotID(curLabel.getText())]!=null){
                if(r == null){
                    r = c.getSchedule(activeDate)[Reservation.getSlotID(curLabel.getText())];
                }
                else if(c.getSchedule(activeDate)[Reservation.getSlotID(curLabel.getText())].getType().equals("Lecture")){
                    r = c.getSchedule(activeDate)[Reservation.getSlotID(curLabel.getText())];
                }
                else if(!r.getType().equals("Lecture") && c.getSchedule(activeDate)[Reservation.getSlotID(curLabel.getText())].getType().equals("Lab")){
                    r = c.getSchedule(activeDate)[Reservation.getSlotID(curLabel.getText())];
                }
            }
        }
        if(r!=null) {
            Course c = Course.deserializeCourse(r.getCourseName(), false);
            String facultyEmail = c.getInstructorEmail();
            if(facultyEmail.equals("")){
                facultyEmail="~~~~";
            }
            slotInfoFaculty.setText(facultyEmail);
            slotInfoCourse.setText(r.getCourseName());
            slotInfoMessage.setText(r.getMessage());
        }
        else{
            slotInfoFaculty.setText("N/A");
            slotInfoCourse.setText("N/A");
            slotInfoMessage.setText("N/A");
        }                                                               // GUI-Helper Integration ends
    }
    /**
     * Displays details attached to the slot on the top center pane
     * @param e Event object
     */
    public void showSlotInfo(Event e){
        slotInfoPane.setVisible(true);
        Label curLabel = (Label) e.getSource();
        slotInfo.setText(curLabel.getText());
        Room r = Room.deserializeRoom(statusRoomID.getText(), false);          // GUI-Helper Integration starts
        Reservation[] bookings = r.getSchedule(activeDate);
        if(bookings[Reservation.getSlotID(curLabel.getText())]!=null) {
            Course c = Course.deserializeCourse(bookings[Reservation.getSlotID(curLabel.getText())].getCourseName(), false);
            String facultyEmail;
            if(c!=null)
                facultyEmail = c.getInstructorEmail();
            else{
                facultyEmail="";
            }
            if(facultyEmail.equals("")){
                facultyEmail="~~~~";
            }
            slotInfoFaculty.setText(facultyEmail);
            slotInfoCourse.setText(bookings[Reservation.getSlotID(curLabel.getText())].getCourseName());
            slotInfoMessage.setText(bookings[Reservation.getSlotID(curLabel.getText())].getMessage());
        }
        else{
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
        Room r = Room.deserializeRoom(current.getText(), false);                                  // GUI-Helper integration begins here
        statusClassSize.setText("  "+Integer.toString(r.getCapacity()));
        Reservation[] reservation = r.getSchedule(activeDate);
        int freeSlots=0;
        for(int i=0;i<28;i++){
            if(reservation[i] == null){
                freeSlots++;
            }
        }
        statusFreeSlots.setText("  "+Integer.toString(freeSlots));                         // GUI-Helper integration ends here
        FadeTransition appear = new FadeTransition(Duration.millis(1000), classStatus);
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
        FadeTransition appear = new FadeTransition(Duration.millis(1000), logo);
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
    /**
     * Booking confirmation pane appears
     */
    public void pullDownReservationPane(){
        courseBooking.setVisible(false);
        otherBooking.setVisible(false);
        preBooking.setVisible(true);
        chosenSlots = new ArrayList<>();
        requestMessage.clear();
        courseDropDown.getItems().clear();
        groupDropDown.getItems().clear();
        purposeDropDown.getItems().clear();
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
            chosenSlots.add(Reservation.getSlotID(getReserveButtonInfo(items.get(i).getId())));
            label[i].setPrefSize(494, 50);
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
        for(int j=0;j<allCourses.size();j++) {
            courseDropDown.getItems().add(allCourses.get(j));
        }
        purposeDropDown.getItems().add("Lecture");
        purposeDropDown.getItems().add("Lab");
        purposeDropDown.getItems().add("Tutorial");
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
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(1000), BookBtn);
        appearBookBtn.setToValue(0);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(1000), BackBtn);
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
            return;
        }
        String chosenGroup;
        try {
            chosenGroup = groupDropDown.getSelectionModel().getSelectedItem().toString();
        }
        catch(NullPointerException e){
            chosenGroup = "0";
        }
        String chosenPurpose;
        try {
            chosenPurpose = purposeDropDown.getSelectionModel().getSelectedItem().toString();
        }
        catch(NullPointerException e){
            return;
        }
        String chosenFaculty;
        if(chosenCourse == ""){
            chosenFaculty = "";
        }
        else{
            courseObject = Course.deserializeCourse(chosenCourse, false);
            chosenFaculty = courseObject.getInstructorEmail();
        }
        String chosenMessage;
        chosenMessage = requestMessage.getText();
        ArrayList<Reservation> listOfReservations = new ArrayList<>();
        for(int i=0;i<chosenSlots.size();i++){              // GUI Integration Begins
            Reservation r;
            r = new Reservation(chosenMessage, chosenGroup, chosenCourse, chosenFaculty, activeRoom, chosenPurpose, chosenSlots.get(i));
            r.setTargetDate(activeDate);
            listOfReservations.add(r);
        }                                                   // GUI Integration Ends
        activeUser.sendReservationRequest(listOfReservations);
        closeReservationPane();
        flyRight();
        purposeBox.clear();
        requestMessage2.clear();
    }
    /**
     * Event handler for confirming booking of a room
     */
    public void bookingCompleted2(){
        String chosenPurpose;
        try {
            chosenPurpose = purposeBox.getText();
        }
        catch(NullPointerException e){
            return;
        }
        String chosenMessage;
        chosenMessage = requestMessage2.getText();
        ArrayList<Reservation> listOfReservations = new ArrayList<>();
        for(int i=0;i<chosenSlots.size();i++){              // GUI Integration Begins
            Reservation r;
            r = new Reservation(chosenMessage, "0", "", "", activeRoom, chosenPurpose, chosenSlots.get(i));
            r.setTargetDate(activeDate);
            listOfReservations.add(r);
        }                                                   // GUI Integration Ends
        activeUser.sendReservationRequest(listOfReservations);
        closeReservationPane();
        flyRight();
        purposeBox.clear();
        requestMessage2.clear();
    }
    /**
     * Reservation pane flys right
     */
    public void flyRight(){
        FadeTransition sequence = new FadeTransition(Duration.millis(500), HoverPane);
        sequence.setToValue(0);
        sequence.play();
        closeClassStatus();
        rightPane.setDisable(false);
        leftPane.setDisable(false);
        sequence.setOnFinished(e->{
            exitReadOnlyBookings();
        });
    }
    public void preBookingProceed(){
        try {
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
     * Resercation pane appears
     * @param action Event object
     */
    public void openBooking(Event action){
        Button current = (Button) action.getSource();
        Room r = Room.deserializeRoom(current.getText(), false);                               // Loading buttons
        if(r==null){
            return;
        }
        HoverPane.setTranslateX(0);
        error1.setVisible(true);
        BookBtn.setDisable(true);
        BackBtn.setVisible(true);
        BookBtn.setVisible(true);
        BookBtn.setOpacity(0);
        BackBtn.setOpacity(0);
        RoomNo.setText(current.getText());
        activeRoom = current.getText();
        Reservation[] reservation = r.getSchedule(activeDate);
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
        FadeTransition appear = new FadeTransition(Duration.millis(1000), HoverPane);
        appear.setToValue(1);
        FadeTransition appearBookBtn = new FadeTransition(Duration.millis(1000), BookBtn);
        appearBookBtn.setToValue(1);
        FadeTransition appearBackBtn = new FadeTransition(Duration.millis(1000), BackBtn);
        appearBackBtn.setToValue(1);
        ParallelTransition inParallel = new ParallelTransition(appear, appearBookBtn, appearBackBtn);
        inParallel.play();
    }
    /**
     * Reservation pane appears, but it remains disabled
     * @param action Event object
     */
    public void showReadOnlyBookings(Event action){
        Button current = (Button) action.getSource();
        Room r = Room.deserializeRoom(current.getText(), false);                               // Loading buttons
        if(r==null){
            return;
        }
        updateClassStatus(action);
        HoverPane.setTranslateX(0);
        BackBtn.setVisible(false);
        BookBtn.setVisible(false);
        double opacitySaturation = 1;
        RoomNo.setText(current.getText());
        Reservation[] reservation = r.getSchedule(activeDate);
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
    /**
     * Closes disabled reservation pane
     */
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
