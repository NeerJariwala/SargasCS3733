package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
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

public class SysadminDeleteSchedulesHandler implements RequestStreamHandler {
	
	private void deleteSchedules(LocalDateTime before) throws Exception {
		//Use DAO to get schedules after the localDateTime passed in
		new ScheduleDAO().deleteSchedulebefore(before);
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
    	SysadminDeleteSchedulesResponse httpResponse = null;
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
    			httpResponse = new SysadminDeleteSchedulesResponse(200);
    			//no schedule/secret code needed because it was an options request
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
    		httpResponse = new SysadminDeleteSchedulesResponse(400);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
    	
    	if(!processed) {
    		//if it's not OPTIONS and not parse exception, should be further processed
    		
    		SysadminDeleteSchedulesRequest request = new Gson().fromJson(httpBody,SysadminDeleteSchedulesRequest.class);
    		//get the request in the form of a class
    		String days = request.days;
    		
    		if(days == null) { //return invalid if null
    			httpResponse = new SysadminDeleteSchedulesResponse(400);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		} else {
    			
    			try {
    				
    				int numDays = Integer.parseInt(days);
    				LocalDateTime lowerBound = LocalDateTime.now().minusDays(numDays);
    				deleteSchedules(lowerBound);
    				
    				httpResponse = new SysadminDeleteSchedulesResponse(200);
            		jsonResponse.put("body", new Gson().toJson(httpResponse));
    				
    			} catch(Exception e) {
    				logger.log(e.toString());
    				e.printStackTrace();
    				httpResponse = new SysadminDeleteSchedulesResponse(400);
            		jsonResponse.put("body", new Gson().toJson(httpResponse));
    			}
    			
    			
    		}
    		
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    	
    }

}
