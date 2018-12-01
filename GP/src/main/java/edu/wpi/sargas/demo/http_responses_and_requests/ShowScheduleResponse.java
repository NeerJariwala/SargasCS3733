package edu.wpi.sargas.demo.http_responses_and_requests;

import edu.wpi.sargas.demo.entity.Week;

public class ShowScheduleResponse {
	
	int httpCode;
	Week week; //the week containing the weekly schedule
	
	public ShowScheduleResponse(int code, Week week) {
		httpCode = code;
		this.week = week;
	}
	
}
