package com.dangerzone.models;

import java.sql.Timestamp;

/**
 * Model class for Safety Guidelines
 */
public class SafetyGuideline {
    
    private int guidelineId;
    private String hazardType;
    private String title;
    private String content;
    private Integer priorityLevel;
    private String category;
    private String targetAudience;
    private String emergencyContact;
    private String visualAidUrl;
    private String language;
    private boolean isActive;
    private Integer createdBy;
    private Timestamp lastUpdated;
    
    public SafetyGuideline() {}
    
    // Getters and Setters
    
    public int getGuidelineId() {
        return guidelineId;
    }
    
    public void setGuidelineId(int guidelineId) {
        this.guidelineId = guidelineId;
    }
    
    public String getHazardType() {
        return hazardType;
    }
    
    public void setHazardType(String hazardType) {
        this.hazardType = hazardType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getPriorityLevel() {
        return priorityLevel;
    }
    
    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTargetAudience() {
        return targetAudience;
    }
    
    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getVisualAidUrl() {
        return visualAidUrl;
    }
    
    public void setVisualAidUrl(String visualAidUrl) {
        this.visualAidUrl = visualAidUrl;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Integer getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
    
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    @Override
    public String toString() {
        return title + " (" + hazardType + ")";
    }
}
