package edu.wpi.sargas.demo.http_responses_and_requests;

import edu.wpi.sargas.demo.entity.Schedule;

public class DeleteScheduleResponse {
	
	int httpCode; //http code of the response
	
	
	public DeleteScheduleResponse(int http) {
		httpCode = http;
	
	}
	
}
