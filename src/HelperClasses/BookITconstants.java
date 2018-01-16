package HelperClasses;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * contains information of server private IP address and the port. Connects to the server
 * @author Nihesh Anderson
 * @since 12/11/2017
 */
public class BookITconstants {
    public static String serverIP;
    public static int serverPort;
    public static String NoReplyEmail;
    public static String NoReplyPassword;
    public static String NoReplyUsername;
    
    
    public BookITconstants(){
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/Server/ServerInfo.txt")));
            serverIP = sc.next();
            serverPort = Integer.parseInt(sc.next());
            sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/StaticTimeTable/NoReply.txt")));
            NoReplyEmail = sc.next();
            NoReplyUsername = sc.next();
            NoReplyPassword = sc.next();
        }
        catch(FileNotFoundException f){
            ;
        }
    }
}
