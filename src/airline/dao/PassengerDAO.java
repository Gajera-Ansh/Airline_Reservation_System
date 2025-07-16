package airline.dao;

import airline.util.DBUtil;
import airline.ds.ArrayList;
import airline.model.Passenger;

import java.sql.*;

public class PassengerDAO {
    static ArrayList<Passenger> passengers = new ArrayList<>();

    public static ArrayList<Passenger> getPassengers() throws Exception {
        String sql = "SELECT * FROM passengers";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        passengers.clear();
        while (rs.next()) {
            passengers.add(new Passenger(rs.getInt("passenger_id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("phone"), rs.getString("password")));
        }
        return passengers;
    }

    public static boolean addPassenger(Passenger passenger) throws Exception {
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
