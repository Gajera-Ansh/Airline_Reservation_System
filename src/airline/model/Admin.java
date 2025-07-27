package airline.model;

public class Admin {
    public int admin_id;
    public String username;
    public String password;

    public Admin(int admin_id, String username, String password) {
        this.admin_id = admin_id;
        this.username = username;
        this.password = password;
    }

    public int getAdminId() {
        return admin_id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
