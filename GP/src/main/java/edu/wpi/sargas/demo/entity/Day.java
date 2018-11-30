package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Day {
	
	LocalDate date;
	ArrayList<Timeslot> timeslots;
	LocalTime startTime;
	LocalTime endTime;
	
	public Day(LocalDate d, LocalTime startTime, LocalTime endTime, int duration) {
		date = d;
		this.startTime = startTime;
		this.endTime = endTime;
		
		generateTimeslots(duration);
	}
	
	private void generateTimeslots(int duration) {
		
		long timeDifference = startTime.until(endTime, ChronoUnit.MINUTES);
		//how many minutes between start and end
		
		int numTimeslots = (int)(timeDifference / duration); //get the number of timeslots we can fit in that period
		LocalTime cursor = startTime; //begin at the start of the day
		
		for(int i = 0; i<numTimeslots; i++) {
			timeslots.add(new Timeslot(cursor, duration));
			cursor.plusMinutes(duration);
		}
		
	}
	
}
