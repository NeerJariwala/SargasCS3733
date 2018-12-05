package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
    public boolean createWeek(Week week) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Week WHERE WeekID = ?;");
            ps.setString(1, week.WeekID);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                if(resultSet.getString("weekID") == week.WeekID) {
                    resultSet.close();
                    return false;
                }
            }
        	ps = conn.prepareStatement("INSERT INTO Week (WeekID, startDate, endDate, Schedule) values(?,?,?,?);");
            ps.setString(1, week.WeekID);
            ps.setDate(2, Date.valueOf(week.startDate));
            ps.setDate(3, Date.valueOf(week.endDate));
            ps.setString(4, week.schedule);
            ps.execute();

            ps.close();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to create week: " + e.getMessage());
        }
    }



}
