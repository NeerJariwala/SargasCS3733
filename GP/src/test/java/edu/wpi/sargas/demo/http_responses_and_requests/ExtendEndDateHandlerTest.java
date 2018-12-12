package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

import edu.wpi.sargas.demo.TestContext;
import edu.wpi.sargas.demo.entity.Schedule;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class ExtendEndDateHandlerTest {
	
	Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }

    @Test
    public void testExtendEndDateHandler() throws IOException {
    	ExtendEndDateHandler handler = new ExtendEndDateHandler();
        
        ExtendEndDateRequest request = new ExtendEndDateRequest();
        
        Schedule sched = null;
        
        try {
        	sched = new Schedule(60, "Test",LocalDate.of(2001, 1, 1), LocalDate.of(2001, 1, 2),5,6);
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
        
        request.secretCode = sched.getSecretCode();
        request.newEnd = "2001-01-04";
        String jsonRequest = new Gson().toJson(request);
        
        InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("random"));
        
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
    
    @Test
    public void test2() throws IOException {
        ExtendEndDateHandler handler = new ExtendEndDateHandler();
        
        ExtendEndDateRequest request = new ExtendEndDateRequest();
        
        Schedule sched = null;
        
        try {
        	sched = new Schedule(60, "Test",LocalDate.of(2001, 1, 1), LocalDate.of(2001, 1, 5),5,6);
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
        
        request.secretCode = sched.getSecretCode();
        request.newEnd = "2001-01-02";
        String jsonRequest = new Gson().toJson(request);
        
        InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("random"));
        
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
        
        Assert.assertEquals(body.get("httpCode").toString(), "400");
        
    }
    
}
