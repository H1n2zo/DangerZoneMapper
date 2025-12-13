package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Safety Guidelines
 */
public class SafetyGuidelineDAO {
    
    private Connection connection;
    
    public SafetyGuidelineDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Get all active safety guidelines
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
     * Get guidelines by hazard type
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
     * Get guidelines by category (Prevention, During, After)
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
     * Get high priority guidelines
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
