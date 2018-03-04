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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;

import static java.lang.Math.max;



class SlotComparator implements Comparator<String> {

    @Override
    public int compare(String a, String b) {
        if(Reservation.getSlotID(a) < Reservation.getSlotID(b)){
            return -1;
        } else {
            return 1;
        }
    }
}

public class StudentReservationGUIController implements Initializable{
    private int appearAfter_HoverPane = 200;
    @FXML
    private StackPane HoverPane,listCoursesPane;
    @FXML
    private Label RoomNo;
    @FXML
    private Button BackBtn, cancelSlotBooking;
    @FXML
    private Button BookBtn, ttbutton, addCourse;
    @FXML
    private ImageView logo;
    @FXML
    private StackPane pullDownPane;
    @FXML
    private StackPane roomGridPane;
    @FXML
    private StackPane classStatus, slotInfoPane, changePasswordPane;
    @FXML
    private ImageView classStatusBG, slotStatusBG, slotStatusBG1, changePasswordBG, addCoursesBG,listCoursesBG, cancelSlotBookingImage;
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
    private Label curDate,curMon,curYear, slotInfoCourse1, slotInfoFaculty1, slotInfoMessage1;
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
    private StackPane preBooking, courseBooking, otherBooking, HolidayMessage, BlockedDayMessage;
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
    private String currentlyShowingSlot;
    private Event classEvent;
    private static int animation = 200;
    private ArrayList<String> allCourses;
    private Boolean holiday;
    private Boolean blockedday;


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

        activeUser = (Student)User.getActiveUser();
        //        batchLabel.setText(activeUser.getBatch());
        batchLabel.setText("Student");
        listCoursesProcessing = false;
        isActiveReservation = false;
        changepassProcessing = false;
        fetchCoursesProcessing = false;
        timetableprocessing = false;
        allCourses = null;
        File file = new File("./src/BookIT_logo.jpg");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        file = new File("./src/AdminReservation/classStatusBG.jpg");
        image = new Image(file.toURI().toString());
        classStatusBG.setImage(image);
        slotStatusBG.setImage(image);
        slotStatusBG1.setImage(image);
        addCoursesBG.setImage(image);
        changePasswordBG.setImage(image);
        listCoursesBG.setImage(image);
        pullDownPane.setTranslateY(pullDownPaneInitial);
        pullDownPane.setVisible(true);

        optionDropDown.getItems().clear();
        optionDropDown.getItems().add("Course");
        optionDropDown.getItems().add("Other");
        optionDropDown.setValue("Course");

        file = new File("./src/AdminReservation/cancel.png");
        image = new Image(file.toURI().toString());
        cancelSlotBookingImage.setImage(image);

        datePicker.setEditable(false);
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
        activeDate=LocalDate.now();
        setDate(activeDate);
        loadDate();
        loadCourses();
    }
    @FXML
    void keyPressed(KeyEvent event) {
        KeyCombination kb = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        if(kb.match(event)) {
            signout();
        }
        else{
        switch(event.getCode()) {
            case ESCAPE:
                if(pullDownPane.isVisible()){
                    closeReservationPane();
                }
                else if(listCoursesPane.isVisible()){
                    gobackFetchCourses();
                }
                else if(fetchCoursesPane.isVisible()){
                    closeFetchCourses();
                }
                else if(TimeTablePane.isVisible()){
                    CloseTimeTable();
                }
                else if(HoverPane.isVisible()){
                    exitReadOnlyBookings();
                }
                break;
            case ENTER:
                if(HoverPane.isVisible()){
                    pullDownReservationPane();
                }
                break;
            default:
                break;
        }
    }
    }
    public void OpenNotifications() {
    	try {
    		User x=User.getActiveUser();
            if(x.getNotifications(false).size()==0) {
                Notification.throwAlert("Information Dialog", "There are no new notifications");
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../Notification/Notify.fxml"));
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
        ArrayList<String> items = Student.searchCourse(keywordTokens, false);
        items.sort(String::compareTo);
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
        FadeTransition appear = new FadeTransition(Duration.millis(animation), listCoursesPane);
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
        Boolean failed = false;
        for(int i=0;i<selectedCourses.size();i++){
            if(!activeUser.addCourse(selectedCourses.get(i), false)){
                failed = true;
            }
        }
        if(failed){
            Notification.throwAlert("Error","Some courses could not be added due to timetable clash");
        }
        activeUser.setActiveUser();
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
        ArrayList<String> myCourses = activeUser.getMyCourses();
        Reservation[] listOfReservations = Course.getStudentTT(activeDate, myCourses, false);
        if(listOfReservations == null){
            return;
        }
        datePicker.setVisible(false);
        timetableprocessing = true;
        TimeTablePane.setVisible(true);
        FadeTransition appear = new FadeTransition(Duration.millis(animation), TimeTablePane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
        String date = Integer.toString(activeDate.getDayOfMonth());
        if(date.length() == 1){
            date="0"+date;
        }
        dateLabel.setText(date+"-"+activeDate.getMonthValue()+"-"+activeDate.getYear());
        dateLabel.setFont(new Font(24));
        for(int i=0;i<courseSlotButtons.size();i++){
            if(listOfReservations[i] != null){
                courseSlotButtons.get(i).setDisable(false);
                courseSlotButtons.get(i).setText(Course.getCourseAcronym(listOfReservations[i].getCourseName(), false));
            }
            else{
                courseSlotButtons.get(i).setDisable(true);
                courseSlotButtons.get(i).setText("Free");
            }
        }
        datePicker.setVisible(false);
        roomGridPane.setDisable(true);
        addCourse.setDisable(true);
        ttbutton.setDisable(true);
    }

    /**
     * Closes the student's custom time table
     */
    public void CloseTimeTable(){
        timetableprocessing = false;
        datePicker.setVisible(true);
        addCourse.setDisable(false);
        datePicker.setVisible(true);
        ttbutton.setDisable(false);
        showLogo();
        slotInfoPane.setVisible(false);
        FadeTransition appear = new FadeTransition(Duration.millis(animation), TimeTablePane);
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
        FadeTransition appear = new FadeTransition(Duration.millis(animation), fetchCoursesPane);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();
        appear.setOnFinished(e->{
            rightPane.setDisable(true);
            leftPane.setDisable(true);
            mainPane.setDisable(true);
        });
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
        if(activeUser.isHoliday(activeDate, false)){
            holiday = true;
        }
        else{
            holiday = false;
        }
        if(activeUser.isBlockedDay(activeDate, false)){
            blockedday = true;
        }
        else{
            blockedday = false;
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
        TTinfoPane.setVisible(true);
        Label curLabel = (Label) e.getSource();
        ArrayList<String> myCourses = activeUser.getMyCourses();
        Reservation r = null;
        for(int i=0;i<myCourses.size();i++){
            Reservation temp = Course.getReservation(myCourses.get(i), activeDate, Reservation.getSlotID(curLabel.getText()), false);
            if(temp!=null){
                if(r == null){
                    r = temp;
                }
                else if(temp.getType().equals("Lecture")){
                    r = temp;
                }
                else if(!r.getType().equals("Lecture") && temp.getType().equals("Lab")){
                    r = temp;
                }
            }
        }
        if(r!=null) {
            String facultyEmail = Course.getCourseFaculty(r.getCourseName(), false);
            if(facultyEmail.equals("")){
                facultyEmail="~~~~";
            }
            slotTTinfo.setText(curLabel.getText()+" | "+r.getRoomName());
            slotInfoFaculty1.setText(facultyEmail);
            slotInfoCourse1.setText(r.getCourseName());
            slotInfoMessage1.setText(r.getMessage());
        }
        else{
            slotTTinfo.setText(curLabel.getText());
            slotInfoFaculty1.setText("N/A");
            slotInfoCourse1.setText("N/A");
            slotInfoMessage1.setText("N/A");
        }                                                               // GUI-Helper Integration ends
    }
    public void cancelSlotBooking(){
        Reservation r = Room.serverFetchRequest(activeUser.getEmail().getEmailID(), activeDate, Reservation.getSlotID(currentlyShowingSlot),activeRoom,false);
        if(r.isRequest()){
            if(!Notification.throwConfirmation("Warning", "You are about to cancel the request. Are you sure you want to proceed?")){
                return;
            }
            Room.serverDeleteRequest(activeUser.getEmail().getEmailID(), activeDate, Reservation.getSlotID(currentlyShowingSlot),activeRoom,false);
        }
        else {
            if(!Notification.throwConfirmation("Warning", "You are about to cancel the booking. Are you sure you want to proceed?")){
                return;
            }
            activeUser.cancelBooking(activeDate, Reservation.getSlotID(currentlyShowingSlot), activeRoom, false);
        }
        Button current = slotButtons.get(Reservation.getSlotID(currentlyShowingSlot));
        current.setDisable(false);
        current.setText("Free");
        updateClassStatus(classEvent);
    }
    /**
     * Displays details attached to the slot on the top center pane
     * @param e Event object
     */
    public void showSlotInfo(Event e){
        Reservation[] bookings = Room.getDailySchedule(activeDate, statusRoomID.getText(), false);
        Reservation[] requests = Room.getPendingReservations(activeUser.getEmail().getEmailID(), activeDate, statusRoomID.getText(), false);
        if(bookings == null){
            return;
        }
        if(requests == null){
            return;
        }
        slotInfoPane.setVisible(true);
        Label curLabel = (Label) e.getSource();
        slotInfo.setText(curLabel.getText());
        currentlyShowingSlot = curLabel.getText();        // GUI-Helper Integration starts
        Reservation temp = bookings[Reservation.getSlotID(curLabel.getText())];
        if(temp==null){
            temp = requests[Reservation.getSlotID(curLabel.getText())];
        }
        if(temp!=null) {
            String facultyName="~~~~";
            if (!temp.getFacultyEmail(false).equals("")){
                Faculty f = (Faculty)User.getUser(temp.getFacultyEmail(false), false);
                if(f!=null) {
                    facultyName = f.getName();
                }
            }
            slotInfoFaculty.setText(facultyName);
            if(temp.getCourseName().length()>30) {
                slotInfoCourse.setText(temp.getCourseName().substring(0,15)+"..."+temp.getCourseName().substring(temp.getCourseName().length()-10,temp.getCourseName().length()));
            }
            else{
                slotInfoCourse.setText(temp.getCourseName());
            }
            slotInfoMessage.setText(temp.getCourseName()+"\n"+temp.getMessage());
            String currentUserEmail = activeUser.getEmail().getEmailID();
            if(currentUserEmail.equals(temp.getFacultyEmail(false)) || currentUserEmail.equals(temp.getReserverEmail())){
                cancelSlotBooking.setDisable(false);
            }
            else{
                cancelSlotBooking.setDisable(true);
            }
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
        statusRoomID.setText(activeRoom);
        int capacity = Room.getCapacity(activeRoom, false);                                  // GUI-Helper integration begins here
        statusClassSize.setText("  "+Integer.toString(capacity));
        Reservation[] reservation = Room.getDailySchedule(activeDate, activeRoom, false);
        if(reservation == null){
            showLogo();
            exitReadOnlyBookings();
            return;
        }
        int freeSlots=0;
        for(int i=0;i<28;i++){
            if(reservation[i] == null){
                freeSlots++;
            }
        }
        if(holiday){
            HolidayMessage.setVisible(true);
        }
        else{
            HolidayMessage.setVisible(false);
        }
        if(blockedday){
            BlockedDayMessage.setVisible(true);
            HolidayMessage.setVisible(false);
        }
        else{
            BlockedDayMessage.setVisible(false);
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
            HolidayMessage.setVisible(false);
            BlockedDayMessage.setVisible(false);
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
            BookBtn.setOpacity(1);
            BookBtn.setVisible(true);
            error1.setVisible(true);
        }
        else{
            BookBtn.setVisible(true);
            BookBtn.setOpacity(1);
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
        ArrayList<String> items = new ArrayList<>();
        selection.forEach((btn, num)->{
            items.add(getReserveButtonInfo(btn.getId()));
        });
        items.sort(new SlotComparator());
        selectedSlotsScrollPane.getChildren().clear();
        int i=0;
        while(i<items.size()){
            label[i] = new Label();
            label[i].setText(items.get(i));
            chosenSlots.add(Reservation.getSlotID(items.get(i)));
            label[i].setPrefSize(494, 50);
            label[i].setAlignment(Pos.CENTER);
            label[i].setTranslateY(i*50);
            label[i].setStyle("-fx-background-color: white; -fx-border-color:  #2a2a2a; -fx-border-width:3");
            label[i].setFont(new Font(22));
            selectedSlotsScrollPane.getChildren().add(label[i]);
            i++;
        }
        selectedSlotsScrollPane.setPrefSize(494,max(474,50*i));
        if(allCourses == null) {
            allCourses = Course.getAllCourses();
            allCourses.sort(String::compareToIgnoreCase);
        }
        courseDropDown.getItems().clear();
        purposeDropDown.getItems().clear();
        groupDropDown.getItems().clear();
        for(int j=0;j<allCourses.size();j++) {
            courseDropDown.getItems().add(allCourses.get(j));
        }
        new AutoCompleteComboBoxListener<>(courseDropDown);
        purposeDropDown.getItems().add("Lecture");
        purposeDropDown.getItems().add("Lab");
        purposeDropDown.getItems().add("Tutorial");
        purposeDropDown.getItems().add("Quiz");
        for(int j=0;j<6;j++) {
            groupDropDown.getItems().add(Integer.toString(j+1));
        }
        SequentialTransition sequence = new SequentialTransition();
        int step=1;
        int location=pullDownPaneInitial;
        while(location>30) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(pullDownPane);
            translate.setToY(location);
            translate.setDuration(Duration.millis(15));
            step+=2;
            location-=max(step,10);
            sequence.getChildren().add(translate);
        }
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(pullDownPane);
        location = 53;
        translate.setToY(location);
        translate.setDuration(Duration.millis(15));
        sequence.getChildren().add(translate);
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
        if(!allCourses.contains(chosenCourse)){
            Notification.throwAlert("Error", "This course can't be chosen! Ensure to choose only those courses available in the drop down box");
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
        LocalDateTime create_time = LocalDateTime.now();
        for(int i=0;i<chosenSlots.size();i++){              // GUI Integration Begins
            Reservation r;
            r = new Reservation(chosenMessage, chosenGroup, chosenCourse, chosenFaculty, activeRoom, chosenPurpose, chosenSlots.get(i));
            r.setCreationDate(create_time);
            r.requestAdmin();
            r.setReserverEmail(activeUser.getEmail().getEmailID());
            r.setTargetDate(activeDate);
            listOfReservations.add(r);
        }                                                   // GUI Integration Ends
        if(!activeUser.sendReservationRequest(listOfReservations, false)){
            Notification.throwAlert("Booking Error","The booking couldn't be complete due to conflict. Try again");
        }
        else{
            for(int i=0;i<chosenSlots.size();i++){
                slotButtons.get(chosenSlots.get(i)).setText("Requested");
                slotButtons.get(chosenSlots.get(i)).setDisable(true);
            }
        }
        closeReservationPane();
        flyRight();
        purposeBox.clear();
        requestMessage2.clear();
    }
    @FXML
    public void launchFeedbackController(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../Feedback/Feedback.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            File file = new File("./src/BookIT_icon.jpg");
            stage.getIcons().add(new Image(file.toURI().toString()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            double width = visualBounds.getWidth();
            double height = visualBounds.getHeight();
            double scaleWidth = (width) / 1366;
            double scaleHeight = (height) / 768;
            stage.setTitle("Feedback");
            stage.setWidth(600 * scaleWidth);
            stage.setHeight(400 * scaleHeight);
            stage.setScene(new Scene(root1, 600 * scaleWidth, 400 * scaleHeight));
            stage.show();
        }
        catch (Exception e){
            System.out.println("Exception occurred while loading feedback fxml");
        }
    }
    /**
     * Event handler for confirming booking of a room
     */
    public void bookingCompleted2(){
        ArrayList<String> chosenGroup = new ArrayList<>();
        chosenGroup.add("0");
        String chosenPurpose="";
        chosenPurpose = purposeBox.getText();
        if(chosenPurpose.equals("")){
            Notification.throwAlert("Error","Purpose field can't be empty");
            return;
        }
        String chosenMessage;
        chosenMessage = requestMessage2.getText();
        ArrayList<Reservation> listOfReservations = new ArrayList<>();
        for(int i=0;i<chosenSlots.size();i++){              // GUI Integration Begins
            Reservation r;
            r = new Reservation(chosenMessage, chosenGroup, "", "", activeRoom, chosenPurpose, chosenSlots.get(i));
            r.requestAdmin();
            r.setTargetDate(activeDate);
            r.setReserverEmail(activeUser.getEmail().getEmailID());
            listOfReservations.add(r);
        }                                                   // GUI Integration Ends
        if(!activeUser.sendReservationRequest(listOfReservations, false)){
            Notification.throwAlert("Booking Error","The booking couldn't be completed as one of the slots you've chosen has been booked. Please refresh the page and try a different slot");
        }
        else{
            for(int i=0;i<chosenSlots.size();i++){
                slotButtons.get(chosenSlots.get(i)).setText("Requested");
                slotButtons.get(chosenSlots.get(i)).setDisable(true);
            }
        }
        closeReservationPane();
        flyRight();
        purposeBox.clear();
        requestMessage2.clear();
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
        activeRoom = current.getText();
        Boolean check = Room.exists(activeRoom,false);                               // Loading buttons
        if(check==false){
            return;
        }
        Reservation[] reservation = Room.getDailySchedule(activeDate, activeRoom, false);
        Reservation[] requests = Room.getPendingReservations(activeUser.getEmail().getEmailID(), activeDate, activeRoom, false);
        if(reservation == null || requests == null){
            return;
        }
        datePicker.setVisible(false);
        addCourse.setDisable(true);
        ttbutton.setDisable(true);
        selection.clear();
        classEvent = action;
        updateClassStatus(action);
        HoverPane.setTranslateX(0);
        error1.setVisible(true);
        BookBtn.setDisable(true);
        BackBtn.setVisible(true);
        BookBtn.setVisible(true);
        BookBtn.setOpacity(0);
        BackBtn.setOpacity(0);
        RoomNo.setText(activeRoom);
        if(blockedday){
            for (int i = 0; i < 28; i++) {
                if (reservation[i] != null) {
                    slotButtons.get(i).setText("Booked");
                    slotButtons.get(i).setDisable(true);
                } else {
                    Reservation temp = requests[i];
                    if (temp == null) {
                        slotButtons.get(i).setText("Free");
                        slotButtons.get(i).setDisable(true);
                    } else {
                        slotButtons.get(i).setText("Requested");
                        slotButtons.get(i).setDisable(true);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < 28; i++) {
                if (reservation[i] != null) {
                    slotButtons.get(i).setText("Booked");
                    slotButtons.get(i).setDisable(true);
                } else {
                    Reservation temp = requests[i];
                    if (temp == null) {
                        slotButtons.get(i).setText("Free");
                        slotButtons.get(i).setDisable(false);
                    } else {
                        slotButtons.get(i).setText("Requested");
                        slotButtons.get(i).setDisable(true);
                    }
                }
            }
        }
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
        double opacitySaturation = 1;
        RoomNo.setText(current.getText());
        Reservation[] reservation = Room.getDailySchedule(activeDate, current.getText(), false);
        Reservation[] requests = Room.getPendingReservations(activeUser.getEmail().getEmailID(), activeDate, current.getText(), false);
        for(int i=0;i<28;i++){
            if(reservation[i] != null){
                slotButtons.get(i).setText("Booked");
                slotButtons.get(i).setDisable(true);
            }
            else{
                Reservation temp = requests[i];
                if(temp==null) {
                    slotButtons.get(i).setText("Free");
                    slotButtons.get(i).setDisable(false);
                }
                else{
                    slotButtons.get(i).setText("Requested");
                    slotButtons.get(i).setDisable(true);
                }
            }
        }                                                                              // Loading ends
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
        if(!isActiveReservation) {
            FadeTransition appear = new FadeTransition(Duration.millis(animation), HoverPane);
            appear.setToValue(0);
            appear.play();
            closeClassStatus();
            addCourse.setDisable(false);
            ttbutton.setDisable(false);
            datePicker.setVisible(true);
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
                else if(fetchCoursesProcessing){
                    rightPane.setDisable(true);
                    leftPane.setDisable(true);
                    mainPane.setDisable(true);
                }
                else if(listCoursesProcessing){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                    mainPane.setDisable(true);
                }
                else if(timetableprocessing){
                    rightPane.setDisable(true);
                    leftPane.setDisable(true);
                    roomGridPane.setDisable(true);
                }
            });
        }
    }
}
