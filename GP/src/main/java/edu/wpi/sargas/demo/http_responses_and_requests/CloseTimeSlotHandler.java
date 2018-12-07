package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import edu.wpi.sargas.db.TimeslotDAO;
import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.demo.entity.Timeslot;

public class CloseTimeSlotHandler implements RequestStreamHandler {
	
	private void errorResponse(JSONObject jsonResponse) {
		CloseTimeSlotResponse response = new CloseTimeSlotResponse(400);
		//jsonResponse.put("body", response);
		jsonResponse.put("body", new Gson().toJson(response));
	}

	public boolean CloseTimeSlot(String secretCode, String timeslotID, int status) throws Exception {
		TimeslotDAO ts_dao = new TimeslotDAO();
		ScheduleDAO sched_dao = new ScheduleDAO();
		
		if(!sched_dao.validateSecretCode(secretCode)) {
			return false;
		}
		else {
			ts_dao.changeTimeslot(timeslotID, status);
			return true;
		}
		
	}
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        
    	LambdaLogger logger = context.getLogger();
    	
    	JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
    	headerJson.put("Access-Control-Allow-Headers", "*");
	    
    	JSONObject jsonResponse = new JSONObject();
    	jsonResponse.put("headers", headerJson);
    	CloseTimeSlotResponse httpResponse = null;
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
    			httpResponse = new CloseTimeSlotResponse(200);
    			//no schedule/secret code needed because it was an options request
    			
    		} else { //otherwise, check out the response body later
    			jsonResponse.put("body", new Gson().toJson(httpResponse));
    			if(httpBody == null) {
    				httpBody = jsonRequest.toJSONString();
    			}
    		}
    	} catch(ParseException e) {
    		//if unable to parse, prepare an invalid input response
    		e.printStackTrace();
    		logger.log(e.toString());
    		httpResponse = new CloseTimeSlotResponse(400);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
    	
    	if(!processed) { 
    		//if it's not OPTIONS and not a parse exception, further processing must be done
    		boolean invalidInput = false;
    		
    		CloseTimeSlotRequest request = new Gson().fromJson(httpBody,CloseTimeSlotRequest.class);
    		//get the request in the form of a class
    		String secretCode = request.secretCode;
    		String timeslotID = request.timeslotID;
    		int status = 0;//1 is open, 0 is closed
    		
    		
    		try {
    		if(CloseTimeSlot(secretCode, timeslotID, status)) {
    			 httpResponse = new CloseTimeSlotResponse(200);
    			 jsonResponse.put("body", new Gson().toJson(httpResponse));
     		 } else {
     			 logger.log("Incorrect secretCode");
     			 errorResponse(jsonResponse);
     		 }
    		} catch (Exception e) {
    				e.printStackTrace();
					logger.log("SQL failure");
					httpResponse = new CloseTimeSlotResponse(400);
	        		jsonResponse.put("body", new Gson().toJson(httpResponse));
 			}
    		
    	
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    }

}
}
