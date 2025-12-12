package com.dangerzonemapper.model;

import java.time.LocalDate;

/**
 * Model class representing a hazard zone
 */
public class HazardZone {
    private int id;
    private String name;
    private String type;
    private double latitude;
    private double longitude;
    private double radius;
    private String description;
    private LocalDate dateAdded;
    
    // Constructor
    public HazardZone(int id, String name, String type, double latitude, double longitude, 
                      double radius, String description, LocalDate dateAdded) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.description = description;
        this.dateAdded = dateAdded;
    }
    
    // Constructor without ID (for new zones)
    public HazardZone(String name, String type, double latitude, double longitude, 
                      double radius, String description, LocalDate dateAdded) {
        this(0, name, type, latitude, longitude, radius, description, dateAdded);
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getRadius() { return radius; }
    public String getDescription() { return description; }
    public LocalDate getDateAdded() { return dateAdded; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setRadius(double radius) { this.radius = radius; }
    public void setDescription(String description) { this.description = description; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }
    
    @Override
    public String toString() {
        return String.format("#%d: %s - %s", id, name, type);
    }
    
    /**
     * Get detailed information as formatted string
     */
    public String getDetailedInfo() {
        return String.format("""
            ID: #%d
            Name: %s
            Type: %s
            Latitude: %.6f
            Longitude: %.6f
            Radius: %.0fm
            Description: %s
            Date Added: %s
            """,
            id, name, type, latitude, longitude, radius, description, dateAdded
        );
    }
}