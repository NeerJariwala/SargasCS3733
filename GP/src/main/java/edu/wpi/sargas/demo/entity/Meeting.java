package edu.wpi.sargas.demo.entity;

import java.util.UUID;

public class Meeting {
	
	public String meetingID;
	public String name;
	public String timeslot;
	public String secretCode;
	
	private static final int secretIdLength = 6;
	

	//to make a new meeting
	public Meeting(String name, String timeslot) {
		this.name = name;
		meetingID = UUID.randomUUID().toString();
		this.timeslot = timeslot;
		this.secretCode = generateSecret();
	}
	
	//after fetch from database
	public Meeting(String meetingID, String name, String timeslot, String secretCode) {
		this.meetingID = meetingID;
		this.name = name;
		this.timeslot = timeslot;
		this.secretCode = secretCode;
	}
	
	/**
	 * Generates a secret code for a schedule
	 * @return a unique secret code
	 */
	private String generateSecret() {
		String secret = "";
		int count = 0;
		
		while(count < secretIdLength) {
			int asciiCode = (int)(Math.random() * 127);
			
			if(asciiCode < 33 || asciiCode > 126) { //only include human-readable text
				continue;
			} else {
				Character c = (char)asciiCode;
				secret = secret.concat(c.toString()); //add character to end of string
				++count;
			}
			
		}
		//TODO: Ensure this secret code is unique by checking the database
		return secret;
	}
}
