/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LandmarkDAO {
    
    private Connection connection;
    
    public LandmarkDAO(Connection connection) {
        this.connection = connection;
    }
    
    public boolean createLandmark(Landmark landmark) throws SQLException {
        String sql = "INSERT INTO landmarks (landmark_name, landmark_type, address, barangay, " +
                    "latitude, longitude, contact_number, capacity, description, operating_hours, " +
                    "is_evacuation_site) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, landmark.getName());
            pstmt.setString(2, landmark.getType());
            pstmt.setString(3, landmark.getAddress());
            pstmt.setString(4, landmark.getBarangay());
            pstmt.setDouble(5, landmark.getLatitude());
            pstmt.setDouble(6, landmark.getLongitude());
            pstmt.setString(7, landmark.getContactNumber());
            pstmt.setObject(8, landmark.getCapacity());
            pstmt.setString(9, landmark.getDescription());
            pstmt.setString(10, landmark.getOperatingHours());
            pstmt.setBoolean(11, landmark.isEvacuationSite());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        landmark.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    public Landmark getLandmarkById(int landmarkId) throws SQLException {
        String sql = "SELECT * FROM landmarks WHERE landmark_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, landmarkId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractLandmarkFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public List<Landmark> getAllLandmarks() throws SQLException {
        String sql = "SELECT * FROM landmarks ORDER BY landmark_name";
        List<Landmark> landmarks = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                landmarks.add(extractLandmarkFromResultSet(rs));
            }
        }
        return landmarks;
    }
    
    public List<Landmark> getLandmarksByType(String type) throws SQLException {
        String sql = "SELECT * FROM landmarks WHERE landmark_type = ? ORDER BY landmark_name";
        List<Landmark> landmarks = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    landmarks.add(extractLandmarkFromResultSet(rs));
                }
            }
        }
        return landmarks;
    }
    
    public List<Landmark> getEvacuationCenters() throws SQLException {
        String sql = "SELECT * FROM landmarks WHERE is_evacuation_site = 1 ORDER BY capacity DESC";
        List<Landmark> evacuationCenters = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                evacuationCenters.add(extractLandmarkFromResultSet(rs));
            }
        }
        return evacuationCenters;
    }
    
    public List<Landmark> searchLandmarksByName(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM landmarks WHERE landmark_name LIKE ? ORDER BY landmark_name";
        List<Landmark> landmarks = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    landmarks.add(extractLandmarkFromResultSet(rs));
                }
            }
        }
        return landmarks;
    }
    
    public List<Landmark> getLandmarksByBarangay(String barangay) throws SQLException {
        String sql = "SELECT * FROM landmarks WHERE barangay = ? ORDER BY landmark_name";
        List<Landmark> landmarks = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, barangay);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    landmarks.add(extractLandmarkFromResultSet(rs));
                }
            }
        }
        return landmarks;
    }
    
    public boolean updateLandmark(Landmark landmark) throws SQLException {
        // Make sure ALL columns match your database schema
        String sql = "UPDATE landmarks SET " +
                    "landmark_name = ?, " +
                    "landmark_type = ?, " +
                    "address = ?, " +
                    "barangay = ?, " +
                    "latitude = ?, " +
                    "longitude = ?, " +
                    "contact_number = ?, " +
                    "capacity = ?, " +
                    "description = ?, " +
                    "operating_hours = ?, " +
                    "is_evacuation_site = ?, " +
                    "last_updated = CURRENT_TIMESTAMP " +
                    "WHERE landmark_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, landmark.getName());
            pstmt.setString(2, landmark.getType());
            pstmt.setString(3, landmark.getAddress());
            pstmt.setString(4, landmark.getBarangay());
            pstmt.setDouble(5, landmark.getLatitude());
            pstmt.setDouble(6, landmark.getLongitude());
            pstmt.setString(7, landmark.getContactNumber());
            pstmt.setObject(8, landmark.getCapacity());
            pstmt.setString(9, landmark.getDescription());
            pstmt.setString(10, landmark.getOperatingHours());
            pstmt.setBoolean(11, landmark.isEvacuationSite());
            pstmt.setInt(12, landmark.getId()); // WHERE clause

            int rowsAffected = pstmt.executeUpdate();

            // Debug output
            System.out.println("UPDATE Landmark ID " + landmark.getId() + ": " + 
                             (rowsAffected > 0 ? "SUCCESS" : "FAILED - No rows affected"));

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public boolean deleteLandmark(int landmarkId) throws SQLException {
        String sql = "DELETE FROM landmarks WHERE landmark_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, landmarkId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private Landmark extractLandmarkFromResultSet(ResultSet rs) throws SQLException {
        Landmark landmark = new Landmark();
        landmark.setId(rs.getInt("landmark_id"));
        landmark.setName(rs.getString("landmark_name"));
        landmark.setType(rs.getString("landmark_type"));
        landmark.setAddress(rs.getString("address"));
        landmark.setBarangay(rs.getString("barangay"));
        landmark.setLatitude(rs.getDouble("latitude"));
        landmark.setLongitude(rs.getDouble("longitude"));
        landmark.setContactNumber(rs.getString("contact_number"));
        
        Integer capacity = rs.getObject("capacity", Integer.class);
        landmark.setCapacity(capacity);
        
        landmark.setDescription(rs.getString("description"));
        landmark.setOperatingHours(rs.getString("operating_hours"));
        landmark.setEvacuationSite(rs.getBoolean("is_evacuation_site"));
        landmark.setCreatedAt(rs.getTimestamp("created_at"));
        landmark.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return landmark;
    }
    
    public List<String> getLandmarkTypes() throws SQLException {
        String sql = "SELECT DISTINCT landmark_type FROM landmarks ORDER BY landmark_type";
        List<String> types = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                types.add(rs.getString("landmark_type"));
            }
        }
        return types;
    }
    
    public List<String> getBarangaysWithLandmarks() throws SQLException {
        String sql = "SELECT DISTINCT barangay FROM landmarks WHERE barangay IS NOT NULL ORDER BY barangay";
        List<String> barangays = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                barangays.add(rs.getString("barangay"));
            }
        }
        return barangays;
    }
}