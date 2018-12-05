package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

public class Day {
	
	public String DayID;
	public LocalDate date;
	public LocalTime startTime;
	public LocalTime endTime;
	
	public ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	
	
	//to make a new one
	public Day(LocalDate d, int startHour, int endHour, int duration) {
		this.DayID = UUID.randomUUID().toString();
		date = d;
		this.startTime = LocalTime.of(startHour, 0);
		this.endTime = LocalTime.of(endHour, 0);
		
		generateTimeslots(duration);
	}
	
	//after fetch from database
	public Day(String DayID, LocalDate d, ArrayList<Timeslot> timeslots) {
		this.DayID = DayID;
		this.date = d;
		this.timeslots = timeslots;
	}
	
	private void generateTimeslots(int duration) {
		
		long timeDifference = startTime.until(endTime, ChronoUnit.MINUTES);
		//how many minutes between start and end
		
		int numTimeslots = (int)(timeDifference / duration); //get the number of timeslots we can fit in that period
		LocalTime cursor = startTime; //begin at the start of the day
		
		for(int i = 0; i<numTimeslots; i++) {
			Timeslot t = new Timeslot(cursor, duration, this.DayID);
			//TODO: put timeslot in RDS w/ DAO
			timeslots.add(t);
			cursor = cursor.plusMinutes(duration);
		}
		
	}
	
}
