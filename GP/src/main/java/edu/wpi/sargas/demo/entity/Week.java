package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Week {
	
	ArrayList<Day> days = new ArrayList<Day>();
	
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
