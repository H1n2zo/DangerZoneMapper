package com.ormoc.dangerzone.dao;

import com.ormoc.dangerzone.config.DatabaseConfig;
import com.ormoc.dangerzone.model.Landmark;
import com.ormoc.dangerzone.model.Incident;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Incident
 */
public class IncidentDAO {

    private final DatabaseConfig dbConfig;

    public IncidentDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public List<Incident> findAll() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents ORDER BY incident_date DESC";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                incidents.add(mapResultSetToIncident(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching incidents: " + e.getMessage());
        }

        return incidents;
    }

    public Incident findById(int incidentId) {
        String sql = "SELECT * FROM incidents WHERE incident_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incidentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToIncident(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching incident: " + e.getMessage());
        }

        return null;
    }

    public List<Incident> findByType(String incidentType) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE incident_type = ? ORDER BY incident_date DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, incidentType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                incidents.add(mapResultSetToIncident(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching incidents by type: " + e.getMessage());
        }

        return incidents;
    }

    public List<Incident> findByYear(int year) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE YEAR(incident_date) = ? ORDER BY incident_date DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                incidents.add(mapResultSetToIncident(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching incidents by year: " + e.getMessage());
        }

        return incidents;
    }

    private Incident mapResultSetToIncident(ResultSet rs) throws SQLException {
        Incident incident = new Incident();
        
        incident.setIncidentId(rs.getInt("incident_id"));
        
        int zoneId = rs.getInt("zone_id");
        if (!rs.wasNull()) incident.setZoneId(zoneId);
        
        incident.setIncidentType(rs.getString("incident_type"));
        
        Date incidentDate = rs.getDate("incident_date");
        if (incidentDate != null) {
            incident.setIncidentDate(incidentDate.toLocalDate());
        }
        
        incident.setIncidentTime(rs.getString("incident_time"));
        incident.setBarangay(rs.getString("barangay"));
        incident.setSeverity(rs.getString("severity"));
        incident.setCasualties(rs.getInt("casualties"));
        incident.setInjuries(rs.getInt("injuries"));
        incident.setMissing(rs.getInt("missing"));
        incident.setFamiliesAffected(rs.getInt("families_affected"));
        incident.setStructuresDamaged(rs.getInt("structures_damaged"));
        
        double estimatedCost = rs.getDouble("estimated_cost");
        if (!rs.wasNull()) incident.setEstimatedCost(estimatedCost);
        
        incident.setDescription(rs.getString("description"));
        incident.setResponseActions(rs.getString("response_actions"));
        incident.setGeojson(rs.getString("geojson"));
        
        double lat = rs.getDouble("latitude");
        if (!rs.wasNull()) incident.setLatitude(lat);
        
        double lon = rs.getDouble("longitude");
        if (!rs.wasNull()) incident.setLongitude(lon);
        
        incident.setCreatedAt(rs.getTimestamp("created_at"));
        incident.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return incident;
    }
}
