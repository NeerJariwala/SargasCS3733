package edu.wpi.sargas.demo;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Test;

import edu.wpi.sargas.demo.entity.Timeslot;

public class TestTimeslot {

	@Test
	public void test() {
		int duration = 15;
		double startTime = 2;
		Timeslot t = new Timeslot(startTime,duration, "string");
		assertEquals(t.startTime,startTime);
		assertEquals(t.duration,duration);
		assertEquals(t.open,true);
	}

}
