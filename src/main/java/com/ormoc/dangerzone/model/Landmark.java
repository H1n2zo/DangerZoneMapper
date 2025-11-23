package com.ormoc.dangerzone.model;

import java.sql.Timestamp;

public class Landmark {
    private int landmarkId;
    private String landmarkName;
    private String landmarkType;
    private String address;
    private String barangay;
    private String description;
    private String operatingHours;
    private String facilities;
    private String accessibilityNotes;
    private boolean isEvacuationSite;
    private boolean isActive;
    private Integer capacity;
    private String geojson;
    private Double latitude;
    private Double longitude;
    private Timestamp createdAt;
    private Timestamp lastUpdated;

    public Landmark() {}

    public Landmark(String landmarkName, String landmarkType, String barangay) {
        this.landmarkName = landmarkName;
        this.landmarkType = landmarkType;
        this.barangay = barangay;
        this.isActive = true;
    }

    // Getters and Setters
    public int getLandmarkId() { return landmarkId; }
    public void setLandmarkId(int landmarkId) { this.landmarkId = landmarkId; }

    public String getLandmarkName() { return landmarkName; }
    public void setLandmarkName(String landmarkName) { this.landmarkName = landmarkName; }

    public String getLandmarkType() { return landmarkType; }
    public void setLandmarkType(String landmarkType) { this.landmarkType = landmarkType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { 
        this.operatingHours = operatingHours; 
    }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public String getAccessibilityNotes() { return accessibilityNotes; }
    public void setAccessibilityNotes(String accessibilityNotes) { 
        this.accessibilityNotes = accessibilityNotes; 
    }

    public boolean isEvacuationSite() { return isEvacuationSite; }
    public void setEvacuationSite(boolean evacuationSite) { 
        isEvacuationSite = evacuationSite; 
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

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
        return landmarkName + " (" + landmarkType + ")";
    }
}
