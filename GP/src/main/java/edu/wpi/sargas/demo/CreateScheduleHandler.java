package edu.wpi.sargas.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class CreateScheduleHandler implements RequestStreamHandler {
	
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        
    	LambdaLogger logger = context.getLogger();
    	logger.log("Beginning to create schedule");
    	
    	JSONObject response = new JSONObject();
    }

}
