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
	public boolean sendReservationRequest(ArrayList<Reservation> r){
		try {
			PriorityQueue<ArrayList<Reservation>> p = null;
				p = Admin.deserializeRequestsQueue(true);
				p.add(r);
				Admin.serializeRequestsQueue(p, false);
			
		}
		catch(IOException e){
			System.out.println("IO Exception while deserialising priority queue");
		}
		catch (ClassNotFoundException f){
			System.out.println("Class not found exception while deserialising priority queue");
		}
		return true;
	}
	//marker need to check
	public static ArrayList<String> searchCourse(ArrayList<String> keyword){
		int flag=1;
		for(int i=0;i<keyword.size();i++){
			if(!keyword.get(i).equals("")){
				flag=0;
				break;
			}
		}
		if(flag==1){
			return Course.getAllCourses();
		}
		ArrayList<ArrayList<String>> arr=new ArrayList<ArrayList<String>>();
		for (int i=0;i<300;i++) {
			arr.add(new ArrayList<String>());
		}
		
		ArrayList<String> temp2=new ArrayList<String>();
		
		ArrayList<String> courseFiles=Course.getAllCourses();
		for(int i=0;i<courseFiles.size();i++) {
			String courseName = courseFiles.get(i).substring(0,courseFiles.get(i).length());
			Course temp=Course.deserializeCourse(courseName, false);
			int match=temp.keyMatch(keyword);
			if(match > 0) {
				arr.get(match).add(courseFiles.get(i).substring(0,courseFiles.get(i).length()));
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
		Course c2=Course.deserializeCourse(c, false);
		for (String string : myCourses) {
			Course temp=Course.deserializeCourse(string, false);
			if(c2.checkCollision(temp)) {
				return false; //cannot add course since there is a collision
			}
		}
		myCourses.add(c);
		this.serialize(false);
		this.setActiveUser();
		return true;
	}
	public String getBatch() {
		return Batch;
	}
	public ArrayList<String> getMyCourses() {
		return myCourses;
	}
	
	
}
