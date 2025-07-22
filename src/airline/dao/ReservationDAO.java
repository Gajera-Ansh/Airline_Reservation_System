package airline.dao;

import airline.App;
import airline.PDFReceiptGenerator;
import airline.ds.ArrayList;
import airline.util.DBUtil;
import airline.ds.HashMap;

import java.io.*;
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
        // insert reservation details into the reservations table
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

            // Confirm reservation with the user before making payment
            System.out.print("Confirming reservation for " + seats + " seats for flight ID: " + flightId + "  (y/n): ");
            char choice = sc.next().trim().toLowerCase().charAt(0);
            if (choice == 'y') {
                if (makePayment(passengerId, flightId, seats)) {
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
                        PDFReceiptGenerator.generateReceipt(flightId, passengerId, seats);
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

    public static boolean makePayment(int passenger_id, int flight_id, int seats) throws Exception {
//        ========== QR Code Generation ==========
        FileInputStream fis = new FileInputStream("src/airline/QR.png");
        FileOutputStream fos = new FileOutputStream("D://QR.png");
        int i = fis.read();

        while (i != -1) {
            fos.write(i);
            i = fis.read();
        }
        fos.close();
        fis.close();
        File f1 = new File("D://QR.png");
        System.out.println("\nQR code is generated at " + App.green + f1.getAbsolutePath() + App.reset);
        System.out.println("\nPlease scan the QR code to make the payment.");
        System.out.print("\nPress Enter after payment is done.");
        sc.nextLine(); // Consume the newline character left by previous input
        sc.nextLine(); // Wait for user to press Enter

//        ========== Password Generation ==========
        FileWriter fw = new FileWriter("D://pass.txt");
        int r = (int) (Math.random() * 1000);
        String pass = r + "";
        for (int j = 0; j < 5; j++) {
            char c = (char) ('A' + (int) (Math.random() * 26));
            pass = pass + c;
        }

        // Write the password to the file
        fw.write(pass);
        fw.close();

        File f2 = new File("D://pass.txt");
        System.out.print("\nEnter the password (which is print in pass.txt file " + App.green + f2.getAbsolutePath() + App.reset + ") to confirm payment: ");
        String inputPass = sc.nextLine().trim();
        if (inputPass.equals(pass)) {

            // Update payment database
            PaymentDAO.addPayment(passenger_id, flight_id, seats);
            System.out.println(App.green + "\nPayment successful." + App.reset);
        } else {
            System.out.println(App.red + "\nPayment failed. Incorrect password." + App.reset);
            return false;
        }
        return true;
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
}
