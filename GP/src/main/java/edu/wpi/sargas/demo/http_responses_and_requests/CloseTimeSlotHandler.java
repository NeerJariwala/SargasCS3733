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

import edu.wpi.sargas.demo.entity.Timeslot;

public class CloseTimeSlotHandler implements RequestStreamHandler {
	
	private void errorResponse(JSONObject jsonResponse) {
		CloseTimeSlotResponse response = new CloseTimeSlotResponse(400);
		jsonResponse.put("body", response);
	}

	public boolean CloseTimeSlot(String change, String timeslotID) {
		//need DAO for this function
		//boolean if time slot is closed return true
		return false;
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
    			httpBody = (String)jsonRequest.get("body");
    			if(httpBody == null) {
    				httpBody = jsonRequest.toJSONString();
    			}
    		}
    	} catch(ParseException e) {
    		//if unable to parse, prepare an invalid input response
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
    		
    	
    		if(CloseTimeSlot("open","aklsdhjkjh")) {
    			 httpResponse = new CloseTimeSlotResponse(200);
      			jsonResponse.put("body", httpResponse);
     		 } else {
     			 logger.log("Incorrect secretCode");
     			 errorResponse(jsonResponse);
     		 }
    	
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    }

}
}