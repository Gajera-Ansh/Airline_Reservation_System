package airline.model;

import java.time.LocalDateTime;

public class Reservation {
    int reservation_id;
    int flight_id;
    int passenger_id;
    String seat_number;
    LocalDateTime reservation_date;
    String status;

    public Reservation(int reservation_id, int flight_id, int passenger_id, String seat_number, LocalDateTime reservation_date, String status) {
        this.reservation_id = reservation_id;
        this.flight_id = flight_id;
        this.passenger_id = passenger_id;
        this.seat_number = seat_number;
        this.reservation_date = reservation_date;
        this.status = status;
    }

}
