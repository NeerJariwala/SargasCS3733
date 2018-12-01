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
	
	@Test
	public void testGetWeekOf() {
		int duration = 20;
		String name = "name";
		LocalDate startDate = LocalDate.of(2001, 1, 1); //starts on monday
		LocalDate endDate = LocalDate.of(2001, 1, 12);
		int startHour = 1;
		int endHour = 13;
		Schedule s = new Schedule(duration,name,startDate,endDate,startHour,endHour);
		
		LocalDate dateInWeek1 = LocalDate.of(2001, 1, 4);
		LocalDate otherDateInWeek1 = LocalDate.of(2001, 1, 2);
		LocalDate dateInWeek2 = LocalDate.of(2001, 1, 10);
		LocalDate outsideDate = LocalDate.of(2001, 1, 15);
		LocalDate week1Saturday = LocalDate.of(2001, 1, 6);
		LocalDate week2Sunday = LocalDate.of(2001, 1, 7);
		
		assertEquals(s.getWeekOf(dateInWeek1), s.getWeekOf(otherDateInWeek1));
		assertFalse(s.getWeekOf(dateInWeek1) == s.getWeekOf(dateInWeek2));
		assertNull(s.getWeekOf(outsideDate));
		assertEquals(s.getWeekOf(dateInWeek1), s.getWeekOf(week1Saturday));
		assertEquals(s.getWeekOf(dateInWeek2), s.getWeekOf(week2Sunday));
	}

}