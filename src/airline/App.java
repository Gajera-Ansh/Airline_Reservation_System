package airline;

import airline.dao.*;
import airline.ds.*;
import airline.model.*;
import airline.util.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.InputMismatchException;
import java.util.InvalidPropertiesFormatException;
import java.util.Objects;
import java.util.Scanner;

public class App {

    public static String red = "\u001B[31m";
    public static String green = "\u001B[32m";
    public static String reset = "\u001B[0m";

    public static Scanner sc = new Scanner(System.in);
    public static DBUtil dbUtil = new DBUtil();
    public static FlightDAO flightDAO = new FlightDAO();
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        System.out.println("\n===============================================");
        System.out.println("    AIRLINE RESERVATION MANAGEMENT SYSTEM     ");
        System.out.println("===============================================\n");
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Passenger Login");
            System.out.println("2. Admin Login");
            System.out.println("3. Exit System");
            System.out.print("Enter choice: ");
            int choice = 0;

            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(red + "\nOnly Digits allowed" + reset);
                sc.nextLine();
                continue;
            }

            switch (choice) {
//                =================================== Passenger Login ==================================
                case 1 -> {
                    passengerLogin();
                }
//                =================================== Admin Login ==================================
                case 2 -> {
                    adminLogin();
                }
//                =================================== Exit System ==================================
                case 3 -> {
                    System.out.println(red + "\nExiting the system\n" + reset);
                    System.exit(0);
                }
//                =================================== Invalid Choice ==================================
                default -> {
                    System.out.println(red + "\nInvalid choice! Please try again." + reset);
                }
            }
        }
    }

    public static void adminLogin() throws Exception {
        ArrayList<Admin> admins = AdminDAO.getAdmins();
        System.out.println("\n===== ADMIN LOGIN =====");
        System.out.print("Enter Username: ");
        String username = sc.next().trim();
        System.out.print("Enter Password: ");
        String password = sc.next().trim();
        boolean flag = true;
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getUsername().equalsIgnoreCase(username) && admins.get(i).getPassword().equals(password)) {
                flag = false;
                System.out.println(green + "\nLogin successful!" + reset);
                adminMenu(admins.get(i).getAdminId());
                return;
            }
        }
        if (flag) {
            System.out.println(red + "\nInvalid username or password!" + reset);
            return;
        }
    }

    public static void adminMenu(int adminId) throws Exception {
        while (true) {
            ArrayList<Flight> flights = FlightDAO.getFlight();
            System.out.println("\n===== ADMINISTRATOR MENU =====");
            System.out.println("1. Add New Flight");
            System.out.println("2. Remove Flight");
            System.out.println("3. View All Flights");
            System.out.println("4. Update Flight Information");
            System.out.println("5. View Passenger List");
            System.out.println("6. Generate Flight Report");
            System.out.println("7. Return to previous Menu");
            System.out.println("8. Exit System");
            System.out.print("Enter choice: ");
            int choice = 0;

            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(red + "\nOnly Digits allowed" + reset);
                sc.nextLine();
                continue;
            }
            switch (choice) {
//              ================================== Add New Flight ==================================
                case 1 -> {
                    String fnumber, departure, destination, departureTime, arrivalTime;
                    LocalDateTime depTime, arrTime;
                    int totalSeats = 0;
                    double price = 0;

                    while (true) {
                        System.out.print("\nEnter flight number: ");
                        fnumber = sc.next().trim().toUpperCase();
                        // Validate flight number format
                        if (fnumber.matches("^[A-Z]{2,3}\\d{1,4}$")) {
                            break;
                        } else {
                            System.out.println(red + "\nInvalid flight number! Please use the format ABC1234." + reset);
                            continue;
                        }
                    }

                    while (true) {
                        System.out.print("Enter departure: ");
                        departure = sc.next().trim().toUpperCase();
                        // Validate departure format
                        if (departure.matches("^[a-zA-Z\\s]+$")) {
                            break;
                        } else {
                            System.out.println(red + "\nInvalid departure! Please use only letters and spaces.\n" + reset);
                            continue;
                        }
                    }

                    while (true) {
                        System.out.print("Enter destination: ");
                        destination = sc.next().trim().toUpperCase();
                        // Validate destination format
                        if (destination.matches("^[a-zA-Z\\s]+$")) {
                            break;
                        } else {
                            System.out.println(red + "\nInvalid destination! Please use only letters and spaces.\n" + reset);
                            continue;
                        }
                    }

                    sc.nextLine();
                    while (true) {
                        System.out.print("Enter departure time (yyyy-MM-dd HH:mm:ss): ");
                        departureTime = sc.nextLine().trim();

                        // Validate departure time format
                        if (departureTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {

                            depTime = LocalDateTime.parse(departureTime, dateTimeFormatter);
                            // Check if departure time is in the past
                            if (depTime.isBefore(LocalDateTime.now())) {
                                System.out.println(red + "\nDeparture time cannot be in the past.\n" + reset);
                                continue;
                            } else {
                                break;
                            }
                        } else {
                            System.out.println(red + "\nInvalid departure time format! Please use yyyy-MM-dd HH:mm:ss.\n" + reset);
                            continue;
                        }
                    }

                    while (true) {
                        System.out.print("Enter arrival time (yyyy-MM-dd HH:mm:ss): ");
                        arrivalTime = sc.nextLine().trim();

                        // Validate arrival time format
                        if (arrivalTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {

                            // Check if it is after departure time
                            arrTime = LocalDateTime.parse(arrivalTime, dateTimeFormatter);
                            if (arrTime.isBefore(depTime) || arrTime.isEqual(depTime)) {
                                System.out.println(red + "\nArrival time must be after departure time.\n" + reset);
                                continue;
                            } else {
                                break;
                            }
                        } else {
                            System.out.println(red + "\nInvalid arrival time format! Please use yyyy-MM-dd HH:mm:ss.\n" + reset);
                            continue;
                        }
                    }

                    while (true) {
                        System.out.print("Enter total seats: ");
                        try {
                            totalSeats = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println(red + "\nInvalid total seats! Please enter a valid number.\n" + reset);
                            sc.nextLine(); // Clear the invalid input
                            continue;
                        }
                        // Validate total seats
                        if (totalSeats > 0) {
                            break;
                        } else {
                            System.out.println(red + "\nInvalid total seats! Please enter a positive number.\n" + reset);
                            continue;
                        }
                    }

                    while (true) {
                        System.out.print("Enter price: ");
                        try {
                            price = sc.nextDouble();
                        } catch (InputMismatchException e) {
                            System.out.println(red + "\nInvalid price! Please enter a valid number.\n" + reset);
                            sc.nextLine(); // Clear the invalid input
                            continue;
                        }
                        // Validate price
                        if (price > 0) {
                            break;
                        } else {
                            System.out.println(red + "\nInvalid price! Please enter a positive number." + reset);
                            continue;
                        }
                    }

                    String fType = "";
                    if (adminId == 1) {
                        fType = "DMST";
                    } else {
                        fType = "INRNL";
                    }
                    Flight newFlight = new Flight(0, fnumber, fType, departure, destination, departureTime, arrivalTime, totalSeats, totalSeats, price, adminId);
                    if (FlightDAO.addFlight(newFlight)) {
                        System.out.println(green + "\nFlight added successfully!" + reset);
                        continue;
                    } else {
                        System.out.println(red + "\nFailed to add flight. Please try again." + reset);
                        continue;
                    }
                }
//              ================================== Remove Flight ===================================
                case 2 -> {
                    AdminDAO.viewFlightsForUpdateDelete(adminId); // View all flights

                    System.out.print("\nEnter flight number to remove: ");
                    String flightNumber = sc.next().trim();
                    boolean flightExists = false;
                    boolean flightNotAccessible = true;
                    for (int i = 0; i < flights.size(); i++) {
                        if (flights.get(i).getFlight_number().equalsIgnoreCase(flightNumber)) {
                            flightExists = true;
                            if (flights.get(i).getAdmin_id() == adminId) {
                                flightNotAccessible = false;
                                // Remove the flight
                                if (FlightDAO.deleteFlight(flightNumber)) {
                                    flightExists = true;
                                    flightNotAccessible = false;
                                    System.out.println(green + "\nFlight removed successfully!" + reset);
                                    break;
                                } else {
                                    System.out.println(red + "\nFailed to remove flight. Please try again." + reset);
                                    break;
                                }
                            } else {
                                flightNotAccessible = true;
                                break;
                            }
                        } else {
                            flightExists = false;
                            continue;
                        }
                    }
                    if (!flightExists) {
                        System.out.println(red + "\nFlight not found! Please check the flight number." + reset);
                    } else if (flightNotAccessible) {
                        System.out.println(red + "\nYou do not have permission to remove this flight." + reset);
                    }
                }
//              ================================== View All Flights ==================================
                case 3 -> {
                    // This method is used to view all flights for a specific admin.
                    AdminDAO.viewAllFlight(adminId);
                }
//              ================================== Update Flight Information ==========================
                case 4 -> {
                    AdminDAO.viewFlightsForUpdateDelete(adminId); // View all flights

                    System.out.print("\nEnter flight number to update: ");
                    String flightNumber = sc.next().trim().toUpperCase();

                    String sql = "SELECT * FROM flights WHERE flight_number = '" + flightNumber + "'";
                    Statement st = DBUtil.con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    // Validate flight number
                    if (rs.next()) {

                        // Check if the flight belongs to the admin
                        if (rs.getInt("admin_id") == adminId) {

                            if (FlightDAO.updateFlight(flightNumber)) {
                                System.out.println(green + "\nFlight updated successfully!" + reset);
                                continue;
                            } else {
                                System.out.println(red + "\nFailed to update flight. Please try again." + reset);
                                continue;
                            }
                        } else {
                            System.out.println(red + "\nYou do not have permission to update this flight." + reset);
                            continue;
                        }
                    } else {
                        System.out.println(red + "\nFlight not found! Please check the flight number." + reset);
                        continue;
                    }
                }
//              ================================== View Passenger List ==============================
                case 5 -> {
                    AdminDAO.viewFlightsForUpdateDelete(adminId); // View all flights

                    System.out.print("\nEnter flight number to view passenger list: ");
                    String flightNumber = sc.next().trim().toUpperCase();

                    String sql = "SELECT * FROM flights WHERE flight_number = '" + flightNumber + "'";
                    Statement st = DBUtil.con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    // Validate flight number
                    if (rs.next()) {

                        // Check if the flight belongs to the admin
                        if (rs.getInt("admin_id") == adminId) {

                            if (AdminDAO.viewPassengerList(flightNumber)) {
                                continue;
                            } else {
                                System.out.println(red + "\nNo passengers found" + reset);
                            }
                        } else {
                            System.out.println(red + "\nYou do not have permission to update this flight." + reset);
                        }
                    } else {
                        System.out.println(red + "\nFlight not found! Please check the flight number." + reset);
                    }
                }
//              ================================== Generate Flight Report ==========================
                case 6 -> {
                    AdminDAO.viewFlightsForUpdateDelete(adminId); // View all flights

                    System.out.print("\nEnter flight number to generate flight report: ");
                    String flightNumber = sc.next().trim().toUpperCase();

                    String sql = "SELECT * FROM flights WHERE flight_number = '" + flightNumber + "'";
                    Statement st = DBUtil.con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    // Validate flight number
                    if (rs.next()) {

                        // Check if the flight belongs to the admin
                        if (rs.getInt("admin_id") == adminId) {
                            AdminDAO.viewFlightReport(flightNumber);
                            continue;
                        } else {
                            System.out.println(red + "\nYou do not have permission to update this flight." + reset);
                            continue;
                        }
                    } else {
                        System.out.println(red + "\nFlight not found! Please check the flight number." + reset);
                        continue;
                    }
                }
//              ================================== Return to Main Menu ==========================
                case 7 -> {
                    return;
                }
//              ================================== Exit System ==================================
                case 8 -> {
                    System.out.println(red + "\nExiting the system\n" + reset);
                    System.exit(0);
                }
//              ================================== Invalid Choice ==============================
                default -> {
                    System.out.println(red + "\nInvalid choice! Please try again." + reset);
                }
            }
        }
    }

    public static void passengerLogin() throws Exception {
        while (true) {
            ArrayList<Passenger> passengers = PassengerDAO.getPassengers();
            System.out.println("\n===== PASSENGER LOGIN =====");
            System.out.println("1. Login via Email and Password");
            System.out.println("2. Register as New Passenger");
            System.out.println("3. Return to previous Menu");
            System.out.println("4. Exit System");
            System.out.print("Enter choice: ");
            int choice = 0;

            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(red + "\nOnly Digits allowed" + reset);
                sc.nextLine();
                continue;
            }
            switch (choice) {
//              ================================== User Login =====================================
                case 1 -> {
                    System.out.print("\nEnter Email: ");
                    String email = sc.next().trim();
                    System.out.print("Enter Password: ");
                    String password = sc.next().trim();
                    boolean found = false;
                    for (int i = 0; i < passengers.size(); i++) {
                        if (passengers.get(i).getEmail().equals(email) && passengers.get(i).getPass().equals(password)) {
                            found = true;
                            System.out.println(green + "\nLogin successful! Welcome " + passengers.get(i).getName() + reset);
                            passengerMenu(passengers.get(i));
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println(red + "\nInvalid email or password!" + reset);
                        continue;
                    }

                }
//              ============================== User Registration ==================================
                case 2 -> {
                    String name = "";
                    sc.nextLine();
                    while (true) {
                        System.out.print("\nEnter Name: ");
                        name = sc.nextLine().trim();
                        if (!name.matches("^[a-zA-Z\\s]+$")) {
                            System.out.println(red + "\nInvalid name! Please use only letters and spaces." + reset);
                        } else {
                            break;
                        }
                    }

                    String email = "";
                    while (true) {
                        System.out.print("Enter Email: ");
                        email = sc.next().trim();
                        if (email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")) {
                            boolean emailExists = false;
                            for (int i = 0; i < passengers.size(); i++) {
                                if (passengers.get(i).getEmail().equals(email)) {
                                    System.out.println(red + "\nEmail already exists! Please use a different email.\n" + reset);
                                    emailExists = true;
                                    break;
                                }
                            }
                            if (emailExists) {
                                continue;
                            }
                            break;
                        } else {
                            System.out.println(red + "\nInvalid email format! (Ex. xyz123@example.com).\n" + reset);
                        }
                    }

                    String phone = "";
                    while (true) {
                        System.out.print("Enter Phone: ");
                        phone = sc.next().trim();
                        if (phone.matches("^\\d{10}$") && (phone.charAt(0) == '9' || phone.charAt(0) == '8' || phone.charAt(0) == '7' || phone.charAt(0) == '6')) {
                            boolean phoneExists = false;
                            for (int i = 0; i < passengers.size(); i++) {
                                if (passengers.get(i).getPhone().equals(phone)) {
                                    System.out.println(red + "\nPhone number already exists! Please use a different phone number.\n" + reset);
                                    phoneExists = true;
                                    break;
                                }
                            }
                            if (phoneExists) {
                                continue;
                            }
                            break;
                        } else {
                            System.out.println(red + "\nInvalid phone number! Please enter a 10-digit number.\n" + reset);
                        }
                    }

                    String password = "";
                    while (true) {
                        System.out.print("Enter Password: ");
                        password = sc.next();
                        if (password.length() >= 6) {
                            boolean hasAlpha = password.matches(".*[a-zA-Z].*");
                            boolean hasDigit = password.matches(".*\\d.*");
                            boolean hasSpecialChar = password.matches(".*[_@#].*");
                            int sum = (hasAlpha ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecialChar ? 1 : 0);
                            if (sum < 2) {
                                System.out.println(red + "\nPassword must contain at least two of the following: letters, digits, or special characters (_@#)\n" + reset);
                            } else {
                                break;
                            }
                        } else {
                            System.out.println(red + "\nPassword must be at least 6 characters long!\n" + reset);
                        }
                    }

                    Passenger newPassenger = new Passenger(0, name, email, phone, password);
                    if (PassengerDAO.addPassenger(newPassenger)) {
                        System.out.println(green + "\nRegistration successful! You can now login." + reset);
                    } else {
                        System.out.println(red + "\nRegistration failed! Please try again." + reset);
                    }
                }
//              ================================== Return to Main Menu ============================
                case 3 -> {
                    return;
                }
//              ================================== Exit System ====================================
                case 4 -> {
                    System.out.println(red + "\nExiting the system\n" + reset);
                    System.exit(0);
                }
//              ================================== Invalid Choice =================================
                default -> {
                    System.out.println(red + "\nInvalid choice! Please try again." + reset);
                }
            }
        }
    }

    public static void passengerMenu(Passenger p) throws Exception {
        while (true) {
            ArrayList<Flight> flights = FlightDAO.getFlight();
            System.out.println("\n===== PASSENGER MENU =====");
            System.out.println("1. Make a Reservation");
            System.out.println("2. View Reservations");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Return to previous Menu");
            System.out.println("5. Exit System");
            System.out.print("Enter choice: ");
            int choice = 0;

            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(red + "\nOnly Digits allowed" + reset);
                sc.nextLine();
                continue;
            }
            switch (choice) {
//              ================================ Make a Reservation ==================================
                case 1 -> {
                    System.out.print("\nEnter departure: ");
                    sc.nextLine();
                    String departure = sc.nextLine().trim();
                    if (!departure.matches("^[a-zA-Z\\s]+$")) {
                        System.out.println(red + "\nInvalid departure! Please use only letters and spaces." + reset);
                        continue;
                    }
                    System.out.print("Enter destination: ");
                    String destination = sc.nextLine().trim();
                    if (!destination.matches("^[a-zA-Z\\s]+$")) {
                        System.out.println(red + "\nInvalid destination! Please use only letters and spaces." + reset);
                        continue;
                    }

//                  ================================== Search for Flights ==================================
                    ArrayList<Flight> availableFlights = new ArrayList<>();
                    for (int i = 0; i < flights.size(); i++) {
                        boolean matchesRoute = flights.get(i).getDeparture().equalsIgnoreCase(departure) && flights.get(i).getDestination().equalsIgnoreCase(destination);

                        boolean hasAvailableSeats = flights.get(i).getAvailable_seats() > 0;

                        if (matchesRoute && hasAvailableSeats) {
                            availableFlights.add(flights.get(i));
                        }
                    }

//                  ================================ Display Available Flights ==================================
                    if (!FlightDAO.displayAvailableFlights(availableFlights)) {
                        continue;
                    }

//                  ================================== Make a Reservation ==================================
                    ReservationDAO.makeAReservation(availableFlights, p);
                }
//              ================================== View Reservations ==================================
                case 2 -> {
                    int flightId = 0;
                    System.out.print("\nEnter flight ID: ");
                    try {
                        flightId = sc.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println(red + "\nInvalid flight ID! Please enter a valid number." + reset);
                        sc.nextLine(); // Clear the invalid input
                        continue;
                    }
                    if (flightId < 0) {
                        System.out.println(red + "\nInvalid flight ID! Please enter a positive number." + reset);
                        continue;
                    }
                    ReservationDAO.viewReservations(p.name, flightId);
                }
//              ================================== Cancel a Reservation ==================================
                case 3 -> {
                    System.out.print("\nEnter flight ID: ");
                    int flightId = 0;
                    try {
                        flightId = sc.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println(red + "\nInvalid flight ID! Please enter a valid number." + reset);
                        sc.nextLine(); // Clear the invalid input
                        continue;
                    }
                    if (flightId < 0) {
                        System.out.println(red + "\nInvalid flight ID! Please enter a positive number." + reset);
                        continue;
                    }
                    if (ReservationDAO.viewReservationForCancelReservation(p.name, flightId)) {
                        if (ReservationDAO.cancelReservation(p.name, flightId)) {
                            continue;
                        } else {
                            System.out.println(red + "\nFailed to cancel reservation. Please try again." + reset);
                        }
                    }
                }
//              ================================== Return to Main Menu ==================================
                case 4 -> {
                    return;
                }
//              ================================== Exit System ==================================
                case 5 -> {
                    System.out.println(red + "\nExiting the system\n" + reset);
                    System.exit(0);
                }
//              ================================== Invalid Choice ==================================
                default -> {
                    System.out.println(red + "\nInvalid choice! Please try again." + reset);
                }
            }
        }
    }
}