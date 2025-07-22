package airline;

import airline.util.DBUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PDFReceiptGenerator {

    public static void generateReceipt(int flight_id, int passenger_id, int seats) throws Exception {
        Document document = new Document();
        String fileName = "D://Ticket.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Airline Reservation Ticket", titleFont));
        document.add(new Paragraph(" "));

        // get passenger details
        String q1 = "{CALL getPassenger(?, ?, ?, ?)}";
        CallableStatement c1 = DBUtil.con.prepareCall(q1);
        c1.setInt(1, passenger_id);
        c1.executeQuery();
        document.add(new Paragraph("Passenger Details", subtitleFont));
        document.add(new Paragraph("Name: " + c1.getString(2), contentFont));
        document.add(new Paragraph("Email: " + c1.getString(3), contentFont));
        document.add(new Paragraph("Phone: " + c1.getString(4), contentFont));
        document.add(new Paragraph(" "));

        // get flight details
        String q2 = "{CALL getFlight(?, ?, ?, ?, ?, ?)}";
        CallableStatement c2 = DBUtil.con.prepareCall(q2);
        c2.setInt(1, flight_id);
        c2.executeQuery();
        document.add(new Paragraph("Flight Details", subtitleFont));
        document.add(new Paragraph("Flight Number: " + c2.getString(2), contentFont));
        document.add(new Paragraph("Departure: " + c2.getString(3), contentFont));
        document.add(new Paragraph("Destination: " + c2.getString(4), contentFont));
        document.add(new Paragraph("Departure Time: " + c2.getString(5), contentFont));
        document.add(new Paragraph("Arrival Time: " + c2.getString(6), contentFont));
        document.add(new Paragraph("Booked Seats: " + seats, contentFont));
        document.add(new Paragraph(" "));

        // get reservation details
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

        // get payment details
        String q4 = "{CALL getPayment(?, ?, ?, ?)}";
        CallableStatement c4 = DBUtil.con.prepareCall(q4);
        c4.setInt(1, flight_id);
        c4.setInt(2, passenger_id);
        c4.executeQuery();
        document.add(new Paragraph("Payment Details", subtitleFont));
        document.add(new Paragraph("Amount Paid: " + c4.getDouble(3), contentFont));
        document.add(new Paragraph("Payment Date: " + c4.getTimestamp(4), contentFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Thank you! for choosing our Reservation System", subtitleFont));

        System.out.println("\nPDF generated successfully at: " + App.green + fileName + App.reset);

        document.close();
    }
}

