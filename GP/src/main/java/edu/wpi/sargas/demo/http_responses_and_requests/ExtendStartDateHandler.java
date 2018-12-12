package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.demo.entity.Schedule;

public class ExtendStartDateHandler implements RequestStreamHandler {
	
	private boolean updateStartDate(String scheduleId, LocalDate newStartDate) throws Exception {
		return new ScheduleDAO().extendstartDate(scheduleId, newStartDate);
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
    	ExtendStartDateResponse httpResponse = null;
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
    			httpResponse = new ExtendStartDateResponse(200);
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
    		httpResponse = new ExtendStartDateResponse(400);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
        
    	if(!processed) {
    		//if it's not OPTIONS and not a parse exception, further processing must be done
    		
    		ExtendStartDateRequest request = new Gson().fromJson(httpBody,ExtendStartDateRequest.class);
    		String secretCode = request.secretCode;
    		String newStart = request.newStart;
    		LocalDate newDate = null;
    		
    		try {
    			
    			newDate = LocalDate.parse(newStart);
    			
    			Schedule sched = new ScheduleDAO().getSchedule(secretCode);
    			
    			if(sched == null) { //if we couldn't get a schedule from that secretCode
    				logger.log("Incorrect secret code");
    				httpResponse = new ExtendStartDateResponse(400);
            		jsonResponse.put("body", new Gson().toJson(httpResponse));
    			} else if(newDate.isAfter(sched.startDate)) {
    				//if the new date is after the start date, it's not being extended
    				logger.log("Invalid date");
    				httpResponse = new ExtendStartDateResponse(400);
            		jsonResponse.put("body", new Gson().toJson(httpResponse));
    				
    			} else {
    				
    				sched.generateWeeks(newDate, sched.startDate.minusDays(1));
    				//go from the new start date to right before the old start date
    				
    				if(updateStartDate(sched.scheduleId, newDate)) {
    					httpResponse = new ExtendStartDateResponse(200);
    					jsonResponse.put("body", new Gson().toJson(httpResponse));
    				} else {
    					logger.log("Database issue");
    					httpResponse = new ExtendStartDateResponse(400);
    	        		jsonResponse.put("body", new Gson().toJson(httpResponse));
    				}
    			}
    			
    		} catch(Exception e) {
    			logger.log(e.toString());
    			httpResponse = new ExtendStartDateResponse(400);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		}
    		
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    	
    }

}
