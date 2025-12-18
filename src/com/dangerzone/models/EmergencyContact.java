package com.dangerzone.models;

import java.sql.Timestamp;

/**
 * Model class for Emergency Contacts
 * FIXED: Added getDepartmentName() and setDepartmentName() methods
 */
public class EmergencyContact {
    
    private int contactId;
    private String contactName;  // Database column name
    private String contactType;
    private String contactNumber;
    private String alternateNumber;
    private String email;
    private String address;
    private String description;
    private String operatingHours;
    private int priorityOrder;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp lastUpdated;
    
    public EmergencyContact() {}
    
    // Getters and Setters
    
    public int getContactId() {
        return contactId;
    }
    
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
    
    // ADDED: getDepartmentName() - alias for getContactName()
    public String getDepartmentName() {
        return contactName;
    }
    
    // ADDED: setDepartmentName() - alias for setContactName()
    public void setDepartmentName(String departmentName) {
        this.contactName = departmentName;
    }
    
    // Original getters/setters for contactName
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    public String getContactType() {
        return contactType;
    }
    
    public void setContactType(String contactType) {
        this.contactType = contactType;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getAlternateNumber() {
        return alternateNumber;
    }
    
    public void setAlternateNumber(String alternateNumber) {
        this.alternateNumber = alternateNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
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
    
    public int getPriorityOrder() {
        return priorityOrder;
    }
    
    public void setPriorityOrder(int priorityOrder) {
        this.priorityOrder = priorityOrder;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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
        return contactName + " - " + contactNumber;
    }
    
    /**
     * Get formatted contact info for display
     */
    public String getFormattedContact() {
        StringBuilder sb = new StringBuilder();
        sb.append(contactName).append("\n");
        sb.append(contactNumber);
        if (alternateNumber != null && !alternateNumber.isEmpty()) {
            sb.append(" / ").append(alternateNumber);
        }
        return sb.toString();
    }
    
    /**
     * Get short display name (for compact views)
     */
    public String getShortName() {
        // Simplify long names
        String name = contactName;
        if (name == null) return "";
        
        name = name.replace("Philippine ", "");
        name = name.replace("National ", "");
        name = name.replace("Department of ", "");
        name = name.replace("Office", "Off.");
        
        if (name.length() > 15) {
            return name.substring(0, 12) + "...";
        }
        return name;
    }
}