package edu.wpi.sargas.demo.http_responses_and_requests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import com.google.gson.Gson;

import edu.wpi.sargas.db.DayDAO;
import edu.wpi.sargas.db.ScheduleDAO;
import edu.wpi.sargas.db.TimeslotDAO;
import edu.wpi.sargas.db.WeekDAO;
import edu.wpi.sargas.demo.TestContext;
import edu.wpi.sargas.demo.entity.Day;
import edu.wpi.sargas.demo.entity.Schedule;
import edu.wpi.sargas.demo.entity.Timeslot;
import edu.wpi.sargas.demo.entity.Week;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class SearchTimeslotsHandlerTest {

    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"FOO\": \"BAR\"}";
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    @Test
    public void testSearchTimeslotsHandler() throws IOException {
        SearchTimeslotsHandler handler = new SearchTimeslotsHandler();
        Schedule sched = null;
        try {
        	sched = new Schedule(30,"Name",LocalDate.of(2001,1,1),LocalDate.of(2001, 1, 3),10,12);
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
        
        SearchTimeslotsRequest request = new SearchTimeslotsRequest(sched.secretCode,"1","","","");
        String jsonRequest = new Gson().toJson(request);
        InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("random"));

        // TODO: validate output here if needed.
        String sampleOutputString = output.toString();
        System.out.println(sampleOutputString);
        
        JSONObject jsonResponse = null;
        JSONObject body = null;
        try {
        	jsonResponse = (JSONObject)new JSONParser().parse(sampleOutputString);
        	body = (JSONObject)new JSONParser().parse(jsonResponse.get("body").toString());
        } catch(ParseException e) {
        	System.out.println("problem");
        }
        SearchTimeslotsResponse response = new Gson().fromJson(body.toJSONString(), SearchTimeslotsResponse.class);
        System.out.println(response.timeslots.size());
        assertEquals(body.get("httpCode").toString(), "200");
        
    }
    
    @Test
    public void testClosed() throws IOException {
        SearchTimeslotsHandler handler = new SearchTimeslotsHandler();
        Schedule sched = null;
        try {
        	sched = new Schedule(30,"Name",LocalDate.of(2001,1,1),LocalDate.of(2001, 1, 3),10,12);
        	ArrayList<Week> weeks = new WeekDAO().getWeeks(sched.scheduleId);
        	ArrayList<Day> days = new DayDAO().getDays(weeks.get(0).WeekID);
        	ArrayList<Timeslot> timeslots = new TimeslotDAO().getTimeslots(days.get(0).DayID);
        	new TimeslotDAO().changeTimeslot(timeslots.get(0).timeslotID, 0);
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
        
        SearchTimeslotsRequest request = new SearchTimeslotsRequest(sched.secretCode,"1","","","");
        String jsonRequest = new Gson().toJson(request);
        InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("random"));

        // TODO: validate output here if needed.
        String sampleOutputString = output.toString();
        System.out.println(sampleOutputString);
        
        JSONObject jsonResponse = null;
        JSONObject body = null;
        try {
        	jsonResponse = (JSONObject)new JSONParser().parse(sampleOutputString);
        	body = (JSONObject)new JSONParser().parse(jsonResponse.get("body").toString());
        } catch(ParseException e) {
        	System.out.println("problem");
        }
        SearchTimeslotsResponse response = new Gson().fromJson(body.toJSONString(), SearchTimeslotsResponse.class);
        System.out.println(response.timeslots.size());
        assertEquals(response.timeslots.size(),11); //we closed one, so there should be 11 instead of 12
        assertEquals(body.get("httpCode").toString(), "200");
        
    }
    
    @Test
    public void testCriteria() throws IOException {
        SearchTimeslotsHandler handler = new SearchTimeslotsHandler();
        Schedule sched = null;
        try {
        	sched = new Schedule(30,"Name",LocalDate.of(2001,1,1),LocalDate.of(2001, 1, 3),10,12);
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
        
        SearchTimeslotsRequest request = new SearchTimeslotsRequest(sched.secretCode,"2","","","");
        //put a criteria none will satisfy
        String jsonRequest = new Gson().toJson(request);
        InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("random"));

        // TODO: validate output here if needed.
        String sampleOutputString = output.toString();
        System.out.println(sampleOutputString);
        
        JSONObject jsonResponse = null;
        JSONObject body = null;
        try {
        	jsonResponse = (JSONObject)new JSONParser().parse(sampleOutputString);
        	body = (JSONObject)new JSONParser().parse(jsonResponse.get("body").toString());
        } catch(ParseException e) {
        	System.out.println("problem");
        }
        SearchTimeslotsResponse response = new Gson().fromJson(body.toJSONString(), SearchTimeslotsResponse.class);
        assertNull(response.timeslots); //we closed one, so there should be 11 instead of 12
        assertEquals(body.get("httpCode").toString(), "200");
        
    }
    
}
