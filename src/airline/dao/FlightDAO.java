package airline.dao;

import airline.ds.ArrayList;
import airline.model.Flight;
import airline.util.DBUtil;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class FlightDAO {

    static {
        try {
            String sql = "UPDATE flights SET departure_time = DATE_ADD(departure_time, INTERVAL 1 DAY), arrival_time = DATE_ADD(arrival_time, INTERVAL 1 DAY)";
//            String sql = "UPDATE flights SET departure_time = departure_time - INTERVAL 1 DAY, arrival_time = arrival_time - INTERVAL 1 DAY;";
//            Statement st = DBUtil.con.createStatement();
//            st.executeUpdate(sql);
        } catch (Exception _) {
        }
    }

    public static ArrayList<Flight> flights = new ArrayList<>();

    public static ArrayList getFlight() throws Exception {
        String query = "SELECT * FROM flights";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(query);
        flights.clear();
        while (rs.next()) {
            flights.add(new Flight(rs.getInt("flight_id"), rs.getString("flight_number"),
                    rs.getString("flight_type"),
                    rs.getString("departure"), rs.getString("destination"),
                    rs.getString("departure_time"), rs.getString("arrival_time"),
                    rs.getInt("total_seats"), rs.getInt("available_seats"),
                    rs.getDouble("price"), rs.getInt("admin_id")));
        }
        return flights;
    }

    public static LocalDate getFlightDate(int id) throws Exception {
        String sql = "SELECT DATE(departure_time) FROM flights WHERE flight_id = " + id;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            return rs.getDate(1).toLocalDate();
        } else {
            return null;
        }
    }
}
