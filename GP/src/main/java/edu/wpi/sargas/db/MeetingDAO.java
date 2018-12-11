package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Day;
import edu.wpi.sargas.demo.entity.Meeting;
import edu.wpi.sargas.demo.entity.Schedule;

public class MeetingDAO {

	java.sql.Connection conn;

    public MeetingDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }
	
    public boolean createMeeting(Meeting meeting) throws Exception {
        try {
        	
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meeting WHERE meetingID = ?;");
            ps.setString(1, meeting.meetingID);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                if(resultSet.getString("meetingID") == meeting.meetingID) {
                    resultSet.close();
                    ps.close();
                    return false;
                }
            }
            
        	ps = conn.prepareStatement("INSERT INTO Meeting (meetingID, name, Timeslot, secretCode) values(?,?,?,?);");
            ps.setString(1, meeting.meetingID);
            ps.setString(2, meeting.name);
            ps.setString(3, meeting.timeslot);
            ps.setString(4, meeting.secretCode);
            ps.execute();
            
        	ps = conn.prepareStatement("UPDATE Timeslot SET open = 0 WHERE TimeslotID = ?;");
            ps.setString(1, meeting.timeslot);
            ps.executeUpdate();
            ps.close();

            ps.close();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to create meeting: " + e.getMessage());
        }
    }
    
    public boolean deleteMeeting(String meetingID) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Meeting WHERE meetingID = ?;");
            ps.setString(1, meetingID);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete meeting: " + e.getMessage());
        }
    }
    
    public boolean deleteMeetingPart(String secretCode) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Meeting WHERE secretCode = ?;");
            ps.setString(1, secretCode);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete meeting: " + e.getMessage());
        }
    }
    
    public Meeting getMeeting(String meetingID) throws Exception {
        try {
            Meeting result = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meeting WHERE MeetingID = ?;");
            ps.setString(1,  meetingID);
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                result = generateMeeting(resultSet);
            }
            resultSet.close();
            ps.close();
            
            return result;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting Meeting: " + e.getMessage());
        }
    }
    
    public Meeting getsecretMeeting(String secretCode) throws Exception {
        try {
            Meeting result = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meeting WHERE secretCode = ?;");
            ps.setString(1,  secretCode);
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                result = generateMeeting(resultSet);
            }
            resultSet.close();
            ps.close();
            
            return result;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting Meeting: " + e.getMessage());
        }
    }

    private Meeting generateMeeting(ResultSet resultSet) throws Exception {
        return new Meeting (resultSet.getString("MeetingID"), resultSet.getString("name"), resultSet.getString("Timeslot"), resultSet.getString("secretCode"));
    }


}
