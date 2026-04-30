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
            addMissingColumns();

            User admin = UserRepository.findByLogin("admin");
            if (admin == null) {
                System.out.println("Creating default admin user...");
                User newAdmin = new User("admin", "admin123", "ADMIN", null, "Редькин", "Сергей", "Алексеевич");
                newAdmin.setId(UUID.randomUUID().toString());
                UserRepository.create(newAdmin, "admin123");
                System.out.println("Default admin created successfully!");
            } else {
                System.out.println("Admin user already exists");
                String storedHash = UserRepository.getPasswordHash("admin");
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
                firstname VARCHAR(50) NOT NULL,
                lastname VARCHAR(50) NOT NULL,
                middlename VARCHAR(50) NOT NULL,
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

    // НОВЫЙ МЕТОД: добавляет недостающие колонки при обновлении схемы
    private static void addMissingColumns() throws SQLException {
        String[] alterStatements = {
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS firstname VARCHAR(50)",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS lastname VARCHAR(50)",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS middlename VARCHAR(50)"
        };

        try (var conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : alterStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    if (!e.getMessage().contains("already exists")) {
                        System.err.println("Warning: " + e.getMessage());
                    }
                }
            }
            System.out.println("Missing columns added successfully.");
        }
    }
}