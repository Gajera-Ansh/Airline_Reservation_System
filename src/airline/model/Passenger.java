package airline.model;

public class Passenger {
    int passenger_id;
    String name;
    String email;
    String phone;

    public Passenger(int passenger_id, String name, String email, String phone) {
        this.passenger_id = passenger_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
