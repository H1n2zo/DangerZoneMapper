package com.ormoc.dangerzone.model;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * HazardZone Model
 */
public class HazardZone {
    private int zoneId;
    private String zoneName;
    private String barangay;
    private String hazardType;
    private String severityLevel;
    private String geojson;
    private Double latitude;
    private Double longitude;
    private String description;
    private Integer affectedPopulation;
    private String mitigationMeasures;
    private String riskFactors;
    private LocalDate dateIdentified;
    private Timestamp lastUpdated;
    private boolean isActive;

    // Constructors
    public HazardZone() {}

    public HazardZone(String zoneName, String barangay, String hazardType) {
        this.zoneName = zoneName;
        this.barangay = barangay;
        this.hazardType = hazardType;
        this.isActive = true;
    }

    // Getters and Setters
    public int getZoneId() { return zoneId; }
    public void setZoneId(int zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getHazardType() { return hazardType; }
    public void setHazardType(String hazardType) { this.hazardType = hazardType; }

    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }

    public String getGeojson() { return geojson; }
    public void setGeojson(String geojson) { this.geojson = geojson; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAffectedPopulation() { return affectedPopulation; }
    public void setAffectedPopulation(Integer affectedPopulation) { 
        this.affectedPopulation = affectedPopulation; 
    }

    public String getMitigationMeasures() { return mitigationMeasures; }
    public void setMitigationMeasures(String mitigationMeasures) { 
        this.mitigationMeasures = mitigationMeasures; 
    }

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }

    public LocalDate getDateIdentified() { return dateIdentified; }
    public void setDateIdentified(LocalDate dateIdentified) { 
        this.dateIdentified = dateIdentified; 
    }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return zoneName + " (" + hazardType + " - " + severityLevel + ")";
    }
}