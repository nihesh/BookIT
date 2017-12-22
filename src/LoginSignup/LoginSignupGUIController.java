package LoginSignup;


import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import HelperClasses.Admin;
import HelperClasses.Email;
import HelperClasses.Faculty;
import HelperClasses.Student;
import HelperClasses.User;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.com.google.api.services.samples.oauth2.cmdline.OAuth2Sample;
/**
 * controller class for login/signup gui
 * @author Harsh
 *
 */
public class LoginSignupGUIController {
	private double initOpacity=0.84;
	private Email Gemail;
	private String GName;
	private User Guser;
	
	private Email email;
	private String joincode;
	private User user;
	@FXML
	private Button GBtn;
	@FXML
	private AnchorPane FirstPane;
	@FXML
	private ComboBox<String> Branch;
	@FXML
	private TextField Name;
	@FXML
	private PasswordField CnfPass;
	@FXML
	private Button Signup_btn;
	@FXML
	private Button Login_btn;
	@FXML
	private AnchorPane Cred_Pane;
	@FXML
	private AnchorPane Login_Pane;
	@FXML
	private AnchorPane Signup_Pane;
	@FXML
	private TextField Login_email;
	@FXML
	private PasswordField Login_password;
	@FXML
	private Button Login_email_btn;
	@FXML
	private Button Login_password_btn;
	@FXML
	private Label Login_password_label;
	@FXML
	private TextField Signup_email;
	@FXML
	private PasswordField Signup_password;
	@FXML
	private Button Signup_email_btn;
	@FXML
	private Button Signup_password_btn;
	@FXML
	private TextField Signup_joincode;
	@FXML
	private Button Signup_joincode_btn;
	@FXML
	private Button Lback_btn;
	@FXML
	private Button Sback_btn;
	@FXML
	private Button Signup_done_btn;
	@FXML
	private Button Cred_btn;
	@FXML
	private Button CredBack_btn;
	@FXML 
	private AnchorPane GPane;
	@FXML
	private WebView browser;
	@FXML
	private Button Gdone;
	@FXML
	private Button Gcancel;
	@FXML
	public WebEngine e;
	@FXML
	private void Close() {
		PortListener.authcode="none";
		if(e!=null){
			e.load("");
			java.net.CookieManager manager = new java.net.CookieManager();
			java.net.CookieHandler.setDefault(manager);
		}
		PortListener.closeSocket();
		GPane.setVisible(false);
	}
	
	@FXML
	private void Continue() {
		if(e!=null && !PortListener.authcode.equals("none")) {
			e.load("");
			GPane.setVisible(false);
			if(PortListener.authcode.equals("denied")) {
				PortListener.authcode="none";
				java.net.CookieManager manager = new java.net.CookieManager();
				java.net.CookieHandler.setDefault(manager);
			}
			if(PortListener.email!=null && PortListener.email.contains("iiitd.ac.in")) {
				Gemail = new Email(PortListener.email);
				GName=PortListener.Name;
				if(User.getUser(Gemail.getEmailID(), false)!=null) {
					System.out.println("account already exists");
					java.net.CookieManager manager = new java.net.CookieManager();
					java.net.CookieHandler.setDefault(manager);
					Alert alert = new Alert(AlertType.INFORMATION);
					PortListener.authcode="none";
					PortListener.email=null;PortListener.Name=null;
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("This Account Already exists. Please use standard login");
					alert.showAndWait();
					return;
				}
				String usertype = User.getUserType(Gemail.getEmailID(), false);
				
				if(usertype.equals("Faculty")) {
					Guser = new Faculty(GName, "", Gemail, "Faculty", new ArrayList<String>());
				}
				else if(usertype.equals("Student")) {
					Guser = new Student(GName, "", Gemail, "Student", "", new ArrayList<String>());
				}
				else{
					Guser= new Admin(GName, "", Gemail, "Admin");
				}
				
				Guser.serialize(false);
				Guser.generatePass(false);
				Guser.mailPass(false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Account Created Successfully. A mail has been sent to you");
				alert.showAndWait();
				return;
			}
			java.net.CookieManager manager = new java.net.CookieManager();
			java.net.CookieHandler.setDefault(manager);
			System.out.println("wrong creds");
			//give some indication
			PortListener.authcode="none";
			PortListener.email=null;PortListener.Name=null;
			GName=null;Gemail=null;Guser=null;
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please use a IIITD account");
			alert.showAndWait();
			return;
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText("Please fill your complete info and then press DONE Button");
		alert.showAndWait();
	}
	
	
	@FXML
	public void initialize() {
		ArrayList<String> temp=new ArrayList<>();
		// TODO Auto-generated method stub
		Branch.getItems().removeAll(Branch.getItems());
		BufferedReader r;
		try {
			r=new BufferedReader(new FileReader("./src/AppData/Year/StudentYear.txt"));
			
			try {
				String x=r.readLine();
				while(x!=null) {
				temp.add(x); x=r.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String string : temp) {
			Branch.getItems().add(string);
		}
		Branch.getSelectionModel().select(temp.get(0));
		
	}
	@FXML
	private void PressG() {
		GPane.setVisible(true);
		e=browser.getEngine();
		e.load(PortListener.webURL1);
		PortListener p= new PortListener();	
	}
	@FXML
	private void CredNext() {
		Login_Pane.setDisable(true);
		Signup_Pane.setDisable(true);
		Cred_btn.setDisable(true);
		TranslateTransition transright=new TranslateTransition();
		transright.setNode(Cred_Pane);
		transright.setToX(Cred_Pane.getPrefWidth());
		transright.setDuration(Duration.millis(700));
		transright.play();
	}
	@FXML
	private void CredBack() {
		FadeTransition seq2=new FadeTransition();
		Login_Pane.setDisable(false);
		Signup_Pane.setDisable(false);
		TranslateTransition transright=new TranslateTransition();
		transright.setNode(Cred_Pane);
		transright.setToX(0);
		transright.setDuration(Duration.millis(700));
		transright.play();
		transright.setOnFinished(e->{
			Cred_btn.setDisable(false);
		});
	}
	@FXML
	/**
	 * back button controller in login pane
	 */
	private void backL() {
		Login_password_label.setVisible(false);
		Login_password_label.setText(null);
		Login_email.getStyleClass().remove("text-field2");
		Login_password.getStyleClass().remove("text-field2");
		Login_email.setVisible(true);
		Login_email_btn.setVisible(true);
		Login_password.setVisible(false);
		Login_password_btn.setVisible(false);
		Lback_btn.setVisible(false);
		Login_email.clear();
		Login_password.clear();
		Login_password.setTranslateY(0);
		
	}
	@FXML
	/**
	 * back button controller in sign-up pane gui
	 */
	private void backS() {
		if(Signup_password.isVisible()==true) {
			Signup_email.getStyleClass().remove("text-field2");
			Signup_password.getStyleClass().remove("text-field2");
			CnfPass.getStyleClass().remove("text-field2");
			Signup_email.setVisible(true);
			Signup_email_btn.setVisible(true);
			Signup_password.setVisible(false);
			CnfPass.setVisible(false);
			Signup_password_btn.setVisible(false);
			Sback_btn.setVisible(false);
			Signup_password.clear();
			CnfPass.clear();
		}
		else if(Signup_password.isVisible()==false && Signup_joincode.isVisible()==true){
			Signup_joincode.getStyleClass().remove("text-field2");
			Signup_password.getStyleClass().remove("text-field2");
			Signup_password.setVisible(true);
			CnfPass.getStyleClass().remove("text-field2");
			CnfPass.setVisible(true);
			Signup_joincode.setVisible(false);
			Signup_password_btn.setVisible(true);
			Signup_joincode_btn.setVisible(false);
			Signup_joincode.clear();
			Signup_password.clear();
			CnfPass.clear();
		}
		else {
			Signup_joincode.clear();
			Signup_joincode.setVisible(false);
			Branch.setVisible(false);
			Signup_joincode.getStyleClass().remove("text-field2");
			Name.getStyleClass().remove("text-field2");
			Name.setVisible(false);
			Signup_done_btn.setVisible(false);
			if(Signup_done_btn.getTranslateY()==75) {
				Signup_done_btn.setTranslateY(0);
			}
			Signup_joincode_btn.setVisible(false);
			Signup_password_btn.setVisible(true);
			Signup_password.clear();
			Name.clear();
			CnfPass.clear();
			Signup_password.getStyleClass().remove("text-field2");
			Signup_password.setVisible(true);
			CnfPass.getStyleClass().remove("text-field2");
			CnfPass.setVisible(true);
		}
	}
	@FXML
	/**
	 * the next button handler after entering email in login pane
	 */
	private void Login_NEXT() {
			//put some email validate code here to check user email
		AccCre.setVisible(false);
		email=new Email(Login_email.getText());
		if(!Login_email.getText().equals("") && email.validateLogin(false)) {
			if(Login_email.getStyleClass().contains("text-field2")) {
				if(!Login_email.getStyleClass().contains("text-field1")) {
					Login_email.getStyleClass().add("text-field1");
				}
				Login_email.getStyleClass().remove("text-field2");
			}
			FadeTransition t1=new FadeTransition();
			Login_password_label.setText(Login_email.getText());
			t1.setNode(Login_password_label);
			t1.setFromValue(0);
			t1.setToValue(1.0);
			t1.setDuration(Duration.millis(2000));
			t1.play();
			TranslateTransition t2=new TranslateTransition();
			Login_password.setVisible(true);
			t2.setNode(Login_password);
			t2.setByY((Login_password_btn.getLayoutY()-Login_password_label.getLayoutY())/2);
			t2.setDuration(Duration.millis(500));
			t2.play();
			t2.setOnFinished(e->{
				Login_password.setPromptText("Password");
				
			});
			Login_password_label.setVisible(true);
			Login_password_btn.setVisible(true);
			Login_email.setVisible(false);
			Login_email_btn.setVisible(false);
			Lback_btn.setVisible(true);
		}
		else {
			email=null;
			if(!Login_email.getStyleClass().contains("text-field2")) {
				Login_email.getStyleClass().add("text-field2");
			}

		}
			
		
	}
	@FXML
	/**
	 * button handler in login pane after entering password
	 */
	private void Login_NEXT2() {
		user=User.getUser(email.getEmailID(), false);
		if(user.authenticate(Login_password.getText())) {
			if(Login_password.getStyleClass().contains("text-field2")) {
				Login_password.getStyleClass().remove("text-field2");
				if(!Login_password.getStyleClass().contains("text-field1")) {
					Login_password.getStyleClass().add("text-field1");
				}
			}
			user.setActiveUser();
			Login_password.clear();
			Login_password.setTranslateY(0);
			
			Stage stage = (Stage) Login_password_btn.getScene().getWindow();
			stage.close();
		}
		else {
			Login_password.clear();
			user=null;
			if(!Login_password.getStyleClass().contains("text-field2")) {
				Login_password.getStyleClass().add("text-field2");
			}
			
		}
	}
	@FXML
	/**
	 * button handler in Sign up pane after entering email
	 */
	private void Signup_NEXT() {
		email=new Email(Signup_email.getText());
		int state=email.validateSignup(false);
		//put some email validate code here to check user email
		if(!Signup_email.getText().equals("") && state==0) {
			if(Signup_email.getStyleClass().contains("text-field2")) {
				if(!Signup_email.getStyleClass().contains("text-field1")) {
					Signup_email.getStyleClass().add("text-field1");
				}
				
				Signup_email.getStyleClass().remove("text-field2");
			}
		Signup_password.setVisible(true);
		CnfPass.setVisible(true);
		Signup_password_btn.setVisible(true);
		Signup_email.setVisible(false);
		Signup_email_btn.setVisible(false);
		Sback_btn.setVisible(true);
		}
		else {
		email=null;
			if(!Signup_email.getStyleClass().contains("text-field2")) {
			Signup_email.getStyleClass().add("text-field2");
		}
		}
	}
	
	@FXML
	/**
	 * button handler in Sign up pane after entering password
	 * and confirm password fields
	 */
	private void Signup_NEXT2() {
		//put some email validate code here to check user email
		if((!Signup_password.getText().equals("")) && Signup_password.getText().equals(CnfPass.getText())) {
			user=new User(" ",CnfPass.getText(),email, " ");
			if(Signup_password.getStyleClass().contains("text-field2")) {
				if(!Signup_password.getStyleClass().contains("text-field1")) {
					Signup_password.getStyleClass().add("text-field1");
				}
				Signup_password.getStyleClass().remove("text-field2");
			}
		Signup_password.setVisible(false);
		CnfPass.setVisible(false);
		Signup_password_btn.setVisible(false);
		Signup_email.setVisible(false);
		Signup_joincode.setVisible(true);
		Signup_joincode_btn.setVisible(true);
		}
		else {
			user=null;
			Signup_password.clear();
			CnfPass.clear();
			if(!Signup_password.getStyleClass().contains("text-field2")) {
				Signup_password.getStyleClass().add("text-field2");
			
			}
			if(!CnfPass.getStyleClass().contains("text-field2")) {
				CnfPass.getStyleClass().add("text-field2");
			
			}
		}
	}
	@FXML
	/**
	 * button handler in Sign up pane after entering join code
	 */
	private void Signup_NEXT3() {
		//validate the joining code as well as identify student/teacher/admin
		HashMap<String, Integer> temp;
		String t=Signup_joincode.getText().toUpperCase();
		try {
		
		if(Admin.containsJoinCode(t, false)) {
			joincode=t;
			//Admin.serializeJoinCode(temp);
			if(Signup_joincode.getStyleClass().contains("text-field2")) {
				if(!Signup_joincode.getStyleClass().contains("text-field1")) {
					Signup_joincode.getStyleClass().add("text-field1");
				}
				Signup_joincode.getStyleClass().remove("text-field2");
			}
		Signup_joincode.setVisible(false);
		Signup_joincode_btn.setVisible(false);
		Name.setVisible(true);
		if(Signup_joincode.getText().toUpperCase().charAt(0)=='S') { //identifies student
			user=new User(user.getName(), user.getPassword(), user.getEmail(), "Student");
			Branch.setVisible(true);
			Signup_done_btn.setTranslateY(75);
		}
		else if(Signup_joincode.getText().toUpperCase().charAt(0)=='A') {
			user=new User(user.getName(), user.getPassword(), user.getEmail(), "Admin");
		}
		else if(Signup_joincode.getText().toUpperCase().charAt(0)=='F') {
			user=new User(user.getName(), user.getPassword(), user.getEmail(), "Faculty");
		}
		//put else's here if want to associate something with teacher and faculty
		Signup_done_btn.setVisible(true);
		}
		else {
			if(!Signup_joincode.getStyleClass().contains("text-field2")) {
				Signup_joincode.getStyleClass().add("text-field2");
			}
		}
		}

		catch(Exception e) {
			System.out.println("excetpion occured");
		}
	}
	@FXML
	private Label AccCre;
	@FXML
	/**
	 * button handler in sign up pane after entering name and batch(only for students)
	 */
	private void Signup_NEXT4() {
		try {
		if(!Name.getText().equals("") && Name.getText().matches("^[\\p{L} .'-]+$")) {

			if(!Admin.containsJoinCode(joincode, false)) {
				joincode=null;
				backS();
				return;
			}
			Admin.removeJoinCode(joincode, false);
			user=new User(Name.getText().trim(), user.getPassword(), user.getEmail(), user.getUsertype());
			if(Name.getStyleClass().contains("text-field2")) {
				if(!Name.getStyleClass().contains("text-field1")) {
					Name.getStyleClass().add("text-field1");
				}
				Name.getStyleClass().remove("text-field2");
			}
			if(Branch.isVisible()==true){
				user=new Student(user.getName(), user.getPassword(), user.getEmail(), user.getUsertype(), Branch.getValue(), new ArrayList<String>());
			}
			else if(user.getUsertype().equals("Faculty")) {
				user=new Faculty(user.getName(), user.getPassword(), user.getEmail(), user.getUsertype(), new ArrayList<String>());
				}
			else {
				user=new Admin(user.getName(), user.getPassword(), user.getEmail(), user.getUsertype());
			}
			user.serialize(false);
		Signup_TranslateRight();
		AccCre.setVisible(true);
		}
		else {
			if(!Name.getStyleClass().contains("text-field2")) {
				Name.getStyleClass().add("text-field2");
			}
		}
		}
		catch(Exception e) {
			System.out.println("error");
		}
	}
	@FXML
	/**
	 * button effect animation for buttons
	 * @param e
	 */
	private void BtnEffect(MouseEvent e) {
		Button b=(Button)e.getSource();
		Timeline t=new Timeline();
		t.setAutoReverse(true);
		KeyValue k=new KeyValue(b.scaleXProperty(), 1.1);
		KeyFrame f=new KeyFrame(Duration.millis(600),k );
		t.getKeyFrames().add(f);
		t.play();
	}
	@FXML
	/**
	 * button effect animation for buttons
	 * @param e
	 */
	private void BtnEffectRev(MouseEvent e) {
		Button b=(Button)e.getSource();
		Timeline t=new Timeline();
		t.setAutoReverse(true);
		KeyValue k=new KeyValue(b.scaleXProperty(), 1);
		KeyFrame f=new KeyFrame(Duration.millis(600),k );
		t.getKeyFrames().add(f);
		t.play();
	}
	@FXML
	/**
	 * clean login screen informations after switching to signup pane
	 */
	private void CleanLogin() {
		//throw all login info collected so far
		if(email!=null) {
			email=null;
		}
		if(user!=null) {
			user=null;
		}
		AccCre.setVisible(false);
		Login_email.setVisible(true);Login_email.clear();
		Login_password.setVisible(false);Login_password.clear();
		Login_email_btn.setVisible(true);;
		Login_password_label.setVisible(false);Login_password_label.setText(null);
		Login_password_btn.setVisible(false);
		Login_email.getStyleClass().remove("text-field2");
		Login_password.getStyleClass().remove("text-field2");
		if(Login_password.getTranslateY()==(Login_password_btn.getLayoutY()-Login_password_label.getLayoutY())/2) {
			Login_password.setTranslateY(0);
		}
		Lback_btn.setVisible(false);
	}
	@FXML
	/**
	 * clean signup pane info after switching to login pane
	 */
	private void CleanSignup() {     //make sure to throw all signup information collected so
		if(email!=null) {
			email=null;
		}
		if(user!=null) {
			user=null;
		}
		joincode=null;
		Name.clear();					//far
		Name.setVisible(false);
		Name.getStyleClass().remove("text-field2");
		Branch.setVisible(false);
		Signup_done_btn.setVisible(false);
		if(Signup_done_btn.getTranslateY()==75) {
			Signup_done_btn.setTranslateY(0);
		}
		Signup_email.setVisible(true);Signup_email.clear();
		Signup_email.getStyleClass().remove("text-field2");
		Signup_email_btn.setVisible(true);
		Signup_joincode.clear();Signup_joincode.setVisible(false);
		Signup_joincode.getStyleClass().remove("text-field2");
		Signup_joincode_btn.setVisible(false);
		Signup_password.clear();Signup_password.setVisible(false);
		Signup_password.getStyleClass().remove("text-field2");
		CnfPass.clear();CnfPass.setVisible(false);
		CnfPass.getStyleClass().remove("text-field2");
		Signup_password_btn.setVisible(false);
		Sback_btn.setVisible(false);
	}
	@FXML
	/**
	 * translate left animation for signup button
	 */
	private void Signup_TranslateLeft() { 
		Login_btn.setDisable(true);
		Signup_btn.setVisible(false);
		Login_btn.setVisible(true);
		FadeTransition seq1=new FadeTransition();
		seq1.setNode(Login_Pane);
		seq1.setFromValue(Login_Pane.getOpacity());
		seq1.setToValue(0.0);
		seq1.setDuration(Duration.millis(800));
		seq1.play();
		SequentialTransition sequence=new SequentialTransition();
		TranslateTransition transleft=new TranslateTransition();
		transleft.setNode(Signup_Pane);
		transleft.setToX(Login_Pane.getLayoutX()-Signup_Pane.getLayoutX());
		transleft.setDuration(Duration.millis(700));
		sequence.getChildren().add(transleft);
		sequence.play();
		sequence.setOnFinished(e->{
			CleanLogin();
			Login_btn.setDisable(false);
		});
		
		
	}
	
	@FXML
	/**
	 * translate right animation for login button animation
	 */
	private void Signup_TranslateRight() {
		Login_Pane.setVisible(true);
		Signup_btn.setVisible(true);
		Signup_btn.setDisable(true);
		Login_btn.setVisible(false);
		FadeTransition seq1=new FadeTransition();
		seq1.setNode(Login_Pane);
		seq1.setFromValue(Login_Pane.getOpacity());
		seq1.setToValue(initOpacity);
		seq1.setDuration(Duration.millis(800));
		seq1.play();
		SequentialTransition sequence=new SequentialTransition();
		TranslateTransition transright=new TranslateTransition();
		transright.setNode(Signup_Pane);
		transright.setToX(0);
		transright.setDuration(Duration.millis(700));
		sequence.getChildren().add(transright);
		sequence.play();
		sequence.setOnFinished(e->{
			CleanSignup();
			Login_Pane.setVisible(true);
			Signup_btn.setDisable(false);
		});
		
		
	}
}
class PortListener implements Runnable{
	static String Name=null;
	static String email=null;
	static String authcode="none";
	static ServerSocket serversocket = null;
	static Socket sock=null;
	static String webURL1 ="https://accounts.google.com/o/oauth2/v2/auth?\r\n" + 
			"scope=https://www.googleapis.com/auth/userinfo.email%20profile&\r\n" + 
			"response_type=code&\r\n" +  
			"redirect_uri=http://127.0.0.1:9004&\r\n" + 
			"client_id=675553038343-joaegqsglukqdti0ukkga8in6st1gl3k.apps.googleusercontent.com";
	static String webURL2 ="none";
	Thread t;
	public static void closeSocket() {
		if(sock!=null) {
			try {
			sock.close();}
			catch(Exception e) {
				System.out.println("error");
			}
		}
	}
	public static ServerSocket needSocket() {
		return serversocket;
	}
	public void getPort() {
		if(serversocket==null) {
			try {
				serversocket=new ServerSocket(9004);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("server already in use");
			}
		}
		
	}
	public PortListener() {
		// TODO Auto-generated constructor stub
		getPort();
		t=new Thread(this);
		t.start();
	}
	public void startAuth() {
		try {
			//serversocket = new ServerSocket(9004);
			try {
				sock=serversocket.accept();
			}
			catch(Exception e) {
				;
			}
			if(sock!=null) {
			BufferedReader in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			authcode=in.readLine();
			if(authcode.contains("denied")) {
				authcode="denied";
				System.out.println("denied");
				//serversocket.close();
				return;
			}
			else {
				//System.out.println(authcode);
				authcode=authcode.substring(11,authcode.length()-9);
				//System.out.println(authcode);
							}
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("https://www.googleapis.com/oauth2/v4/token");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(5);
			params.add(new BasicNameValuePair("code",authcode+"&" ));
			params.add(new BasicNameValuePair("client_id", "675553038343-joaegqsglukqdti0ukkga8in6st1gl3k.apps.googleusercontent.com"));
			params.add(new BasicNameValuePair("client_secret", "p8m6oBuodC2IyT1PaalafAHi"));
			params.add(new BasicNameValuePair("redirect_uri", "http://127.0.0.1:9004"));
			params.add(new BasicNameValuePair("grant_type", "authorization_code"));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			//Execute and get the response.
			org.apache.http.HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
			    InputStream instream = entity.getContent();
			    
			    try {
			    java.util.Scanner s = new java.util.Scanner(instream).useDelimiter("\\A");
			    String result = s.hasNext() ? s.next() : "";
			    //System.out.println(result);
			    String accesstoken = (result.split("\n"))[1].substring(18, (result.split("\n"))[1].length()-2);		
			    //System.out.println(accesstoken);
			    String url = "https://www.googleapis.com/userinfo/v2/me?access_token="+accesstoken;
			    String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
			    URLConnection connection = new URL(url).openConnection();
			    connection.setRequestProperty("Accept-Charset", charset);
			    InputStream responseID = connection.getInputStream();
			    s = new java.util.Scanner(responseID).useDelimiter("\\A");
			    String result2 = s.hasNext() ? s.next() : "";
			    //System.out.println(result2);
			    String[] finalAns=result2.split("\n");
			    Name = finalAns[4].substring(10, finalAns[4].length()-2);
			    email = finalAns[2].substring(11, finalAns[2].length()-2);
			    System.out.println(Name);
			    System.out.println(email);
			    }
			    	// do something useful
			     finally {
			    	instream.close();
			    }
			}
			//serversocket.close();
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			;
		}

	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		startAuth();
				
	}}

