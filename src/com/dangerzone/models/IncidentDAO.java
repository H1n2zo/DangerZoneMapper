/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {
    
    private Connection connection;
    
    public IncidentDAO(Connection connection) {
        this.connection = connection;
    }
    
    public boolean createIncident(Incident incident) throws SQLException {
        String sql = "INSERT INTO incidents (zone_id, incident_type, incident_date, barangay, " +
                    "severity, casualties, injuries, families_affected, structures_damaged, " +
                    "estimated_cost, description, response_actions, latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setObject(1, incident.getZoneId());
            pstmt.setString(2, incident.getIncidentType());
            pstmt.setDate(3, incident.getIncidentDate());
            pstmt.setString(4, incident.getBarangay());
            pstmt.setString(5, incident.getSeverity());
            pstmt.setInt(6, incident.getCasualties());
            pstmt.setInt(7, incident.getInjuries());
            pstmt.setInt(8, incident.getFamiliesAffected());
            pstmt.setInt(9, incident.getStructuresDamaged());
            pstmt.setBigDecimal(10, incident.getEstimatedCost());
            pstmt.setString(11, incident.getDescription());
            pstmt.setString(12, incident.getResponseActions());
            pstmt.setObject(13, incident.getLatitude());
            pstmt.setObject(14, incident.getLongitude());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        incident.setIncidentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    public List<Incident> getAllIncidents() throws SQLException {
        String sql = "SELECT * FROM incidents ORDER BY incident_date DESC";
        List<Incident> incidents = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                incidents.add(extractIncidentFromResultSet(rs));
            }
        }
        return incidents;
    }
    
    public List<Incident> getIncidentsByYear(int year) throws SQLException {
        String sql = "SELECT * FROM incidents WHERE YEAR(incident_date) = ? ORDER BY incident_date DESC";
        List<Incident> incidents = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    incidents.add(extractIncidentFromResultSet(rs));
                }
            }
        }
        return incidents;
    }
    
    public List<Incident> getIncidentsByType(String type) throws SQLException {
        String sql = "SELECT * FROM incidents WHERE incident_type = ? ORDER BY incident_date DESC";
        List<Incident> incidents = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    incidents.add(extractIncidentFromResultSet(rs));
                }
            }
        }
        return incidents;
    }
    
    public boolean updateIncident(Incident incident) throws SQLException {
        String sql = "UPDATE incidents SET " +
                    "zone_id = ?, " +
                    "incident_type = ?, " +
                    "incident_date = ?, " +
                    "barangay = ?, " +
                    "severity = ?, " +
                    "casualties = ?, " +
                    "injuries = ?, " +
                    "families_affected = ?, " +
                    "structures_damaged = ?, " +
                    "estimated_cost = ?, " +
                    "description = ?, " +
                    "response_actions = ?, " +
                    "latitude = ?, " +
                    "longitude = ? " +
                    "WHERE incident_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, incident.getZoneId());
            pstmt.setString(2, incident.getIncidentType());
            pstmt.setDate(3, incident.getIncidentDate());
            pstmt.setString(4, incident.getBarangay());
            pstmt.setString(5, incident.getSeverity());
            pstmt.setInt(6, incident.getCasualties());
            pstmt.setInt(7, incident.getInjuries());
            pstmt.setInt(8, incident.getFamiliesAffected());
            pstmt.setInt(9, incident.getStructuresDamaged());
            pstmt.setBigDecimal(10, incident.getEstimatedCost());
            pstmt.setString(11, incident.getDescription());
            pstmt.setString(12, incident.getResponseActions());
            pstmt.setObject(13, incident.getLatitude());
            pstmt.setObject(14, incident.getLongitude());
            pstmt.setInt(15, incident.getIncidentId()); // WHERE clause

            int rowsAffected = pstmt.executeUpdate();

            // Debug output
            System.out.println("UPDATE Incident ID " + incident.getIncidentId() + ": " + 
                             (rowsAffected > 0 ? "SUCCESS" : "FAILED - No rows affected"));

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public boolean deleteIncident(int incidentId) throws SQLException {
        String sql = "DELETE FROM incidents WHERE incident_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, incidentId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
private Incident extractIncidentFromResultSet(ResultSet rs) throws SQLException { 
    Incident incident = new Incident(); incident.setIncidentId(rs.getInt("incident_id")); 
    incident.setZoneId((Integer) rs.getObject("zone_id")); 
    incident.setIncidentType(rs.getString("incident_type")); 
    incident.setIncidentDate(rs.getDate("incident_date")); 
    incident.setBarangay(rs.getString("barangay")); 
    incident.setSeverity(rs.getString("severity")); 
    incident.setCasualties(rs.getInt("casualties")); 
    incident.setInjuries(rs.getInt("injuries")); 
    incident.setFamiliesAffected(rs.getInt("families_affected")); 
    incident.setStructuresDamaged(rs.getInt("structures_damaged")); 
    incident.setEstimatedCost(rs.getBigDecimal("estimated_cost")); 
    incident.setDescription(rs.getString("description")); 
    incident.setResponseActions(rs.getString("response_actions")); 
    incident.setLatitude((Double) rs.getObject("latitude")); 
    incident.setLongitude((Double) rs.getObject("longitude")); 
    incident.setCreatedAt(rs.getTimestamp("created_at")); 
    
    return incident; }

}
