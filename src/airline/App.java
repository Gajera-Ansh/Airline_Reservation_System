package airline;

import airline.dao.*;
import airline.ds.*;
import airline.model.*;
import airline.util.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
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
                case 1 -> {
                    passengerLogin();
                    break;
                }
                case 2 -> {

                    break;
                }
                case 3 -> {
                    System.out.println(red + "\nExiting the system\n" + reset);
                    System.exit(0);
                }
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
            System.out.println("3. Return to Main Menu");
            System.out.println("4. Exit System");
            System.out.print("Enter choice: ");
            switch (sc.nextInt()) {
//              ================================== User Login =====================================
                case 1 -> {
//                    System.out.print("\nEnter Email: ");
//                    String email = sc.next().trim();
//                    System.out.print("Enter Password: ");
//                    String password = sc.next().trim();
//                    boolean found = false;
//                    for (int i = 0; i < passengers.size(); i++) {
//                        if (passengers.get(i).getEmail().equals(email) && passengers.get(i).getPass().equals(password)) {
//                            found = true;
//                            System.out.println(green + "\nLogin successful! Welcome " + passengers.get(i).getName() + reset);
//                            passengerMenu();
//                            break;
//                        }
//                    }
//                    if (!found) {
//                        System.out.println(red + "\nInvalid email or password!" + reset);
//                        continue;
//                    }
                            passengerMenu();

                }
//              =================================== User Registration ===================================
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
                    if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")) {
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
                case 3 -> {
                    return;
                }
                case 4 -> {
                    System.out.println(red + "\nExiting the system\n" + reset);
                    System.exit(0);
                }
                default -> {
                    System.out.println(red + "\nInvalid choice! Please try again." + reset);
                }
            }
        }
    }

    public static void passengerMenu() throws Exception {
        while (true) {
            ArrayList<Flight> flights = FlightDAO.getFlight();
            System.out.println("\n===== PASSENGER MENU =====");
            System.out.println("1. Make a Reservation");
            System.out.println("2. View Reservations");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Return to Main Menu");
            System.out.println("5. Exit System");
            System.out.print("Enter choice: ");
            switch (sc.nextInt()) {
                case 1 -> {
                    System.out.print("\nEnter departure: ");
                    String departure = sc.next().trim();
                    if (!departure.matches("^[a-zA-Z\\s]+$")) {
                        System.out.println(red + "\nInvalid departure! Please use only letters and spaces." + reset);
                        continue;
                    }
                    System.out.print("Enter destination: ");
                    String destination = sc.next().trim();
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

                    System.out.println("\nSearching for available flights...");
                    Thread.sleep(2000);
                    ArrayList<Flight> availableFlights = new ArrayList<>();
                    for (int i = 0; i < flights.size(); i++) {
                        boolean matchesRoute = flights.get(i).getDeparture().equalsIgnoreCase(departure) &&
                                flights.get(i).getDestination().equalsIgnoreCase(destination);

                        boolean matchesDate = Objects.requireNonNull(FlightDAO.getFlightDate(flights.get(i).getFlight_id())).isEqual(LocalDate.parse(date, dateFormatter));

                        boolean hasAvailableSeats = flights.get(i).getAvailable_seats() > 0;

                        if (matchesRoute && matchesDate && hasAvailableSeats) {
                            availableFlights.add(flights.get(i));
                        }
                    }

                    if (!displayAvailableFlights(availableFlights)) {
                        continue;
                    }
                }
                case 2 -> {
                    break;
                }
                case 3 -> {
                    break;
                }
                case 4 -> {
                    return;
                }
                case 5 -> {
                    System.out.println(red + "\nExiting the system\n" + reset);
                    System.exit(0);
                }
            }
        }
    }

    public static boolean displayAvailableFlights(ArrayList<Flight> availableFlights) {
        if (availableFlights.size() == 0) {
            System.out.println(red + "\nNo flights available for the selected criteria." + reset);
            return false;
        } else {
            System.out.println("\nAvailable Flights:");
            for (int i = 0; i < availableFlights.size(); i++) {
                System.out.printf("\n%-10s %-15s %-15s %-15s %-20s %-20s %-13s %-17s %-10s\n",
                        "Flight ID", "Flight Number", "Departure", "Destination",
                        "Departure Time", "Arrival Time",
                        "Total Seats", "Available Seats", "Price");
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.printf("%-10s %-15s %-15s %-15s %-20s %-20s %-13d %-17d ₹%-10.2f\n",
                        availableFlights.get(i).getFlight_id(),
                        availableFlights.get(i).getFlight_number(),
                        availableFlights.get(i).getDeparture(),
                        availableFlights.get(i).getDestination(),
                        availableFlights.get(i).getDeparture_time().format(dateTimeFormatter),
                        availableFlights.get(i).getArrival_time().format(dateTimeFormatter),
                        availableFlights.get(i).getTotal_seats(),
                        availableFlights.get(i).getAvailable_seats(),
                        availableFlights.get(i).getPrice()
                );


//                System.out.println("\nFlight ID: " + availableFlights.get(i).getFlight_id() +"\tFlight Number: " + availableFlights.get(i).getFlight_number() +
//                                   "\n\nDeparture: " + availableFlights.get(i).getDeparture() +"\tDestination: " + availableFlights.get(i).getDestination() +
//                                   "\nDeparture Time: " + availableFlights.get(i).getDeparture_time().format(dateTimeFormatter) +
//                                   "\tArrival Time: " + availableFlights.get(i).getArrival_time().format(dateTimeFormatter) +
//                                   "\n\nTotal Seats:     " + availableFlights.get(i).getTotal_seats() +"\tAvailable Seats: " + availableFlights.get(i).getAvailable_seats() +
//                                   "\nPrice:  ₹" + availableFlights.get(i).getPrice());
            }
            return true;
        }
    }
}