package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.sql.Date;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Meeting;

public class ScheduleDAO {

	java.sql.Connection conn;

    public ScheduleDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }
	
    public void createMeeting(Meeting meeting) throws Exception {
        try {
            ps = conn.prepareStatement("INSERT INTO Meeting (meetingID, name, Timeslot) values(?,?,?);");
            ps.setString(1, meeting.meetingID);
            ps.setString(2, meeting.name);
            ps.setDate(3, Date.valueOf(meeting.timelsotId));
            ps.execute();

            ps.close();

        } catch (Exception e) {
            throw new Exception("Failed to create schedule: " + e.getMessage());
        }
    }
    
    public boolean deleteMeeting(String meetingID) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Meeting WHERE meetingID = ?;");
            ps.setString(1, meetingID);
            int numAffected = ps.executeUpdate();
            ps.close();6
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete meeting: " + e.getMessage());
        }
    }


}
