package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import edu.wpi.sargas.demo.entity.Schedule;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class RetrieveSchedulesHandlerTest {


    @Test
    public void testDaysAgo() throws IOException {
    	
    	Schedule sched = null;
    	
    	try {
    		sched = new Schedule(60, "Name", LocalDate.of(2001, 1, 1), LocalDate.of(2001, 1, 2), 7, 8, LocalDateTime.now().minusDays(3));
    	} catch(Exception e) {
    		System.out.println(e.toString());
    	}
    	
    	RetrieveSchedulesRequest request = new RetrieveSchedulesRequest();
    	request.daysAgo = "3";
    	String httpRequest = new Gson().toJson(request);
        RetrieveSchedulesHandler handler = new RetrieveSchedulesHandler();

        InputStream input = new ByteArrayInputStream(httpRequest.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, null);

        String sampleOutputString = output.toString();
        System.out.println(sampleOutputString);
        JSONObject response = null;
        JSONObject body = null;
        try {
        	response = (JSONObject)new JSONParser().parse(sampleOutputString);
        	body = (JSONObject)new JSONParser().parse(response.get("body").toString());
        } catch(ParseException e) {
        	System.out.println("problem");
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "200");
    }
    
    
    
}
