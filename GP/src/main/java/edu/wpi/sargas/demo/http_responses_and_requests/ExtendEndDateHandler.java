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

public class ExtendEndDateHandler implements RequestStreamHandler {
	
	private boolean updateEndDate(String secretCode, LocalDate newEndDate) {
		//this method would use a DAO to update
		//the endDate field in the schedule table,
		//depending on secretCode
		return false;
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
    	ExtendEndDateResponse httpResponse = null;
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
    			httpResponse = new ExtendEndDateResponse(200);
    			processed = true;
    		} else { //otherwise, check out the response body later
    			httpBody = (String)jsonRequest.get("body");
    			if(httpBody == null) {
    				httpBody = jsonRequest.toJSONString();
    			}
    		}
    		
    	} catch(ParseException e) {
    		//unable to parse, invalid response
    		logger.log(e.toString());
    		httpResponse = new ExtendEndDateResponse(400);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
        
    	if(!processed) {
    		//if neither OPTIONS nor parse error, further process the input
    		
    		ExtendEndDateRequest request = new Gson().fromJson(httpBody, ExtendEndDateRequest.class);
    		String secretCode = request.secretCode;
    		String newEnd = request.newEnd;
    		LocalDate newDate = null;
    		
    		try {
    			newDate = LocalDate.parse(newEnd);
    			Schedule sched = new ScheduleDAO().getSchedule(secretCode);
    			
    			if(sched == null) { //if we couldn't get a schedule from that secretCode
    				logger.log("Incorrect secret code");
    				httpResponse = new ExtendEndDateResponse(400);
            		jsonResponse.put("body", new Gson().toJson(httpResponse));
    			} else if(newDate.isBefore(sched.endDate)) {
    				//if the new date is before the end date, it's not being extended
    				logger.log("Invalid date");
    				httpResponse = new ExtendEndDateResponse(400);
            		jsonResponse.put("body", new Gson().toJson(httpResponse));
    			} else {
    				
    				sched.generateWeeks(sched.endDate.plusDays(1), newDate);
    				
    				if(updateEndDate(secretCode, newDate)) {
    					httpResponse = new ExtendEndDateResponse(200);
    					jsonResponse.put("body", new Gson().toJson(httpResponse));
    				} else {
    					logger.log("Database issue");
    					httpResponse = new ExtendEndDateResponse(400);
    	        		jsonResponse.put("body", new Gson().toJson(httpResponse));
    				}
    				
    			}
    			
    		} catch(Exception e) {
    			logger.log(e.toString());
    			httpResponse = new ExtendEndDateResponse(400);
    			jsonResponse.put("body", new Gson().toJson(httpResponse));
    		}
    		
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    	
    }

}
