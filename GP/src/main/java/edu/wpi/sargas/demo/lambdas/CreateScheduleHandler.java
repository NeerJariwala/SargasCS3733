package edu.wpi.sargas.demo.lambdas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import edu.wpi.sargas.demo.http_responses_and_requests.CreateScheduleResponse;

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
    		
    		logger.log("Input was " + jsonRequest.toJSONString());
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
    		
    		
    		
    	}
    	
    }

}
