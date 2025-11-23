package com.ormoc.dangerzone.dao;

import com.ormoc.dangerzone.config.DatabaseConfig;
import com.ormoc.dangerzone.model.Landmark;
import com.ormoc.dangerzone.model.Incident;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Landmark
 */
public class LandmarkDAO {

    private final DatabaseConfig dbConfig;

    public LandmarkDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public List<Landmark> findAll() {
        List<Landmark> landmarks = new ArrayList<>();
        String sql = "SELECT * FROM landmarks WHERE is_active = 1 ORDER BY landmark_name";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                landmarks.add(mapResultSetToLandmark(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching landmarks: " + e.getMessage());
        }

        return landmarks;
    }

    public Landmark findById(int landmarkId) {
        String sql = "SELECT * FROM landmarks WHERE landmark_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, landmarkId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLandmark(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching landmark: " + e.getMessage());
        }

        return null;
    }

    public List<Landmark> findEvacuationSites() {
        List<Landmark> landmarks = new ArrayList<>();
        String sql = "SELECT * FROM landmarks WHERE is_evacuation_site = 1 AND is_active = 1";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                landmarks.add(mapResultSetToLandmark(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching evacuation sites: " + e.getMessage());
        }

        return landmarks;
    }

    public boolean insert(Landmark landmark) {
        String sql = "INSERT INTO landmarks (landmark_name, landmark_type, address, " +
                    "barangay, description, operating_hours, facilities, " +
                    "accessibility_notes, is_evacuation_site, is_active, capacity, " +
                    "geojson, latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, landmark.getLandmarkName());
            pstmt.setString(2, landmark.getLandmarkType());
            pstmt.setString(3, landmark.getAddress());
            pstmt.setString(4, landmark.getBarangay());
            pstmt.setString(5, landmark.getDescription());
            pstmt.setString(6, landmark.getOperatingHours());
            pstmt.setString(7, landmark.getFacilities());
            pstmt.setString(8, landmark.getAccessibilityNotes());
            pstmt.setBoolean(9, landmark.isEvacuationSite());
            pstmt.setBoolean(10, landmark.isActive());
            
            if (landmark.getCapacity() != null) {
                pstmt.setInt(11, landmark.getCapacity());
            } else {
                pstmt.setNull(11, Types.INTEGER);
            }
            
            pstmt.setString(12, landmark.getGeojson());
            
            if (landmark.getLatitude() != null) {
                pstmt.setDouble(13, landmark.getLatitude());
            } else {
                pstmt.setNull(13, Types.DOUBLE);
            }
            
            if (landmark.getLongitude() != null) {
                pstmt.setDouble(14, landmark.getLongitude());
            } else {
                pstmt.setNull(14, Types.DOUBLE);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    landmark.setLandmarkId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting landmark: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Landmark landmark) {
        String sql = "UPDATE landmarks SET landmark_name = ?, landmark_type = ?, " +
                    "address = ?, barangay = ?, description = ?, operating_hours = ?, " +
                    "facilities = ?, accessibility_notes = ?, is_evacuation_site = ?, " +
                    "is_active = ?, capacity = ?, geojson = ?, latitude = ?, " +
                    "longitude = ? WHERE landmark_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, landmark.getLandmarkName());
            pstmt.setString(2, landmark.getLandmarkType());
            pstmt.setString(3, landmark.getAddress());
            pstmt.setString(4, landmark.getBarangay());
            pstmt.setString(5, landmark.getDescription());
            pstmt.setString(6, landmark.getOperatingHours());
            pstmt.setString(7, landmark.getFacilities());
            pstmt.setString(8, landmark.getAccessibilityNotes());
            pstmt.setBoolean(9, landmark.isEvacuationSite());
            pstmt.setBoolean(10, landmark.isActive());
            
            if (landmark.getCapacity() != null) {
                pstmt.setInt(11, landmark.getCapacity());
            } else {
                pstmt.setNull(11, Types.INTEGER);
            }
            
            pstmt.setString(12, landmark.getGeojson());
            
            if (landmark.getLatitude() != null) {
                pstmt.setDouble(13, landmark.getLatitude());
            } else {
                pstmt.setNull(13, Types.DOUBLE);
            }
            
            if (landmark.getLongitude() != null) {
                pstmt.setDouble(14, landmark.getLongitude());
            } else {
                pstmt.setNull(14, Types.DOUBLE);
            }
            
            pstmt.setInt(15, landmark.getLandmarkId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating landmark: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int landmarkId) {
        String sql = "UPDATE landmarks SET is_active = 0 WHERE landmark_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, landmarkId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting landmark: " + e.getMessage());
        }

        return false;
    }

    private Landmark mapResultSetToLandmark(ResultSet rs) throws SQLException {
        Landmark landmark = new Landmark();
        
        landmark.setLandmarkId(rs.getInt("landmark_id"));
        landmark.setLandmarkName(rs.getString("landmark_name"));
        landmark.setLandmarkType(rs.getString("landmark_type"));
        landmark.setAddress(rs.getString("address"));
        landmark.setBarangay(rs.getString("barangay"));
        landmark.setDescription(rs.getString("description"));
        landmark.setOperatingHours(rs.getString("operating_hours"));
        landmark.setFacilities(rs.getString("facilities"));
        landmark.setAccessibilityNotes(rs.getString("accessibility_notes"));
        landmark.setEvacuationSite(rs.getBoolean("is_evacuation_site"));
        landmark.setActive(rs.getBoolean("is_active"));
        
        int capacity = rs.getInt("capacity");
        if (!rs.wasNull()) landmark.setCapacity(capacity);
        
        landmark.setGeojson(rs.getString("geojson"));
        
        double lat = rs.getDouble("latitude");
        if (!rs.wasNull()) landmark.setLatitude(lat);
        
        double lon = rs.getDouble("longitude");
        if (!rs.wasNull()) landmark.setLongitude(lon);
        
        landmark.setCreatedAt(rs.getTimestamp("created_at"));
        landmark.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return landmark;
    }
}

