/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.models;

import java.sql.Date;
import java.sql.Timestamp;

public class HazardZone {
    
    private int zoneId;
    private String zoneName;
    private String barangay;
    private String hazardType;
    private String severityLevel;
    private double latitude;
    private double longitude;
    private int radiusMeters;
    private String description;
    private String riskFactors;
    private int affectedPopulation;
    private Date dateIdentified;
    private Timestamp lastUpdated;
    private boolean isActive;
    
    public HazardZone() {}
    
    // Getters and Setters
    public int getZoneId() {
        return zoneId;
    }
    
    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }
    
    public String getZoneName() {
        return zoneName;
    }
    
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }
    
    public String getBarangay() {
        return barangay;
    }
    
    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }
    
    public String getHazardType() {
        return hazardType;
    }
    
    public void setHazardType(String hazardType) {
        this.hazardType = hazardType;
    }
    
    public String getSeverityLevel() {
        return severityLevel;
    }
    
    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public int getRadiusMeters() {
        return radiusMeters;
    }
    
    public void setRadiusMeters(int radiusMeters) {
        this.radiusMeters = radiusMeters;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public int getAffectedPopulation() {
        return affectedPopulation;
    }
    
    public void setAffectedPopulation(int affectedPopulation) {
        this.affectedPopulation = affectedPopulation;
    }
    
    public Date getDateIdentified() {
        return dateIdentified;
    }
    
    public void setDateIdentified(Date dateIdentified) {
        this.dateIdentified = dateIdentified;
    }
    
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return zoneName + " (" + hazardType + " - " + severityLevel + ")";
    }
    
    public String getCoordinates() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
}