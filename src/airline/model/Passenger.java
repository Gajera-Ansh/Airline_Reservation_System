package airline.model;

public class Passenger {
    int passenger_id;
    String name;
    String email;
    String phone;
    String pass;

    public Passenger(int passenger_id, String name, String email, String phone, String pass) {
        this.passenger_id = passenger_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pass = pass;
    }
}
