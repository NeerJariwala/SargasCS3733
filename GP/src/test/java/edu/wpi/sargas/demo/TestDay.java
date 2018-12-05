package edu.wpi.sargas.demo;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Test;

import edu.wpi.sargas.demo.entity.Day;

public class TestDay {

	@Test
	public void test() {
		LocalDate date = LocalDate.of(2000, 1, 1);
		int startHour = 3;
		int endHour = 4;
		int duration = 20;
		//3 timeslots in this day
		Day d = new Day(date,startHour,endHour,duration, "ID");
		
		assertEquals(d.date,date);
		assertEquals(d.startTime,LocalTime.of(startHour, 0));
		assertEquals(d.endTime,LocalTime.of(endHour, 0));
		assertEquals(d.timeslots.size(), 3);
	}

}
