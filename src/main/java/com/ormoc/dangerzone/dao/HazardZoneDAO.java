package com.ormoc.dangerzone.dao;

import com.ormoc.dangerzone.config.DatabaseConfig;
import com.ormoc.dangerzone.model.HazardZone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for HazardZone
 */
public class HazardZoneDAO {

    private final DatabaseConfig dbConfig;

    public HazardZoneDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Get all hazard zones
     */
    public List<HazardZone> findAll() {
        List<HazardZone> zones = new ArrayList<>();
        String sql = "SELECT * FROM hazard_zones WHERE is_active = 1 ORDER BY zone_name";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                zones.add(mapResultSetToHazardZone(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hazard zones: " + e.getMessage());
            e.printStackTrace();
        }

        return zones;
    }

    /**
     * Get hazard zone by ID
     */
    public HazardZone findById(int zoneId) {
        String sql = "SELECT * FROM hazard_zones WHERE zone_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zoneId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToHazardZone(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hazard zone: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get hazard zones by barangay
     */
    public List<HazardZone> findByBarangay(String barangay) {
        List<HazardZone> zones = new ArrayList<>();
        String sql = "SELECT * FROM hazard_zones WHERE barangay = ? AND is_active = 1";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barangay);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                zones.add(mapResultSetToHazardZone(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hazard zones by barangay: " + e.getMessage());
            e.printStackTrace();
        }

        return zones;
    }

    /**
     * Get hazard zones by type
     */
    public List<HazardZone> findByHazardType(String hazardType) {
        List<HazardZone> zones = new ArrayList<>();
        String sql = "SELECT * FROM hazard_zones WHERE hazard_type = ? AND is_active = 1";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hazardType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                zones.add(mapResultSetToHazardZone(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hazard zones by type: " + e.getMessage());
            e.printStackTrace();
        }

        return zones;
    }

    /**
     * Insert new hazard zone
     */
    public boolean insert(HazardZone zone) {
        String sql = "INSERT INTO hazard_zones (zone_name, barangay, hazard_type, " +
                    "severity_level, geojson, latitude, longitude, description, " +
                    "affected_population, mitigation_measures, risk_factors, " +
                    "date_identified, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, zone.getZoneName());
            pstmt.setString(2, zone.getBarangay());
            pstmt.setString(3, zone.getHazardType());
            pstmt.setString(4, zone.getSeverityLevel());
            pstmt.setString(5, zone.getGeojson());
            
            if (zone.getLatitude() != null) {
                pstmt.setDouble(6, zone.getLatitude());
            } else {
                pstmt.setNull(6, Types.DOUBLE);
            }
            
            if (zone.getLongitude() != null) {
                pstmt.setDouble(7, zone.getLongitude());
            } else {
                pstmt.setNull(7, Types.DOUBLE);
            }
            
            pstmt.setString(8, zone.getDescription());
            
            if (zone.getAffectedPopulation() != null) {
                pstmt.setInt(9, zone.getAffectedPopulation());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
            pstmt.setString(10, zone.getMitigationMeasures());
            pstmt.setString(11, zone.getRiskFactors());
            
            if (zone.getDateIdentified() != null) {
                pstmt.setDate(12, Date.valueOf(zone.getDateIdentified()));
            } else {
                pstmt.setDate(12, new Date(System.currentTimeMillis()));
            }
            
            pstmt.setBoolean(13, zone.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    zone.setZoneId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting hazard zone: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update existing hazard zone
     */
    public boolean update(HazardZone zone) {
        String sql = "UPDATE hazard_zones SET zone_name = ?, barangay = ?, " +
                    "hazard_type = ?, severity_level = ?, geojson = ?, " +
                    "latitude = ?, longitude = ?, description = ?, " +
                    "affected_population = ?, mitigation_measures = ?, " +
                    "risk_factors = ?, date_identified = ?, is_active = ? " +
                    "WHERE zone_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, zone.getZoneName());
            pstmt.setString(2, zone.getBarangay());
            pstmt.setString(3, zone.getHazardType());
            pstmt.setString(4, zone.getSeverityLevel());
            pstmt.setString(5, zone.getGeojson());
            
            if (zone.getLatitude() != null) {
                pstmt.setDouble(6, zone.getLatitude());
            } else {
                pstmt.setNull(6, Types.DOUBLE);
            }
            
            if (zone.getLongitude() != null) {
                pstmt.setDouble(7, zone.getLongitude());
            } else {
                pstmt.setNull(7, Types.DOUBLE);
            }
            
            pstmt.setString(8, zone.getDescription());
            
            if (zone.getAffectedPopulation() != null) {
                pstmt.setInt(9, zone.getAffectedPopulation());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
            pstmt.setString(10, zone.getMitigationMeasures());
            pstmt.setString(11, zone.getRiskFactors());
            
            if (zone.getDateIdentified() != null) {
                pstmt.setDate(12, Date.valueOf(zone.getDateIdentified()));
            } else {
                pstmt.setNull(12, Types.DATE);
            }
            
            pstmt.setBoolean(13, zone.isActive());
            pstmt.setInt(14, zone.getZoneId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating hazard zone: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete hazard zone (soft delete)
     */
    public boolean delete(int zoneId) {
        String sql = "UPDATE hazard_zones SET is_active = 0 WHERE zone_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zoneId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting hazard zone: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get distinct barangays
     */
    public List<String> getDistinctBarangays() {
        List<String> barangays = new ArrayList<>();
        String sql = "SELECT DISTINCT barangay FROM hazard_zones " +
                    "WHERE barangay IS NOT NULL AND is_active = 1 " +
                    "ORDER BY barangay";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                barangays.add(rs.getString("barangay"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching barangays: " + e.getMessage());
        }

        return barangays;
    }

    /**
     * Get distinct hazard types
     */
    public List<String> getDistinctHazardTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT hazard_type FROM hazard_zones " +
                    "WHERE hazard_type IS NOT NULL AND is_active = 1 " +
                    "ORDER BY hazard_type";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(rs.getString("hazard_type"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hazard types: " + e.getMessage());
        }

        return types;
    }

    /**
     * Map ResultSet to HazardZone object
     */
    private HazardZone mapResultSetToHazardZone(ResultSet rs) throws SQLException {
        HazardZone zone = new HazardZone();
        
        zone.setZoneId(rs.getInt("zone_id"));
        zone.setZoneName(rs.getString("zone_name"));
        zone.setBarangay(rs.getString("barangay"));
        zone.setHazardType(rs.getString("hazard_type"));
        zone.setSeverityLevel(rs.getString("severity_level"));
        zone.setGeojson(rs.getString("geojson"));
        
        double lat = rs.getDouble("latitude");
        if (!rs.wasNull()) zone.setLatitude(lat);
        
        double lon = rs.getDouble("longitude");
        if (!rs.wasNull()) zone.setLongitude(lon);
        
        zone.setDescription(rs.getString("description"));
        
        int affectedPop = rs.getInt("affected_population");
        if (!rs.wasNull()) zone.setAffectedPopulation(affectedPop);
        
        zone.setMitigationMeasures(rs.getString("mitigation_measures"));
        zone.setRiskFactors(rs.getString("risk_factors"));
        
        Date dateIdentified = rs.getDate("date_identified");
        if (dateIdentified != null) {
            zone.setDateIdentified(dateIdentified.toLocalDate());
        }
        
        zone.setLastUpdated(rs.getTimestamp("last_updated"));
        zone.setActive(rs.getBoolean("is_active"));
        
        return zone;
    }
}