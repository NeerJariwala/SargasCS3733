package edu.wpi.sargas.demo.entity;

import java.util.UUID;

public class Meeting {
	
	public String meetingID;
	public String name;
	public String timeslot;
	

	//to make a new meeting
	public Meeting(String name, String timeslot) {
		this.name = name;
		meetingID = UUID.randomUUID().toString();
		this.timeslot = timeslot;
	}
	
	//after fetch from database
	public Meeting(String meetingID, String name, String timeslot) {
		this.meetingID = meetingID;
		this.name = name;
		this.timeslot = timeslot;
		
	}
}
