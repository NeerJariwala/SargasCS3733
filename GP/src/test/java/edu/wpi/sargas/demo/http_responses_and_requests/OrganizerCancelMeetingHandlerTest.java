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

import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.demo.TestContext;
import edu.wpi.sargas.demo.entity.Schedule;
import edu.wpi.sargas.demo.entity.Meeting;
import edu.wpi.sargas.demo.http_responses_and_requests.CreateScheduleHandler;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class OrganizerCancelMeetingHandlerTest {
	
	Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
	
    static String SAMPLE_INPUT_STRING = "{\"meetingId\": ";
    Schedule sched = new Schedule(20,"name",LocalDate.of(2000, 1, 1), LocalDate.of(2000, 2, 1), 4,16);
    Meeting meeting = new Meeting("meeting", sched.weeks.get(0).days.get(0).timeslots.get(0).timeslotID);

    @Test
    public void testOrganizerCancelMeetingHandler() throws IOException {
    	ScheduleDAO dao = new ScheduleDAO();
    	
    	try {
    	dao.createSchedule(sched);
    	} catch(Exception e) {
    		System.out.println("problem");
    	}
    	
        OrganizerCancelMeetingHandler handler = new OrganizerCancelMeetingHandler();
        
        String testInput = SAMPLE_INPUT_STRING + "\"" + meeting.meetingID + "\" \"secretCode\": \"" + sched.getSecretCode() + "\"}";
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
        	System.out.println("problem");
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "200");
        
    }
    
    @Test
    public void testError() throws IOException {
    	ScheduleDAO dao = new ScheduleDAO();
    	
    	try {
    	dao.createSchedule(sched);
    	} catch(Exception e) {
    		System.out.println("problem");
    	}
    	
        OrganizerCancelMeetingHandler handler = new OrganizerCancelMeetingHandler();
        
        String testInput = SAMPLE_INPUT_STRING + "\"" + meeting.meetingID + "\" \"secretCode\": \"" + "does not exist" + "\"}";
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
        	System.out.println("problem");
        }
        
        Assert.assertEquals(body.get("httpCode").toString(), "400");
        
    }
}
