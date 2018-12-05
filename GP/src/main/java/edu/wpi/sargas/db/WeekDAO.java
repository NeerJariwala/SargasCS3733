package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.Date;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Week;

public class WeekDAO {

	java.sql.Connection conn;

    public WeekDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }
	
    public void createWeek(Week week) throws Exception {
        try {
        	PreparedStatement ps = conn.prepareStatement("INSERT INTO Meeting (WeekID, startDate, endDate, Schedule) values(?,?,?,?);");
            ps.setString(1, week.WeekID);
            ps.setDate(2, Date.valueOf(week.startDate));
            ps.setDate(3, Date.valueOf(week.endDate));
            ps.setString(4, week.schedule);
            ps.execute();

            ps.close();

        } catch (Exception e) {
            throw new Exception("Failed to create week: " + e.getMessage());
        }
    }



}
