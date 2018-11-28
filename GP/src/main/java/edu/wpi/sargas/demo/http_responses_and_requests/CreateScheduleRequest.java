package edu.wpi.sargas.demo.http_responses_and_requests;

public class CreateScheduleRequest {
	String name;
	int duration;
	String startDate;
	String endDate;
	int startHour; //start and end hour are ints in 24hr format
	int endHour;
}
