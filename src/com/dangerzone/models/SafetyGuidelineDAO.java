package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Data Access Object for Safety Guidelines with full CRUD operations
 */
public class SafetyGuidelineDAO {
    
    private Connection connection;
    
    public SafetyGuidelineDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * CREATE - Add new safety guideline
     */
    public boolean createGuideline(SafetyGuideline guideline) throws SQLException {
        String sql = "INSERT INTO safety_guidelines (hazard_type, guideline_title, guideline_content, " +
                    "priority_level, category, target_audience, emergency_contact, visual_aid_url, " +
                    "language, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guideline.getHazardType());
            pstmt.setString(2, guideline.getTitle());
            pstmt.setString(3, guideline.getContent());
            pstmt.setObject(4, guideline.getPriorityLevel());
            pstmt.setString(5, guideline.getCategory());
            pstmt.setString(6, guideline.getTargetAudience());
            pstmt.setString(7, guideline.getEmergencyContact());
            pstmt.setString(8, guideline.getVisualAidUrl());
            pstmt.setString(9, guideline.getLanguage());
            pstmt.setBoolean(10, guideline.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        guideline.setGuidelineId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    /**
     * READ - Get guideline by ID
     */
    public SafetyGuideline getGuidelineById(int guidelineId) throws SQLException {
        String sql = "SELECT * FROM safety_guidelines WHERE guideline_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guidelineId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractGuidelineFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * READ - Get all safety guidelines
     */
    public List<SafetyGuideline> getAllGuidelines() throws SQLException {
        String sql = "SELECT * FROM safety_guidelines ORDER BY priority_level, hazard_type, guideline_id";
        List<SafetyGuideline> guidelines = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                guidelines.add(extractGuidelineFromResultSet(rs));
            }
        }
        
        return guidelines;
    }
    
    /**
     * READ - Get all active safety guidelines
     */
    public List<SafetyGuideline> getActiveGuidelines() throws SQLException {
        String sql = "SELECT * FROM safety_guidelines WHERE is_active = 1 ORDER BY priority_level, hazard_type, guideline_id";
        List<SafetyGuideline> guidelines = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                guidelines.add(extractGuidelineFromResultSet(rs));
            }
        }
        
        return guidelines;
    }
    
    /**
     * READ - Get guidelines by hazard type
     */
    public List<SafetyGuideline> getGuidelinesByHazardType(String hazardType) throws SQLException {
        String sql = "SELECT * FROM safety_guidelines WHERE hazard_type = ? AND is_active = 1 ORDER BY priority_level, guideline_id";
        List<SafetyGuideline> guidelines = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hazardType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    guidelines.add(extractGuidelineFromResultSet(rs));
                }
            }
        }
        
        return guidelines;
    }
    
    /**
     * READ - Get guidelines by category (Prevention, During, After)
     */
    public List<SafetyGuideline> getGuidelinesByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM safety_guidelines WHERE category = ? AND is_active = 1 ORDER BY priority_level, hazard_type";
        List<SafetyGuideline> guidelines = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    guidelines.add(extractGuidelineFromResultSet(rs));
                }
            }
        }
        
        return guidelines;
    }
    
    /**
     * READ - Get high priority guidelines
     */
    public List<SafetyGuideline> getHighPriorityGuidelines() throws SQLException {
        String sql = "SELECT * FROM safety_guidelines WHERE priority_level = 1 AND is_active = 1 ORDER BY hazard_type";
        List<SafetyGuideline> guidelines = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                guidelines.add(extractGuidelineFromResultSet(rs));
            }
        }
        
        return guidelines;
    }
    
    /**
     * UPDATE - Update existing safety guideline
     */
    public boolean updateGuideline(SafetyGuideline guideline) throws SQLException {
        String sql = "UPDATE safety_guidelines SET " +
                    "hazard_type = ?, " +
                    "guideline_title = ?, " +
                    "guideline_content = ?, " +
                    "priority_level = ?, " +
                    "category = ?, " +
                    "target_audience = ?, " +
                    "emergency_contact = ?, " +
                    "visual_aid_url = ?, " +
                    "language = ?, " +
                    "is_active = ?, " +
                    "last_updated = CURRENT_TIMESTAMP " +
                    "WHERE guideline_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, guideline.getHazardType());
            pstmt.setString(2, guideline.getTitle());
            pstmt.setString(3, guideline.getContent());
            pstmt.setObject(4, guideline.getPriorityLevel());
            pstmt.setString(5, guideline.getCategory());
            pstmt.setString(6, guideline.getTargetAudience());
            pstmt.setString(7, guideline.getEmergencyContact());
            pstmt.setString(8, guideline.getVisualAidUrl());
            pstmt.setString(9, guideline.getLanguage());
            pstmt.setBoolean(10, guideline.isActive());
            pstmt.setInt(11, guideline.getGuidelineId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.println("UPDATE SafetyGuideline ID " + guideline.getGuidelineId() + ": " + 
                             (rowsAffected > 0 ? "SUCCESS" : "FAILED - No rows affected"));
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * DELETE - Delete safety guideline
     */
    public boolean deleteGuideline(int guidelineId) throws SQLException {
        String sql = "DELETE FROM safety_guidelines WHERE guideline_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guidelineId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all unique hazard types
     */
    public List<String> getHazardTypes() throws SQLException {
        String sql = "SELECT DISTINCT hazard_type FROM safety_guidelines ORDER BY hazard_type";
        List<String> types = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                types.add(rs.getString("hazard_type"));
            }
        }
        return types;
    }
    
    /**
     * Get all unique categories
     */
    public List<String> getCategories() throws SQLException {
        String sql = "SELECT DISTINCT category FROM safety_guidelines WHERE category IS NOT NULL ORDER BY category";
        List<String> categories = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }
    
    /**
     * Extract SafetyGuideline object from ResultSet
     */
    private SafetyGuideline extractGuidelineFromResultSet(ResultSet rs) throws SQLException {
        SafetyGuideline guideline = new SafetyGuideline();
        
        guideline.setGuidelineId(rs.getInt("guideline_id"));
        guideline.setHazardType(rs.getString("hazard_type"));
        guideline.setTitle(rs.getString("guideline_title"));
        guideline.setContent(rs.getString("guideline_content"));
        guideline.setPriorityLevel(rs.getObject("priority_level", Integer.class));
        guideline.setCategory(rs.getString("category"));
        guideline.setTargetAudience(rs.getString("target_audience"));
        guideline.setEmergencyContact(rs.getString("emergency_contact"));
        guideline.setVisualAidUrl(rs.getString("visual_aid_url"));
        guideline.setLanguage(rs.getString("language"));
        guideline.setActive(rs.getBoolean("is_active"));
        guideline.setCreatedBy(rs.getObject("created_by", Integer.class));
        guideline.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return guideline;
    }
}