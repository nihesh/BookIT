package HelperClasses;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import HelperClasses.Course;
public class Student extends User{
	private static final long serialVersionUID = 1L;
	protected String Batch;
	private ArrayList<String> myCourses;
	public Student(String name, String password, Email emailID, String userType, String batch,
			ArrayList<String> myCourses) {
		super(name, password, emailID, userType);
		Batch = batch;
		this.myCourses = myCourses;
	}
	@SuppressWarnings("unchecked")
	public boolean sendReservationRequest(ArrayList<Reservation> r) throws IOException, ClassNotFoundException{
		PriorityQueue<ArrayList<Reservation>> p=null;
		ObjectInputStream in=null;
		ObjectOutputStream out=null;
		try
	        {   
			 in = new ObjectInputStream(new FileInputStream("./src/AppData/Requests/requests.txt"));
	         p = ((PriorityQueue<ArrayList<Reservation>>)in.readObject());  
	         p.add(r);
	         out = new ObjectOutputStream(new FileOutputStream("./src/AppData/Requests/requests.txt"));
	         out.writeObject(p);
	         }
		
		 finally {
			 if(in!=null) {
				 in.close();
				 }
			 if(out!=null) {
				 out.close();
			 }
			 
		 }
		return true;
	}
	//marker need to check
	public static ArrayList<String> searchCourse(ArrayList<String> keyword){
		ArrayList<ArrayList<String>> arr=new ArrayList<ArrayList<String>>();
		for (ArrayList<String> arrayList : arr) {
			arrayList=new ArrayList<String>();
		}
		ArrayList<String> temp2=new ArrayList<String>();
		File directory= new File("./src/AppData/Course");
		File[] courseFiles=directory.listFiles();
		for(int i=0;i<courseFiles.length;i++) {
			String courseName = courseFiles[i].getName().substring(0,courseFiles[i].getName().length()-4);
			Course temp=Course.deserializeCourse(courseName);
			int match=temp.keyMatch(keyword);
			if(match > 0) {
				arr.get(match).add(courseFiles[i].getName());
			}
		}
		for(int i=arr.size()-1;i>=0;i--) {
			for (String str : arr.get(i)) {
				temp2.add(str);
			}
		}
		return temp2;
	}
	public boolean addCourse(String c) {
		Course c2=Course.deserializeCourse(c);
		for (String string : myCourses) {
			Course temp=Course.deserializeCourse(string);
			if(!c2.checkCollision(temp)) {
				return false; //cannot add course since there is a collision
			}
		}
		myCourses.add(c);
		return true;
	}
	public String getBatch() {
		return Batch;
	}
	public ArrayList<String> getMyCourses() {
		return myCourses;
	}
	
	
}
