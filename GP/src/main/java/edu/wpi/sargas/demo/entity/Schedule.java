package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.util.UUID;

public class Schedule {
	
	private static final int secretIdLength = 6;
	
	int timeslotDuration; //duration of each timeslot
	String scheduleId; //id of the schedule
	String name; //name of the schedule
	LocalDate startDate; //start and end dates of the schedule's duration
	LocalDate endDate;
	int startHour; //start and end hours of the schedule each day
	int endHour;
	//ArrayList<Week> weeks; would be here, but that would require making all of
	//the entity classes. Get rid of this comment in iteration #2
	String secretCode;
	LocalDate dateCreated;
	
	public Schedule(int td, String name, LocalDate sd, LocalDate ed, int sh, int eh) {
		timeslotDuration = td;
		scheduleId = UUID.randomUUID().toString(); 
		this.name = name;
		startDate = sd;
		endDate = ed;
		startHour = sh;
		endHour = eh;
		secretCode = generateSecret();
		dateCreated = LocalDate.now(); //make date created today
	}
	
	public String getSecretCode() {
		return secretCode;
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
				secret.concat(c.toString()); //add character to end of string
				++count;
			}
			
		}
		//TODO: Ensure this secret code is unique by checking the database
		return secret;
	}
	
}
