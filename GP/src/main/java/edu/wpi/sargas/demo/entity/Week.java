package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Week {
	
	Day[] days = new Day[5];
	
	public Week(LocalDate startDate, LocalTime startTime, LocalTime endTime, int duration) {
		
		LocalDate cursor = startDate;
		
		for(Day d : days) {
			d = new Day(cursor, startTime, endTime, duration);
			cursor.plusDays(1); //make it the next day
		}
		
	}
	
}
