package com.ormoc.dangerzone.model;

import java.sql.Timestamp;
import java.time.LocalDate;

public class Incident {
    private int incidentId;
    private Integer zoneId;
    private String incidentType;
    private LocalDate incidentDate;
    private String incidentTime;
    private String barangay;
    private String severity;
    private int casualties;
    private int injuries;
    private int missing;
    private int familiesAffected;
    private int structuresDamaged;
    private Double estimatedCost;
    private String description;
    private String responseActions;
    private String geojson;
    private Double latitude;
    private Double longitude;
    private Timestamp createdAt;
    private Timestamp lastUpdated;

    public Incident() {}

    public Incident(String incidentType, LocalDate incidentDate, String barangay) {
        this.incidentType = incidentType;
        this.incidentDate = incidentDate;
        this.barangay = barangay;
    }

    // Getters and Setters
    public int getIncidentId() { return incidentId; }
    public void setIncidentId(int incidentId) { this.incidentId = incidentId; }

    public Integer getZoneId() { return zoneId; }
    public void setZoneId(Integer zoneId) { this.zoneId = zoneId; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { 
        this.incidentDate = incidentDate; 
    }

    public String getIncidentTime() { return incidentTime; }
    public void setIncidentTime(String incidentTime) { this.incidentTime = incidentTime; }

    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public int getCasualties() { return casualties; }
    public void setCasualties(int casualties) { this.casualties = casualties; }

    public int getInjuries() { return injuries; }
    public void setInjuries(int injuries) { this.injuries = injuries; }

    public int getMissing() { return missing; }
    public void setMissing(int missing) { this.missing = missing; }

    public int getFamiliesAffected() { return familiesAffected; }
    public void setFamiliesAffected(int familiesAffected) { 
        this.familiesAffected = familiesAffected; 
    }

    public int getStructuresDamaged() { return structuresDamaged; }
    public void setStructuresDamaged(int structuresDamaged) { 
        this.structuresDamaged = structuresDamaged; 
    }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { 
        this.estimatedCost = estimatedCost; 
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getResponseActions() { return responseActions; }
    public void setResponseActions(String responseActions) { 
        this.responseActions = responseActions; 
    }

    public String getGeojson() { return geojson; }
    public void setGeojson(String geojson) { this.geojson = geojson; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }

    @Override
    public String toString() {
        return incidentType + " - " + incidentDate + " (" + barangay + ")";
    }
}