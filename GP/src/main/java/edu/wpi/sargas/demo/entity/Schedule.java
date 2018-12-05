package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import edu.wpi.sargas.db.DayDAO;
import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.db.TimeslotDAO;
import edu.wpi.sargas.db.WeekDAO;

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
	public Schedule(int td, String name, LocalDate sd, LocalDate ed, int sh, int eh) throws Exception {
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
		new ScheduleDAO().createSchedule(this);
		try {
			generateWeeks();
		} catch(Exception e) {
			throw new Exception("SQL failure");
		}
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
	public void generateWeeks() throws Exception {
		
		DayDAO dayDao = new DayDAO();
		WeekDAO weekDao = new WeekDAO();
		
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
				weekDao.createWeek(w);
				//add a new day to the week and advance
				while(cursor.getDayOfWeek() != java.time.DayOfWeek.SATURDAY) {
					Day newDay = new Day(cursor, startHour, endHour, timeslotDuration, w.WeekID);
					w.addDay(newDay);
					cursor = cursor.plusDays(1);
					
					dayDao.createDay(newDay);
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
			weekDao.createWeek(w);
			//keep making the week until either saturday or the end date comes 
			while(cursor.getDayOfWeek() != java.time.DayOfWeek.SATURDAY && !cursor.isAfter(endDate)) {
				
				Day newDay = new Day(cursor, startHour, endHour, timeslotDuration, w.WeekID);
				w.addDay(newDay);
				cursor = cursor.plusDays(1);
				
				dayDao.createDay(newDay);
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
		
		WeekDAO weekDao = new WeekDAO();
		DayDAO dayDao = new DayDAO();
		TimeslotDAO timeslotDao = new TimeslotDAO();
		
		try {
			ArrayList<Week> weeks = weekDao.getWeeks(scheduleId);
			Week weekOf = null;
			
			//find the week we need
			for(Week week: weeks) {
				if((week.startDate.isBefore(target) || week.startDate.isEqual(target)) && (week.endDate.isAfter(target)) || week.endDate.isEqual(target)) {
					//make sure the target is between the start and end date
					weekOf = week;
				}
			}
			
			if(weekOf == null) {
				return null;
			} else {
				//put the days and timeslots in the week
				ArrayList<Day> days = dayDao.getDays(weekOf.WeekID);
				
				for(Day day: days) {
					weekOf.addDay(day);
					
					ArrayList<Timeslot> timeslots = timeslotDao.getTimeslots(day.DayID);
					
					for(Timeslot timeslot : timeslots) {
						day.addTimeslot(timeslot);
					}
					
				}
				return weekOf;
			}
			
		} catch(Exception e) {
			return null;
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
