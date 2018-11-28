package edu.wpi.sargas.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 /*           PreparedStatement ps = conn.prepareStatement("SELECT * FROM Constants WHERE name = ?;");
            ps.setString(1, schedule.name);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                Constant c = generateConstant(resultSet);
                resultSet.close();
                return false;
            }
*/
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Schedule (scheduleID, name, startDate, endDate, startHour, endHour, timeslotDuration, dateCreated, secretCode) values(?,?,?,?,?,?,?,?,?);");
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
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to create schedule: " + e.getMessage());
        }
    }
}
