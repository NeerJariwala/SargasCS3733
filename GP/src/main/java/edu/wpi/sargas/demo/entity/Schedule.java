package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
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
	public ArrayList<Week> weeks; 
	public final String secretCode;
	public final LocalDate dateCreated;
	
	//to make a new schedule
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
		weeks = new ArrayList<Week>();
		generateWeeks();
	}
	
	//retrieved from database
	//TODO: needs to put its weeks into the schedule
	public Schedule(int td, String ID, String name, LocalDate sd, LocalDate ed, int sh, int eh, String secretCode, LocalDate dateCreated) {
		timeslotDuration = td;
		scheduleId = ID;
		this.name = name;
		startDate = sd;
		endDate = ed;
		startHour = sh;
		endHour = eh;
		this.secretCode = secretCode;
		this.dateCreated = dateCreated;
	}
	
	//TODO: Actually make this method
	public void generateWeeks() {
		
		LocalDate cursor = startDate;
		
		switch(cursor.getDayOfWeek()) {
			
			//if it's a weekend, skip to monday.
			case SUNDAY: 
			case SATURDAY:
				while(cursor.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
					cursor = cursor.plusDays(1);
				}
				break;
			
			//if it's not monday, do the remainder
			case TUESDAY:
			case WEDNESDAY:
			case THURSDAY:
			case FRIDAY:
				Week w = new Week(cursor,this.scheduleId);
				//TODO: put this in RDS w/ DAO
				
				//add a new day to the week and advance
				while(cursor.getDayOfWeek() != java.time.DayOfWeek.SATURDAY) {
					Day newDay = new Day(cursor, startHour, endHour, timeslotDuration);
					w.addDay(newDay);
					cursor = cursor.plusDays(1);
					//TODO: put the day and week in RDS w/ DAO
				}
				//cursor would be on a saturday right now
				//bring it to the next monday
				cursor = cursor.plusDays(2);
				weeks.add(w);
				break;
			
			default: break;
			
		}
		
		//keep making weeks until we've passed the end date
		while(cursor.isBefore(endDate) || cursor.isEqual(endDate)) {
			
			Week w = new Week(cursor,this.scheduleId);
			//keep making the week until either saturday or the end date comes 
			while(cursor.getDayOfWeek() != java.time.DayOfWeek.SATURDAY && !cursor.isAfter(endDate)) {
				
				Day newDay = new Day(cursor, startHour, endHour, timeslotDuration);
				w.addDay(newDay);
				cursor = cursor.plusDays(1);
				//TODO: put the day and week in RDS w/ DAO
			}
			weeks.add(w);
			//should be on saturday now. bring it to next monday
			cursor = cursor.plusDays(2);
		}
		
	}
	
	/**
	 * Returns the week containing a certain date
	 * @param date the date that a week should contain
	 * @return
	 */
	public Week getWeekOf(LocalDate date) {
		
		LocalDate target = LocalDate.from(date);
		
		switch(target.getDayOfWeek()) {
			//weeks won't contain any weekends
			
			//if saturday, bring it to the nearest friday
			case SATURDAY:
				target = target.minusDays(1);
				break;
			
			//but sunday is the start of a new week.
			//so we bring that to the nearest monday
			case SUNDAY:
				target = target.plusDays(1);
				break;
		
		}
		
		for(Week week : weeks) {
			
			if(week.containsDate(target)) {
				return week;
			}
			
		}
		
		//if we checked all weeks and haven't returned, no weeks contain the date
		return null;
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
