package airline.model;

import java.time.LocalDateTime;

public class Flight {
    int flight_id;
    String flight_number;
    String departure;
    String destination;
    LocalDateTime departure_time;
    LocalDateTime arrival_time;
    int total_seats;
    int available_seats;
    double price;
    int admin_id;

    public Flight(int flight_id, String flight_number, String departure, String destination, LocalDateTime departure_time,
                  LocalDateTime arrival_time, int total_seats, int available_seats, double price, int admin_id) {
        this.flight_id = flight_id;
        this.flight_number = flight_number;
        this.departure = departure;
        this.destination = destination;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.price = price;
        this.admin_id = admin_id;
    }
}
