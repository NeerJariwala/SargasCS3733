package edu.wpi.sargas.demo.http_responses_and_requests;

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

public class CreateMeetingHandler implements RequestStreamHandler {
	
	private void errorResponse(JSONObject response) {
		CreateMeetingResponse httpResponse = new CreateMeetingResponse(400, null);
		response.put("body", new Gson().toJson(httpResponse));
	}
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    	
    	LambdaLogger logger = context.getLogger();
        logger.log("Beginning to create meeting");
    	
        JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
    	headerJson.put("Access-Control-Allow-Headers", "*");
    	
    	JSONObject jsonResponse = new JSONObject();
    	jsonResponse.put("headers", headerJson);
    	CreateMeetingResponse httpResponse = null;
    	String httpBody = null;
    	boolean processed = false;
    	
    	try {
    		
    		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    		JSONParser parser = new JSONParser();
    		JSONObject jsonRequest = (JSONObject)parser.parse(reader);
    		//parse request, turn input into a json object
    		
    		logger.log("Input was " + jsonRequest.toJSONString());
    		String requestType = (String)jsonRequest.get("httpMethod");
    		
    		if(requestType != null && requestType.equalsIgnoreCase("OPTIONS")) {
    			//if OPTIONS request, provide 200 response
    			logger.log("Options request");
    			httpResponse = new CreateMeetingResponse(200,null);
    			processed = true;
    		} else {
    			httpBody = (String)jsonRequest.get("body");
    			if(httpBody == null) {
    				httpBody = jsonRequest.toJSONString();
    			}
    		}
    		
    	} catch(ParseException e) {
    		//if unable to parse, respond as invalid input
    		logger.log(e.toString());
    		errorResponse(jsonResponse);
    	}
    	
    	if(!processed) {
    		
    	}
    	
    }

}
