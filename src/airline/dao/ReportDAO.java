package airline.dao;

import airline.util.DBUtil;

import java.sql.ResultSet;
import java.sql.Statement;

public class ReportDAO {
    public static void addReport(int flight_id) throws Exception {

        // This method is used to add a new report for a flight when it is created.
        String sql = "INSERT INTO reports (flight_id, revenue, seats_booked) VALUES (" + flight_id + ", 0, 0)";
        Statement st = DBUtil.con.createStatement();
        st.executeUpdate(sql);
    }

    public static void updateReportForReserveSeat(int flight_id, int seats) throws Exception {
        // This method is used to update the report when a seat is reserved.

        // Get the price of the flight
        String sql = "SELECT price FROM flights WHERE flight_id = " + flight_id;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        double price = 0;
        if (rs.next()) {
            price = rs.getDouble("price");
        }

        // Update the report with the new revenue and seats booked
        String updateSql = "UPDATE reports SET revenue = revenue + " + (seats * price) + ", seats_booked = seats_booked + " + seats + ", report_date = NOW() WHERE flight_id = " + flight_id;
        Statement updateSt = DBUtil.con.createStatement();
        updateSt.executeUpdate(updateSql);
    }

    public static void updateReportForCancelSeat(int flight_id, int seats) throws Exception {
        // This method is used to update the report when a seat is cancelled.

        // Get the price of the flight
        String sql = "SELECT price FROM flights WHERE flight_id = " + flight_id;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        double price = 0;
        if (rs.next()) {
            price = rs.getDouble("price");
        }

        // Update the report with the new revenue and seats booked
        String updateSql = "UPDATE reports SET revenue = revenue - " + (seats * price) + ", seats_booked = seats_booked - " + seats + ", report_date = NOW() WHERE flight_id = " + flight_id;
        Statement updateSt = DBUtil.con.createStatement();
        updateSt.executeUpdate(updateSql);
        return;
    }
}