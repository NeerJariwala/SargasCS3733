package edu.wpi.sargas.demo.entity;

import java.util.UUID;

public class Meeting {
	
	public String name;
	public String meetingID;
	
	public Meeting(String name) {
		this.name = name;
		meetingID = UUID.randomUUID().toString();
	}
	
}
