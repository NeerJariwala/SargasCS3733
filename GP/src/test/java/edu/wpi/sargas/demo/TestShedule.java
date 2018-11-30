package edu.wpi.sargas.demo;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import edu.wpi.sargas.demo.entity.Schedule;

public class TestShedule {

	@Test
	public void test() {
		int duration = 20;
		String name = "name";
		LocalDate startDate = LocalDate.of(2000, 1, 1);
		LocalDate endDate = LocalDate.of(2001, 1, 1);
		int startHour = 1;
		int endHour = 13;
		Schedule s = new Schedule(duration,name,startDate,endDate,startHour,endHour);
		
		assertEquals(name.equals(s.name),true);
		assertEquals(s.timeslotDuration,duration);
		assertEquals(s.startDate,startDate);
		assertEquals(s.endDate,endDate);
		assertEquals(s.startHour, startHour);
		assertEquals(s.endHour, endHour);
		assertEquals(s.dateCreated, LocalDate.now());
	}

}
