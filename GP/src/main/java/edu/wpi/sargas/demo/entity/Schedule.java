package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
	ArrayList<Week> weeks; 
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
	
	//TODO: Actually make this method
	public void generateWeeks() {
		
		LocalDate cursor = startDate;
		
		switch(cursor.getDayOfWeek()) {
			
			//if it's a weekend, skip to monday.
			case SUNDAY: 
			case SATURDAY:
				while(cursor.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
					cursor.plusDays(1);
				}
				break;
			
			//if it's not monday, do the remainder
			case TUESDAY:
			case WEDNESDAY:
			case THURSDAY:
			case FRIDAY:
				Week w = new Week();
				
				//add a new day to the week and advance
				while(cursor.getDayOfWeek() != java.time.DayOfWeek.SATURDAY) {
					Day newDay = new Day(cursor, startHour, endHour, timeslotDuration);
					w.addDay(newDay);
					cursor.plusDays(1);
				}
				//cursor would be on a saturday right now
				//bring it to the next monday
				cursor.plusDays(2);
				break;
		}
		
		
		
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
