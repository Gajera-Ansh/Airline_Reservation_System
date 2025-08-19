package airline;

import airline.ds.ArrayList;
import airline.model.Flight;
import airline.util.DBUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;

public class PDFReceiptGenerator {

    public static void generateReceipt(int flight_id, int passenger_id, int seats) throws Exception {
        Document document = new Document();
        String fileName = "D://Ticket_fId_" + flight_id + "_pId_" + passenger_id + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        Paragraph title = new Paragraph("RESERVATION TICKET", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30f);
        document.add(title);

        // get passenger details
        String q1 = "{CALL getPassenger(?, ?, ?, ?)}";
        CallableStatement c1 = DBUtil.con.prepareCall(q1);
        c1.setInt(1, passenger_id);
        c1.executeQuery();
        document.add(new Paragraph("User Details", subtitleFont));
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
        String q3 = "SELECT * FROM reservations WHERE flight_id = " + flight_id + " AND passenger_id = " + passenger_id + " AND status = 'CONFIRMED'";
        PreparedStatement p3 = DBUtil.con.prepareStatement(q3);
        ResultSet rs3 = p3.executeQuery();
        document.add(new Paragraph("Reservation Details", subtitleFont));
        while (rs3.next()) {
            document.add(new Paragraph("Passenger Name: "+rs3.getString("passengerName"), contentFont));
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

    public static void getAllFlights(ArrayList<Flight> flights) throws Exception {
        Document document = new Document();
        String fileName = "D://FLights_data.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);


        // Add title to the document
        Paragraph title = new Paragraph("FLIGHTS DATA", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30f);
        document.add(title);

        // Create table with optimized column widths
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);

        // Set custom column widths
        float[] columnWidths = {1f, 1.3f, 1.8f, 1.8f, 1.8f, 1.7f, 1.0f, 1.5f, 1.2f};
        table.setWidths(columnWidths);

        // Table headers
        String[] headers = {"Flight ID", "Flight Number", "Departure", "Destination", "Departure Time", "Arrival Time", "Total Seats", "Available Seats", "Price"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(3);
            table.addCell(cell);
        }

        // Add flight data to the table
        for (int i = 0; i < flights.size(); i++) {
            table.addCell(createCell(String.valueOf(flights.get(i).getFlight_id()), contentFont));
            table.addCell(createCell(flights.get(i).getFlight_number(), contentFont));
            table.addCell(createCell(flights.get(i).getDeparture(), contentFont));
            table.addCell(createCell(flights.get(i).getDestination(), contentFont));
            table.addCell(createCell(String.valueOf(flights.get(i).getDeparture_time()), contentFont));
            table.addCell(createCell(String.valueOf(flights.get(i).getArrival_time()), contentFont));
            table.addCell(createCell(String.valueOf(flights.get(i).getTotal_seats()), contentFont));
            table.addCell(createCell(String.valueOf(flights.get(i).getAvailable_seats()), contentFont));
            table.addCell(createCell(String.format("₹%.2f", flights.get(i).getPrice()), contentFont));
        }

        document.add(table); // Add the table to the document
        document.close();
        System.out.println("\nPDF generated successfully at: " + App.green + fileName + App.reset);
    }

    public static PdfPCell createCell(String content, Font font) {
        // This method creates a PdfPCell with the given content and font, and sets padding for better readability.
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(3);
        return cell;
    }

    public static void passengerPDF(String flightNumber, int flightId) throws Exception {
        Document document = new Document();
        String fileName = "D://" + flightNumber + "_passenger_list.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        Paragraph title = new Paragraph("PASSENGER LIST", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30f);
        document.add(title);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);

        float[] columnWidths = {1f, 1.2f, 1.2f, 1.8f, 1f, 0.8f};
        table.setWidths(columnWidths);

        String[] headers = {"Res ID", "Pass ID", "Name", "Email", "Phone", "Seat"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(3);
            table.addCell(cell);
        }

        String sql1 = "SELECT * FROM reservations INNER JOIN passengers ON reservations.passenger_id = passengers.passenger_id WHERE flight_id = " + flightId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql1);
        while (rs.next()) {
            table.addCell(createCell(String.valueOf(rs.getInt("reservation_id")), contentFont));
            table.addCell(createCell(String.valueOf(rs.getInt("passenger_id")), contentFont));
            table.addCell(createCell(rs.getString("passengerName"), contentFont));
            table.addCell(createCell(rs.getString("email"), contentFont));
            table.addCell(createCell(rs.getString("phone"), contentFont));
            table.addCell(createCell(rs.getString("seat_number"), contentFont));
        }

        document.add(table);
        document.close();
        System.out.println("\nPDF generated successfully at: " + App.green + fileName + App.reset);
    }

    public static void generateReport(String flightNumber, int flightId) throws Exception {
        Document document = new Document();
        String fileName = "D://" + flightNumber + "_report.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        Paragraph title = new Paragraph("FLIGHT REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30f);
        document.add(title);

        // Get data from the report table
        String sql = "SELECT * FROM reports WHERE flight_id = " + flightId;
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) { // Write data into the PDF
            document.add(new Paragraph("Report ID: " + rs.getInt(1), contentFont));
            document.add(new Paragraph("Flight ID: " + rs.getInt(2), contentFont));
            document.add(new Paragraph("Seats Booked: " + rs.getInt(3), contentFont));
            document.add(new Paragraph("Revenue: " + rs.getDouble(4), contentFont));
            document.add(new Paragraph("Report updated on: " + rs.getString(5), contentFont));
            document.add(new Paragraph("Report generated on: " + LocalDateTime.now().format(App.dateTimeFormatter), contentFont));
        }
        document.close();
        System.out.println("\nPDF generated successfully at: " + App.green + fileName + App.reset);
    }
}

