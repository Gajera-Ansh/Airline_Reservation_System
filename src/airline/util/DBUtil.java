package airline.util;

import java.sql.*;

public class DBUtil {
    public static final String URL = "jdbc:mysql://localhost:3306/airline_db";
    public static final String USER = "root";
    public static final String PASSWORD = "yourpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
