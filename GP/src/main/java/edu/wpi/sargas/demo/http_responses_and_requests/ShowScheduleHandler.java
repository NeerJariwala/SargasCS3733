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

import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.demo.entity.Schedule;
import edu.wpi.sargas.demo.entity.Week;

public class ShowScheduleHandler implements RequestStreamHandler {

	private Schedule showSchedule(String secretCode) throws Exception {
		ScheduleDAO dao = new ScheduleDAO();
		return dao.showSchedule(secretCode);
	}

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

    	LambdaLogger logger = context.getLogger();
    	logger.log("Beginning to create schedule");
    	
    	JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
    	
    	JSONObject jsonResponse = new JSONObject();
    	jsonResponse.put("headers", headerJson);
    	ShowScheduleResponse httpResponse = null;
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
    			httpResponse = new ShowScheduleResponse(200, null);
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
    		httpResponse = new ShowScheduleResponse(400, null);
    		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		processed = true;
    	}
    	
    	if(!processed) {
    		//if it's not OPTIONS and not a parse exception, further processing must be done
    		boolean invalidInput = false;
    		
    		ShowScheduleRequest request = new Gson().fromJson(httpBody,ShowScheduleRequest.class);
    		//get the request in the form of a class
    		Schedule sched = null;
    		LocalDate date = LocalDate.parse(request.date);
    		
    		try {
    			sched = showSchedule(request.secretCode);
    			
    			if(sched != null) {
    				Week week = sched.getWeekOf(date);

    				if(week != null) {
    					
    				}
    				
    			}
    			
    		} catch(Exception e) {
    			//400 response if database issue.
    			logger.log(e.toString());
        		httpResponse = new ShowScheduleResponse(400, null);
        		jsonResponse.put("body", new Gson().toJson(httpResponse));
    		}
    		
    		
    		
    	}
		
	}	
}
