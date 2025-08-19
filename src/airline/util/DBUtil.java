package airline.util;

import java.sql.*;

import airline.App;

public class DBUtil {
    public static final String URL = "jdbc:mysql://localhost:3306/airline_db";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    public static Connection con;

    static {
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException _) {
        }
        if (con != null) {
            System.out.println(App.green + "Database connection established successfully." + App.reset);
        } else {
            System.out.println(App.red + "Failed to establish database connection." + App.reset);
        }
    }
}
