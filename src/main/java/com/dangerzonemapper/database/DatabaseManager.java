package com.dangerzonemapper.database;

import com.dangerzonemapper.model.HazardZone;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/danger_zone_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    private static DatabaseManager instance;
    
    private DatabaseManager() {}
    
    /**
     * Singleton pattern to ensure single database connection manager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return conn != null;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get connection to database
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Add a new hazard zone to database
     */
    public boolean addHazardZone(HazardZone zone) {
        String sql = "INSERT INTO hazard_zones (name, type, latitude, longitude, radius, description, date_added) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, zone.getName());
            pstmt.setString(2, zone.getType());
            pstmt.setDouble(3, zone.getLatitude());
            pstmt.setDouble(4, zone.getLongitude());
            pstmt.setDouble(5, zone.getRadius());
            pstmt.setString(6, zone.getDescription());
            pstmt.setDate(7, Date.valueOf(zone.getDateAdded()));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding hazard zone: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all hazard zones from database
     */
    public List<HazardZone> getAllHazardZones() {
        List<HazardZone> zones = new ArrayList<>();
        String sql = "SELECT * FROM hazard_zones ORDER BY date_added DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                HazardZone zone = new HazardZone(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getDouble("radius"),
                    rs.getString("description"),
                    rs.getDate("date_added").toLocalDate()
                );
                zones.add(zone);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading hazard zones: " + e.getMessage());
        }
        
        return zones;
    }
    
    /**
     * Get a specific hazard zone by ID
     */
    public HazardZone getHazardZoneById(int id) {
        String sql = "SELECT * FROM hazard_zones WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new HazardZone(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getDouble("radius"),
                    rs.getString("description"),
                    rs.getDate("date_added").toLocalDate()
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting hazard zone: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update an existing hazard zone
     */
    public boolean updateHazardZone(HazardZone zone) {
        String sql = "UPDATE hazard_zones SET name=?, type=?, latitude=?, longitude=?, " +
                     "radius=?, description=? WHERE id=?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, zone.getName());
            pstmt.setString(2, zone.getType());
            pstmt.setDouble(3, zone.getLatitude());
            pstmt.setDouble(4, zone.getLongitude());
            pstmt.setDouble(5, zone.getRadius());
            pstmt.setString(6, zone.getDescription());
            pstmt.setInt(7, zone.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating hazard zone: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a hazard zone by ID
     */
    public boolean deleteHazardZone(int id) {
        String sql = "DELETE FROM hazard_zones WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting hazard zone: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get hazard zones by type
     */
    public List<HazardZone> getHazardZonesByType(String type) {
        List<HazardZone> zones = new ArrayList<>();
        String sql = "SELECT * FROM hazard_zones WHERE type = ? ORDER BY date_added DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                HazardZone zone = new HazardZone(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getDouble("radius"),
                    rs.getString("description"),
                    rs.getDate("date_added").toLocalDate()
                );
                zones.add(zone);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading hazard zones by type: " + e.getMessage());
        }
        
        return zones;
    }
    
    /**
     * Get total count of hazard zones
     */
    public int getHazardZoneCount() {
        String sql = "SELECT COUNT(*) FROM hazard_zones";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting hazard zone count: " + e.getMessage());
        }
        
        return 0;
    }
}