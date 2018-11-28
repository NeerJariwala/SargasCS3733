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

import edu.wpi.sargas.demo.entity.Schedule;

public class CreateScheduleHandler implements RequestStreamHandler {
	
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        
    	LambdaLogger logger = context.getLogger();
    	logger.log("Beginning to create schedule");
    	
    	JSONObject jsonResponse = new JSONObject();
    	CreateScheduleResponse httpResponse = null;
    	String httpBody = null;
    	boolean processed = false;
    	
    	try {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    		JSONParser parser = new JSONParser();
    		JSONObject jsonRequest = (JSONObject)parser.parse(reader);
    		//parse the request
    		
    		//logger.log("Input was " + jsonRequest.toJSONString());
    		String requestType = (String)jsonRequest.get("httpMethod");
    		
    		if(requestType != null && requestType.equalsIgnoreCase("OPTIONS")) {
    			//if we got an OPTIONS request, provide a 200 response
    			logger.log("Options request");
    			httpResponse = new CreateScheduleResponse(200, null, null);
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
    		httpResponse = new CreateScheduleResponse(400, null, null);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
    	
    	if(!processed) { 
    		//if it's not OPTIONS and not a parse exception, further processing must be done
    		boolean invalidInput = false;
    		
    		CreateScheduleRequest request = new Gson().fromJson(httpBody,CreateScheduleRequest.class);
    		//get the request in the form of a class
    		String name = request.name;
    		int duration = request.duration;
    		LocalDate sd = null;
    		LocalDate ed = null;
    		
    		try {
    			sd = LocalDate.parse(request.startDate);
    			ed = LocalDate.parse(request.endDate);
    		} catch(DateTimeParseException e) {
    			logger.log(e.toString());
        		httpResponse = new CreateScheduleResponse(400, null, null);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
        		invalidInput = true;
    		} catch(NullPointerException e) {
    			httpResponse = new CreateScheduleResponse(400, null, null);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
        		invalidInput = true;
    		}
    		//TODO: Above only works for date format YYYY-MM-DD. Ensure that format is used in the requests
    		int startHour = request.startHour;
    		int endHour = request.endHour;
    		
    		if(startHour < 0 || startHour > 23 || endHour <= startHour || endHour > 23) {
    			//if hour input is invalid, 400
    			logger.log("Invalid hours");
    			httpResponse = new CreateScheduleResponse(400, null, null);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
        		invalidInput = true;
    		}
    		
    		if(!invalidInput) { //if there was no invalid input, prepare success response
    			Schedule responseSched = new Schedule(duration,name,sd,ed,startHour,endHour);
    			httpResponse = new CreateScheduleResponse(200, responseSched, responseSched.getSecretCode());
    			jsonResponse.put("body", new Gson().toJson(httpResponse));
    			System.out.println(responseSched.getSecretCode());
    			//TODO: Use DAO to add schedule to database
    		}
    		
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    }

}
