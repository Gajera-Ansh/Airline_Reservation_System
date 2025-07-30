package airline.dao;

import airline.PDFReceiptGenerator;
import airline.ds.ArrayList;
import airline.model.Admin;
import airline.model.Flight;
import airline.util.DBUtil;

import java.sql.ResultSet;
import java.sql.Statement;
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
            flights.add( new Flight(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
                    rs.getInt(8), rs.getInt(9), rs.getDouble(10), rs.getInt(11)));
        }
        if(flights.size() > 0) {

            // Prompt the user to download the flight list as a PDF
            System.out.print("\nYou want to download the PDF of this flight list? (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if(choice == 'y') {

                // Generate the PDF for all flights
                PDFReceiptGenerator.getAllFlights(flights);
                return;
            } else {
                return;
            }
        } else {
            return;
        }
    }
}
