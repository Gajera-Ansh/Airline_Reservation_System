package airline.util;

import java.sql.*;

public class DBUtil {
    public static final String URL = "jdbc:mysql://localhost:3306/airline_db";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    public static Connection  con;

    static  {
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
        }
        if (con != null) {
            System.out.println("Database connection established successfully.");
        } else {
            System.out.println("Failed to establish database connection.");
        }
    }
}
