/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.models;

import java.sql.Timestamp;

public class Landmark {
    
    private int id;
    private String name;
    private String type;
    private String address;
    private String barangay;
    private double latitude;
    private double longitude;
    private String contactNumber;
    private Integer capacity;
    private String description;
    private String operatingHours;
    private boolean isEvacuationSite;
    private Timestamp createdAt;
    private Timestamp lastUpdated;
    
    public Landmark() {}
    
    public Landmark(String name, String type, String address, String barangay, 
                   double latitude, double longitude) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.barangay = barangay;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getBarangay() {
        return barangay;
    }
    
    public void setBarangay(String barangay) {
        this.barangay = barangay;
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
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOperatingHours() {
        return operatingHours;
    }
    
    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }
    
    public boolean isEvacuationSite() {
        return isEvacuationSite;
    }
    
    public void setEvacuationSite(boolean evacuationSite) {
        isEvacuationSite = evacuationSite;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ") - " + barangay;
    }
    
    public String getCoordinates() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
    
    public String getFullAddress() {
        if (address != null && barangay != null) {
            return address + ", " + barangay + ", Ormoc City";
        } else if (barangay != null) {
            return barangay + ", Ormoc City";
        } else if (address != null) {
            return address + ", Ormoc City";
        }
        return "Ormoc City";
    }
}