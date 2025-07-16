package airline;

import airline.dao.*;
import airline.ds.*;
import airline.model.*;
import airline.util.*;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Scanner;

public class App {

    public static String red = "\u001B[31m";
    public static String green = "\u001B[32m";
    public static String reset = "\u001B[0m";
    public static Scanner sc = new Scanner(System.in);
    public static DBUtil dbUtil = new DBUtil();
    public static FlightDAO flightDAO = new FlightDAO();
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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
                    break;
                }
                default -> {
                    System.out.println(red + "\nInvalid choice! Please try again." + reset);
                }
            }
        }
    }

    public static void passengerLogin() throws Exception {
        ArrayList<Passenger> passengers = PassengerDAO.getPassengers();
        System.out.println("\n===== PASSENGER LOGIN =====");
        System.out.println("1. Login via Email and Password");
        System.out.println("2. Register as New Passenger");
        System.out.println("3. Return to Main Menu");
        System.out.print("Enter choice: ");
        switch (sc.nextInt()) {
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
                        passengerMenu();
                        break;
                    }
                }
                if (!found) {
                    System.out.println(red + "\nInvalid email or password!" + reset);
                }
            }
            case 2 -> {
                System.out.print("\nEnter Name: ");
                sc.nextLine();
                String name = sc.nextLine().trim();
                if (!name.matches("^[a-zA-Z\\s]+$")) {
                    System.out.println(red + "\nInvalid name! Please use only letters and spaces." + reset);
                    return;
                }
                System.out.print("Enter Email: ");
                String email = sc.next().trim();
                if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")) {
                    for (int i = 0; i < passengers.size(); i++) {
                        if (passengers.get(i).getEmail().equals(email)) {
                            System.out.println(red + "\nEmail already exists! Please use a different email." + reset);
                            return;
                        }
                    }
                } else {
                    System.out.println(red + "\nInvalid email format!" + reset);
                    return;
                }
                System.out.print("Enter Phone: ");
                String phone = sc.next().trim();
                if (phone.matches("^\\d{10}$")) {
                    for (int i = 0; i < passengers.size(); i++) {
                        if (passengers.get(i).getPhone().equals(phone)) {
                            System.out.println(red + "\nPhone number already exists! Please use a different phone number." + reset);
                            return;
                        }
                    }
                } else {
                    System.out.println(red + "\nInvalid phone number! Please enter a 10-digit number." + reset);
                    return;
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
                        return;
                    }
                } else {
                    System.out.println(red + "\nPassword must be at least 6 characters long!" + reset);
                    return;
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
            default -> {
                System.out.println(red + "\nInvalid choice! Please try again." + reset);
            }
        }
    }

    public static void passengerMenu() {
        while (true) {
            System.out.println("\n===== PASSENGER MENU =====");
            System.out.println("1. Make a Reservation");
            System.out.println("2. View Reservations");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter choice: ");
            switch (sc.nextInt()) {
                case 1 -> {
                    break;
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
            }
        }
    }
}