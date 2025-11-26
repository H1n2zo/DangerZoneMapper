/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.models;

import java.sql.Date;
import java.sql.Timestamp;
import java.math.BigDecimal;

public class Incident {
    
    private int incidentId;
    private Integer zoneId;
    private String incidentType;
    private Date incidentDate;
    private String barangay;
    private String severity;
    private int casualties;
    private int injuries;
    private int familiesAffected;
    private int structuresDamaged;
    private BigDecimal estimatedCost;
    private String description;
    private String responseActions;
    private Double latitude;
    private Double longitude;
    private Timestamp createdAt;
    
    public Incident() {}
    
    // Getters and Setters
    public int getIncidentId() {
        return incidentId;
    }
    
    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }
    
    public Integer getZoneId() {
        return zoneId;
    }
    
    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }
    
    public String getIncidentType() {
        return incidentType;
    }
    
    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }
    
    public Date getIncidentDate() {
        return incidentDate;
    }
    
    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }
    
    public String getBarangay() {
        return barangay;
    }
    
    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public int getCasualties() {
        return casualties;
    }
    
    public void setCasualties(int casualties) {
        this.casualties = casualties;
    }
    
    public int getInjuries() {
        return injuries;
    }
    
    public void setInjuries(int injuries) {
        this.injuries = injuries;
    }
    
    public int getFamiliesAffected() {
        return familiesAffected;
    }
    
    public void setFamiliesAffected(int familiesAffected) {
        this.familiesAffected = familiesAffected;
    }
    
    public int getStructuresDamaged() {
        return structuresDamaged;
    }
    
    public void setStructuresDamaged(int structuresDamaged) {
        this.structuresDamaged = structuresDamaged;
    }
    
    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }
    
    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getResponseActions() {
        return responseActions;
    }
    
    public void setResponseActions(String responseActions) {
        this.responseActions = responseActions;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return incidentType + " - " + barangay + " (" + incidentDate + ")";
    }
    
    public String getFormattedCost() {
        if (estimatedCost != null) {
            return String.format("₱%,.2f", estimatedCost);
        }
        return "N/A";
    }
}
