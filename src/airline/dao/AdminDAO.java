package airline.dao;

import airline.App;
import airline.PDFReceiptGenerator;
import airline.ds.ArrayList;
import airline.model.Admin;
import airline.model.Flight;
import airline.util.DBUtil;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Scanner;

public class AdminDAO {
    public static Scanner sc = new Scanner(System.in);
    public static ArrayList<Admin> admins = new ArrayList<>();

    public static ArrayList<Admin> getAdmins() throws Exception {
        String sql = "SELECT * FROM admins";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        admins.clear();
        while (rs.next()) {
            admins.add(new Admin(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        return admins;
    }

    public static void viewAllFlight(int AdminId) throws Exception {

        // This method is used to view all flights for a specific admin.
        ArrayList<Flight> flights = new ArrayList<>();

        // Get all flights for the admin with the given AdminId
        String sql = "SELECT * FROM flights WHERE admin_id = " + AdminId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        // Print the flight details in a formatted manner
        System.out.printf("\n%-10s %-15s %-15s %-15s %-25s %-25s %-13s %-17s %-10s\n", "Flight ID", "Flight Number", "Departure", "Destination", "Departure Time", "Arrival Time", "Total Seats", "Available Seats", "Price");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-10s %-15s %-15s %-15s %-25s %-25s %-13d %-17d ₹%-10.2f\n", rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(5), rs.getTimestamp(6), rs.getTimestamp(7), rs.getInt(8), rs.getInt(9), rs.getDouble(10));

            // Add each flight to the ArrayList
            flights.add(new Flight(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
                    rs.getInt(8), rs.getInt(9), rs.getDouble(10), rs.getInt(11)));
        }
        if (flights.size() > 0) {

            // Prompt the user to download the flight list as a PDF
            while (true) {
                System.out.print("\nYou want to download the PDF of this flight list? (y/n): ");
                char choice = sc.next().trim().toLowerCase().charAt(0);
                if (choice == 'y') {

                    // Generate the PDF for all flights
                    PDFReceiptGenerator.getAllFlights(flights);
                    return;
                } else if (choice == 'n') {
                    return;
                } else {
                    System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
                }
            }
        } else {
            return;
        }
    }

    public static void viewFlightsForUpdateDelete(int AdminId) throws Exception {
        // Get all flights for the admin with the given AdminId
        String sql = "SELECT * FROM flights WHERE admin_id = " + AdminId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        // Print the flight details in a formatted manner
        System.out.printf("\n%-10s %-15s %-15s %-15s %-25s %-25s %-13s %-17s %-10s\n", "Flight ID", "Flight Number", "Departure", "Destination", "Departure Time", "Arrival Time", "Total Seats", "Available Seats", "Price");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-10s %-15s %-15s %-15s %-25s %-25s %-13d %-17d ₹%-10.2f\n", rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(5), rs.getTimestamp(6), rs.getTimestamp(7), rs.getInt(8), rs.getInt(9), rs.getDouble(10));
        }
    }

    public static boolean viewPassengerList(String flightNumber) throws Exception {

        String sql = "SELECT * FROM flights WHERE flight_number = '" + flightNumber + "'";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        int flightId = 0;
        if (rs.next()) {
            flightId = rs.getInt(1);
        }

        // Get reservation and passenger data who have booked seat
        String sql1 = "SELECT * FROM reservations INNER JOIN passengers ON reservations.passenger_id = passengers.passenger_id WHERE flight_id = " + flightId + " AND status = 'CONFIRMED'";
        ResultSet rs1 = st.executeQuery(sql1);

        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(rs1);

        if (crs.size() > 0) {

            // Display passenger list
            System.out.printf("%-17s %-17s %-20s %-20s %-15s %-15s\n", "\nReservation id", "Passenger id", "Name", "Email", "Phone", "Seat Number");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            while (crs.next()) {
                System.out.printf("%-15s %-15s %-20s %-20s %-15s %-15s\n", crs.getInt("reservation_id"), crs.getInt("passenger_id"), crs.getString("passengerName"), crs.getString("email"), crs.getString("phone"), crs.getString("seat_number"));
            }

            while (true) {
                System.out.print("\nDo you want to download the pdf (y/n): ");
                char choice = sc.next().trim().toLowerCase().charAt(0);
                if (choice == 'y') {
                    PDFReceiptGenerator.passengerPDF(flightNumber, flightId); // Generate PDF of passenger list
                    return true;
                } else if (choice == 'n') {
                    return true;
                } else {
                    System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
                }
            }
        } else {
            return false;
        }
    }

    public static void viewFlightReport(String flightNumber) throws Exception {
        String sql = "SELECT * FROM flights WHERE flight_number = '" + flightNumber + "'";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        int flightId = 0;
        if (rs.next()) {
            flightId = rs.getInt("flight_id");
        }

        String sql1 = "SELECT * FROM reports WHERE flight_id = " + flightId;
        ResultSet rs1 = st.executeQuery(sql1);
        if (rs1.next()) {
            System.out.println("\nFlight ID: " + rs1.getInt("flight_id"));
            System.out.println("Seats Booked: " + rs1.getInt("seats_booked"));
            System.out.println("Revenue: " + rs1.getDouble("revenue"));
            System.out.println("Report updated on: " + rs1.getString("report_date"));
            System.out.println("Report generated on: " + LocalDateTime.now().format(App.dateTimeFormatter));
        }

        while (true) {
            System.out.print("\nDo you want to download the PDF (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if (choice == 'y') {
                PDFReceiptGenerator.generateReport(flightNumber, flightId);
                return;
            } else if (choice == 'n') {
                return;
            } else {
                System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
            }
        }
    }
}
