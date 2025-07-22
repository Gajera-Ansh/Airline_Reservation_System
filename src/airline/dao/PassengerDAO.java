package airline.dao;

import airline.util.DBUtil;
import airline.ds.ArrayList;
import airline.model.Passenger;

import java.sql.*;

public class PassengerDAO {
    static ArrayList<Passenger> passengers = new ArrayList<>();

    public static ArrayList<Passenger> getPassengers() throws Exception {

        // Fetch all passengers from the database and return as an ArrayList
        String sql = "SELECT * FROM passengers";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        passengers.clear();
        while (rs.next()) {

            // Create a Passenger object for each row in the result set and add it to the list
            passengers.add(new Passenger(rs.getInt("passenger_id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("phone"), rs.getString("password")));
        }
        return passengers;
    }

    public static boolean addPassenger(Passenger passenger) throws Exception {

        // Insert a new passenger into the database
        String sql = "INSERT INTO passengers (name, email, phone, password) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = DBUtil.con.prepareStatement(sql);
        pst.setString(1, passenger.getName());
        pst.setString(2, passenger.getEmail());
        pst.setString(3, passenger.getPhone());
        pst.setString(4, passenger.getPass());
        if(pst.executeUpdate() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
