package edu.wpi.sargas.demo.entity;

import java.time.LocalTime;
import java.util.UUID;

public class Timeslot {
	
	public boolean open;
	public int duration;
	public LocalTime startTime;
	public String timeslotID;
	public Meeting meeting;
	
	/**
	 * To make a new timeslot
	 * @param startTime the time the timeslot starts
	 * @param duration how long it lasts
	 */
	public Timeslot(LocalTime startTime, int duration) {
		this.startTime = startTime;
		this.duration = duration;
		open = true;
		timeslotID = UUID.randomUUID().toString();
		meeting = null;
	}
	
	//to fetch from database
	public Timeslot(boolean open, int duration, LocalTime startTime, String timeslotID, Meeting meeting) {
		this.open = open;
		this.duration = duration;
		this.startTime = startTime;
		this.timeslotID = timeslotID;
		this.meeting = meeting;
	}
	
}
