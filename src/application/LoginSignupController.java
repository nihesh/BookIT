package application;


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

public class LoginSignupController{
	double initOpacity=0.84;
	int Signup_TransX=570;
	
	@FXML
		private ComboBox<String> Branch;
	@FXML
		private TextField Name;
	@FXML
	private PasswordField CnfPass;
	@FXML
		private AnchorPane OuterSign_Login;
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
		// TODO Auto-generated method stub
		Branch.getItems().removeAll(Branch.getItems());
		Branch.getItems().addAll("Option A", "Option B", "Option C");
	    Branch.getSelectionModel().select("Option B");
	}
	@FXML
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
	private void backS() {
		if(Signup_password.isVisible()==true) {
			System.out.println(1);
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
			System.out.println(2);
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
			System.out.println(3);
			Signup_joincode.clear();
			Signup_joincode.setVisible(false);
			Branch.setVisible(false);
			Signup_joincode.getStyleClass().remove("text-field2");
			Name.getStyleClass().remove("text-field2");
			Name.setVisible(false);
			Signup_done_btn.setVisible(false);
			if(Signup_done_btn.getTranslateY()==35) {
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
	private void Login_NEXT() {
			//put some email validate code here to check user email
		AccCre.setVisible(false);
		if(!Login_email.getText().equals("")) {
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
			t2.setByY(30);
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
				if(!Login_email.getStyleClass().contains("text-field2")) {
					Login_email.getStyleClass().add("text-field2");
				}
				
			}
			
		
	}
	@FXML
	private void Login_NEXT2() {
		if(!Login_password.getText().equals("")) {
			if(Login_password.getStyleClass().contains("text-field2")) {
				Login_password.getStyleClass().remove("text-field2");
				if(!Login_password.getStyleClass().contains("text-field1")) {
					Login_password.getStyleClass().add("text-field1");
				}
			}
			Login_password.clear();
			Login_password.setTranslateY(0);
			 Stage stage = (Stage) Login_password_btn.getScene().getWindow();
			 stage.close();
		}
		else {
			if(!Login_password.getStyleClass().contains("text-field2")) {
				Login_password.getStyleClass().add("text-field2");
			}
			
		}
	}
	@FXML
	private void Signup_NEXT() {
		
		//put some email validate code here to check user email
		if(!Signup_email.getText().equals("")) {
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
		if(!Signup_email.getStyleClass().contains("text-field2")) {
			Signup_email.getStyleClass().add("text-field2");
		}
	}
	
	@FXML
	private void Signup_NEXT2() {
		//put some email validate code here to check user email
		if((!Signup_password.getText().equals("")) && Signup_password.getText().equals(CnfPass.getText())) {
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
			if(!Signup_password.getStyleClass().contains("text-field2")) {
				Signup_password.getStyleClass().add("text-field2");
			
			}
			if(!CnfPass.getStyleClass().contains("text-field2")) {
				CnfPass.getStyleClass().add("text-field2");
			
			}
		}
	}
	@FXML
	private void Signup_NEXT3() {
		//validate the joining code as well as identify student/teacher/admin
		if(!Signup_joincode.getText().equals("")) {
			if(Signup_joincode.getStyleClass().contains("text-field2")) {
				if(!Signup_joincode.getStyleClass().contains("text-field1")) {
					Signup_joincode.getStyleClass().add("text-field1");
				}
				Signup_joincode.getStyleClass().remove("text-field2");
			}
		Signup_joincode.setVisible(false);
		Signup_joincode_btn.setVisible(false);
		Name.setVisible(true);
		if(Signup_joincode.getText().equals("1")) { //identifies student
			Branch.setVisible(true);
			Signup_done_btn.setTranslateY(35);
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
	@FXML
	private Label AccCre;
	@FXML
	private void Signup_NEXT4() {
		if(!Name.getText().equals("")) {
			if(Name.getStyleClass().contains("text-field2")) {
				if(!Name.getStyleClass().contains("text-field1")) {
					Name.getStyleClass().add("text-field1");
				}
				Name.getStyleClass().remove("text-field2");
			}
		Signup_TranslateRight();
		AccCre.setVisible(true);
		}
		else {
			if(!Name.getStyleClass().contains("text-field2")) {
				Name.getStyleClass().add("text-field2");
			}
		}
	}
	@FXML
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
	private void CleanLogin() {         //throw all login info collected so far
		Login_email.setVisible(true);Login_email.clear();
		Login_password.setVisible(false);Login_password.clear();
		Login_email_btn.setVisible(true);;
		Login_password_label.setVisible(false);Login_password_label.setText(null);
		Login_password_btn.setVisible(false);
		Login_email.getStyleClass().remove("text-field2");
		Login_password.getStyleClass().remove("text-field2");
		if(Login_password.getTranslateY()==30) {
			Login_password.setTranslateY(0);
		}
		Lback_btn.setVisible(false);
	}
	@FXML
	private void CleanSignup() {     //make sure to throw all signup information collected so
		Name.clear();					//far
		Name.setVisible(false);
		Name.getStyleClass().remove("text-field2");
		Branch.setVisible(false);
		Signup_done_btn.setVisible(false);
		if(Signup_done_btn.getTranslateY()==35) {
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
		//System.out.println(Signup_Pane.getLayoutX());
		transleft.setToX(Login_Pane.getLayoutX()-Signup_Pane.getLayoutX());
		transleft.setDuration(Duration.millis(1000));
		sequence.getChildren().add(transleft);
		sequence.play();
		sequence.setOnFinished(e->{
			
			//Login_Pane.setVisible(false);
			CleanLogin();
			Login_btn.setDisable(false);
		});
		
		
	}
	
	@FXML
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
		//System.out.println(Signup_Pane.getLayoutX());
		transright.setToX(Signup_TransX);
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
