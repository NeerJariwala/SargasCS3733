package edu.wpi.sargas.demo.entity;

import java.time.LocalTime;

public class Timeslot {
	
	boolean open;
	int duration;
	LocalTime startTime;
	String timeslotID;
	Meeting meeting;
}
