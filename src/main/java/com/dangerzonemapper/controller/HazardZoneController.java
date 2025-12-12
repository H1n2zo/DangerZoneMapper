package com.dangerzonemapper.controller;

import com.dangerzonemapper.database.DatabaseManager;
import com.dangerzonemapper.model.HazardZone;
import com.dangerzonemapper.ui.MapRenderer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing hazard zone operations
 */
public class HazardZoneController {
    private final DatabaseManager dbManager;
    private final MapRenderer mapRenderer;
    private final ObservableList<HazardZone> hazardZones;
    
    public HazardZoneController(MapRenderer mapRenderer) {
        this.dbManager = DatabaseManager.getInstance();
        this.mapRenderer = mapRenderer;
        this.hazardZones = FXCollections.observableArrayList();
    }
    
    /**
     * Add a new hazard zone
     */
    public boolean addHazardZone(String name, String type, double latitude, double longitude,
                                  double radius, String description) {
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // Create new hazard zone
        HazardZone zone = new HazardZone(
            name.trim(),
            type,
            latitude,
            longitude,
            radius,
            description != null ? description.trim() : "",
            LocalDate.now()
        );
        
        // Add to database
        boolean success = dbManager.addHazardZone(zone);
        
        if (success) {
            loadHazardZones(); // Reload all zones
        }
        
        return success;
    }
    
    /**
     * Delete a hazard zone by ID
     */
    public boolean deleteHazardZone(int id) {
        boolean success = dbManager.deleteHazardZone(id);
        
        if (success) {
            loadHazardZones(); // Reload all zones
        }
        
        return success;
    }
    
    /**
     * Update an existing hazard zone
     */
    public boolean updateHazardZone(HazardZone zone) {
        boolean success = dbManager.updateHazardZone(zone);
        
        if (success) {
            loadHazardZones(); // Reload all zones
        }
        
        return success;
    }
    
    /**
     * Load all hazard zones from database and display on map
     */
    public void loadHazardZones() {
        // Clear existing zones
        hazardZones.clear();
        mapRenderer.clearHazardZones();
        
        // Load from database
        List<HazardZone> zones = dbManager.getAllHazardZones();
        
        // Add to observable list and draw on map
        for (HazardZone zone : zones) {
            hazardZones.add(zone);
            mapRenderer.drawHazardZone(zone);
        }
    }
    
    /**
     * Get a hazard zone by ID
     */
    public HazardZone getHazardZoneById(int id) {
        for (HazardZone zone : hazardZones) {
            if (zone.getId() == id) {
                return zone;
            }
        }
        return dbManager.getHazardZoneById(id);
    }
    
    /**
     * Get all hazard zones
     */
    public ObservableList<HazardZone> getHazardZones() {
        return hazardZones;
    }
    
    /**
     * Get hazard zones as display strings
     */
    public ObservableList<String> getHazardZonesAsStrings() {
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (HazardZone zone : hazardZones) {
            displayList.add(zone.toString());
        }
        return displayList;
    }
    
    /**
     * Get hazard zones by type
     */
    public List<HazardZone> getHazardZonesByType(String type) {
        return dbManager.getHazardZonesByType(type);
    }
    
    /**
     * Get total count of hazard zones
     */
    public int getHazardZoneCount() {
        return hazardZones.size();
    }
    
    /**
     * Search hazard zones by name
     */
    public List<HazardZone> searchHazardZones(String searchTerm) {
        return hazardZones.stream()
            .filter(zone -> zone.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .toList();
    }
    
    /**
     * Test database connection
     */
    public boolean testDatabaseConnection() {
        return dbManager.testConnection();
    }
}