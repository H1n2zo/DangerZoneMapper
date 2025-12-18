package com.dangerzone.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MapDAO {
    
    private Connection connection;
    
    public MapDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Get the default/active map to display
     */
    public Map getDefaultMap() throws SQLException {
        String sql = "SELECT * FROM maps WHERE is_active = 1 AND is_default = 1 ORDER BY display_order LIMIT 1";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return extractMapFromResultSet(rs);
            }
        }
        
        // If no default, get first active map
        return getFirstActiveMap();
    }
    
    /**
     * Get first active map
     */
    public Map getFirstActiveMap() throws SQLException {
        String sql = "SELECT * FROM maps WHERE is_active = 1 ORDER BY display_order, map_id LIMIT 1";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return extractMapFromResultSet(rs);
            }
        }
        return null;
    }
    
    /**
     * Get all active maps
     */
    public List<Map> getAllActiveMaps() throws SQLException {
        String sql = "SELECT * FROM maps WHERE is_active = 1 ORDER BY display_order, map_name";
        List<Map> maps = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                maps.add(extractMapFromResultSet(rs));
            }
        }
        return maps;
    }
    
    /**
     * Get all maps (including inactive)
     */
    public List<Map> getAllMaps() throws SQLException {
        String sql = "SELECT * FROM maps ORDER BY display_order, map_name";
        List<Map> maps = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                maps.add(extractMapFromResultSet(rs));
            }
        }
        return maps;
    }
    
    /**
     * Get maps by type
     */
    public List<Map> getMapsByType(String mapType) throws SQLException {
        String sql = "SELECT * FROM maps WHERE map_type = ? AND is_active = 1 ORDER BY display_order";
        List<Map> maps = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mapType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    maps.add(extractMapFromResultSet(rs));
                }
            }
        }
        return maps;
    }
    
    /**
     * Get map by ID
     */
    public Map getMapById(int mapId) throws SQLException {
        String sql = "SELECT * FROM maps WHERE map_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, mapId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMapFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new map
     */
    public boolean createMap(Map map) throws SQLException {
        String sql = "INSERT INTO maps (map_name, map_type, map_description, file_path, " +
                    "file_size_kb, image_width, image_height, coverage_area, map_scale, " +
                    "creation_date, source, is_active, is_default, display_order, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, map.getMapName());
            pstmt.setString(2, map.getMapType());
            pstmt.setString(3, map.getMapDescription());
            pstmt.setString(4, map.getFilePath());
            pstmt.setObject(5, map.getFileSizeKb());
            pstmt.setObject(6, map.getImageWidth());
            pstmt.setObject(7, map.getImageHeight());
            pstmt.setString(8, map.getCoverageArea());
            pstmt.setString(9, map.getMapScale());
            pstmt.setDate(10, map.getCreationDate());
            pstmt.setString(11, map.getSource());
            pstmt.setBoolean(12, map.isActive());
            pstmt.setBoolean(13, map.isDefault());
            pstmt.setInt(14, map.getDisplayOrder());
            pstmt.setObject(15, map.getCreatedBy());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        map.setMapId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    /**
     * Update map
     */
    public boolean updateMap(Map map) throws SQLException {
        String sql = "UPDATE maps SET " +
                    "map_name = ?, " +
                    "map_type = ?, " +
                    "map_description = ?, " +
                    "file_path = ?, " +
                    "file_size_kb = ?, " +
                    "image_width = ?, " +
                    "image_height = ?, " +
                    "coverage_area = ?, " +
                    "map_scale = ?, " +
                    "creation_date = ?, " +
                    "source = ?, " +
                    "is_active = ?, " +
                    "is_default = ?, " +
                    "display_order = ? " +
                    "WHERE map_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, map.getMapName());
            pstmt.setString(2, map.getMapType());
            pstmt.setString(3, map.getMapDescription());
            pstmt.setString(4, map.getFilePath());
            pstmt.setObject(5, map.getFileSizeKb());
            pstmt.setObject(6, map.getImageWidth());
            pstmt.setObject(7, map.getImageHeight());
            pstmt.setString(8, map.getCoverageArea());
            pstmt.setString(9, map.getMapScale());
            pstmt.setDate(10, map.getCreationDate());
            pstmt.setString(11, map.getSource());
            pstmt.setBoolean(12, map.isActive());
            pstmt.setBoolean(13, map.isDefault());
            pstmt.setInt(14, map.getDisplayOrder());
            pstmt.setInt(15, map.getMapId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete map
     */
    public boolean deleteMap(int mapId) throws SQLException {
        String sql = "DELETE FROM maps WHERE map_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, mapId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Set a map as default (and unset others)
     */
    public boolean setDefaultMap(int mapId) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            // Unset all defaults
            String unsetSql = "UPDATE maps SET is_default = 0";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(unsetSql);
            }
            
            // Set new default
            String setSql = "UPDATE maps SET is_default = 1 WHERE map_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(setSql)) {
                pstmt.setInt(1, mapId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Get all map types
     */
    public List<String> getMapTypes() throws SQLException {
        String sql = "SELECT DISTINCT map_type FROM maps ORDER BY map_type";
        List<String> types = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                types.add(rs.getString("map_type"));
            }
        }
        return types;
    }
    
    /**
     * Extract map from result set
     */
    private Map extractMapFromResultSet(ResultSet rs) throws SQLException {
        Map map = new Map();
        map.setMapId(rs.getInt("map_id"));
        map.setMapName(rs.getString("map_name"));
        map.setMapType(rs.getString("map_type"));
        map.setMapDescription(rs.getString("map_description"));
        map.setFilePath(rs.getString("file_path"));
        map.setFileSizeKb(rs.getObject("file_size_kb", Integer.class));
        map.setImageWidth(rs.getObject("image_width", Integer.class));
        map.setImageHeight(rs.getObject("image_height", Integer.class));
        map.setCoverageArea(rs.getString("coverage_area"));
        map.setMapScale(rs.getString("map_scale"));
        map.setCreationDate(rs.getDate("creation_date"));
        map.setSource(rs.getString("source"));
        map.setActive(rs.getBoolean("is_active"));
        map.setDefault(rs.getBoolean("is_default"));
        map.setDisplayOrder(rs.getInt("display_order"));
        map.setCreatedBy(rs.getObject("created_by", Integer.class));
        map.setCreatedAt(rs.getTimestamp("created_at"));
        map.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return map;
    }
}