package edu.wpi.sargas.demo.http_responses_and_requests;

import java.util.ArrayList;

import edu.wpi.sargas.demo.entity.Schedule;

public class RetrieveSchedulesResponse {
	
	int httpCode;
	ArrayList<Schedule> schedules;
	
	public RetrieveSchedulesResponse(int code, ArrayList<Schedule> schedules) {
		httpCode = code;
		this.schedules = schedules;
	}
	
}
