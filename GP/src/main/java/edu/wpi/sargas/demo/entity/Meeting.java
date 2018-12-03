package edu.wpi.sargas.demo.entity;

import java.util.UUID;

public class Meeting {
	
	public String name;
	public String meetingID;
	public String timeslotId;
	
	public Meeting(String name, String timeslotId) {
		this.name = name;
		meetingID = UUID.randomUUID().toString();
		this.timeslotId = timeslotId;
	}
	
}
