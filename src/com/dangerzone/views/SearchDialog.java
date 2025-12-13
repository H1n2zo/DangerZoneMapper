/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.views;

import com.dangerzone.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SearchDialog extends Stage {
    
    private Connection connection;
    private TextField searchField;
    private ComboBox<String> searchTypeCombo;
    private ListView<String> resultsList;
    private ObservableList<String> resultsData;
    
    public SearchDialog(Connection connection) {
        this.connection = connection;
        this.resultsData = FXCollections.observableArrayList();
        
        setTitle("🔍 Global Search - Ormoc City");
        initModality(Modality.NONE);
        setWidth(700);
        setHeight(600);
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        Label titleLabel = new Label("🔍 Search Everything");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        HBox searchBox = createSearchBox();
        
        Label resultsLabel = new Label("Search Results:");
        resultsLabel.setStyle("-fx-font-weight: bold;");
        
        resultsList = new ListView<>();
        resultsList.setItems(resultsData);
        resultsList.setPlaceholder(new Label("Enter a search term and click Search"));
        VBox.setVgrow(resultsList, Priority.ALWAYS);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> close());
        buttonBox.getChildren().add(closeBtn);
        
        root.getChildren().addAll(titleLabel, searchBox, resultsLabel, resultsList, buttonBox);
        
        Scene scene = new Scene(root);
        setScene(scene);
    }
    
    private HBox createSearchBox() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        searchField = new TextField();
        searchField.setPromptText("Enter search term...");
        searchField.setPrefWidth(300);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        searchTypeCombo = new ComboBox<>();
        searchTypeCombo.getItems().addAll("All", "Landmarks", "Hazard Zones", "Incidents");
        searchTypeCombo.setValue("All");
        
        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        searchBtn.setOnAction(e -> performSearch());
        
        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearSearch());
        
        box.getChildren().addAll(new Label("Search:"), searchField, searchTypeCombo, searchBtn, clearBtn);
        
        // Allow Enter key to search
        searchField.setOnAction(e -> performSearch());
        
        return box;
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showWarning("Please enter a search term");
            return;
        }
        
        resultsData.clear();
        String searchType = searchTypeCombo.getValue();
        
        try {
            if ("All".equals(searchType) || "Landmarks".equals(searchType)) {
                searchLandmarks(searchTerm);
            }
            
            if ("All".equals(searchType) || "Hazard Zones".equals(searchType)) {
                searchHazardZones(searchTerm);
            }
            
            if ("All".equals(searchType) || "Incidents".equals(searchType)) {
                searchIncidents(searchTerm);
            }
            
            if (resultsData.isEmpty()) {
                resultsData.add("No results found for: " + searchTerm);
            }
            
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    private void searchLandmarks(String searchTerm) throws SQLException {
        LandmarkDAO dao = new LandmarkDAO(connection);
        List<Landmark> landmarks = dao.searchLandmarksByName(searchTerm);
        
        if (!landmarks.isEmpty()) {
            resultsData.add("=== LANDMARKS ===");
            for (Landmark landmark : landmarks) {
                resultsData.add(String.format("📍 %s (%s) - %s, %s",
                    landmark.getName(),
                    landmark.getType(),
                    landmark.getBarangay(),
                    landmark.isEvacuationSite() ? "EVACUATION CENTER" : ""));
            }
            resultsData.add("");
        }
    }
    
    private void searchHazardZones(String searchTerm) throws SQLException {
        HazardZoneDAO dao = new HazardZoneDAO(connection);
        List<HazardZone> zones = dao.getAllHazardZones();
        
        boolean found = false;
        for (HazardZone zone : zones) {
            if (zone.getZoneName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                zone.getBarangay().toLowerCase().contains(searchTerm.toLowerCase()) ||
                zone.getHazardType().toLowerCase().contains(searchTerm.toLowerCase())) {
                
                if (!found) {
                    resultsData.add("=== HAZARD ZONES ===");
                    found = true;
                }
                
                resultsData.add(String.format("⚠️ %s - %s (%s severity) - %s",
                    zone.getZoneName(),
                    zone.getHazardType(),
                    zone.getSeverityLevel(),
                    zone.getBarangay()));
            }
        }
        
        if (found) {
            resultsData.add("");
        }
    }
    
    private void searchIncidents(String searchTerm) throws SQLException {
        IncidentDAO dao = new IncidentDAO(connection);
        List<Incident> incidents = dao.getAllIncidents();
        
        boolean found = false;
        for (Incident incident : incidents) {
            if (incident.getBarangay().toLowerCase().contains(searchTerm.toLowerCase()) ||
                incident.getIncidentType().toLowerCase().contains(searchTerm.toLowerCase()) ||
                (incident.getDescription() != null && incident.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))) {
                
                if (!found) {
                    resultsData.add("=== INCIDENTS ===");
                    found = true;
                }
                
                resultsData.add(String.format("📋 %s - %s (%s) - %d casualties",
                    incident.getIncidentDate(),
                    incident.getIncidentType(),
                    incident.getBarangay(),
                    incident.getCasualties()));
            }
        }
    }
    
    private void clearSearch() {
        searchField.clear();
        resultsData.clear();
        resultsList.setPlaceholder(new Label("Enter a search term and click Search"));
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
