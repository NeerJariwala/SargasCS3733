package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Date;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Day;
import edu.wpi.sargas.demo.entity.Week;

public class DayDAO {

	java.sql.Connection conn;

    public DayDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }
	
    public boolean createDay(Day day) throws Exception {
        try {
        	
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Day WHERE DayID = ?;");
            ps.setString(1, day.DayID);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                if(resultSet.getString("DayID") == day.DayID) {
                    resultSet.close();
                    ps.close();
                    return false;
                }
            }
            
        	ps = conn.prepareStatement("INSERT INTO Day (DayID, date, Week) values(?,?,?);");
            ps.setString(1, day.DayID);
            ps.setDate(2, Date.valueOf(day.date));
            ps.setString(3, day.week);
            ps.execute();

            ps.close();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to create day: " + e.getMessage());
        }
    }
    
    public ArrayList<Day> getDays(String weekID) throws Exception {
    	ArrayList<Day> result = new ArrayList<Day>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Day WHERE Week = ? ORDER BY date;");
            ps.setString(1, weekID);
            ResultSet resultSet = ps.executeQuery();
            

            while (resultSet.next()) {
            	result.add(generateDay(resultSet));
            }
            
            return result;

        } catch (Exception e) {
            throw new Exception("Failed to get days: " + e.getMessage());
        }
    }

    private Day generateDay(ResultSet resultSet) throws Exception {
        return new Day (resultSet.getString("DayID"), resultSet.getDate("date").toLocalDate(), resultSet.getString("week"));
    }



}
