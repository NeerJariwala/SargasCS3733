package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import edu.wpi.sargas.db.DayDAO;
import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.db.TimeslotDAO;
import edu.wpi.sargas.db.WeekDAO;
import edu.wpi.sargas.demo.entity.Day;
import edu.wpi.sargas.demo.entity.Schedule;
import edu.wpi.sargas.demo.entity.Timeslot;
import edu.wpi.sargas.demo.entity.Week;

public class SearchTimeslotsHandler implements RequestStreamHandler {
	
	private void errorResponse(JSONObject response) {
		SearchTimeslotsResponse httpResponse = new SearchTimeslotsResponse(400,null);
		response.put("body", new Gson().toJson(httpResponse));
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
    	
    	SearchTimeslotsResponse httpResponse = null;
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
    			httpResponse = new SearchTimeslotsResponse(200, null);
    			//nothing needed because it was an options request
    			processed = true;
    		} else { //otherwise, check out the response body later
    			httpBody = (String)jsonRequest.get("body");
    			if(httpBody == null) {
    				httpBody = jsonRequest.toJSONString();
    			}
    		}
    		
    	} catch(ParseException e) {
    		logger.log(e.toString());
    		errorResponse(jsonResponse);
    		processed = true;
    	}
    	
    	if(!processed) {
    		
    		SearchTimeslotsRequest request = new Gson().fromJson(httpBody,SearchTimeslotsRequest.class);
    		String secretCode = request.secretCode;
    		String month = request.month;
    		String year = request.year;
    		String dayOfWeek = request.dayOfWeek;
    		String dayOfMonth = request.dayOfMonth;
    		
    		try {
    			Schedule sched = new ScheduleDAO().getSchedule(secretCode);
    			ArrayList<Week> weeks = new WeekDAO().getWeeks(sched.scheduleId);
    			ArrayList<Day> days = new ArrayList<Day>();
    			ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
    			
    			for(Week week : weeks) {
    				//get all of the days
    				days.addAll(new DayDAO().getDays(week.WeekID));
    				
    			}
    			Iterator<Day> iter = days.iterator();
    			
    			//loop filters out what we aren't searching for
    			while(iter.hasNext()) {
    				Day d = iter.next();
    				//filter out the days that don't meet search criteria
    				if(!month.equals("") && d.date.getMonthValue() != Integer.parseInt(month)) {
    					//if the date isn't in the month, delete it from arraylist
    					iter.remove();
    					continue;
    				}
    				
    				if(!year.equals("") && d.date.getYear() != Integer.parseInt(year)) {
    					iter.remove();
    					continue;
    				}
    				
    				if(!dayOfWeek.equals("") && d.date.getDayOfWeek().getValue() != Integer.parseInt(dayOfWeek)) {
    					iter.remove();
    					continue;
    				}
    				
    				if(!dayOfMonth.equals("") && d.date.getDayOfMonth() != Integer.parseInt(dayOfMonth)) {
    					iter.remove();
    					continue;
    				}
    				
    			}
    			
    			for(Day day : days) {
    				timeslots.addAll(new TimeslotDAO().getTimeslots(day.DayID));
    			}
    			
    			Iterator<Timeslot> timeslotIter = timeslots.iterator();
    			
    			//filter out timeslots that aren't open
    			while(iter.hasNext()) {
    				Timeslot t = timeslotIter.next();
    				
    				if(t.open == 0) {
    					timeslotIter.remove();
    				}
    				
    			}
    			
    			if(timeslots.isEmpty()) {
    				timeslots = null;
    			}
    			
    			httpResponse = new SearchTimeslotsResponse(200, timeslots);
    			jsonResponse.put("body", new Gson().toJson(httpResponse));
    			
    		} catch(Exception e) {
    			System.out.println(e.toString());
    			errorResponse(jsonResponse);
    		}
    		
    	}
    	
    	logger.log("result: " + jsonResponse.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(jsonResponse.toJSONString());  
        writer.close();
    	
    }

}
