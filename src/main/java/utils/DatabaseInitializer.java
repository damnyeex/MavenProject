package utils;

import dao.UserRepository;
import models.User;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseInitializer {

    public static void init() {
        try {
            createTablesIfNotExist();

            // Проверка админа
            User admin = UserRepository.findByLogin("admin");
            if (admin == null) {
                System.out.println("Creating default admin user...");
                User newAdmin = new User("admin", "admin123", "ADMIN", null);
                newAdmin.setId(UUID.randomUUID().toString());
                UserRepository.create(newAdmin, "admin123");
                System.out.println("Default admin created successfully!");
            } else {
                System.out.println("Admin user already exists");
                // Проверка пароля админа
                String storedHash = UserRepository.getPasswordHash("admin");
                System.out.println("Stored password hash: " + storedHash);
                System.out.println("Test 'admin123' -> " + (PasswordUtil.verify("admin123", storedHash) ? "OK" : "FAIL"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist() throws SQLException {
        String sqlTenants = """
            CREATE TABLE IF NOT EXISTS tenants (
                id UUID PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                description TEXT
            )
            """;

        String sqlUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id UUID PRIMARY KEY,
                login VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
                tenant_id UUID REFERENCES tenants(id) ON DELETE SET NULL
            )
            """;

        try (var conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlTenants);
            stmt.execute(sqlUsers);
            System.out.println("Tables created/verified successfully.");
        }
    }
}