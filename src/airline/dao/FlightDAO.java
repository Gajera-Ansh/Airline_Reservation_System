package airline.dao;

import airline.util.DBUtil;

import java.sql.Connection;
import java.sql.Statement;

public class FlightDAO {

    static {
        try {
            String sql = "UPDATE flights SET departure_time = DATE_ADD(departure_time, INTERVAL 1 DAY), arrival_time = DATE_ADD(arrival_time, INTERVAL 1 DAY)";
//            String sql = "UPDATE flights SET departure_time = departure_time - INTERVAL 1 DAY, arrival_time = arrival_time - INTERVAL 1 DAY;";
//            Statement st = DBUtil.con.createStatement();
//            st.executeUpdate(sql);
        } catch (Exception e) {
        }
    }
}
