package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.util.UUID;

public class Schedule {
	
	private static final int secretIdLength = 6;
	
	public final int timeslotDuration; //duration of each timeslot
	public final String scheduleId; //id of the schedule
	public final String name; //name of the schedule
	public final LocalDate startDate; //start and end dates of the schedule's duration
	public final LocalDate endDate;
	public final int startHour; //start and end hours of the schedule each day
	public final int endHour;
	//ArrayList<Week> weeks; would be here, but that would require making all of
	//the entity classes. Get rid of this comment in iteration #2
	public final String secretCode;
	public final LocalDate dateCreated;
	
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
				secret = secret.concat(c.toString()); //add character to end of string
				++count;
			}
			
		}
		//TODO: Ensure this secret code is unique by checking the database
		return secret;
	}
	
}
