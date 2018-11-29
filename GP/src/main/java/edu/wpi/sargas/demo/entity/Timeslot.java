package edu.wpi.sargas.demo.entity;

import java.time.LocalTime;
import java.util.UUID;

public class Timeslot {
	
	boolean open;
	int duration;
	LocalTime startTime;
	String timeslotID;
	Meeting meeting;
	
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
