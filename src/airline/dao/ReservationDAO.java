package airline.dao;

import airline.App;
import airline.ds.ArrayList;
import airline.util.DBUtil;
import airline.ds.HashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class ReservationDAO {
    public static Scanner sc = new Scanner(System.in);
    public static Connection con = DBUtil.con;
    public static HashMap<Integer, ArrayList<String>> seatNumbers = new HashMap<>();

    public static boolean addReservation(int flightId, int passengerId, int seats) throws Exception {
        con.setAutoCommit(false);
        for (int i = 1; i <= seats; i++) {
            String sql = "INSERT INTO reservations (flight_id, passenger_id, seat_number) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, flightId);
            pst.setInt(2, passengerId);
            pst.setString(3, generateSeatNumbers(flightId));
            if (pst.executeUpdate() > 0) {
                continue;
            } else {
                return false;
            }
        }

        while (true) {
            System.out.print("Confirming reservation for " + seats + " seats for flight ID: " + flightId + "  (y/n):");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if (choice == 'y') {
                con.commit();
                String updateSeatsSql = "UPDATE flights SET available_seats = available_seats - ? WHERE flight_id = ?";
                PreparedStatement updateSeatsPst = con.prepareStatement(updateSeatsSql);
                updateSeatsPst.setInt(1, seats);
                updateSeatsPst.setInt(2, flightId);
                if (updateSeatsPst.executeUpdate() > 0) {
                    con.commit();
                    System.out.println(App.green + "\nReservation confirmed successfully." + App.reset);
                } else {
                    con.rollback();
                    System.out.println(App.red + "\nFailed to update available seats. Reservation rolled back." + App.reset);
                    return false;
                }
                break;
            } else if (choice == 'n') {
                con.rollback();
                System.out.println(App.red + "\nReservation cancelled." + App.reset);
                return false;
            } else {
                System.out.println(App.red + "\nInvalid choice. Reservation cancelled." + App.reset);
                con.rollback();
                continue;
            }
        }
        return true;
    }

    public static String generateSeatNumbers(int flightId) throws Exception {
        String seatNum = "";
        int totalSeats = 0;
        ArrayList<String> seatList = new ArrayList<>();
        seatNumbers.putIfAbsent(flightId, seatList);
        String sql = "SELECT total_seats FROM flights WHERE flight_id = " + flightId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            totalSeats = rs.getInt(1);
        }
        do {
            int row = (int) (Math.random() * totalSeats) + 1;
            char col = (char) ('A' + (int) (Math.random() * 6));
            seatNum = row + String.valueOf(col);
        } while (seatList.contains(seatNum));
        seatList.add(seatNum);
        return seatNum;
    }
}
