package edu.wpi.sargas.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Week {
	
	ArrayList<Day> days = new ArrayList<Day>();
	
	public void addDay(Day d) {
		days.add(d);
	}
	
}
