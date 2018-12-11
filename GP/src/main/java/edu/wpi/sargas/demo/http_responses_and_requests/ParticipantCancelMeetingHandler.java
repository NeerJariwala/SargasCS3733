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
import edu.wpi.sargas.db.TimeslotDAO;
import edu.wpi.sargas.demo.entity.Meeting;

public class ParticipantCancelMeetingHandler implements RequestStreamHandler {
	
	public void errorResponse(JSONObject jsonResponse) {
		ParticipantCancelMeetingResponse httpResponse = new ParticipantCancelMeetingResponse(400);
		jsonResponse.put("body", new Gson().toJson(httpResponse));
	}
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

    	LambdaLogger logger = context.getLogger();
    	logger.log("Beginning to create schedule");
    	
    	JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
    	headerJson.put("Access-Control-Allow-Headers", "*");
	    
    	JSONObject jsonResponse = new JSONObject();
    	jsonResponse.put("headers", headerJson);
    	ParticipantCancelMeetingResponse httpResponse = null;
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
    			httpResponse = new ParticipantCancelMeetingResponse(200);
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
    		httpResponse = new ParticipantCancelMeetingResponse(400);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
    	
    	if(!processed) {
    		ParticipantCancelMeetingRequest request = new Gson().fromJson(httpBody, ParticipantCancelMeetingRequest.class);
    		String secretCode = request.secretCode;
    		MeetingDAO dao = new MeetingDAO();
    		TimeslotDAO tDao = new TimeslotDAO();   		
    		
    		try {
    			Meeting m = dao.getsecretMeeting(secretCode);
    			String timeslot = m.timeslot;
    			tDao.changeTimeslot(timeslot, 1);
	    		if(dao.deleteMeetingPart(secretCode)) {
	    			httpResponse = new ParticipantCancelMeetingResponse(200);
	    			jsonResponse.put("body", new Gson().toJson(httpResponse));
	    		} else {
	    			errorResponse(jsonResponse);
	    		}
    		} catch(Exception e) {
    			logger.log(e.toString());
    			errorResponse(jsonResponse);
    		}
    		
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    	
    }

}
