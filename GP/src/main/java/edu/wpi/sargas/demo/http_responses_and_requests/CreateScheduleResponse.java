package edu.wpi.sargas.demo.http_responses_and_requests;

import edu.wpi.sargas.demo.entity.Schedule;

public class CreateScheduleResponse {
	
	int httpCode; //http code of the response
	Schedule schedule; //schedule returned
	String secretCode; //secret code of that schedule
	
	public CreateScheduleResponse(int http, Schedule s, String secret) {
		httpCode = http;
		schedule = s;
		secretCode = secret;
	}
	
}
