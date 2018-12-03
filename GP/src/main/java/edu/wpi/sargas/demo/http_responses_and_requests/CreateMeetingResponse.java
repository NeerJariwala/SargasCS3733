package edu.wpi.sargas.demo.http_responses_and_requests;

public class CreateMeetingResponse {
	
	int httpCode;
	String secretCode;
	
	public CreateMeetingResponse(int httpCode, String secretCode) {
		this.httpCode = httpCode;
		this.secretCode = secretCode;
	}
	
}
