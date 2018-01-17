package LoginSignup;


import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.com.google.api.services.samples.oauth2.cmdline.OAuth2Sample;
/**
 * controller class for login/signup gui
 * @author Harsh
 *
 */
public class LoginSignup1Controller {
	private double initOpacity=0.84;
	private double init_Cred_Pane;
	private Email Gemail;
	private String GName;
	private User Guser;
	
	private Email email;
	private String joincode;
	private User user;
	@FXML
	private VBox Login_Pane;
	@FXML
	private Button GBtn;
	@FXML
	private StackPane FirstPane;
	@FXML
	private Button Cred_btn;
	@FXML
	private Button CredBack_btn;
	@FXML 
	private StackPane GPane;
	@FXML
	private WebView browser;
	@FXML
	private Button Gdone;
	@FXML
	private Button Gcancel;
	@FXML
	public WebEngine e;
	@FXML
	private StackPane Cred_Pane;
	@FXML
	private Label footer1;
	@FXML
	private Label footer2;
	@FXML
	private void Press_Cred() {
		Login_Pane.setDisable(true);
		Cred_Pane.setVisible(true);
		TranslateTransition transright=new TranslateTransition();
		//System.out.println(Cred_Pane.getTranslateX());
		transright.setNode(Cred_Pane);
		transright.setToX(0);
		transright.setDuration(Duration.millis(700));
		transright.play();
	}
	@FXML
	private void Press_Cred_Back() {
		//Cred_Pane.setVisible(false);
		Login_Pane.setDisable(false);
		TranslateTransition transleft=new TranslateTransition();
		transleft.setNode(Cred_Pane);
		transleft.setToX(init_Cred_Pane);
		transleft.setDuration(Duration.millis(700));
		transleft.play();
		transleft.setOnFinished(e->{
			Cred_Pane.setVisible(true);
		});
	}
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
		if(e!=null && !PortListener.authcode.equals("none") && PortListener.getStatus().equals("Updated")) {
			e.load("");
			GPane.setVisible(false);
			if(PortListener.authcode.equals("denied")) {
				PortListener.authcode="none";
				System.out.println("should never happen");
				java.net.CookieManager manager = new java.net.CookieManager();
				java.net.CookieHandler.setDefault(manager);
			}
			if(PortListener.email!=null && PortListener.email.contains("iiitd.ac.in")) {
				Gemail = new Email(PortListener.email);
				GName=PortListener.Name;
				User inQ = User.getUser(Gemail.getEmailID(), false); 
				System.out.println(Gemail.getEmailID());
				if(inQ != null) {
					//System.out.println("account already exists");
					java.net.CookieManager manager = new java.net.CookieManager();
					java.net.CookieHandler.setDefault(manager);
					PortListener.authcode="none";
					PortListener.email=null;PortListener.Name=null;
					PortListener.status="NotUpdated";
					inQ.setActiveUser();
					Stage stage = (Stage) GBtn.getScene().getWindow();
					stage.close();
					return;
				}
				String usertype = User.getUserType(Gemail.getEmailID(), false);
				System.out.println(usertype);
				if(usertype==null) {
					java.net.CookieManager manager = new java.net.CookieManager();
					java.net.CookieHandler.setDefault(manager);
					PortListener.authcode="none";
					PortListener.email=null;PortListener.Name=null;
					PortListener.status="NotUpdated";
					GName=null;Gemail=null;Guser=null;
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("You can't have access to the app. Please contact Admin for this");
					alert.showAndWait();
					return;
					
				}
				if(usertype.equals("Faculty")) {
					Guser = new Faculty(GName, null, Gemail, "Faculty", new ArrayList<String>());
				}
				
				else if(usertype.equals("Admin")){
					java.net.CookieManager manager = new java.net.CookieManager();
					java.net.CookieHandler.setDefault(manager);
					PortListener.authcode="none";
					PortListener.email=null;PortListener.Name=null;
					PortListener.status="NotUpdated";
					Admin noreply =(Admin) User.getUser(HelperClasses.BookITconstants.NoReplyEmail, false);
					noreply.setActiveUser();
					Stage stage = (Stage) GBtn.getScene().getWindow();
					stage.close();
					return;
				}
				else if(usertype.equals("Student")) {
					Guser = new Student(GName, null, Gemail, "Student", "", new ArrayList<String>());
				}
				Guser.serialize(false);
				Guser.setActiveUser();
				java.net.CookieManager manager = new java.net.CookieManager();
				java.net.CookieHandler.setDefault(manager);
				Stage stage = (Stage) GBtn.getScene().getWindow();
				stage.close();
				//Guser.generatePass(false);
				//Guser.mailPass(false);
				/*Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Account Created Successfully. Use google sign in button again to gain access to account");
				alert.showAndWait();*/
				return;
			}
			java.net.CookieManager manager = new java.net.CookieManager();
			java.net.CookieHandler.setDefault(manager);
			//System.out.println("wrong creds");
			//give some indication
			PortListener.authcode="none";
			PortListener.email=null;PortListener.Name=null;
			PortListener.status="NotUpdated";
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
		alert.setContentText("Please fill your complete info, press Accept and then press DONE Button");
		alert.showAndWait();
	}
	
	
	@FXML
	public void initialize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double scaleWidth = (width)/1366;
        double scaleHeight = (height)/768;
        FirstPane.setScaleX(scaleWidth);
        FirstPane.setScaleY(scaleHeight);
        Cred_Pane.setTranslateX((width)/1366.0 * Cred_Pane.getTranslateX());
        init_Cred_Pane = Cred_Pane.getTranslateX();
        //footer1.setScaleX((width)/1366.0 * footer1.getWidth());
       // footer2.setScaleX((width)/1366.0 * footer2.getWidth());
       // footer1.setScaleX((width)/768.0 * footer1.getHeight());
       // footer2.setScaleX((width)/768.0 * footer2.getHeight());

			
	}
	@FXML
	private void PressG() {
		GPane.setVisible(true);
		e=browser.getEngine();
		e.load(PortListener.webURL1);
		PortListener p= new PortListener();	
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
	}
class PortListener implements Runnable{
	static String status = "NotUpdated";
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
	public static String getStatus() {
		return status;
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
			}
			if(sock!=null) {
			BufferedReader in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			authcode=in.readLine();
			if(authcode.contains("denied")) {
				authcode="none";
				PrintWriter out = new PrintWriter(sock.getOutputStream());
			    out.println("HTTP/1.1 200 OK");
			    out.println("Content-Type: text/html");
			    out.println("\r\n");
			    out.println("<p>Access denied by user. Please click back button/p>");
			    out.flush();
			    out.close();
				java.net.CookieManager manager = new java.net.CookieManager();
				java.net.CookieHandler.setDefault(manager);
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
			    //System.out.println(Name);
			    //System.out.println(email);
			    status = "Updated";
			    PrintWriter out = new PrintWriter(sock.getOutputStream());
			    out.println("HTTP/1.1 200 OK");
			    out.println("Content-Type: text/html");
			    out.println("\r\n");
			    out.println("<p>Authentication Successful. Please click done button to proceed</p>");
			    out.flush();
			    out.close();
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

