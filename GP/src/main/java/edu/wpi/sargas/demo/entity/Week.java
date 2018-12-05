package edu.wpi.sargas.demo.entity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Week {
	
	public String WeekID;
	public final LocalDate startDate; //start and end dates of the schedule's duration
	public final LocalDate endDate;
	public String schedule;   //aka the id
	
	public ArrayList<Day> days = new ArrayList<Day>();
	
	
	public Week(LocalDate startDate, String schedule) {
		this.WeekID = UUID.randomUUID().toString();
		this.startDate = startDate;
		
		LocalDate cursor = startDate;
		
		//loop finds the end date
		while(cursor.getDayOfWeek() != DayOfWeek.FRIDAY) {
			cursor = cursor.plusDays(1);
		}
		
		this.endDate = cursor;
		
		this.schedule = schedule;
	}
	
	
	///after fetch from db
	public Week(String WeekID, LocalDate startDate, LocalDate endDate, String schedule) {
		this.WeekID = WeekID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.schedule = schedule;
				
	}
	
	
	
	public void addDay(Day d) {
		days.add(d);
	}
	
	
	/**
	 * Whether the week contains a certain date
	 * @param date the date we're looking for in the week
	 * @return whether the week contains the date
	 */
	public boolean containsDate(LocalDate date) {
		boolean found = false;
		
		for(Day day : days) {
			
			if(date.isEqual(day.date)) { //if the dates are the same
				found = true;
				break;
			}
			
		}
		
		return found;
		
	}
	
}
