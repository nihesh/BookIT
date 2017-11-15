package LoginSignup;
	

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
public class LoginSignupGUI extends Application{
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("LoginSignup1080p.fxml"));
			Scene scene = new Scene(root,850,567);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image("./LoginSignup/BookIT_icon.jpg"));
			primaryStage.setTitle("BookIT-Login");
			primaryStage.showAndWait();
			root.requestFocus();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
