package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.UUID;
import java.sql.Date;

import edu.wpi.sargas.db.DatabaseUtil;
import edu.wpi.sargas.demo.entity.Schedule;

public class ScheduleDAO {

	java.sql.Connection conn;

    public ScheduleDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }
	
    public boolean createSchedule(Schedule schedule) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleID = ?;");
            ps.setString(1, schedule.scheduleId);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                if(resultSet.getString("scheduleID") == schedule.scheduleId) {
                    resultSet.close();
                    return false;
                }
            }

            ps = conn.prepareStatement("INSERT INTO Schedule (scheduleID, name, startDate, endDate, startHour, endHour, timeslotDuration, dateCreated, secretCode) values(?,?,?,?,?,?,?,?,?);");
            ps.setString(1, schedule.scheduleId);
            ps.setString(2, schedule.name);
            ps.setDate(3, Date.valueOf(schedule.startDate));
            ps.setDate(4, Date.valueOf(schedule.endDate));
            ps.setInt(5, schedule.startHour);
            ps.setInt(6, schedule.endHour);
            ps.setInt(7, schedule.timeslotDuration);
            ps.setDate(8, Date.valueOf(schedule.dateCreated));
            ps.setString(9, schedule.secretCode);
            ps.execute();
            
            
            ps.close();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to create schedule: " + e.getMessage());
        }
    }
    
    public Schedule getSchedule(String secretCode) throws Exception {
        try {
            Schedule result = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE secretCode = ?;");
            ps.setString(1,  secretCode);
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                result = generateSchedule(resultSet);
            }
            resultSet.close();
            ps.close();
            
            return result;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting constant: " + e.getMessage());
        }
    }
    
    public boolean deleteSchedule(String scheduleID) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Schedule WHERE scheduleID = ?;");
            ps.setString(1, scheduleID);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete schedule: " + e.getMessage());
        }
    }
    
    private Schedule generateSchedule(ResultSet resultSet) throws Exception {
        return new Schedule (resultSet.getInt("timeslotDuration"), resultSet.getString("scheduleId"), resultSet.getString("name"), resultSet.getDate("startDate").toLocalDate(), resultSet.getDate("endDate").toLocalDate(), resultSet.getInt("startHour"), resultSet.getInt("endHour"), resultSet.getString("secretCode"), resultSet.getDate("dateCreated").toLocalDate());
    }
    
    
    
    public boolean validateSecretCode(String secretCode) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule;");
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                if(resultSet.getString("secretCode").equals(secretCode)) {
                    resultSet.close();
                    ps.close();
                    return true;
                }
            }
            ps.close();
            return false;

        } catch (Exception e) {
            throw new Exception("Invalid SecretCode: " + e.getMessage());
        }
    }

    

}
