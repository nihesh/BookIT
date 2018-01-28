package HelperClasses;

import java.io.*;
import java.net.Socket;
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
    public static FileWriter log;
    public static FileWriter transactions;

    public static void writeTransaction(String s){
        try {
            transactions.write(s + "\n");
            transactions.flush();
        }
        catch(Exception e){
            writeLog("Transaction writing failed");
            writeLog(e.getMessage());
        }
    }
    public static void writeLog(String s){
        try {
            log.write(s + "\n");
            log.flush();
        }
        catch(Exception e){
            ; // Ignored as it fails only during setup time
        }
    }
    
    
    public BookITconstants(String mode){
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/Server/ServerInfo.txt")));
            serverIP = sc.next();
            serverPort = Integer.parseInt(sc.next());
            if(mode.equals("Server")) {
                sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/StaticTimeTable/NoReply.txt")));
                NoReplyEmail = sc.next();
                NoReplyUsername = sc.next();
                NoReplyPassword = sc.next();
            }
            else{
                try{
                    Socket server = new Socket(BookITconstants.serverIP, BookITconstants.serverPort);
                    ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(server.getInputStream());
                    out.writeObject("Pass");
                    out.flush();
                    out.writeObject("adminEmail");
                    out.flush();
                    NoReplyEmail = (String) in.readObject();
                    out.close();
                    in.close();
                    server.close();
                }
                catch(Exception e){
                    ;
                }
            }
        }
        catch(FileNotFoundException f){
            ;
        }
    }
}
