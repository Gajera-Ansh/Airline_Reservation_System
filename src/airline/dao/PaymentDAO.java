package airline.dao;

import airline.App;
import airline.util.DBUtil;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        sc.nextLine(); // Wait for user to press Enter

        int r = (int) (Math.random() * 1000);
        String pass = r + "";
        for (int j = 0; j < 5; j++) {
            char c = (char) ('A' + (int) (Math.random() * 26));
            pass = pass + c;
        }

        PaymentDAO.getDetails(pass); // Get Email

        System.out.print("\nEnter password which is sent to your email address: ");
        String inputPass = sc.next().trim();
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

    public static void sendEmail(String to, String from, String password, String subject, String body) {
        // SMTP server configuration
        String host = "smtp.gmail.com"; // For Gmail
        int port = 587;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Get the Session object
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Set the actual message
            message.setText(body);

            // Send message
            Transport.send(message);

            System.out.println(App.green + "\nEmail sent successfully!" + App.reset);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getDetails(String pass) {
        // Example usage
        String to = "------your email address------";
        String from = "anshcheck4227@gmail.com";
        String password = "byfr qckt oyrk pohc"; // Use app password for Gmail
        String subject = "Password for Payment Confirmation";
        String body = "Your password is: " + pass;

        sendEmail(to, from, password, subject, body);
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
