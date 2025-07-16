package airline.model;

public class Passenger {
    public int passenger_id;
    public String name;
    public String email;
    public String phone;
    public String pass;

    public Passenger(int passenger_id, String name, String email, String phone, String pass) {
        this.passenger_id = passenger_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPass() {
        return pass;
    }
}
