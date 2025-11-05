/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.views;

import com.dangerzone.models.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DashboardPanel extends VBox {
    
    private Connection connection;
    
    public DashboardPanel(Connection connection) {
        this.connection = connection;
        setPadding(new Insets(20));
        setSpacing(20);
        
        Label titleLabel = new Label("📊 Dashboard - Ormoc City Overview");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        GridPane statsGrid = createStatsGrid();
        
        VBox recentIncidents = createRecentIncidentsSection();
        
        getChildren().addAll(titleLabel, statsGrid, recentIncidents);
        
        loadDashboardData();
    }
    
    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        
        // Stat cards
        VBox landmarksCard = createStatCard("Landmarks", "0", "#3498db", "📍");
        VBox evacCentersCard = createStatCard("Evacuation Centers", "0", "#27ae60", "🏥");
        VBox hazardZonesCard = createStatCard("Hazard Zones", "0", "#e74c3c", "⚠");
        VBox incidentsCard = createStatCard("Total Incidents", "0", "#f39c12", "📋");
        
        grid.add(landmarksCard, 0, 0);
        grid.add(evacCentersCard, 1, 0);
        grid.add(hazardZonesCard, 2, 0);
        grid.add(incidentsCard, 3, 0);
        
        return grid;
    }
    
    private VBox createStatCard(String title, String value, String color, String icon) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 36;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: white;");
        valueLabel.setId(title.toLowerCase().replace(" ", "_") + "_value");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: white;");
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        
        return card;
    }
    
    private VBox createRecentIncidentsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");
        
        Label title = new Label("🔥 Recent Incidents (Last 5)");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        VBox incidentsList = new VBox(5);
        incidentsList.setId("recent_incidents_list");
        
        section.getChildren().addAll(title, incidentsList);
        
        return section;
    }
    
    private void loadDashboardData() {
        try {
            // Load landmark counts
            LandmarkDAO landmarkDAO = new LandmarkDAO(connection);
            List<Landmark> landmarks = landmarkDAO.getAllLandmarks();
            List<Landmark> evacCenters = landmarkDAO.getEvacuationCenters();
            
            updateStatCard("landmarks_value", String.valueOf(landmarks.size()));
            updateStatCard("evacuation_centers_value", String.valueOf(evacCenters.size()));
            
            // Load hazard zones
            HazardZoneDAO hazardDAO = new HazardZoneDAO(connection);
            List<HazardZone> hazardZones = hazardDAO.getAllHazardZones();
            updateStatCard("hazard_zones_value", String.valueOf(hazardZones.size()));
            
            // Load critical zones
            VBox criticalZonesList = (VBox) lookup("#critical_zones_list");
            if (criticalZonesList != null) {
                for (HazardZone zone : hazardZones) {
                    if ("Critical".equals(zone.getSeverityLevel()) || "High".equals(zone.getSeverityLevel())) {
                        Label zoneLabel = new Label("• " + zone.getZoneName() + " (" + zone.getHazardType() + ") - " + zone.getBarangay());
                        zoneLabel.setStyle("-fx-font-size: 13;");
                        criticalZonesList.getChildren().add(zoneLabel);
                    }
                }
            }
            
            // Load incidents
            IncidentDAO incidentDAO = new IncidentDAO(connection);
            List<Incident> incidents = incidentDAO.getAllIncidents();
            updateStatCard("total_incidents_value", String.valueOf(incidents.size()));
            
            // Load recent incidents
            VBox recentIncidentsList = (VBox) lookup("#recent_incidents_list");
            if (recentIncidentsList != null) {
                int count = 0;
                for (Incident incident : incidents) {
                    if (count >= 5) break;
                    
                    HBox incidentBox = new HBox(10);
                    incidentBox.setAlignment(Pos.CENTER_LEFT);
                    
                    Label dateLabel = new Label(incident.getIncidentDate().toString());
                    dateLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");
                    
                    Label typeLabel = new Label(incident.getIncidentType());
                    typeLabel.setStyle("-fx-min-width: 80;");
                    
                    Label locationLabel = new Label(incident.getBarangay());
                    locationLabel.setStyle("-fx-text-fill: #7f8c8d;");
                    
                    Label impactLabel = new Label("💀 " + incident.getCasualties() + " casualties");
                    impactLabel.setStyle("-fx-text-fill: #e74c3c;");
                    
                    incidentBox.getChildren().addAll(dateLabel, typeLabel, locationLabel, impactLabel);
                    recentIncidentsList.getChildren().add(incidentBox);
                    
                    count++;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }
    
    private void updateStatCard(String id, String value) {
        Label label = (Label) lookup("#" + id);
        if (label != null) {
            label.setText(value);
        }
    }
    
    public void refreshDashboard() {
        // Clear and reload
        getChildren().clear();
        
        Label titleLabel = new Label("📊 Dashboard - Ormoc City Overview");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        GridPane statsGrid = createStatsGrid();
        VBox recentIncidents = createRecentIncidentsSection();
        
        getChildren().addAll(titleLabel, statsGrid, recentIncidents);
        
        loadDashboardData();
    }
}