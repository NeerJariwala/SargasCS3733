package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import edu.wpi.sargas.db.MeetingDAO;

public class OrganizerCancelMeetingHandler implements RequestStreamHandler {
	
	private void errorResponse(JSONObject jsonResponse) {
		OrganizerCancelMeetingResponse response = new OrganizerCancelMeetingResponse(400);
		jsonResponse.put("body", new Gson().toJson(response));
	}
	
	/**
	 * Deletes a schedule's meeting from the database
	 * @param meetingId the ID of the meeting to delete
	 * @param secretCode the secret code of the meeting to delete it from
	 * @return whether the meeting could be deleted
	 */
	private boolean deleteMeetingFromDatabase(String meetingId, String secretCode) {
		try {
			MeetingDAO dao = new MeetingDAO();
			//TODO:verify secretCode exists first
			dao.deleteMeeting(meetingId);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    	
    	LambdaLogger logger = context.getLogger();
        logger.log("Beginning to cancel meeting");
        
        JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
    	headerJson.put("Access-Control-Allow-Headers", "*");
	    
    	JSONObject jsonResponse = new JSONObject();
    	jsonResponse.put("headers", headerJson);
    	OrganizerCancelMeetingResponse httpResponse = null;
    	String httpBody = null;
    	boolean processed = false;
    	
    	try {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    		JSONParser parser = new JSONParser();
    		JSONObject jsonRequest = (JSONObject)parser.parse(reader);
    		//parse the request
    		
    		logger.log("Input was " + jsonRequest.toJSONString());
    		String requestType = (String)jsonRequest.get("httpMethod");
    		
    		if(requestType != null && requestType.equalsIgnoreCase("OPTIONS")) {
    			//if we got an OPTIONS request, provide a 200 response
    			logger.log("Options request");
    			httpResponse = new OrganizerCancelMeetingResponse(200);
    			jsonResponse.put("body", new Gson().toJson(httpResponse));
    			//nothing needed because it was an options request
    			processed = true;
    		} else { //otherwise, check out the response body later
    			httpBody = (String)jsonRequest.get("body");
    			if(httpBody == null) {
    				httpBody = jsonRequest.toJSONString();
    			}
    		}
    	} catch(ParseException e) {
    		//if unable to parse, prepare an invalid input response
    		logger.log(e.toString());
    		errorResponse(jsonResponse);
    		processed = true;
    	}
    	
    	//if it wasn't OPTIONS, nor a parsing error, handle the request
    	if(!processed) {
    		 OrganizerCancelMeetingRequest request = new Gson().fromJson(httpBody,OrganizerCancelMeetingRequest.class);
    		 String id = request.meetingId;
    		 String secretCode = request.secretCode;
    		 
    		 if(deleteMeetingFromDatabase(id,secretCode)) {
    			 httpResponse = new OrganizerCancelMeetingResponse(200);
     			jsonResponse.put("body", new Gson().toJson(httpResponse));
    		 } else {
    			 logger.log("Incorrect secretCode");
    			 errorResponse(jsonResponse);
    		 }
    		 
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    	
    }

}
