package edu.wpi.sargas.demo;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.wpi.sargas.demo.entity.Meeting;

public class TestMeeting {

	@Test
	public void test() {
		String name = "Meeting1";
		Meeting m = new Meeting(name, "timeslotID");
		assertEquals(m.name,name);
	}

}
