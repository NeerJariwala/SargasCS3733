package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
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
	
	//TODO: Actually make this method
	public void generateTimeslots(int duration) {
		
	}
	
}
