package edu.wpi.sargas.db;

import java.sql.PreparedStatement;

import edu.wpi.sargas.db.DatabaseUtil;
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
	
    public void createTimeslot(Timeslot timeslot) throws Exception {
        try {
        	PreparedStatement ps = conn.prepareStatement("INSERT INTO Timeslot (TimeslotID, open, duration, startTime, Day) values(?,?,?,?,?);");
            ps.setString(1, timeslot.timeslotID);
            ps.setInt(2, timeslot.open);
            ps.setInt(3, timeslot.duration);
            ps.setInt(4, timeslot.startTime);
            ps.setString(5, timeslot.day);
            ps.execute();

            ps.close();

        } catch (Exception e) {
            throw new Exception("Failed to create timeslot: " + e.getMessage());
        }
    }



}
