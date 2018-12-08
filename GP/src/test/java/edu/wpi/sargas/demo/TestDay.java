package edu.wpi.sargas.demo;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Test;

import edu.wpi.sargas.db.DayDAO;
import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.db.WeekDAO;
import edu.wpi.sargas.demo.entity.Day;
import edu.wpi.sargas.demo.entity.Schedule;
import edu.wpi.sargas.demo.entity.Week;
import junit.framework.TestCase;

public class TestDay extends TestCase {

	@Test
	public void test() {
		LocalDate date = LocalDate.of(2000, 1, 1);
		int startHour = 3;
		int endHour = 4;
		int duration = 20;
		//3 timeslots in this day
		String weekId = null;
		
		try {
			Schedule testSched = new Schedule(60,"TestSched1",LocalDate.of(2002, 1, 1), LocalDate.of(2002, 2, 1),8,16);
			Week w = new WeekDAO().getWeeks(testSched.getScheduleID()).get(0);
			weekId = w.WeekID;
			} catch(Exception e) {
				System.out.println("problem");
			}
		
		Day d = new Day(date,startHour,endHour,duration, weekId);
		try {
			new DayDAO().createDay(d);
		} catch(Exception e) {
			System.out.println("problem");
		}
		
		d.generateTimeslots(duration);
		
		assertEquals(d.date,date);
		assertEquals(d.startTime,LocalTime.of(startHour, 0));
		assertEquals(d.endTime,LocalTime.of(endHour, 0));
		assertEquals(d.timeslots.size(), 3);
	}
}
