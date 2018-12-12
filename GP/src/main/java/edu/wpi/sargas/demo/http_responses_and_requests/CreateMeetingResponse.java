package edu.wpi.sargas.demo.http_responses_and_requests;

public class CreateMeetingResponse {
	
	int httpCode;
	String secretCode;
	String name;
	
	public CreateMeetingResponse(int httpCode, String secretCode, String name) {
		this.httpCode = httpCode;
		this.secretCode = secretCode;
		this.name = name;
		
	}
	
}
