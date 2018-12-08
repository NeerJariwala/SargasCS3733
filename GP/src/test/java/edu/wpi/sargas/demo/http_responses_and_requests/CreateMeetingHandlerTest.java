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

import edu.wpi.sargas.db.DayDAO;
import edu.wpi.sargas.db.TimeslotDAO;
import edu.wpi.sargas.db.WeekDAO;
import edu.wpi.sargas.demo.TestContext;
import edu.wpi.sargas.demo.entity.Day;
import edu.wpi.sargas.demo.entity.Schedule;
import edu.wpi.sargas.demo.entity.Week;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class CreateMeetingHandlerTest {
	
	Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
	
    private static final String SAMPLE_INPUT_STRING = "{\"name\": \"Meeting\", \"timeslotId\": \"ID\"}";
    

    @Test
    public void testcreateMeetingHandler() throws IOException {
    	
    	Schedule testSched = null;
		String timeslotId = null;
    	
		try {
			testSched = new Schedule(20,"TestSched1",LocalDate.of(2002, 1, 1), LocalDate.of(2002, 2, 1),8,16);
			Week w = new WeekDAO().getWeeks(testSched.getScheduleID()).get(0);
			Day d = new DayDAO().getDays(w.WeekID).get(0);
			timeslotId = new TimeslotDAO().getTimeslots(d.DayID).get(0).timeslotID;
			} catch(Exception e) {
				System.out.println("problem");
			}
    	CreateMeetingRequest request = new CreateMeetingRequest();
    	request.name = "Name";
    	request.timeslotId = timeslotId;
		String jsonRequest = new Gson().toJson(request);
		
        CreateMeetingHandler handler = new CreateMeetingHandler();

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
}
