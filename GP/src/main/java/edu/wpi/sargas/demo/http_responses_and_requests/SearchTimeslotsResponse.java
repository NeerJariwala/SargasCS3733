package edu.wpi.sargas.demo.http_responses_and_requests;

import java.util.ArrayList;

import edu.wpi.sargas.demo.entity.Timeslot;

public class SearchTimeslotsResponse {
	int httpCode;
	ArrayList<Timeslot> timeslots;
	
	public SearchTimeslotsResponse(int code, ArrayList<Timeslot> arr) {
		httpCode = code;
		timeslots = arr;
	}
	
}
