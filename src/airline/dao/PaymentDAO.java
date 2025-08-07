package airline.dao;

import airline.App;
import airline.util.DBUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class PaymentDAO {
    public static Scanner sc = new Scanner(System.in);

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

    public static void addPayment(int passengerId, int flightId, int seats) throws Exception {
        // This method is used to add a payment record for a passenger when they book a flight.

        double amount = 0;

        // Get the price of the flight
        String sql = "SELECT price FROM flights WHERE flight_id = " + flightId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            amount = rs.getDouble(1);
        }

        // Calculate the total amount based on the number of seats booked
        amount *= seats;

        // Insert the payment record into the payments table
        String sql1 = "INSERT INTO payments (passenger_id, flight_id, amount) VALUES (?, ?, ?)";
        PreparedStatement pst = DBUtil.con.prepareStatement(sql1);
        pst.setInt(1, passengerId);
        pst.setInt(2, flightId);
        pst.setDouble(3, amount);
        pst.executeUpdate();
    }
}
