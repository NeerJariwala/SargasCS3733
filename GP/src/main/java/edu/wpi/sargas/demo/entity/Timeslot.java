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
	 * 
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
	
}
