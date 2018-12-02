package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Day {
	
	public LocalDate date;
	public ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	public LocalTime startTime;
	public LocalTime endTime;
	
	//to make a new one
	public Day(LocalDate d, int startHour, int endHour, int duration) {
		date = d;
		this.startTime = LocalTime.of(startHour, 0);
		this.endTime = LocalTime.of(endHour, 0);
		
		generateTimeslots(duration);
	}
	
	//to fetch from database
	public Day(LocalDate d, ArrayList<Timeslot> timeslots, int startHour, int endHour) {
		date = d;
		this.timeslots = timeslots;
		startTime = LocalTime.of(startHour, 0);
		endTime = LocalTime.of(endHour, 0);
	}
	
	private void generateTimeslots(int duration) {
		
		long timeDifference = startTime.until(endTime, ChronoUnit.MINUTES);
		//how many minutes between start and end
		
		int numTimeslots = (int)(timeDifference / duration); //get the number of timeslots we can fit in that period
		LocalTime cursor = startTime; //begin at the start of the day
		
		for(int i = 0; i<numTimeslots; i++) {
			Timeslot t = new Timeslot(cursor, duration);
			//TODO: put timeslot in RDS w/ DAO
			timeslots.add(t);
			cursor = cursor.plusMinutes(duration);
		}
		
	}
	
}
