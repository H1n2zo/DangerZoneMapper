/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HazardZoneDAO {
    
    private Connection connection;
    
    public HazardZoneDAO(Connection connection) {
        this.connection = connection;
    }
    
    // CREATE
    public boolean createHazardZone(HazardZone zone) throws SQLException {
        String sql = "INSERT INTO hazard_zones (zone_name, barangay, hazard_type, severity_level, " +
                    "latitude, longitude, radius_meters, description, risk_factors, affected_population, " +
                    "date_identified, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, zone.getZoneName());
            pstmt.setString(2, zone.getBarangay());
            pstmt.setString(3, zone.getHazardType());
            pstmt.setString(4, zone.getSeverityLevel());
            pstmt.setDouble(5, zone.getLatitude());
            pstmt.setDouble(6, zone.getLongitude());
            pstmt.setInt(7, zone.getRadiusMeters());
            pstmt.setString(8, zone.getDescription());
            pstmt.setString(9, zone.getRiskFactors());
            pstmt.setInt(10, zone.getAffectedPopulation());
            pstmt.setDate(11, zone.getDateIdentified());
            pstmt.setBoolean(12, zone.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        zone.setZoneId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    // READ - Get all hazard zones
    public List<HazardZone> getAllHazardZones() throws SQLException {
        String sql = "SELECT * FROM hazard_zones ORDER BY severity_level DESC, zone_name";
        List<HazardZone> zones = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                zones.add(extractHazardZoneFromResultSet(rs));
            }
        }
        return zones;
    }
    
    // READ - Get by hazard type
    public List<HazardZone> getHazardZonesByType(String hazardType) throws SQLException {
        String sql = "SELECT * FROM hazard_zones WHERE hazard_type = ? ORDER BY severity_level DESC";
        List<HazardZone> zones = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hazardType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    zones.add(extractHazardZoneFromResultSet(rs));
                }
            }
        }
        return zones;
    }
    
    // READ - Get by severity level
    public List<HazardZone> getHazardZonesBySeverity(String severityLevel) throws SQLException {
        String sql = "SELECT * FROM hazard_zones WHERE severity_level = ? ORDER BY zone_name";
        List<HazardZone> zones = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, severityLevel);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    zones.add(extractHazardZoneFromResultSet(rs));
                }
            }
        }
        return zones;
    }
    
    // READ - Get active zones only
    public List<HazardZone> getActiveHazardZones() throws SQLException {
        String sql = "SELECT * FROM hazard_zones WHERE is_active = 1 ORDER BY severity_level DESC";
        List<HazardZone> zones = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                zones.add(extractHazardZoneFromResultSet(rs));
            }
        }
        return zones;
    }
    
    // UPDATE
    public boolean updateHazardZone(HazardZone zone) throws SQLException {
        String sql = "UPDATE hazard_zones SET " +
                    "zone_name = ?, " +
                    "barangay = ?, " +
                    "hazard_type = ?, " +
                    "severity_level = ?, " +
                    "latitude = ?, " +
                    "longitude = ?, " +
                    "radius_meters = ?, " +
                    "description = ?, " +
                    "risk_factors = ?, " +
                    "affected_population = ?, " +
                    "date_identified = ?, " +
                    "is_active = ?, " +
                    "last_updated = CURRENT_TIMESTAMP " +
                    "WHERE zone_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, zone.getZoneName());
            pstmt.setString(2, zone.getBarangay());
            pstmt.setString(3, zone.getHazardType());
            pstmt.setString(4, zone.getSeverityLevel());
            pstmt.setDouble(5, zone.getLatitude());
            pstmt.setDouble(6, zone.getLongitude());
            pstmt.setInt(7, zone.getRadiusMeters());
            pstmt.setString(8, zone.getDescription());
            pstmt.setString(9, zone.getRiskFactors());
            pstmt.setInt(10, zone.getAffectedPopulation());
            pstmt.setDate(11, zone.getDateIdentified());
            pstmt.setBoolean(12, zone.isActive());
            pstmt.setInt(13, zone.getZoneId()); // WHERE clause

            int rowsAffected = pstmt.executeUpdate();

            // Debug output
            System.out.println("UPDATE Hazard Zone ID " + zone.getZoneId() + ": " + 
                             (rowsAffected > 0 ? "SUCCESS" : "FAILED - No rows affected"));

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // DELETE
    public boolean deleteHazardZone(int zoneId) throws SQLException {
        String sql = "DELETE FROM hazard_zones WHERE zone_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, zoneId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Helper method
    private HazardZone extractHazardZoneFromResultSet(ResultSet rs) throws SQLException {
        HazardZone zone = new HazardZone();
        zone.setZoneId(rs.getInt("zone_id"));
        zone.setZoneName(rs.getString("zone_name"));
        zone.setBarangay(rs.getString("barangay"));
        zone.setHazardType(rs.getString("hazard_type"));
        zone.setSeverityLevel(rs.getString("severity_level"));
        zone.setLatitude(rs.getDouble("latitude"));
        zone.setLongitude(rs.getDouble("longitude"));
        zone.setRadiusMeters(rs.getInt("radius_meters"));
        zone.setDescription(rs.getString("description"));
        zone.setRiskFactors(rs.getString("risk_factors"));
        zone.setAffectedPopulation(rs.getInt("affected_population"));
        zone.setDateIdentified(rs.getDate("date_identified"));
        zone.setLastUpdated(rs.getTimestamp("last_updated"));
        zone.setActive(rs.getBoolean("is_active"));
        
        return zone;
    }
    
    // Get hazard types for filter
    public List<String> getHazardTypes() throws SQLException {
        String sql = "SELECT DISTINCT hazard_type FROM hazard_zones ORDER BY hazard_type";
        List<String> types = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                types.add(rs.getString("hazard_type"));
            }
        }
        return types;
    }
}
