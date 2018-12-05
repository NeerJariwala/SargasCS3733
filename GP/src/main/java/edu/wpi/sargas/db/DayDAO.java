package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.Date;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Day;

public class DayDAO {

	java.sql.Connection conn;

    public DayDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }
	
    public void createDay(Day day) throws Exception {
        try {
        	PreparedStatement ps = conn.prepareStatement("INSERT INTO Day (DayID, date, Week) values(?,?,?);");
            ps.setString(1, day.DayID);
            ps.setDate(2, Date.valueOf(day.date));
            ps.setString(3, day.week);
            ps.execute();

            ps.close();

        } catch (Exception e) {
            throw new Exception("Failed to create day: " + e.getMessage());
        }
    }



}
