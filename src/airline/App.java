package airline;

import airline.dao.*;
import airline.ds.*;
import airline.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import airline.util.*;

import java.util.Scanner;

public class App {
    static {
        System.out.println("\n===============================================");
        System.out.println("    AIRLINE RESERVATION MANAGEMENT SYSTEM     ");
        System.out.println("===============================================\n");
    }

    public static final String red = "\u001B[31m";
    public static final String green = "\u001B[32m";
    public static final String reset = "\u001B[0m";
    public static final Scanner sc = new Scanner(System.in);
    public static final FlightDAO flightDAO = new FlightDAO();
    public static final PassengerDAO passengerDAO = new PassengerDAO();
    public static final ReservationDAO reservationDAO = new ReservationDAO();
    public static final AdminDAO adminDAO = new AdminDAO();
    public static final ReportDAO reportDAO = new ReportDAO();
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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
                    System.out.println(green + "\nExiting the system\n" + reset);
                    System.exit(0);
                    break;
                }
                default -> {
                    System.out.println(red + "\nInvalid choice! Please try again." + reset);
                }
            }
        }
    }

    public static void passengerLogin() {

    }

    public static void passengerMenu() {
        while(true) {
            System.out.println("\n===== PASSENGER MENU =====");
            System.out.println("1. Make a Reservation");
            System.out.println("2. View Reservations");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter choice: ");
            switch(sc.nextInt()) {
                case 1 -> {

                }
            }
        }
    }
}
