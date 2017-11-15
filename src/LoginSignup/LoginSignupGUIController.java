package LoginSignup;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * controller class for login/signup gui
 * @author Harsh
 *
 */
public class LoginSignupGUIController {
	private double initOpacity=0.84;
	private Email email;
	private String joincode;
	private User user;
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
		if(!Login_email.getText().equals("") && email.validateLogin()) {
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
		user=User.getUser(email.getEmailID(),false);
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
		int state=email.validateSignup();
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
		if((Signup_password.getText().matches("[A-Za-z0-9]+")) && Signup_password.getText().equals(CnfPass.getText())) {
			user=new User(" ", CnfPass.getText(), email, " ");
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
			temp = Admin.deserializeJoinCodes(false);
		
		if(temp.containsKey(t)) {
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
			
			HashMap<String,Integer> temp = Admin.deserializeJoinCodes(true);
			if(!temp.containsKey(joincode)) {
				
				joincode=null;
				backS();
				return;
			}
			Integer x=temp.remove(joincode);
			Admin.serializeJoinCode(temp, false);
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
			user.serialize(true);
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
		seq1.setDuration(Duration.millis(1500));
		seq1.play();
		SequentialTransition sequence=new SequentialTransition();
		TranslateTransition transleft=new TranslateTransition();
		transleft.setNode(Signup_Pane);
		transleft.setToX(Login_Pane.getLayoutX()-Signup_Pane.getLayoutX());
		transleft.setDuration(Duration.millis(1000));
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
		seq1.setDuration(Duration.millis(1500));
		seq1.play();
		SequentialTransition sequence=new SequentialTransition();
		TranslateTransition transright=new TranslateTransition();
		transright.setNode(Signup_Pane);
		transright.setToX(0);
		transright.setDuration(Duration.millis(1000));
		sequence.getChildren().add(transright);
		sequence.play();
		sequence.setOnFinished(e->{
			CleanSignup();
			Login_Pane.setVisible(true);
			Signup_btn.setDisable(false);
		});
		
		
	}
}
