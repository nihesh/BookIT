// Author   : Nihesh Anderson
// Date     : 4 Oct, 2017
// File     : BookIT.java

package Reservation;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;



public class ReservationGUIController {
    @FXML
    private StackPane HoverPane;
    @FXML
    private Label RoomNo;
    public void showReadOnlyBookings(){
        RoomNo.setText("C01");
        try {
            Thread.sleep(100);
        }
        catch(Exception e){
            System.out.println("Error in ReservationGUIController: ShowReadOnlyBookings");
        }
        HoverPane.setVisible(true);
        HoverPane.setDisable(true);
        HoverPane.setOpacity(0.95);
    }
    public void exitReadOnlyBookings(){
        try {
            Thread.sleep(100);
        }
        catch(Exception e){
            System.out.println("Error in ReservationGUIController: ShowReadOnlyBookings");
        }
        HoverPane.setVisible(false);
        HoverPane.setDisable(false);
        HoverPane.setOpacity(1);
        RoomNo.setText("Not Set");
    }
}
