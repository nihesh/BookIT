package HelperClasses;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Math.max;

/**
 * Created by nihesh on 10/11/17.
 */
public class SpamFilter {
    private static Double tolerance = 0.3;
    public static HashMap<String, Boolean> dict;

    public static int totalSpam;
    public static int totalHam;
    private static HashMap<String, Integer> SpamData = new HashMap<>();
    private static HashMap<String, Integer> HamData = new HashMap<>();

    public SpamFilter(){
        totalHam = 0;
        totalSpam = 0;
        try {
            System.out.println("Setting up spam filter... ");
            dict = new HashMap<>();
            Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/SpamFilterDataset/dictionary.txt")));
            while (sc.hasNext()) {
                String word = sc.next();
                dict.put(word.toLowerCase(), true);
            }
            System.out.println("Spam filter has been set up.");
        }
        catch (FileNotFoundException f){
            System.out.println("Exception occured in spam filter's constructor");
        }
        try{
            for(int i=1;;i++) {
                Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/SpamFilterDataset/Ham/"+Integer.toString(i)+".txt")));
                HashMap<String, Boolean> temp = new HashMap<>();
                while(sc.hasNext()){
                    String word = sc.next().toLowerCase();
                    if(!temp.containsKey(word)){
                        temp.put(word, true);
                        if(!HamData.containsKey(word)){
                            HamData.put(word,0);
                            SpamData.put(word,0);
                        }
                        HamData.put(word,HamData.get(word)+1);
                        totalHam++;
                    }
                }
            }
        }
        catch (FileNotFoundException f1){
            ;
        }
        try{
            for(int i=1;;i++) {
                Scanner sc = new Scanner(new BufferedReader(new FileReader("./src/AppData/SpamFilterDataset/Spam/"+Integer.toString(i)+".txt")));
                HashMap<String, Boolean> temp = new HashMap<>();
                while(sc.hasNext()){
                    String word = sc.next().toLowerCase();
                    if(!temp.containsKey(word)){
                        temp.put(word, true);
                        if(!SpamData.containsKey(word)){
                            HamData.put(word,0);
                            SpamData.put(word,0);
                        }
                        SpamData.put(word,SpamData.get(word)+1);
                        totalSpam++;
                    }
                }
            }
        }
        catch (FileNotFoundException f2){
            ;
        }
    }
    public static Boolean Predict(String raw_message){            // Returns true if spam
        String[] processedMessage = raw_message.split("\\s+");
        ArrayList<String> message = new ArrayList<>();
        for(int i=0;i<processedMessage.length;i++){
            message.add(processedMessage[i]);
        }
        double spamLoglikelihood = Math.log10(totalSpam)-Math.log10(totalSpam+totalHam);               // Bayesian priors
        double hamLoglikelihood = Math.log10(totalSpam)-Math.log10(totalSpam+totalHam);
        int uncertainity = 0;
        for(int i=0;i<message.size();i++){
            if(!SpamData.containsKey(message.get(i).toLowerCase())){
                uncertainity++;
            }
        }
        double percUncertainity = uncertainity/message.size();
        if(!isTolerable(message)){
            return false;
        }
        else if(percUncertainity >= 0.3){
            return true;
        }
        for(int i=message.size()-1;i>=0;i--){
            if(!SpamData.containsKey(message.get(i).toLowerCase())){
                message.remove(i);
            }
        }
        double minFrequency=0.0001;
        for(int i=0;i<message.size();i++){
            spamLoglikelihood+=Math.log10(max(minFrequency,SpamData.get(message.get(i).toLowerCase()))) - Math.log10(totalSpam);
            hamLoglikelihood+=Math.log10(max(minFrequency,HamData.get(message.get(i).toLowerCase()))) - Math.log10(totalHam);
        }
        return spamLoglikelihood>hamLoglikelihood;
    }
    private static int getTypos(ArrayList<String> query){
        int count=0;
        for(int i=0;i<query.size();i++){
            if(!dict.containsKey(query.get(i).toLowerCase())){
                count++;
            }
        }
        return count;
    }
    private static Boolean isTolerable(ArrayList<String> query){
        double t = getTypos(query)/(query.size());
        return t<=tolerance;
    }
}
