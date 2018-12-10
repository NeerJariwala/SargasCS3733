package edu.wpi.sargas.demo;

import static org.junit.Assert.*;
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
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.demo.entity.Schedule;
import edu.wpi.sargas.demo.http_responses_and_requests.ShowScheduleHandler;
import org.junit.Test;

public class ShowScheduleHandlerTest {
	
	String testInput1 = "{\"date\": \"2002-01-03\",";
	String testInput2 = "{\"date\": \"2002-02-03\",";
	
	@Before
	public void setUp() {
		
	}
	
	Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
	
	
	@Test
	public void testHandleRequest() throws IOException {
		
		Schedule testSched = null;
		
		try {
			testSched = new Schedule(20,"TestSched1",LocalDate.of(2002, 1, 1), LocalDate.of(2002, 2, 1),8,16);
			} catch(Exception e) {
				System.out.println("problem");
			}
		
		testInput1 += "\"secretCode\": \"" + testSched.getSecretCode() + "\"}"; //add the secret code
		
		ShowScheduleHandler handler = new ShowScheduleHandler();
		
		InputStream input = new ByteArrayInputStream(testInput1.getBytes());
		OutputStream output = new ByteArrayOutputStream();
		
        
		handler.handleRequest(input,output,createContext("random"));
		
		String outputString = output.toString();
		System.out.println(outputString);
		JSONObject response = null;
        JSONObject body = null;
        
        
        try {
        	response = (JSONObject)new JSONParser().parse(outputString);
        	body = (JSONObject)new JSONParser().parse(response.get("body").toString());
        } catch(ParseException e) {
        	System.out.println("problem");
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "200");
        
	}
	
	@Test
	public void outsideWeek() throws IOException {
		
		Schedule testSched = null;
		
		try {
		 testSched = new Schedule(20,"TestSched1",LocalDate.of(2002, 1, 1), LocalDate.of(2002, 2, 1),8,16);
		} catch(Exception e) {
			System.out.println("problem");
		}
		
		testInput2 += "\"secretCode\": \"" + testSched.getSecretCode() + "\"}"; //add the secret code
		
		ShowScheduleHandler handler = new ShowScheduleHandler();
		
		InputStream input = new ByteArrayInputStream(testInput2.getBytes());
		OutputStream output = new ByteArrayOutputStream();
		
		ScheduleDAO dao = new ScheduleDAO();
        
        try {
        	dao.createSchedule(testSched);
        	//put it in the database first
        } catch(Exception e) {
        	return;
        }
		
		handler.handleRequest(input,output,createContext("random"));
		
		String outputString = output.toString();
		System.out.println(outputString);
		JSONObject response = null;
        JSONObject body = null;
        
        
        try {
        	response = (JSONObject)new JSONParser().parse(outputString);
        	body = (JSONObject)new JSONParser().parse(response.get("body").toString());
        } catch(ParseException e) {
        	System.out.println("problem");
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "400");
	}
	
	@Test
	public void notExist() throws IOException {
		testInput2 += "\"secretCode\": \"" + "does not exist" + "\"}"; //add the secret code
		ShowScheduleHandler handler = new ShowScheduleHandler();
		
		InputStream input = new ByteArrayInputStream(testInput2.getBytes());
		OutputStream output = new ByteArrayOutputStream();
		
		handler.handleRequest(input,output,createContext("random"));
		
		String outputString = output.toString();
		System.out.println(outputString);
		JSONObject response = null;
        JSONObject body = null;
        
        
        try {
        	response = (JSONObject)new JSONParser().parse(outputString);
        	body = (JSONObject)new JSONParser().parse(response.get("body").toString());
        } catch(ParseException e) {
        	System.out.println("problem");
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "400");
		
	}

}
