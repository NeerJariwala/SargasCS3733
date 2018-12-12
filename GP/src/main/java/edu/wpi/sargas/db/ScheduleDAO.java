package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Timestamp;
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
            ps.setTimestamp(8, Timestamp.valueOf(schedule.dateCreated));
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
            throw new Exception("Failed in getting Schedule: " + e.getMessage());
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

    public boolean extendstartDate(String scheduleID, LocalDate newstart) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Schedule SET startDate = ? WHERE scheduleID = ?;");
            ps.setDate(1,  Date.valueOf(newstart));
            ps.setString(2, scheduleID);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete schedule: " + e.getMessage());
        }
    }
    
    public boolean extendendDate(String scheduleID, LocalDate newend) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Schedule SET endDate = ? WHERE scheduleID = ?;");
            ps.setDate(1,  Date.valueOf(newend));
            ps.setString(2, scheduleID);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete schedule: " + e.getMessage());
        }
    }
   
    
    public ArrayList<Schedule> retrieveSchedules(LocalDateTime datetime) throws Exception {
    	ArrayList<Schedule> result = new ArrayList<Schedule>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE dateCreated >= '?';");
            ps.setTimestamp(1, Timestamp.valueOf(datetime.toString()));
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
            	result.add(generateSchedule(resultSet));
            }
            
            return result;

        } catch (Exception e) {
            throw new Exception("Failed to get Schedules: " + e.getMessage());
        }
    }
    
    

    public void deleteSchedulebefore(LocalDateTime datetime) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Schedule WHERE dateCreated <= '?';");
            ps.setTimestamp(1, Timestamp.valueOf(datetime));
            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {
            throw new Exception("Failed to delete schedules: " + e.getMessage());
        }
    }
    
    private Schedule generateSchedule(ResultSet resultSet) throws Exception {
        return new Schedule (resultSet.getInt("timeslotDuration"), resultSet.getString("scheduleId"), resultSet.getString("name"), resultSet.getDate("startDate").toLocalDate(), resultSet.getDate("endDate").toLocalDate(), resultSet.getInt("startHour"), resultSet.getInt("endHour"), resultSet.getString("secretCode"), resultSet.getTimestamp("dateCreated").toLocalDateTime());
    }
}
