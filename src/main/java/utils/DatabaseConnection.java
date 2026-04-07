package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    static {
        try {
            // Драйвер H2
            Class.forName("org.h2.Driver");
            System.out.println("H2 Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        // Файловая БД в папке ./data
        // Режим PostgreSQL
        String url = "jdbc:h2:file:./data/is_lab;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_UPPER=false";
        String user = "sa";
        String password = "";

        System.out.println("=== Database Connection Details ===");
        System.out.println("URL: " + url);
        System.out.println("User: " + user);
        System.out.println("===================================");

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");
            return conn;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            throw e;
        }
    }
}