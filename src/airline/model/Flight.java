package airline.model;

import airline.App;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Flight {
    int flight_id;
    String flight_number;
    String flight_type;
    String departure;
    String destination;
    LocalDateTime departure_time;
    LocalDateTime arrival_time;
    int total_seats;
    int available_seats;
    double price;
    int admin_id;

    public Flight(int flight_id, String flight_number, String flight_type, String departure, String destination, String departure_time,
                  String arrival_time, int total_seats, int available_seats, double price, int admin_id) {
        this.flight_id = flight_id;
        this.flight_number = flight_number;
        this.flight_type = flight_type;
        this.departure = departure;
        this.destination = destination;
        this.departure_time = LocalDateTime.parse(departure_time, App.dateTimeFormatter);
        this.arrival_time = LocalDateTime.parse(arrival_time, App.dateTimeFormatter);
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.price = price;
        this.admin_id = admin_id;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDeparture_time() {
        return departure_time;
    }

    public int getTotal_seats() {
        return total_seats;
    }

    public int getFlight_id() {
        return flight_id;
    }

    public String getFlight_number() {
        return flight_number;
    }

    public String getFlight_type() {
        return flight_type;
    }

    public LocalDateTime getArrival_time() {
        return arrival_time;
    }

    public int getAvailable_seats() {
        return available_seats;
    }

    public double getPrice() {
        return price;
    }

    public int getAdmin_id() {
        return admin_id;
    }
}
