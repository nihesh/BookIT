package LoginSignup;


import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.Port;
import javax.swing.JOptionPane;

import AdminReservation.AdminReservationGUIController;
import HelperClasses.*;
import javafx.scene.input.KeyEvent;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * controller class for login/signup gui
 * @author Harsh
 *
 */
public class LoginSignupGUIController {
	private double initOpacity=0.84;
	private double init_Cred_Pane;
	private Email Gemail;
	private String GName;
	private User Guser;

	@FXML
	private Button GBtn;
	@FXML
	private StackPane FirstPane;
	@FXML 
	private StackPane GPane;
	@FXML
	private WebView browser;
	@FXML
	public WebEngine e;
	@FXML
	private StackPane Cred_Pane;
	@FXML
	void keyPressed(KeyEvent event) {
		switch(event.getCode()) {
		case ESCAPE:
			if(Cred_Pane.isVisible() == true) {
				Press_Cred_Back();
			}
			else if(GPane.isVisible() == true) {
				Close();
			}
			break;
		case V:
			if(Cred_Pane.isVisible() == false && GPane.isVisible() == false) {
				PressG();
			}
			else if(GPane.isVisible() == true) {
				Continue();
			}
		case C:
			if(Cred_Pane.isVisible() == false && GPane.isVisible() == false) {
				Press_Cred();
			}
		default:
			break;
		}
	}
	public static boolean pingHost(String host, int port, int timeout){
		try{
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host, port),  timeout);
			return true;
		}
		catch(IOException e){
			return false;
		}
	}
	@FXML
	private void Press_Cred() {
		Cred_Pane.setVisible(true);
		FadeTransition fadein = new FadeTransition(Duration.millis(500), Cred_Pane);
		fadein.setFromValue(0);
		fadein.setToValue(1);
		fadein.play();
		
		
	}
	@FXML
	private void Press_Cred_Back() {
		FadeTransition fadeout = new FadeTransition(Duration.millis(500), Cred_Pane);
		fadeout.setFromValue(1);
		fadeout.setToValue(0);
		fadeout.play();
		fadeout.setOnFinished(e->{
			Cred_Pane.setVisible(false);
				
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
				java.net.CookieManager manager = new java.net.CookieManager();
				java.net.CookieHandler.setDefault(manager);
			}
			if(PortListener.email!=null) {
				Gemail = new Email(PortListener.email);
				GName=PortListener.Name;
				User inQ = User.getUser(Gemail.getEmailID(), false); 
				if(inQ != null) {
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
				if(usertype==null) {
					java.net.CookieManager manager = new java.net.CookieManager();
					java.net.CookieHandler.setDefault(manager);
					PortListener.authcode="none";
					PortListener.email=null;PortListener.Name=null;
					PortListener.status="NotUpdated";
					GName=null;Gemail=null;Guser=null;
					Notification.throwAlert("Error", "You don't have access to the application. Kindly contact admin to gain access");
					return;
					
				}
				if(usertype.equals("Faculty")) {
					Guser = new Faculty(GName, null, Gemail, "Faculty", new ArrayList<String>());
				}
				
				else if(usertype.equals("Admin")){
					File file = new File("./src/AppData/ActiveUser/Email.txt");
					try {
						PrintWriter ex = new PrintWriter(file);
						ex.println(Gemail.getEmailID());
						ex.flush();
						ex.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
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
				return;
			}
			java.net.CookieManager manager = new java.net.CookieManager();
			java.net.CookieHandler.setDefault(manager);
			PortListener.authcode="none";
			PortListener.email=null;PortListener.Name=null;
			PortListener.status="NotUpdated";
			GName=null;Gemail=null;Guser=null;
			Notification.throwAlert("Error", "Please use a IIITD account");
			return;
		}
		Notification.throwAlert("Invalid Action","Kindly authenticate using valid google email and password, press Allow and then press DONE Button");
	}
	
	
	@FXML
	public void initialize() {
		AdminReservationGUIController.admin_email_used = null;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double scaleWidth; 
        double scaleHeight;
        if(width <= 1300) {
        	scaleWidth = (width)/800;
            scaleHeight = (height)/600;
        	browser.setZoom(0.65);
        }
        else {
        	scaleWidth = (width)/1366;
            scaleHeight = (height)/768;
        	 browser.setZoom(0.8);
       
        }
        FirstPane.setScaleX(scaleWidth);
        FirstPane.setScaleY(scaleHeight);
        //Cred_Pane.setTranslateX((width)/1366.0 * Cred_Pane.getTranslateX());
        init_Cred_Pane = Cred_Pane.getTranslateX();
		PortListener.fill_ID_Secret();
			
	}
	@FXML
	private void PressG() {
		GPane.setVisible(true);
		if(e == null){
			e = browser.getEngine();
		}
		e.load(PortListener.webURL1);
		PortListener p= new PortListener();
		if(pingHost("www.google.com", 80, 2000) == false){
			Notification.throwAlert("Error", "It seems that your internet connection is slow or not working. Please try again");
			Close();
			return;
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
	}
class PortListener implements Runnable{
	static String status = "NotUpdated";
	static String Name=null;
	static String email=null;
	static String authcode="none";
	static ServerSocket serversocket = null;
	static Socket sock=null;
	static String clientID = null;
	static String clientSecret = null;
	static String apiScope = "https://www.googleapis.com/auth/userinfo.email%20profile";
	static String webURL1 = null;
	private volatile static Thread t = null;
	public static void closeSocket() {
		if(sock!=null) {
			System.out.println("enter");
			try {
				sock.close();
			}
			catch(Exception e) {
				System.out.println("error");
			}
		}
		if(serversocket != null){
			try {
				serversocket.close();
				t = null;
			}
			catch(Exception e) {
				System.out.println(e);
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
		if(serversocket == null) {
			try {
				serversocket=new ServerSocket(9004);
			} catch (IOException e) {
				Notification.throwAlert("Multiple Client Error", "Kindly close the previous session of the app and try again");
				// TODO Auto-generated catch block
			}
		}
		
	}
	public static void fill_ID_Secret(){
		Scanner sc = null;
		try {
			sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/GoogleAPIConsole/ApiInfo.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		clientID = sc.next();
		clientSecret = sc.next();
		webURL1 = "https://accounts.google.com/o/oauth2/v2/auth?scope=" + apiScope + "&access_type=offline&redirect_uri=" + "http://127.0.0.1:9004" + "&response_type=code&client_id=" + clientID;
	}
	public PortListener() {
		// TODO Auto-generated constructor stub
		getPort();
		t = new Thread(this);
		t.start();
	}
	public void startAuth() {
		try {
			try {
				sock = serversocket.accept();
			}
			catch(Exception e) {
			}
			if(sock!=null) {
			BufferedReader in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			authcode=in.readLine();
			if(authcode == null) {
				return;
			}
			if(authcode.contains("denied")) {
				authcode="none";
				PrintWriter out = new PrintWriter(sock.getOutputStream());
			    out.println("HTTP/1.1 200 OK");
			    out.println("Content-Type: text/html");
			    out.println("\r\n");
			    out.println("<p>Access denied by user. Please click back button</p>");
			    out.flush();
			    out.close();
				java.net.CookieManager manager = new java.net.CookieManager();
				java.net.CookieHandler.setDefault(manager);
				return;
			}
			else {
				URLDecoder decoder = new URLDecoder();
				authcode = decoder.decode(authcode, "utf-8");
				//System.out.println(authcode);
				int sindex = authcode.indexOf("code=");
				int eindex = authcode.indexOf("&scope");
				sindex += 5;
				authcode = authcode.substring(sindex, eindex);
			}
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("https://www.googleapis.com/oauth2/v4/token");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(5);
			params.add(new BasicNameValuePair("code",authcode ));
			params.add(new BasicNameValuePair("client_id", clientID));
			params.add(new BasicNameValuePair("client_secret", clientSecret));
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
			    List<String> allMatches = new ArrayList<String>();
			    Pattern pattern = Pattern.compile("\"[A-Za-z0-9-._~+/]+\"");
			    Matcher match_obj = pattern.matcher(result);
			    while(match_obj.find()){
			    	allMatches.add(match_obj.group());
				}
			    String accesstoken = allMatches.get(1).substring(1, allMatches.get(1).length() - 1);
			    //System.out.println(accesstoken);
			    String url = "https://www.googleapis.com/userinfo/v2/me?access_token="+accesstoken;
			    String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
			    URLConnection connection = new URL(url).openConnection();
			    connection.setRequestProperty("Accept-Charset", charset);
			    InputStream responseID = connection.getInputStream();
			    s = new java.util.Scanner(responseID).useDelimiter("\\A");
			    String result2 = s.hasNext() ? s.next() : "";
				String []splitfields = result2.split("[,:\n]");
			    Name = splitfields[11].substring(2, splitfields[11].length() - 1);
			    email = splitfields[5].substring(2, splitfields[5].length() - 1);
			    status = "Updated";
			    PrintWriter out = new PrintWriter(sock.getOutputStream());
			    out.println("HTTP/1.1 200 OK");
			    out.println("Content-Type: text/html");
			    out.println("\r\n");
			    out.println("<p>Authentication Successful. Please click done button to proceed</p>");
			    out.flush();
			    out.close();
			    }
			     finally {
			    	instream.close();
			    }
			}
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			;
		}

	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(PortListener.t != null){
			startAuth();
		}

	}}

