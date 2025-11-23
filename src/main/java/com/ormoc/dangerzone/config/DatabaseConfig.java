package com.ormoc.dangerzone.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration and connection management
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Properties properties;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;

    private DatabaseConfig() {
        loadProperties();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Unable to find application.properties");
                setDefaultProperties();
                return;
            }
            properties.load(input);
            
            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");
            dbDriver = properties.getProperty("db.driver");
            
            // Load JDBC driver
            Class.forName(dbDriver);
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        dbUrl = "jdbc:mysql://localhost:3306/dangerzone_ormoc?useSSL=false&serverTimezone=UTC";
        dbUsername = "root";
        dbPassword = "";
        dbDriver = "com.mysql.cj.jdbc.Driver";
    }

    /**
     * Get a database connection
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close connection safely
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Getters
    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getServerPort() {
        return Integer.parseInt(getProperty("server.port", "8080"));
    }

    public String getServerHost() {
        return getProperty("server.host", "127.0.0.1");
    }
}