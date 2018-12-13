package edu.wpi.sargas.demo.http_responses_and_requests;

import edu.wpi.sargas.demo.entity.Timeslot;

public class CloseDayResponse {
	
	int httpCode; //http code of the response
	
	public CloseDayResponse(int http) {
		this.httpCode = http;
		
	}
	
}
