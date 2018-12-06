package edu.wpi.sargas.demo.http_responses_and_requests;

public class SearchTimeslotsRequest {
	String secretCode;
	String month; //1 to 12
	String year; 
	String dayOfWeek; //1 to 7
	String dayOfMonth; //1 to 31
	
	public SearchTimeslotsRequest() {
		
	}
	
	public SearchTimeslotsRequest(String secretCode, String month, String year, String dayOfWeek, String dayOfMonth) {
		this.secretCode = secretCode;
		this.month = month;
		this.year = year;
		this.dayOfWeek = dayOfWeek;
		this.dayOfMonth = dayOfMonth;
	}
	
}
