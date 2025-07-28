package airline;

import airline.dao.*;
import airline.ds.*;
import airline.model.*;
import airline.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
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

            switch (sc.nextInt()) {
//                =================================== Passenger Login ==================================
                case 1 -> {
                    passengerLogin();
                    break;
                }
//                =================================== Admin Login ==================================
                case 2 -> {
                    adminLogin();
                    break;
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
            System.out.println("5. View Flight Reservations");
            System.out.println("6. View Passenger List");
            System.out.println("7. Generate Flight Report");
            System.out.println("8. Return to previous Menu");
            System.out.println("9. Exit System");
            System.out.print("Enter choice: ");
            switch (sc.nextInt()) {
//              ================================== Add New Flight ==================================
                case 1 -> {
                    System.out.print("\nEnter flight number: ");
                    String fnumber = sc.next().trim().toUpperCase();

                    // Validate flight number format
                    if (fnumber.matches("^[A-Z]{2,3}\\d{1,4}$")) {
                        System.out.print("Enter departure: ");
                        String departure = sc.next().trim().toUpperCase();

                        // Validate departure format
                        if (departure.matches("^[a-zA-Z\\s]+$")) {
                            System.out.print("Enter destination: ");
                            String destination = sc.next().trim().toUpperCase();

                            // Validate destination format
                            if (destination.matches("^[a-zA-Z\\s]+$")) {
                                System.out.print("Enter departure time (yyyy-MM-dd HH:mm:ss): ");
                                sc.nextLine();
                                String departureTime = sc.nextLine().trim();
                                LocalDateTime depTime = LocalDateTime.parse(departureTime, dateTimeFormatter);

                                // Validate departure time format
                                if (departureTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {

                                    // Check if departure time is in the past
                                    if (depTime.isBefore(LocalDateTime.now())) {
                                        System.out.println(red + "\nDeparture time cannot be in the past." + reset);
                                        continue;
                                    }
                                    System.out.print("Enter arrival time (yyyy-MM-dd HH:mm:ss): ");
                                    String arrivalTime = sc.nextLine().trim();

                                    // Validate arrival time format
                                    if (arrivalTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {

                                        // Check if it is after departure time
                                        LocalDateTime arrTime = LocalDateTime.parse(arrivalTime, dateTimeFormatter);
                                        if (arrTime.isBefore(depTime) || arrTime.isEqual(depTime)) {
                                            System.out.println(red + "\nArrival time must be after departure time." + reset);
                                            continue;
                                        }
                                        System.out.print("Enter total seats: ");
                                        int totalSeats = sc.nextInt();

                                        // Validate total seats
                                        if (totalSeats > 0) {
                                            System.out.print("Enter price: ");
                                            double price = sc.nextDouble();

                                            // Validate price
                                            if (price > 0) {
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
                                            } else {
                                                System.out.println(red + "\nInvalid price! Please enter a positive number." + reset);
                                                continue;
                                            }
                                        } else {
                                            System.out.println(red + "\nInvalid total seats! Please enter a positive number." + reset);
                                            continue;
                                        }
                                    } else {
                                        System.out.println(red + "\nInvalid arrival time format! Please use yyyy-MM-dd HH:mm:ss." + reset);
                                        continue;
                                    }
                                } else {
                                    System.out.println(red + "\nInvalid departure time format! Please use yyyy-MM-dd HH:mm:ss." + reset);
                                    continue;
                                }
                            } else {
                                System.out.println(red + "\nInvalid destination! Please use only letters and spaces." + reset);
                                continue;
                            }
                        } else {
                            System.out.println(red + "\nInvalid departure! Please use only letters and spaces." + reset);
                            continue;
                        }
                    } else {
                        System.out.println(red + "\nInvalid flight number! Please use the format ABC1234." + reset);
                        continue;
                    }
                }
//              ================================== Remove Flight ==================================
                case 2 -> {
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
                    if(!flightExists) {
                        System.out.println(red + "\nFlight not found! Please check the flight number." + reset);
                    } else if (flightNotAccessible) {
                        System.out.println(red + "\nYou do not have permission to remove this flight." + reset);
                    }
                }
//              ================================== View All Flights ==================================
                case 3 -> {
                }
//              ================================== Update Flight Information ==========================
                case 4 -> {
                }
//              ================================== View Flight Reservations ==========================
                case 5 -> {
                }
//              ================================== View Passenger List ==============================
                case 6 -> {
                }
//              ================================== Generate Flight Report ==========================
                case 7 -> {
                }
//              ================================== Return to Main Menu ==========================
                case 8 -> {
                    return;
                }
//              ================================== Exit System ==================================
                case 9 -> {
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
            switch (sc.nextInt()) {
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
                    System.out.print("\nEnter Name: ");
                    sc.nextLine();
                    String name = sc.nextLine().trim();
                    if (!name.matches("^[a-zA-Z\\s]+$")) {
                        System.out.println(red + "\nInvalid name! Please use only letters and spaces." + reset);
                        continue;
                    }
                    System.out.print("Enter Email: ");
                    String email = sc.next().trim();
                    if (email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")) {
                        boolean emailExists = false;
                        for (int i = 0; i < passengers.size(); i++) {
                            if (passengers.get(i).getEmail().equals(email)) {
                                System.out.println(red + "\nEmail already exists! Please use a different email." + reset);
                                emailExists = true;
                                break;
                            }
                        }
                        if (emailExists) {
                            continue;
                        }
                    } else {
                        System.out.println(red + "\nInvalid email format!" + reset);
                        continue;
                    }
                    System.out.print("Enter Phone: ");
                    String phone = sc.next().trim();
                    if (phone.matches("^\\d{10}$")) {
                        boolean phoneExists = false;
                        for (int i = 0; i < passengers.size(); i++) {
                            if (passengers.get(i).getPhone().equals(phone)) {
                                System.out.println(red + "\nPhone number already exists! Please use a different phone number." + reset);
                                phoneExists = true;
                                break;
                            }
                        }
                        if (phoneExists) {
                            continue;
                        }
                    } else {
                        System.out.println(red + "\nInvalid phone number! Please enter a 10-digit number." + reset);
                        continue;
                    }
                    System.out.print("Enter Password: ");
                    String password = sc.next();
                    if (password.length() >= 6) {
                        boolean hasAlpha = password.matches(".*a-zA-Z].*");
                        boolean hasDigit = password.matches(".*\\d.*");
                        boolean hasSpecialChar = password.matches(".*[_@#].*");
                        int sum = (hasAlpha ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecialChar ? 1 : 0);
                        if (sum < 2) {
                            System.out.println(red + "\nPassword must contain at least two of the following: letters, digits, or special characters (_@#)" + reset);
                            continue;
                        }
                    } else {
                        System.out.println(red + "\nPassword must be at least 6 characters long!" + reset);
                        continue;
                    }
                    Passenger newPassenger = new Passenger(0, name, email, phone, password);
                    if (PassengerDAO.addPassenger(newPassenger)) {
                        System.out.println(green + "\nRegistration successful! You can now login." + reset);
                    } else {
                        System.out.println(red + "\nRegistration failed! Please try again." + reset);
                    }
                }
//              ================================== Return to Main Menu ==========================
                case 3 -> {
                    return;
                }
//              ================================== Exit System ==================================
                case 4 -> {
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
            switch (sc.nextInt()) {
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
                    System.out.print("Enter date(yyyy-MM-dd): ");
                    String date = sc.next().trim();
                    if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        System.out.println(red + "\nInvalid date format! Please use yyyy-MM-dd." + reset);
                        continue;
                    } else {
                        LocalDate today = LocalDate.now();
                        LocalDate inputDate = LocalDate.parse(date, dateFormatter);
                        if (inputDate.isBefore(today)) {
                            System.out.println(red + "\nDate cannot be in the past! Please enter a valid date." + reset);
                            continue;
                        }
                    }

//                  ================================== Search for Flights ==================================
                    ArrayList<Flight> availableFlights = new ArrayList<>();
                    for (int i = 0; i < flights.size(); i++) {
                        boolean matchesRoute = flights.get(i).getDeparture().equalsIgnoreCase(departure) && flights.get(i).getDestination().equalsIgnoreCase(destination);

                        boolean matchesDate = Objects.requireNonNull(FlightDAO.getFlightDate(flights.get(i).getFlight_id())).isEqual(LocalDate.parse(date, dateFormatter));

                        boolean hasAvailableSeats = flights.get(i).getAvailable_seats() > 0;

                        if (matchesRoute && matchesDate && hasAvailableSeats) {
                            availableFlights.add(flights.get(i));
                        }
                    }

//                  ================================ Display Available Flights ==================================
                    if (!displayAvailableFlights(availableFlights)) {
                        continue;
                    }

//                  ================================== More Flights Available ==================================
                    ArrayList<Flight> moreFlights = moreFlights(departure, destination, flights);
                    availableFlights = moreFlights == null ? availableFlights : moreFlights;

//                   ================================= Make a Reservation ==================================
                    makeAReservation(availableFlights, p);
                }
//              ================================== View Reservations ==================================
                case 2 -> {
                    System.out.print("\nEnter your full name: ");
                    sc.nextLine();
                    String name = sc.nextLine().trim();
                    if (!name.matches("^[a-zA-Z\\s]+$")) {
                        System.out.println(red + "\nInvalid name! Please use only letters and spaces." + reset);
                        continue;
                    }
                    System.out.print("Enter flight ID: ");
                    int flightId = sc.nextInt();
                    if (flightId < 0) {
                        System.out.println(red + "\nInvalid flight ID! Please enter a positive number." + reset);
                        continue;
                    }
                    ReservationDAO.viewReservations(name, flightId);
                }
//              ================================== Cancel a Reservation ==================================
                case 3 -> {
                    System.out.print("\nEnter your full name: ");
                    sc.nextLine();
                    String name = sc.nextLine().trim();
                    if (!name.matches("^[a-zA-Z\\s]+$")) {
                        System.out.println(red + "\nInvalid name! Please use only letters and spaces." + reset);
                        continue;
                    }
                    System.out.print("Enter flight ID: ");
                    int flightId = sc.nextInt();
                    if (flightId < 0) {
                        System.out.println(red + "\nInvalid flight ID! Please enter a positive number." + reset);
                        continue;
                    }
                    if (ReservationDAO.viewReservations(name, flightId)) {
                        if (ReservationDAO.cancelReservation(name, flightId)) {
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

    public static void makeAReservation(ArrayList<Flight> flights, Passenger p) throws Exception {
        while (true) {
            boolean reservationStatus = false;
            ArrayList<Flight> flightList = flights;
//          ================================== Select Flight ==================================
            System.out.print("\nWant to make a reservation? (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if (choice == 'y') {
                System.out.print("\nEnter flight ID: ");
                int flightId = sc.nextInt();
                System.out.print("Enter number of seats to reserve: ");
                int seats = sc.nextInt();
//               ================================= Validate Seats ==================================
                if (seats > 0) {
                    boolean flag = false;
//                    ================================ Check Flight ID and Available Seats ==================================
                    for (int i = 0; i < flightList.size(); i++) {
                        if (flightList.get(i).getFlight_id() == flightId && flightList.get(i).getAvailable_seats() >= seats) {
                            flag = false;
//                           ================================= Add Reservation ==================================
                            reservationStatus = ReservationDAO.addReservation(flightList.get(i).getFlight_id(), p.getPassenger_id(), seats);
                            break;
                        } else {
                            flag = true;
                        }
                    }
//                   ================================= Invalid Flight ID or Seats ==================================
                    if (flag) {
                        System.out.println(red + "\nInvalid flight ID or insufficient seats available! Please try again." + reset);
                        continue;
                    }
                }
//                ================================ Invalid Number of Seats ==================================
                else {
                    System.out.println(red + "\nInvalid number of seats! Please enter a positive number." + reset);
                    continue;
                }
            } else if (choice == 'n') {
                return;
            } else {
                System.out.println(red + "\nInvalid choice! Please try again." + reset);
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

    public static ArrayList<Flight> moreFlights(String departure, String destination, ArrayList<Flight> flights) throws
            Exception {
        while (true) {
//            ================================ Check for More Flights ==================================
            ArrayList<Flight> flight = flights;
            ArrayList<Flight> availableFlights = new ArrayList<>();
            System.out.print("\nWant to see more flights? (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);

            if (choice == 'y') {
//                ================================ Search for More Flights ==================================
                for (int i = 0; i < flight.size(); i++) {
                    boolean matchesRoute = flight.get(i).getDeparture().equalsIgnoreCase(departure) && flight.get(i).getDestination().equalsIgnoreCase(destination);

                    boolean hasAvailableSeats = flight.get(i).getAvailable_seats() > 0;

                    if (matchesRoute && hasAvailableSeats) {
                        availableFlights.add(flight.get(i));
                    }
                }
//                ================================ Display Available Flights ==================================
                displayAvailableFlights(availableFlights);
                return availableFlights;
            } else if (choice == 'n') {
                return null;
            } else {
                System.out.println(red + "\nInvalid choice! Please try again." + reset);
                continue;
            }
        }
    }

    public static boolean displayAvailableFlights(ArrayList<Flight> availableFlights) throws Exception {
//        ================================ No Flights Available ==================================
        if (availableFlights.size() == 0) {
            System.out.println(red + "\nNo flights available for the selected criteria." + reset);
            return false;
        }
//        ================================ Display Available Flights ==================================
        else {
            System.out.println("\nSearching for available flights...");
            Thread.sleep(2000);
            System.out.println("\nAvailable Flights:");
            System.out.printf("\n%-10s %-15s %-15s %-15s %-20s %-20s %-13s %-17s %-10s\n", "Flight ID", "Flight Number", "Departure", "Destination", "Departure Time", "Arrival Time", "Total Seats", "Available Seats", "Price");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------");
            for (int i = 0; i < availableFlights.size(); i++) {
                System.out.printf("%-10s %-15s %-15s %-15s %-20s %-20s %-13d %-17d ₹%-10.2f\n", availableFlights.get(i).getFlight_id(), availableFlights.get(i).getFlight_number(), availableFlights.get(i).getDeparture(), availableFlights.get(i).getDestination(), availableFlights.get(i).getDeparture_time().format(dateTimeFormatter), availableFlights.get(i).getArrival_time().format(dateTimeFormatter), availableFlights.get(i).getTotal_seats(), availableFlights.get(i).getAvailable_seats(), availableFlights.get(i).getPrice());
            }
            return true;
        }
    }
}