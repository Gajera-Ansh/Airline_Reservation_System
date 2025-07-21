package airline;

import airline.dao.ReservationDAO;
import airline.util.DBUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import kotlin.reflect.KParameter;

import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

public class PDFReceiptGenerator {

    public static void generateReceipt(int flight_id, int passenger_id, int seats) throws Exception {
        Document document = new Document();
        String fileName = "D://Ticket.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Add content
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Airline Reservation Ticket", titleFont));
        document.add(new Paragraph(" "));
        String q1 = "SELECT * FROM passengers WHERE passenger_id = " + passenger_id;
        PreparedStatement p1 = DBUtil.con.prepareStatement(q1);
        ResultSet rs1 = p1.executeQuery();
        if (rs1.next()) {
            document.add(new Paragraph("Passenger Details", subtitleFont));
            document.add(new Paragraph("Name: " + rs1.getString("name"), contentFont));
            document.add(new Paragraph("Email: " + rs1.getString("email"), contentFont));
            document.add(new Paragraph("Phone: " + rs1.getString("phone"), contentFont));
            document.add(new Paragraph(" "));
        }
        String q2 = "SELECT * FROM flights WHERE flight_id = " + flight_id;
        PreparedStatement p2 = DBUtil.con.prepareStatement(q2);
        ResultSet rs2 = p2.executeQuery();
        if (rs2.next()) {
            document.add(new Paragraph("Flight Details", subtitleFont));
            document.add(new Paragraph("Flight Number: " + rs2.getString("flight_number"), contentFont));
            document.add(new Paragraph("Departure: " + rs2.getString("departure"), contentFont));
            document.add(new Paragraph("Destination: " + rs2.getString("destination"), contentFont));
            document.add(new Paragraph("Departure Time: " + rs2.getString("departure_time"), contentFont));
            document.add(new Paragraph("Arrival Time: " + rs2.getString("arrival_time"), contentFont));
            document.add(new Paragraph("Booked Seats: " + seats, contentFont));
            document.add(new Paragraph(" "));
        }
        String q3 = "SELECT * FROM reservations WHERE flight_id = " + flight_id + " AND passenger_id = " + passenger_id;
        PreparedStatement p3 = DBUtil.con.prepareStatement(q3);
        ResultSet rs3 = p3.executeQuery();
        document.add(new Paragraph("Reservation Details", subtitleFont));
        while (rs3.next()) {
            document.add(new Paragraph("Reservation ID: " + rs3.getInt("reservation_id"), contentFont));
            document.add(new Paragraph("Seat Numbers: " + rs3.getString("seat_number"), contentFont));
            document.add(new Paragraph("Reservation Date: " + rs3.getTimestamp("reservation_date"), contentFont));
            document.add(new Paragraph(" "));
        }
        String q4 = "SELECT * FROM payments WHERE flight_id = " + flight_id + " AND passenger_id = " + passenger_id;
        PreparedStatement p4 = DBUtil.con.prepareStatement(q4);
        ResultSet rs4 = p4.executeQuery();
        if (rs4.next()) {
            document.add(new Paragraph("Payment Details", subtitleFont));
            document.add(new Paragraph("Amount Paid: " + rs4.getDouble("amount"), contentFont));
            document.add(new Paragraph("Payment Date: " + rs4.getTimestamp("payment_time"), contentFont));
            document.add(new Paragraph(" "));
        }

        document.add(new Paragraph("Thank you for choosing our Reservation System!", subtitleFont));

        System.out.println("\nPDF generated successfully at: " + App.green + fileName + App.reset);

        document.close();

    }
}

