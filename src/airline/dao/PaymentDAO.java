package airline.dao;

import airline.util.DBUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PaymentDAO {
    public static void addPayment(int passengerId, int flightId, int seats) throws Exception {
        // This method is used to add a payment record for a passenger when they book a flight.

        double amount = 0;

        // Get the price of the flight
        String sql = "SELECT price FROM flights WHERE flight_id = " + flightId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            amount = rs.getDouble(1);
        }

        // Calculate the total amount based on the number of seats booked
        amount *= seats;

        // Insert the payment record into the payments table
        String sql1 = "INSERT INTO payments (passenger_id, flight_id, amount) VALUES (?, ?, ?)";
        PreparedStatement pst = DBUtil.con.prepareStatement(sql1);
        pst.setInt(1, passengerId);
        pst.setInt(2, flightId);
        pst.setDouble(3, amount);
        pst.executeUpdate();
    }
}
