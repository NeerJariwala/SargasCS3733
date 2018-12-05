package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.sql.Date;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Meeting;

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
            
        	ps = conn.prepareStatement("INSERT INTO Meeting (meetingID, name, Timeslot) values(?,?,?);");
            ps.setString(1, meeting.meetingID);
            ps.setString(2, meeting.name);
            ps.setString(3, meeting.timeslot);
            ps.execute();

            ps.close();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to create schedule: " + e.getMessage());
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


}
