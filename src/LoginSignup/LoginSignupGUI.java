package LoginSignup;
	

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class LoginSignupGUI extends Application{
	@Override
	public void start(Stage primaryStage) {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			double screenWidth = screenSize.getWidth();
			double screenHeight = screenSize.getHeight();
			Parent root;
			double width,height;
			/*if(Screenwidth>=1920) {
				root = FXMLLoader.load(getClass().getResource("LoginSignup1080p.fxml"));
				width=850;
				height=567;
			}*/
			//else{
				root = FXMLLoader.load(getClass().getResource("LoginSignup1.fxml"));
				width=(int)(600.0 * screenWidth/1366.0);
				height=(int)(400.0 * screenHeight/768.0);
			//}
			Scene scene = new Scene(root,width,height);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			File file = new File("./src/BookIT_icon.jpg");
			primaryStage.setOnCloseRequest(e->{
				ServerSocket temp = PortListener.needSocket();
				try {
					if(temp!=null) {
					temp.close();}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("error in line 48 Login\n" +
							"1920.0\n" +
							"1080.0\n" +
							"1.4055636896046853\n" +
							"1.40625\n" +
							"843.0\n" +
							"562.0\nSignupGUI.java");;
				}
			});
			primaryStage.getIcons().add(new Image(file.toURI().toString()));
			primaryStage.setTitle("BookIT - Login");
			primaryStage.showAndWait();
			root.requestFocus();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
