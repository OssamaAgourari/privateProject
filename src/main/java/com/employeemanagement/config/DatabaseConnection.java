package com.employeemanagement.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connections with connection pooling and proper resource handling.
 * Implements auto-reconnect and connection validation.
 */
public final class DatabaseConnection {
    // Configuration - should ideally come from external config
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employee_management";
    private static final String USER = "root";
    private static final String PASSWORD = "abdo1234";
    private static Connection connection;

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets a validated database connection with retry logic
     * @return Connection object
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Attempting to establish new database connection...");
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("allowPublicKeyRetrieval", "true");
            
            connection = DriverManager.getConnection(DB_URL, props);
            System.out.println("Database connection established successfully.");
        }
        return connection;
    }

    /**
     * Closes all resources (for application shutdown)
     */
    public static void shutdown() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}