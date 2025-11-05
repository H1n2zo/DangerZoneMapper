package com.dangerzone.models;

import java.sql.Date;
import java.sql.Timestamp;

public class Map {
    
    private int mapId;
    private String mapName;
    private String mapType;
    private String mapDescription;
    private String filePath;
    private Integer fileSizeKb;
    private Integer imageWidth;
    private Integer imageHeight;
    private String coverageArea;
    private String mapScale;
    private Date creationDate;
    private String source;
    private boolean isActive;
    private boolean isDefault;
    private int displayOrder;
    private Integer createdBy;
    private Timestamp createdAt;
    private Timestamp lastUpdated;
    
    public Map() {}
    
    // Getters and Setters
    public int getMapId() {
        return mapId;
    }
    
    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
    
    public String getMapType() {
        return mapType;
    }
    
    public void setMapType(String mapType) {
        this.mapType = mapType;
    }
    
    public String getMapDescription() {
        return mapDescription;
    }
    
    public void setMapDescription(String mapDescription) {
        this.mapDescription = mapDescription;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Integer getFileSizeKb() {
        return fileSizeKb;
    }
    
    public void setFileSizeKb(Integer fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }
    
    public Integer getImageWidth() {
        return imageWidth;
    }
    
    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }
    
    public Integer getImageHeight() {
        return imageHeight;
    }
    
    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }
    
    public String getCoverageArea() {
        return coverageArea;
    }
    
    public void setCoverageArea(String coverageArea) {
        this.coverageArea = coverageArea;
    }
    
    public String getMapScale() {
        return mapScale;
    }
    
    public void setMapScale(String mapScale) {
        this.mapScale = mapScale;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
    
    public int getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public Integer getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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
        return mapName + " (" + mapType + ")";
    }
}