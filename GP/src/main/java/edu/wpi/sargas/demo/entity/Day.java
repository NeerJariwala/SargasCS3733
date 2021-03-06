package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

import edu.wpi.sargas.db.TimeslotDAO;

public class Day {
	
	public String DayID;
	public LocalDate date;
	public LocalTime startTime;
	public LocalTime endTime;
	public String week;
	
	public ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	
	
	//to make a new one
	public Day(LocalDate d, int startHour, int endHour, int duration, String week) {
		this.DayID = UUID.randomUUID().toString();
		this.date = d;
		this.startTime = LocalTime.of(startHour, 0);
		this.endTime = LocalTime.of(endHour, 0);
		this.week = week;
	}
	
	//after fetch from database
	public Day(String DayID, LocalDate d, String week) {
		this.DayID = DayID;
		this.date = d;
	//	this.startTime = startTime;
	//	this.endTime = endTime;
		this.week = week;
	}
	
	//Method that adds a timeslot to its arraylist
	public void addTimeslot(Timeslot t) {
		timeslots.add(t);
	}
	
	public void generateTimeslots(int duration) {
		
		long timeDifference = startTime.until(endTime, ChronoUnit.MINUTES);
		//how many minutes between start and end
		
		int numTimeslots = (int)(timeDifference / duration); //get the number of timeslots we can fit in that period
		LocalTime cursor = startTime; //begin at the start of the day
		TimeslotDAO dao = new TimeslotDAO();
		for(int i = 0; i<numTimeslots; i++) {
			//double start = cursor.getHour() + (cursor.getMinute() / 60.0);
			Timeslot t = new Timeslot(cursor, duration, this.DayID);
			try {
				dao.createTimeslot(t);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			
			timeslots.add(t);
			cursor = cursor.plusMinutes(duration);
		}
		
	}
	
}
