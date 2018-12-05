package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.util.ArrayList;
import java.sql.Time;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Day;
import edu.wpi.sargas.demo.entity.Timeslot;

public class TimeslotDAO {

	java.sql.Connection conn;

    public TimeslotDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }
	
    public boolean createTimeslot(Timeslot timeslot) throws Exception {
        try {
        	
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE timeslotID = ?;");
            ps.setString(1, timeslot.timeslotID);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                if(resultSet.getString("timeslotID") == timeslot.timeslotID) {
                    resultSet.close();
                    ps.close();
                    return false;
                }
            }
            
        	ps = conn.prepareStatement("INSERT INTO Timeslot (TimeslotID, open, duration, startTime, Day) values(?,?,?,?,?);");
            ps.setString(1, timeslot.timeslotID);
            ps.setInt(2, timeslot.open);
            ps.setInt(3, timeslot.duration);
            ps.setTime(4, Time.valueOf(timeslot.startTime));
            ps.setString(5, timeslot.day);
            ps.execute();

            ps.close();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to create timeslot: " + e.getMessage());
        }
    }
    
    public boolean changeTimeslot(String timeslotID, String status) throws Exception{
        try {
        	String query = "UPDATE Timeslot SET open = ? WHERE TimeslotID = ?;";
        	PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setString(2, timeslotID);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);
        } catch (Exception e) {
            throw new Exception("Failed to update Timeslot: " + e.getMessage());
        }
    	
    }
    
    public ArrayList<Timeslot> getTimeslots(String dayID) throws Exception {
    	ArrayList<Timeslot> result = new ArrayList<Timeslot>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE Day = ?;");
            ps.setString(1, dayID);
            ResultSet resultSet = ps.executeQuery();
            

            while (resultSet.next()) {
            	result.add(generateTimeslot(resultSet));
            }
            
            return result;

        } catch (Exception e) {
            throw new Exception("Failed to get timeslots: " + e.getMessage());
        }
    }

    private Timeslot generateTimeslot(ResultSet resultSet) throws Exception {
        return new Timeslot (resultSet.getString("TimeslotID"), resultSet.getInt("open"), resultSet.getInt("duration"), resultSet.getTime("startTime").toLocalTime(), resultSet.getString("Day"));
    }



}