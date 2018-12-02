package edu.wpi.sargas.demo.entity;

import java.util.UUID;

public class Meeting {
	
	public String name;
	public String meetingID;
	
	//to make a new meeting
	public Meeting(String name) {
		this.name = name;
		meetingID = UUID.randomUUID().toString();
	}
	
	//to fetch from database
	public Meeting(String name, String id) {
		this.name = name;
		meetingID = id;
	}
}
