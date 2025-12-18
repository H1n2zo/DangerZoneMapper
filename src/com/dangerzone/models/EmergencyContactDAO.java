package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Emergency Contacts
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
        String sql = "INSERT INTO emergency_contacts (department_name, contact_type, contact_number, " +
                    "alternate_number, email, address, description, operating_hours, priority_order, " +
                    "is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, contact.getDepartmentName());
            pstmt.setString(2, contact.getContactType());
            pstmt.setString(3, contact.getContactNumber());
            pstmt.setString(4, contact.getAlternateNumber());
            pstmt.setString(5, contact.getEmail());
            pstmt.setString(6, contact.getAddress());
            pstmt.setString(7, contact.getDescription());
            pstmt.setString(8, contact.getOperatingHours());
            pstmt.setInt(9, contact.getPriorityOrder());
            pstmt.setBoolean(10, contact.isActive());
            
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
        String sql = "SELECT * FROM emergency_contacts WHERE is_active = 1 ORDER BY priority_order, department_name";
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
        String sql = "SELECT * FROM emergency_contacts ORDER BY priority_order, department_name";
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
     */
    public boolean updateContact(EmergencyContact contact) throws SQLException {
        String sql = "UPDATE emergency_contacts SET " +
                    "department_name = ?, " +
                    "contact_type = ?, " +
                    "contact_number = ?, " +
                    "alternate_number = ?, " +
                    "email = ?, " +
                    "address = ?, " +
                    "description = ?, " +
                    "operating_hours = ?, " +
                    "priority_order = ?, " +
                    "is_active = ?, " +
                    "last_updated = CURRENT_TIMESTAMP " +
                    "WHERE contact_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, contact.getDepartmentName());
            pstmt.setString(2, contact.getContactType());
            pstmt.setString(3, contact.getContactNumber());
            pstmt.setString(4, contact.getAlternateNumber());
            pstmt.setString(5, contact.getEmail());
            pstmt.setString(6, contact.getAddress());
            pstmt.setString(7, contact.getDescription());
            pstmt.setString(8, contact.getOperatingHours());
            pstmt.setInt(9, contact.getPriorityOrder());
            pstmt.setBoolean(10, contact.isActive());
            pstmt.setInt(11, contact.getContactId());
            
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
                    "(department_name LIKE ? OR contact_number LIKE ? OR description LIKE ?) " +
                    "AND is_active = 1 ORDER BY priority_order";
        List<EmergencyContact> contacts = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
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
     */
    private EmergencyContact extractContactFromResultSet(ResultSet rs) throws SQLException {
        EmergencyContact contact = new EmergencyContact();
        
        contact.setContactId(rs.getInt("contact_id"));
        contact.setDepartmentName(rs.getString("department_name"));
        contact.setContactType(rs.getString("contact_type"));
        contact.setContactNumber(rs.getString("contact_number"));
        contact.setAlternateNumber(rs.getString("alternate_number"));
        contact.setEmail(rs.getString("email"));
        contact.setAddress(rs.getString("address"));
        contact.setDescription(rs.getString("description"));
        contact.setOperatingHours(rs.getString("operating_hours"));
        contact.setPriorityOrder(rs.getInt("priority_order"));
        contact.setActive(rs.getBoolean("is_active"));
        contact.setCreatedAt(rs.getTimestamp("created_at"));
        contact.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return contact;
    }
}