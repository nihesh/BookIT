package HelperClasses;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Notification implements Serializable{
	private static final long serialVersionUID = 1L;
    String type;
    String status;
	private String message;
    private String course;
    private LocalDate targetDate;
    private String room;
    private String reserverEmail;
    private int slotID;
	public Notification(String type, String status, String message, String course, LocalDate targetDate,
			String room, String reserverEmail, int slotID) {
		this.type = type;
		this.status = status;
		this.message = message;
		this.course = course;
		this.targetDate = targetDate;
		this.room = room;
		this.reserverEmail = reserverEmail;
		this.slotID = slotID;
	}
	public String getType() {
		return type;
	}
	public String getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	public String getCourse() {
		return course;
	}
	public LocalDate getTargetDate() {
		return targetDate;
	}
	public String getRoom() {
		return room;
	}
	public String getReserverEmail() {
		return reserverEmail;
	}
	public int getSlotID() {
		return slotID;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public void setTargetDate(LocalDate targetDate) {
		this.targetDate = targetDate;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public void setReserverEmail(String reserverEmail) {
		this.reserverEmail = reserverEmail;
	}
	public void setSlotID(int slotID) {
		this.slotID = slotID;
	}
	
    
}
