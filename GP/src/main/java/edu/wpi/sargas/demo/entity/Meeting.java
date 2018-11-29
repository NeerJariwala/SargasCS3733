package edu.wpi.sargas.demo.entity;

import java.util.UUID;

public class Meeting {
	
	String name;
	String meetingID;
	
	public Meeting(String name) {
		this.name = name;
		meetingID = UUID.randomUUID().toString();
	}
	
}
