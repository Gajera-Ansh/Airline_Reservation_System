package airline.dao;

import airline.App;
import airline.PDFReceiptGenerator;
import airline.ds.ArrayList;
import airline.model.Flight;
import airline.model.Passenger;
import airline.util.DBUtil;
import airline.ds.HashMap;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.*;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ReservationDAO {
    public static Scanner sc = new Scanner(System.in);
    public static Connection con = DBUtil.con;
    public static HashMap<Integer, ArrayList<String>> seatNumbers = new HashMap<>();

    public static void makeAReservation(ArrayList<Flight> flights, Passenger p) throws Exception {
        while (true) {
            boolean reservationStatus = false;
            ArrayList<Flight> flightList = flights;
            ArrayList<String> passNames = new ArrayList<>();
//          ================================== Select Flight ==================================
            System.out.print("\nWant to make a reservation? (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if (choice == 'y') {
                System.out.print("\nEnter flight ID: ");
                int flightId = 0;
                try {
                    flightId = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println(App.red + "\nInvalid input! Please enter a valid flight ID." + App.reset);
                    sc.next(); // Clear the invalid input
                    continue; // Restart the loop to ask for flight ID again
                }
                System.out.print("Enter number of seats to reserve: ");
                int seats = 0;
                try {
                    seats = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println(App.red + "\nInvalid input! Please enter a valid number of seats." + App.reset);
                    sc.next(); // Clear the invalid input
                    continue; // Restart the loop to ask for number of seats again
                }
//               ================================= Validate Seats ==================================
                if (seats > 0 && seats <= 6) { // Assuming max 6 seats can be reserved at once
                    boolean flag = false;
//                    ================================ Check Flight ID and Available Seats ==================================
                    for (int i = 0; i < flightList.size(); i++) {
                        if (flightList.get(i).getFlight_id() == flightId && flightList.get(i).getAvailable_seats() >= seats) {
                            flag = false;

                            for (int j = 1; j <= seats; j++) {
                                // Enter passenger name for each seat
                                System.out.print("Enter name for passenger " + j + ": ");
                                String name = sc.next().trim();
                                if (name.matches("^[a-zA-Z\\s]+$")) {
                                    passNames.add(name);
                                } else {
                                    System.out.println(App.red + "\nInvalid name! Please enter a valid name." + App.reset);
                                    j--; // Decrement to re-enter the name for the same seat
                                    continue;
                                }
                            }
//                           ================================= Add Reservation ==================================
                            reservationStatus = ReservationDAO.addReservation(flightList.get(i).getFlight_id(), p.getPassenger_id(), seats, passNames);
                            break;
                        } else {
                            flag = true;
                        }
                    }
//                   ================================= Invalid Flight ID or Seats ==================================
                    if (flag) {
                        System.out.println(App.red + "\nInvalid flight ID or insufficient seats available! Please try again." + App.reset);
                        continue;
                    }
                }
//                ================================ Invalid Number of Seats ==================================
                else {
                    if (seats <= 0) {
                        System.out.println(App.red + "\nInvalid number of seats! Please enter a positive number. " + App.reset);
                        continue;
                    } else {
                        System.out.println(App.red + "\nYou can reserve a maximum of 6 seats at a time. Please try again." + App.reset);
                        continue;
                    }
                }
            } else if (choice == 'n') {
                return;
            } else {
                System.out.println(App.red + "\nInvalid choice! Please try again." + App.reset);
                continue;
            }
//            ================================ Reservation Confirmation ==================================
            if (reservationStatus) {
                return;
            } else {
                continue;
            }
        }
    }

    public static boolean addReservation(int flightId, int passengerId, int seats, ArrayList<String> passNames) throws Exception {
        con.setAutoCommit(false);
        // insert reservation details into the reservations table
        int j = 0;
        for (int i = 1; i <= seats; i++) {
            String sql = "INSERT INTO reservations (flight_id, passenger_id, passengerName, seat_number) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, flightId);
            pst.setInt(2, passengerId);
            while (j <= seats - 1) {
                pst.setString(3, passNames.get(j));
                break;
            }
            j++;
            pst.setString(4, generateSeatNumbers(flightId));
            if (pst.executeUpdate() > 0) {
                continue;
            } else {
                return false;
            }
        }

        while (true) {

            // Confirm reservation with the user before making payment
            System.out.print("Confirming reservation for " + seats + " seats for flight ID: " + flightId + "  (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if (choice == 'y') {
                if (PaymentDAO.makePayment(passengerId, flightId, seats)) {
                    con.commit();

                    // Update available seats in flights table
                    String updateSeatsSql = "UPDATE flights SET available_seats = available_seats - ? WHERE flight_id = ?";
                    PreparedStatement updateSeatsPst = con.prepareStatement(updateSeatsSql);
                    updateSeatsPst.setInt(1, seats);
                    updateSeatsPst.setInt(2, flightId);
                    if (updateSeatsPst.executeUpdate() > 0) {

                        // Update the report with the new reservation
                        ReportDAO.updateReportForReserveSeat(flightId, seats);

                        // Commit the transaction
                        con.commit();
                        System.out.println(App.green + "\nReservation confirmed successfully." + App.reset);
                        PDFReceiptGenerator.generateReceipt(flightId, passengerId, seats); // Generate PDF receipt for the reservation
                        return true;
                    } else {
                        con.rollback();
                        System.out.println(App.red + "\nFailed to update available seats. Reservation rolled back." + App.reset);
                        return false;
                    }
                } else {
                    con.rollback();
                    return false;
                }
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
    }

    public static String generateSeatNumbers(int flightId) throws Exception {
        String seatNum = "";
        int totalSeats = 0;
        ArrayList<String> seatList = new ArrayList<>();

        String sql = "SELECT total_seats FROM flights WHERE flight_id = " + flightId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            totalSeats = rs.getInt(1);
        }

        do {
            int row = (int) (Math.random() * totalSeats) + 1;
            char col = (char) ('A' + (int) (Math.random() * 6));
            seatNum = row + String.valueOf(col);
        } while (seatList.contains(seatNum));
        seatList.add(seatNum);
        seatNumbers.putIfAbsent(flightId, seatList);
        return seatNum;
    }

    public static boolean viewReservations(String passengerName, int flightId) throws Exception {

        // Query to get reservations details for the given passenger name and flight ID
        String sql = "SELECT * FROM reservations INNER JOIN passengers ON reservations.passenger_id = passengers.passenger_id WHERE passengers.name = ? AND reservations.flight_id = ? AND reservations.status = 'CONFIRMED'";
        PreparedStatement pst = DBUtil.con.prepareStatement(sql);
        pst.setString(1, passengerName);
        pst.setInt(2, flightId);
        ResultSet rs = pst.executeQuery();

        // Create a CachedRowSet to hold the result set in memory
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(rs); // Copy all rows into memory

        int seats = 0;
        if (crs.size() > 0) {
            int passId = 0;
            System.out.println(App.green + "\nReservation Details\n-------------------\n" + App.reset);

            // Get flight details
            String sql1 = "CALL getFlight(?, ?, ?, ?, ?, ?)";
            CallableStatement pst1 = DBUtil.con.prepareCall(sql1);
            pst1.setInt(1, flightId);
            pst1.executeQuery();
            System.out.println("Name: " + passengerName);
            System.out.println("Flight ID: " + flightId);
            System.out.println("Flight Number: " + pst1.getString(2));
            System.out.println("Departure: " + pst1.getString(3));
            System.out.println("Destination: " + pst1.getString(4) + "\n");
            while (crs.next()) {
                seats++;
                System.out.println("Passenger Name: " + crs.getString("passengerName"));
                passId = crs.getInt("passenger_id"); // Get passenger ID for payment and PDF generation
                System.out.println("Reservation ID: " + crs.getInt("reservation_id"));
                System.out.println("Seat Number: " + crs.getString("seat_number"));
                System.out.println("Reservation Date: " + crs.getString("reservation_date") + "\n");
            }

            // Ask user if they want to download the PDF receipt
            System.out.print("\nYou want to download PDF (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if (choice == 'y') {
                PDFReceiptGenerator.generateReceipt(flightId, passId, seats);
            }
            return true;
        } else {
            System.out.println(App.red + "\nNo reservations found for the given passenger name and flight ID." + App.reset);
            return false;
        }
    }

    public static boolean viewReservationForCancelReservation(String passengerName, int flightId) throws Exception {
        String sql = "SELECT * FROM reservations INNER JOIN passengers ON reservations.passenger_id = passengers.passenger_id WHERE passengers.name = ? AND reservations.flight_id = ? AND reservations.status = 'CONFIRMED'";
        PreparedStatement pst = DBUtil.con.prepareStatement(sql);
        pst.setString(1, passengerName);
        pst.setInt(2, flightId);
        ResultSet rs = pst.executeQuery();

        // Create a CachedRowSet to hold the result set in memory
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(rs); // Copy all rows into memory

        if (crs.size() > 0) {
            System.out.println(App.green + "\nReservation Details\n-------------------\n" + App.reset);

            // Get flight details
            String sql1 = "CALL getFlight(?, ?, ?, ?, ?, ?)";
            CallableStatement pst1 = DBUtil.con.prepareCall(sql1);
            pst1.setInt(1, flightId);
            pst1.executeQuery();
            System.out.println("Name: " + passengerName);
            System.out.println("Flight ID: " + flightId);
            System.out.println("Flight Number: " + pst1.getString(2));
            System.out.println("Departure: " + pst1.getString(3));
            System.out.println("Destination: " + pst1.getString(4) + "\n");
            while (crs.next()) {
                System.out.println("Passenger Name: " + crs.getString("passengerName"));
                System.out.println("Reservation ID: " + crs.getInt("reservation_id"));
                System.out.println("Seat Number: " + crs.getString("seat_number"));
                System.out.println("Reservation Date: " + crs.getString("reservation_date") + "\n");
            }
            return true;
        } else {
            System.out.println(App.red + "\nNo reservations found for the given passenger name and flight ID." + App.reset);
            return false;
        }
    }

    public static boolean cancelReservation(String name, int flightId) throws Exception {
        System.out.print("\nDo you want to cancel all the reservations? (y/n): ");
        char choice = sc.next().trim().toLowerCase().charAt(0);
        if (choice == 'y') {
            con.setAutoCommit(false);

            int passId = 0;
            // Get passenger ID based on the name
            String query = "SELECT passenger_id FROM passengers WHERE name = '" + name + "'";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                passId = rs.getInt(1);
            }

            // Update reservations to 'CANCELLED' status for the given passenger and flight
            String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE passenger_id = ? AND flight_id = ? AND status = 'CONFIRMED'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, passId);
            pst.setInt(2, flightId);
            int r = pst.executeUpdate();

            // Update available seats in the flights table
            String sql1 = "UPDATE flights SET available_seats = available_seats + " + r + " WHERE flight_id = " + flightId;
            st = con.createStatement();
            st.executeUpdate(sql1);

            // Calculate the total price for the cancelled reservations
            String sql2 = "SELECT price FROM flights WHERE flight_id = " + flightId;
            ResultSet rs1 = st.executeQuery(sql2);
            double price = 0;
            if (rs1.next()) {
                price = rs1.getDouble(1);
            }
            price *= r;

            // Update the reports table with the cancelled reservations
            String sql3 = "UPDATE reports SET seats_booked = seats_booked - " + r + ", revenue = revenue - " + price + "   WHERE flight_id = " + flightId;
            st.executeUpdate(sql3);

            // Update the payments table to reflect the cancellation
            String sql4 = "UPDATE payments SET status = 'REFUNDED' WHERE passenger_id = ? AND flight_id = ? AND status = 'CONFIRMED'";
            PreparedStatement pst4 = con.prepareStatement(sql4);
            pst4.setInt(1, passId);
            pst4.setInt(2, flightId);
            pst4.executeUpdate();
            con.commit(); // Commit the transaction

            System.out.println(App.green + "\nAll reservations cancelled successfully." + App.reset);
            return true;

        } else if (choice == 'n') {
            con.setAutoCommit(false);

            System.out.print("\nDo you want to cancel a specific reservation? (y/n): ");
            char choice1 = sc.next().trim().toLowerCase().charAt(0);
            if (choice1 == 'y') {
                System.out.print("\nEnter the reservation ID to cancel: ");
                int reservationId = sc.nextInt();

                int passId = 0;
                // Get passenger ID based on the name
                String query = "SELECT passenger_id FROM passengers WHERE name = '" + name + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    passId = rs.getInt(1);
                }

                // Update the specific reservation to 'CANCELLED' status
                String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = ? AND passenger_id = ? AND flight_id = ? AND status = 'CONFIRMED'";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, reservationId);
                pst.setInt(2, passId);
                pst.setInt(3, flightId);
                pst.executeUpdate();
                int r = pst.executeUpdate();

                // Update available seats in the flights table
                String sql1 = "UPDATE flights SET available_seats = available_seats + 1 WHERE flight_id = ?";
                PreparedStatement pst1 = con.prepareStatement(sql1);
                pst1.setInt(1, flightId);
                pst1.executeUpdate();

                // Calculate the price for the cancelled reservation
                String sql2 = "SELECT price FROM flights WHERE flight_id = " + flightId;
                ResultSet rs1 = st.executeQuery(sql2);
                double price = 0;
                if (rs1.next()) {
                    price = rs1.getDouble(1);
                }

                // Update the reports table with the cancelled reservation
                String sql3 = "UPDATE reports SET seats_booked = seats_booked - 1, revenue = revenue - " + price + " WHERE flight_id = " + flightId;
                st.executeUpdate(sql3);

                // Update the payments table to reflect the cancellation
                String sql4 = "UPDATE payments SET amount = amount - ? WHERE passenger_id = ? AND flight_id = ? AND status = 'CONFIRMED'";
                PreparedStatement pst4 = con.prepareStatement(sql4);
                pst4.setDouble(1, price);
                pst4.setInt(2, passId);
                pst4.setInt(3, flightId);
                pst4.executeUpdate();

                String sql5 = "INSERT INTO payments (passenger_id, flight_id, amount, status) VALUES (?, ?, ?, 'REFUNDED')";
                PreparedStatement pst5 = con.prepareStatement(sql5);
                pst5.setInt(1, passId);
                pst5.setInt(2, flightId);
                pst5.setDouble(3, price);
                pst5.executeUpdate();
                con.commit(); // Commit the transaction

                System.out.println(App.green + "\nReservation cancelled successfully for Reservation ID " + reservationId + App.reset);
                PDFReceiptGenerator.generateReceipt(flightId, passId, r); // Generate PDF receipt for the reservation update
                return true;
            } else if (choice1 == 'n') {
                System.out.println(App.red + "\nReservation cancellation aborted." + App.reset);
            } else {
                System.out.println(App.red + "\nInvalid choice. Reservation cancellation aborted." + App.reset);
            }
        } else {
            System.out.println(App.red + "\nInvalid choice. Reservation cancellation aborted." + App.reset);
        }
        return false;
    }
}