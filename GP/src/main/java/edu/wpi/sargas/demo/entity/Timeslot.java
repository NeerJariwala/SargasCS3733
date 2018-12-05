package edu.wpi.sargas.demo.entity;

import java.time.LocalTime;
import java.util.UUID;

public class Timeslot {
	
	public String timeslotID;
	public int open;                 //1 is true, 0 is false
	public int duration;
	public double startTime;           //LocalTime startTime;
	public String day;
	
	/**
	 * To make a new timeslot
	 * @param startTime the time the timeslot starts
	 * @param duration how long it lasts
	 */
	public Timeslot(double startTime, int duration, String day) {
		this.startTime = startTime;
		this.duration = duration;
		open = 1;
		timeslotID = UUID.randomUUID().toString();
		this.day = day;
	}
	
	//after fetch from database
	public Timeslot(String timeslotID, int open, int duration, double startTime, String day) {
		this.timeslotID = timeslotID;
		this.open = open;
		this.duration = duration;
		this.startTime = startTime;
		this.day = day;
	}
	
}
