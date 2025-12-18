package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Emergency Contacts
 * FIXED VERSION - Works with actual database schema
 */
public class EmergencyContactDAO {
    
    private Connection connection;
    
    public EmergencyContactDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * CREATE - Add new emergency contact
     */
    public boolean createContact(EmergencyContact contact) throws SQLException {
        String sql = "INSERT INTO emergency_contacts (contact_name, organization, contact_type, " +
                    "phone_number, hotline, email, address, barangay, availability, priority_order, " +
                    "is_active, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, contact.getDepartmentName());
            pstmt.setString(2, contact.getDepartmentName());
            pstmt.setString(3, contact.getContactType());
            pstmt.setString(4, contact.getContactNumber());
            pstmt.setString(5, contact.getAlternateNumber());
            pstmt.setString(6, contact.getEmail());
            pstmt.setString(7, contact.getAddress());
            pstmt.setString(8, null);
            pstmt.setString(9, contact.getOperatingHours());
            pstmt.setInt(10, contact.getPriorityOrder());
            pstmt.setBoolean(11, contact.isActive());
            pstmt.setString(12, contact.getDescription());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contact.setContactId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    /**
     * READ - Get all active emergency contacts
     */
    public List<EmergencyContact> getAllActiveContacts() throws SQLException {
        String sql = "SELECT * FROM emergency_contacts WHERE is_active = 1 ORDER BY priority_order, contact_name";
        List<EmergencyContact> contacts = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contacts.add(extractContactFromResultSet(rs));
            }
        }
        
        return contacts;
    }
    
    /**
     * READ - Get all contacts (including inactive)
     */
    public List<EmergencyContact> getAllContacts() throws SQLException {
        String sql = "SELECT * FROM emergency_contacts ORDER BY priority_order, contact_name";
        List<EmergencyContact> contacts = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contacts.add(extractContactFromResultSet(rs));
            }
        }
        
        return contacts;
    }
    
    /**
     * READ - Get contacts by type
     */
    public List<EmergencyContact> getContactsByType(String type) throws SQLException {
        String sql = "SELECT * FROM emergency_contacts WHERE contact_type = ? AND is_active = 1 ORDER BY priority_order";
        List<EmergencyContact> contacts = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(extractContactFromResultSet(rs));
                }
            }
        }
        
        return contacts;
    }
    
    /**
     * READ - Get high priority contacts (for emergency card)
     */
    public List<EmergencyContact> getHighPriorityContacts(int limit) throws SQLException {
        String sql = "SELECT * FROM emergency_contacts WHERE is_active = 1 ORDER BY priority_order LIMIT ?";
        List<EmergencyContact> contacts = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(extractContactFromResultSet(rs));
                }
            }
        }
        
        return contacts;
    }
    
    /**
     * READ - Get contact by ID
     */
    public EmergencyContact getContactById(int contactId) throws SQLException {
        String sql = "SELECT * FROM emergency_contacts WHERE contact_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractContactFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * UPDATE - Update existing contact
     * FIXED: Uses correct column names from database schema
     */
    public boolean updateContact(EmergencyContact contact) throws SQLException {
        String sql = "UPDATE emergency_contacts SET " +
                    "contact_name = ?, " +
                    "organization = ?, " +
                    "contact_type = ?, " +
                    "phone_number = ?, " +
                    "hotline = ?, " +
                    "email = ?, " +
                    "address = ?, " +
                    "barangay = ?, " +
                    "availability = ?, " +
                    "priority_order = ?, " +
                    "is_active = ?, " +
                    "notes = ?, " +
                    "last_updated = CURRENT_TIMESTAMP " +
                    "WHERE contact_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, contact.getDepartmentName());
            pstmt.setString(2, contact.getDepartmentName()); // organization
            pstmt.setString(3, contact.getContactType());
            pstmt.setString(4, contact.getContactNumber()); // phone_number
            pstmt.setString(5, contact.getAlternateNumber()); // hotline
            pstmt.setString(6, contact.getEmail());
            pstmt.setString(7, contact.getAddress());
            pstmt.setString(8, null); // barangay (not used)
            pstmt.setString(9, contact.getOperatingHours()); // availability
            pstmt.setInt(10, contact.getPriorityOrder()); // priority_order
            pstmt.setBoolean(11, contact.isActive());
            pstmt.setString(12, contact.getDescription()); // notes
            pstmt.setInt(13, contact.getContactId()); // WHERE clause
            
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.println("UPDATE EmergencyContact ID " + contact.getContactId() + ": " + 
                             (rowsAffected > 0 ? "SUCCESS" : "FAILED - No rows affected"));
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * DELETE - Delete contact
     */
    public boolean deleteContact(int contactId) throws SQLException {
        String sql = "DELETE FROM emergency_contacts WHERE contact_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all contact types
     */
    public List<String> getContactTypes() throws SQLException {
        String sql = "SELECT DISTINCT contact_type FROM emergency_contacts ORDER BY contact_type";
        List<String> types = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                types.add(rs.getString("contact_type"));
            }
        }
        return types;
    }
    
    /**
     * Search contacts by name or number
     */
    public List<EmergencyContact> searchContacts(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM emergency_contacts WHERE " +
                    "(contact_name LIKE ? OR phone_number LIKE ? OR hotline LIKE ? OR notes LIKE ?) " +
                    "AND is_active = 1 ORDER BY priority_order";
        List<EmergencyContact> contacts = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(extractContactFromResultSet(rs));
                }
            }
        }
        
        return contacts;
    }
    
    /**
     * Extract EmergencyContact from ResultSet
     * Maps database columns to model fields correctly
     */
    private EmergencyContact extractContactFromResultSet(ResultSet rs) throws SQLException {
        EmergencyContact contact = new EmergencyContact();
        
        contact.setContactId(rs.getInt("contact_id"));
        contact.setDepartmentName(rs.getString("contact_name"));
        contact.setContactType(rs.getString("contact_type"));
        contact.setContactNumber(rs.getString("phone_number"));
        contact.setAlternateNumber(rs.getString("hotline"));
        contact.setEmail(rs.getString("email"));
        contact.setAddress(rs.getString("address"));
        contact.setDescription(rs.getString("notes"));
        contact.setOperatingHours(rs.getString("availability"));
        contact.setPriorityOrder(rs.getInt("priority_order"));
        contact.setActive(rs.getBoolean("is_active"));
        contact.setCreatedAt(rs.getTimestamp("created_at"));
        contact.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return contact;
    }
}