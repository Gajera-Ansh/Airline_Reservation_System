package airline.dao;

import airline.App;
import airline.ds.ArrayList;
import airline.model.Flight;
import airline.util.DBUtil;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FlightDAO {

    public static Scanner sc = new Scanner(System.in);

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

        // Fetch all flights from the database and return as an ArrayList
        String query = "SELECT * FROM flights";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(query);
        flights.clear();
        while (rs.next()) {

            // Create a Flight object for each row in the result set and add it to the list
            flights.add(new Flight(rs.getInt("flight_id"), rs.getString("flight_number"),
                    rs.getString("flight_type"),
                    rs.getString("departure"), rs.getString("destination"),
                    rs.getString("departure_time"), rs.getString("arrival_time"),
                    rs.getInt("total_seats"), rs.getInt("available_seats"),
                    rs.getDouble("price"), rs.getInt("admin_id")));
        }
        return flights;
    }

    public static boolean displayAvailableFlights(ArrayList<Flight> availableFlights) throws Exception {
//        ================================ No Flights Available ==================================
        if (availableFlights.size() == 0) {
            System.out.println(App.red + "\nNo flights available for the selected criteria." + App.reset);
            return false;
        }
//        ================================ Display Available Flights ==================================
        else {
            System.out.println("\nSearching for available flights...");
            Thread.sleep(2000);
            System.out.println("\nAvailable Flights:");
            System.out.printf("\n%-10s %-15s %-15s %-15s %-25s %-25s %-13s %-17s %-10s\n", "Flight ID", "Flight Number", "Departure", "Destination", "Departure Time", "Arrival Time", "Total Seats", "Available Seats", "Price");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------");
            for (int i = 0; i < availableFlights.size(); i++) {
                System.out.printf("%-10s %-15s %-15s %-15s %-25s %-25s %-13d %-17d ₹%-10.2f\n", availableFlights.get(i).getFlight_id(), availableFlights.get(i).getFlight_number(), availableFlights.get(i).getDeparture(), availableFlights.get(i).getDestination(), availableFlights.get(i).getDeparture_time().format(App.dateTimeFormatter), availableFlights.get(i).getArrival_time().format(App.dateTimeFormatter), availableFlights.get(i).getTotal_seats(), availableFlights.get(i).getAvailable_seats(), availableFlights.get(i).getPrice());
            }
            return true;
        }
    }

    public static boolean addFlight(Flight flight) throws Exception {
        // Insert a new flight into the database
        Connection con = DBUtil.con;
        con.setAutoCommit(false);

        // Check if a flight with the same flight number already exists
        String sql = "SELECT * FROM flights WHERE flight_number = '" + flight.getFlight_number() + "'";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (!rs.next()) {

            // If no flight with the same flight number exists, check for duplicate flight details
            String sql1 = "{CALL checkFlight(?, ?, ?, ?, ?)}";
            CallableStatement cst = con.prepareCall(sql1);
            cst.setString(1, flight.getDeparture());
            cst.setString(2, flight.getDestination());
            cst.setString(3, flight.getDeparture_time().toString());
            cst.setString(4, flight.getArrival_time().toString());
            cst.executeQuery();

            // If no duplicate flight details exist, insert the new flight
            String flightExists = cst.getString(5);
            if (flightExists == null || flightExists.equals("0")) {

                // Insert the flight
                String sql2 = "INSERT INTO flights (flight_number, flight_type, departure, destination, departure_time, arrival_time, total_seats, available_seats, price, admin_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql2);
                pst.setString(1, flight.getFlight_number());
                pst.setString(2, flight.getFlight_type());
                pst.setString(3, flight.getDeparture());
                pst.setString(4, flight.getDestination());
                pst.setString(5, flight.getDeparture_time().toString());
                pst.setString(6, flight.getArrival_time().toString());
                pst.setInt(7, flight.getTotal_seats());
                pst.setInt(8, flight.getAvailable_seats());
                pst.setDouble(9, flight.getPrice());
                pst.setInt(10, flight.getAdmin_id());
                pst.executeUpdate();

                // Confirmation about flight addition
                while (true) {
                    System.out.print("\nAre you sure you want to add this flight? (y/n): ");
                    char choice = sc.next().trim().toLowerCase().charAt(0);
                    if (choice == 'y') {
                        con.commit(); // Commit the transaction

                        // Add a report for the new flight
                        String sql3 = "SELECT flight_id FROM flights WHERE flight_number = '" + flight.getFlight_number() + "'";
                        st.executeQuery(sql3);
                        ResultSet rs1 = st.getResultSet();
                        if (rs1.next()) {
                            ReportDAO.addReport(rs1.getInt(1));
                        }
                        con.commit();
                        return true;
                    } else if (choice == 'n') {
                        con.rollback(); // Rollback the transaction
                        return false;
                    } else {
                        System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
                    }
                }
            } else {
                System.out.println(App.red + "\nFlight already exists with the same details." + App.reset);
                return false;
            }
        } else {
            System.out.println(App.red + "\nFlight with this flight number already exists." + App.reset);
            return false;
        }
    }

    public static boolean deleteFlight(String flightNumber) throws Exception {
        Connection con = DBUtil.con;
        con.setAutoCommit(false);

        // Get the flight ID based on the flight number
        String query = "SELECT flight_id FROM flights WHERE flight_number = '" + flightNumber + "'";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int flightId = 0;
        if (rs.next()) {
            flightId = rs.getInt("flight_id");
        }
        rs.close();

        String sql = "SELECT DISTINCT passenger_id FROM reservations WHERE flight_id = " + flightId + " AND status = 'CONFIRMED'";
        ResultSet rs1 = st.executeQuery(sql);
        // If there are no confirmed reservations, delete the flight directly
        if (!rs1.next()) {
            while (true) {
                System.out.print("\nConfirm deletion of flight " + flightNumber + "? (y/n): ");
                char choice = sc.next().trim().toLowerCase().charAt(0);
                if (choice == 'y') {
                    String sql2 = "{CALL updateForRemoveFlight(?)}";
                    CallableStatement cst = con.prepareCall(sql2);
                    cst.setInt(1, flightId);
                    int r = cst.executeUpdate();
                    if (r > 0) {
                        con.commit(); // Commit the transaction
                        return true;
                    } else {
                        con.rollback(); // Rollback the transaction
                        return false;
                    }
                } else if (choice == 'n') {
                    con.rollback(); // Rollback the transaction
                    return false;
                } else {
                    System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
                    continue;
                }
            }

        } else { // If there are confirmed reservations, ask for confirmation before deleting

            while (true) {

                System.out.print("\nConfirm deletion of flight " + flightNumber + "? (y/n): ");
                char choice = sc.next().trim().toLowerCase().charAt(0);

                if (choice == 'y') {

                    // Notify passengers about the cancellation
                    String sql3 = "SELECT DISTINCT p.name, p.email FROM passengers p JOIN reservations r ON p.passenger_id = r.passenger_id WHERE r.flight_id = " + flightId + " AND r.status = 'CONFIRMED'";
                    Statement st1 = con.createStatement();
                    ResultSet rs2 = st1.executeQuery(sql3);
                    while (rs2.next()) {
                        FileWriter fw = new FileWriter("D://" + rs2.getString(2) + ".txt");
                        fw.write("Subject: Flight Cancellation Notice\n\n");
                        fw.write("Dear " + rs2.getString(1) + ",\n\n");
                        String sql4 = "{CALL getFlight(?, ?, ?, ?, ?, ?)}";
                        CallableStatement cst1 = con.prepareCall(sql4);
                        cst1.setInt(1, flightId);
                        cst1.executeQuery();
                        fw.write("We regret to inform you that your flight with flight number " + flightNumber + " from " + cst1.getString(3) + " to " + cst1.getString(4) + " on " + cst1.getString(5) + " has been cancelled by the administrator.\n\n");
                        fw.write("We apologize for any inconvenience this may cause and will process a full refund to your account.\n\n");
                        fw.write("Thank you for your understanding.\n\n");
                        fw.write("Best regards,\n\n");
                        fw.write("Airline Management");
                        fw.close();
                    }

                    String sql5 = "{CALL updateForRemoveFlight(?)}";
                    CallableStatement cst2 = con.prepareCall(sql5);
                    cst2.setInt(1, flightId);
                    int r = cst2.executeUpdate();
                    if (r > 0) {
                        con.commit(); // Commit the transaction
                        return true;
                    } else {
                        con.rollback(); // Rollback the transaction
                        return false;
                    }
                } else if (choice == 'n') {
                    con.rollback(); // Rollback the transaction
                    return false;
                } else {
                    System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
                }
            }
        }
    }

    public static boolean updateFlight(String flightNumber) throws Exception {
        Connection con = DBUtil.con;
        con.setAutoCommit(false);
        String sql = "SELECT * from flights WHERE flight_number = '" + flightNumber + "'";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        int flightId = 0;
        String departure = null;
        String destination = null;
        String depTime = null;
        String arrTime = null;
        if (rs.next()) {
            flightId = rs.getInt(1);
            departure = rs.getString(4);
            destination = rs.getString(5);
            depTime = rs.getString(6);
            arrTime = rs.getString(7);
        }

        String newFlightNumber = "";
        while (true) {
            System.out.print("Enter new Flight number: ");
            newFlightNumber = sc.next().trim().toUpperCase();
            if (newFlightNumber.matches("^[A-Z]{2,3}\\d{1,4}$")) {
                break;
            } else {
                System.out.println(App.red + "\nInvalid flight number! Please use the format ABC1234." + App.reset);
                continue;
            }
        }

        String sql1 = "{CALL checkFlight(?, ?, ?, ?, ?)}";
        CallableStatement cst = DBUtil.con.prepareCall(sql1);
        cst.setString(1, departure);
        cst.setString(2, destination);

        String newDepartureTime = "";
        LocalDateTime departureTime = null;
        while (true) {
            System.out.print("Enter new departure time (yyyy-mm-dd hh:mm:ss): ");
            sc.nextLine();
            newDepartureTime = sc.nextLine().trim();
            if (newDepartureTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                // Validate the new departure time
                departureTime = LocalDateTime.parse(newDepartureTime, App.dateTimeFormatter);
                if (departureTime.isAfter(LocalDateTime.now())) {
                    cst.setString(3, newDepartureTime);
                    break;
                } else {
                    System.out.println(App.red + "\nDeparture time must be in the future.\n" + App.reset);
                }
            } else {
                System.out.println(App.red + "\nInvalid departure time format! Please use yyyy-MM-dd HH:mm:ss.\n" + App.reset);
            }
        }


        String newArrivalTime = "";
        LocalDateTime arrivalTime = null;
        while (true) {
            System.out.print("Enter new arrival time (yyyy-mm-dd hh:mm:ss): ");
            newArrivalTime = sc.nextLine().trim();

            if (newArrivalTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                // Validate the new arrival time
                arrivalTime = LocalDateTime.parse(newArrivalTime, App.dateTimeFormatter);
                if (arrivalTime.isAfter(departureTime)) {
                    cst.setString(4, newArrivalTime);
                    break;
                } else {
                    System.out.println(App.red + "\nArrival time must be after departure time." + App.reset);
                }
            } else {
                System.out.println(App.red + "\nInvalid arrival time format! Please use yyyy-MM-dd HH:mm:ss." + App.reset);
            }
        }

        try {
            cst.executeQuery();
        } catch (Exception e) {
            System.out.println(App.red + "\nError checking flight details: " + e.getMessage() + App.reset);
            return false;
        }

        int totalSeats = 0;
        while (true) {
            System.out.print("Enter new total seats: ");
            try {
                totalSeats = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println(App.red + "\nInvalid total seats! Please enter a valid number.\n" + App.reset);
                sc.next(); // Clear the invalid input
                continue;
            }
        }

        // Check the total passenger in the flight
        String query = "SELECT count(*) FROM reservations WHERE flight_id = " + flightId + " AND status = 'CONFIRMED'";
        ResultSet result = st.executeQuery(query);
        int bookedSeats = 0;
        if (result.next()) {
            bookedSeats = result.getInt(1);
        }
        int availableSeats = totalSeats - bookedSeats;

        double price = 0;
        while (true) {
            System.out.print("Enter new price: ");
            try {
                price = sc.nextDouble();
                break;
            } catch (InputMismatchException e) {
                System.out.println(App.red + "\nInvalid price! Please enter a valid number." + App.reset);
                sc.next(); // Clear the invalid input
            }
        }


        // Check if the flight already exists with the same details
        String flightExists = cst.getString(5);
        if (flightExists == null || flightExists.equals("0")) {

            // Check for confirmed reservations
            String sql2 = "SELECT DISTINCT passenger_id FROM reservations WHERE flight_id = " + flightId + " AND status = 'CONFIRMED'";
            ResultSet rs1 = st.executeQuery(sql2);

            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs1);

            // If there are no confirmed reservations, update the flight directly
            if (!(crs.size() > 0)) {

                // Update the flight details
                String sql3 = "{CALL updateFlight(?, ?, ?, ?, ?, ?, ?)}";
                CallableStatement cst1 = con.prepareCall(sql3);
                cst1.setInt(1, flightId);
                cst1.setString(2, newFlightNumber);
                cst1.setString(3, newDepartureTime);
                cst1.setString(4, newArrivalTime);
                cst1.setInt(5, totalSeats);
                cst1.setInt(6, availableSeats);
                cst1.setDouble(7, price);
                cst1.executeUpdate();

                while (true) {
                    System.out.print("\nAre you sure you want to update this flight? (y/n): ");
                    char choice = sc.next().trim().toLowerCase().charAt(0);
                    if (choice == 'y') {
                        con.commit();
                        return true;
                    } else if (choice == 'n') {
                        con.rollback();
                        return false;
                    } else {
                        System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
                    }
                }
            } else {  // If there are confirmed reservations, notify passengers
                String sql3 = "SELECT DISTINCT p.name, p.email FROM passengers p JOIN reservations r ON p.passenger_id = r.passenger_id WHERE r.flight_id = " + flightId + " AND r.status = 'CONFIRMED'";
                ResultSet rs2 = st.executeQuery(sql3);
                FileWriter fw = null;
                while (rs2.next()) {
                    fw = new FileWriter("D://" + rs2.getString(2) + ".txt");
                    fw.write("Subject: Flight Update Notice\n\n");
                    fw.write("Dear " + rs2.getString(1) + ",\n\n");
                    fw.write("We regret to inform you that the flight " + flightNumber + " from " + departure + " to " + destination + " on " + depTime + " has been updated by the administrator.\n\n");
                    fw.write("New Flight Details:\n");
                    fw.write("Flight Number: " + newFlightNumber + "\n");
                    fw.write("Departure: " + departure + "\n");
                    fw.write("Destination: " + destination + "\n");
                    fw.write("Departure Time: " + newDepartureTime + "\n");
                    fw.write("Arrival Time: " + newArrivalTime + "\n\n");
                    fw.write("Thank you for your understanding.\n\n");
                    fw.write("Best regards,\n");
                    fw.write("Airline Management");
                }
                String sql4 = "{CALL updateFlight(?, ?, ?, ?, ?, ?, ?)}";
                CallableStatement cst2 = con.prepareCall(sql4);
                cst2.setInt(1, flightId);
                cst2.setString(2, newFlightNumber);
                cst2.setString(3, newDepartureTime);
                cst2.setString(4, newArrivalTime);
                cst2.setInt(5, totalSeats);
                cst2.setInt(6, availableSeats);
                cst2.setDouble(7, price);
                cst2.executeUpdate();

                while (true) {
                    System.out.println("\nAre you sure you want to update this flight? (y/n): ");
                    char choice = sc.next().trim().toLowerCase().charAt(0);
                    if (choice == 'y') {
                        con.commit();
                        fw.close();

                        return true;
                    } else if (choice == 'n') {
                        con.rollback();
                        return false;
                    } else {
                        System.out.println(App.red + "\nInvalid choice. Please enter 'y' or 'n'." + App.reset);
                    }
                }
            }
        } else {
            System.out.println(App.red + "\nFlight already exists with the same details." + App.reset);
            return false;
        }
    }
}
