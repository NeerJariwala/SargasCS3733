package edu.wpi.sargas.demo.http_responses_and_requests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

import edu.wpi.sargas.db.*;
import edu.wpi.sargas.demo.TestContext;
import edu.wpi.sargas.demo.entity.*;
import edu.wpi.sargas.demo.http_responses_and_requests.CloseTimeSlotHandler;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class CloseTimeSlotHandlerTest {
	
	Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
	
    static String SAMPLE_INPUT_STRING = "{\"timeslotID\": \"";

    @Test
    public void testCloseTimeSlotHandler() throws IOException {
    	ScheduleDAO sched_dao = new ScheduleDAO();
    	WeekDAO week_dao = new WeekDAO();
    	DayDAO day_dao = new DayDAO();
    	TimeslotDAO slot_dao = new TimeslotDAO();
    	ArrayList<Timeslot> slots = new ArrayList<Timeslot>();
    	ArrayList<Week> weeks = new ArrayList<Week>();
    	ArrayList<Day> days = new ArrayList<Day>();
    	Week w = null;
    	String dayID = null;
    	String tsID = "asdf";
    	
    	
    	Schedule sched = null;
    	
    	try {
    		sched = new Schedule(60,"name",LocalDate.of(2018, 12, 10), LocalDate.of(2018, 12, 11), 4,6);
    		weeks = week_dao.getWeeks(sched.scheduleId);
    		w = weeks.get(0);
    		days = day_dao.getDays(w.WeekID);
    		dayID = days.get(0).DayID;
    		slots = slot_dao.getTimeslots(dayID);
    		tsID = slots.get(0).timeslotID;
    	} catch(Exception e) {
    		e.printStackTrace();
    		System.out.println("problem");
    	}
    	
        CloseTimeSlotHandler handler = new CloseTimeSlotHandler();
        
        String testInput = SAMPLE_INPUT_STRING + tsID +"\" \"status\": \"" + 0 + "\"}";
        System.out.println(testInput);
        
        InputStream input = new ByteArrayInputStream(testInput.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("random"));
        
        String sampleOutputString = output.toString();
        System.out.println(sampleOutputString);
        JSONObject response = null;
        JSONObject body = null;
        try {
        	System.out.println(sampleOutputString);
        	response = (JSONObject)new JSONParser().parse(sampleOutputString);
        	body = (JSONObject)new JSONParser().parse(response.get("body").toString());
        } catch(ParseException e) {
        	e.printStackTrace();
        	System.out.println(e.toString());
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "200");
        
    }
    
    @Test
    public void testError() throws IOException {
    	ScheduleDAO dao = new ScheduleDAO();
    	Schedule sched = null;
    	String tsID = "asdf";
    	
    	try {
    		sched = new Schedule(60,"name",LocalDate.of(2018, 12, 10), LocalDate.of(2018, 12, 11), 4,6);
    	} catch(Exception e) {
    		e.printStackTrace();
    		System.out.println("problem");
    	}
        CloseTimeSlotHandler handler = new CloseTimeSlotHandler();
        
        String testInput = SAMPLE_INPUT_STRING + tsID +"\" \"status\": \"" + 1 + "\"}";
        System.out.println(testInput);
        
        InputStream input = new ByteArrayInputStream(testInput.getBytes());;
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
        	e.printStackTrace();
        	System.out.println(e.toString());
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "400");
        
    }
}
