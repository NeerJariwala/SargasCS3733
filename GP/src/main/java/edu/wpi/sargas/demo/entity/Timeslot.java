package edu.wpi.sargas.demo.entity;

import java.time.LocalTime;
import java.util.UUID;

public class Timeslot {
	
	public String timeslotID;
	public boolean open;
	public int duration;
	public LocalTime startTime;
	public String day;
	
	/**
	 * To make a new timeslot
	 * @param startTime the time the timeslot starts
	 * @param duration how long it lasts
	 */
	public Timeslot(LocalTime startTime, int duration, String day) {
		this.startTime = startTime;
		this.duration = duration;
		open = true;
		timeslotID = UUID.randomUUID().toString();
		this.day = day;
	}
	
	//after fetch from database
	public Timeslot(String timeslotID, boolean open, int duration, LocalTime startTime, String day) {
		this.timeslotID = timeslotID;
		this.open = open;
		this.duration = duration;
		this.startTime = startTime;
		this.day = day;
	}
	
}
