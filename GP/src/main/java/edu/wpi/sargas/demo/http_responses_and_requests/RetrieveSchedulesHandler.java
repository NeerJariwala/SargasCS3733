package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.demo.entity.Schedule;

public class RetrieveSchedulesHandler implements RequestStreamHandler {
	
	private ArrayList<Schedule> retrieveSchedules(LocalDateTime since) throws Exception {
		//Use DAO to get schedules after the localDateTime passed in
		return new ScheduleDAO().retrieveSchedules(since);
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
    	RetrieveSchedulesResponse httpResponse = null;
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
    			httpResponse = new RetrieveSchedulesResponse(200, null);
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
    		httpResponse = new RetrieveSchedulesResponse(400, null);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
        
    	if(!processed) {
    		
    		RetrieveSchedulesRequest request = new Gson().fromJson(httpBody,RetrieveSchedulesRequest.class);
    		//get the request in the form of a class
    		Integer hoursAgo = null;
    		boolean noHours = false;
    		boolean invalidInput = false;
    		
    		if(request.hoursAgo.equals("")) { //if no hour input
    			noHours = true;
    		}
    		
    		
    		if(noHours) {
    			//if we have either none or both, input is invalid
    			logger.log("Invalid input");
    			invalidInput = true;
        		httpResponse = new RetrieveSchedulesResponse(400, null);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		} else { //if we have hours input, use hours ago
    			hoursAgo = Integer.parseInt(request.hoursAgo);
    		}
    		
    		try {
	    		if(!invalidInput) { //process hours input here
	    			
	    			LocalDateTime currentTime = LocalDateTime.now();
	    			LocalDateTime lowerBound = currentTime.minusHours(hoursAgo);
	    			ArrayList<Schedule> schedules = retrieveSchedules(lowerBound);
	    			httpResponse = new RetrieveSchedulesResponse(200, schedules);
	    			jsonResponse.put("body", new Gson().toJson(httpResponse));
	    			
	    		} 
    		} catch(Exception e) {
    			logger.log(e.toString());
    			e.printStackTrace();
        		httpResponse = new RetrieveSchedulesResponse(400, null);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		}
    		
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    	
    }

}
