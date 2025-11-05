package com.dangerzone.views;

import com.dangerzone.models.HazardZone;
import com.dangerzone.models.HazardZoneDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HazardZoneManagerDialog extends Stage {
    
    private HazardZoneDAO hazardZoneDAO;
    private TableView<HazardZone> hazardTable;
    private ObservableList<HazardZone> hazardData;
    private ComboBox<String> filterTypeCombo;
    private ComboBox<String> filterSeverityCombo;
    private WebView mapView;
    private WebEngine webEngine;
    
    // For storing clicked coordinates and radius
    private double selectedLat = 11.0059;
    private double selectedLng = 124.6075;
    private int selectedRadius = 500;
    
    public HazardZoneManagerDialog(Connection connection) {
        this.hazardZoneDAO = new HazardZoneDAO(connection);
        this.hazardData = FXCollections.observableArrayList();
        
        setTitle("Hazard Zone Manager - Ormoc City");
        initModality(Modality.APPLICATION_MODAL);
        setWidth(1400);
        setHeight(800);
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        
        // Top
        VBox topSection = new VBox(10);
        Label titleLabel = new Label("Manage Hazard Zones - Click Map & Adjust Radius");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        HBox filterBox = createFilterControls();
        topSection.getChildren().addAll(titleLabel, filterBox);
        root.setTop(topSection);
        
        // Center: Split
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);
        
        // Left: Table
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        hazardTable = createHazardTable();
        VBox.setVgrow(hazardTable, Priority.ALWAYS);
        tableBox.getChildren().add(hazardTable);
        
        // Right: Map with radius control
        VBox mapBox = new VBox(10);
        mapBox.setPadding(new Insets(10));
        Label mapLabel = new Label("🗺 Click map + adjust radius slider");
        mapLabel.setStyle("-fx-font-weight: bold;");
        
        HBox radiusControl = createRadiusControl();
        
        mapView = createInteractiveMap();
        VBox.setVgrow(mapView, Priority.ALWAYS);
        
        Label coordsLabel = new Label("Selected: 11.0059°N, 124.6075°E | Radius: 500m");
        coordsLabel.setId("coordsLabel");
        coordsLabel.setStyle("-fx-font-size: 12px;");
        
        mapBox.getChildren().addAll(mapLabel, radiusControl, mapView, coordsLabel);
        
        splitPane.getItems().addAll(tableBox, mapBox);
        root.setCenter(splitPane);
        
        // Bottom
        HBox buttonBox = createButtonControls();
        root.setBottom(buttonBox);
        
        Scene scene = new Scene(root);
        setScene(scene);
        setMaximized(true);
        
        loadHazardZones();
    }
    
    private HBox createFilterControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label typeLabel = new Label("Type:");
        filterTypeCombo = new ComboBox<>();
        filterTypeCombo.getItems().add("All");
        loadHazardTypes();
        filterTypeCombo.setValue("All");
        
        Label severityLabel = new Label("Severity:");
        filterSeverityCombo = new ComboBox<>();
        filterSeverityCombo.getItems().addAll("All", "Low", "Medium", "High", "Critical");
        filterSeverityCombo.setValue("All");
        
        Button filterBtn = new Button("Apply Filter");
        filterBtn.setOnAction(e -> applyFilters());
        
        Button resetBtn = new Button("Show All");
        resetBtn.setOnAction(e -> loadHazardZones());
        
        box.getChildren().addAll(typeLabel, filterTypeCombo, severityLabel, 
                                 filterSeverityCombo, filterBtn, resetBtn);
        
        return box;
    }
    
    private HBox createRadiusControl() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label radiusLabel = new Label("Hazard Radius:");
        Slider radiusSlider = new Slider(100, 2000, 500);
        radiusSlider.setShowTickLabels(true);
        radiusSlider.setShowTickMarks(true);
        radiusSlider.setMajorTickUnit(500);
        radiusSlider.setPrefWidth(300);
        
        Label radiusValue = new Label("500m");
        radiusValue.setStyle("-fx-font-weight: bold;");
        
        radiusSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedRadius = newVal.intValue();
            radiusValue.setText(selectedRadius + "m");
            updateRadiusOnMap(selectedRadius);
            updateCoordsLabel();
        });
        
        box.getChildren().addAll(radiusLabel, radiusSlider, radiusValue);
        return box;
    }
    
    private TableView<HazardZone> createHazardTable() {
        TableView<HazardZone> table = new TableView<>();
        table.setItems(hazardData);
        
        TableColumn<HazardZone, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("zoneId"));
        idCol.setPrefWidth(50);
        
        TableColumn<HazardZone, String> nameCol = new TableColumn<>("Zone Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("zoneName"));
        nameCol.setPrefWidth(150);
        
        TableColumn<HazardZone, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("hazardType"));
        typeCol.setPrefWidth(100);
        
        TableColumn<HazardZone, String> severityCol = new TableColumn<>("Severity");
        severityCol.setCellValueFactory(new PropertyValueFactory<>("severityLevel"));
        severityCol.setPrefWidth(80);
        
        // Color code severity
        severityCol.setCellFactory(column -> new TableCell<HazardZone, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Critical": setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); break;
                        case "High": setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;"); break;
                        case "Medium": setStyle("-fx-background-color: #f1c40f;"); break;
                        case "Low": setStyle("-fx-background-color: #95a5a6;"); break;
                    }
                }
            }
        });
        
        TableColumn<HazardZone, Integer> radiusCol = new TableColumn<>("Radius(m)");
        radiusCol.setCellValueFactory(new PropertyValueFactory<>("radiusMeters"));
        radiusCol.setPrefWidth(80);
        
        table.getColumns().addAll(idCol, nameCol, typeCol, severityCol, radiusCol);
        
        // Show on map when selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showHazardOnMap(newVal);
            }
        });
        
        return table;
    }
    
    private WebView createInteractiveMap() {
        WebView webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        
        String mapHTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { width: 100%; height: 100vh; cursor: crosshair; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([11.0059, 124.6075], 13);
                
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '© OpenStreetMap',
                    maxZoom: 19
                }).addTo(map);
                
                var clickCircle = null;
                var currentRadius = 500;
                
                map.on('click', function(e) {
                    var lat = e.latlng.lat.toFixed(6);
                    var lng = e.latlng.lng.toFixed(6);
                    
                    if (clickCircle) {
                        map.removeLayer(clickCircle);
                    }
                    
                    clickCircle = L.circle(e.latlng, {
                        radius: currentRadius,
                        color: '#f39c12',
                        fillColor: '#f39c12',
                        fillOpacity: 0.3,
                        weight: 2
                    }).addTo(map);
                    
                    clickCircle.bindPopup('<b>Hazard Zone</b><br>Center: ' + lat + ', ' + lng + 
                                         '<br>Radius: ' + currentRadius + 'm').openPopup();
                    
                    document.title = lat + ',' + lng;
                });
                
                function updateRadius(radius) {
                    currentRadius = radius;
                    if (clickCircle) {
                        clickCircle.setRadius(radius);
                        clickCircle.getPopup().setContent('<b>Hazard Zone</b><br>Radius: ' + radius + 'm');
                    }
                }
                
                function showZone(lat, lng, radius, name, color) {
                    map.setView([lat, lng], 15);
                    L.circle([lat, lng], {
                        radius: radius,
                        color: color,
                        fillColor: color,
                        fillOpacity: 0.3
                    }).addTo(map).bindPopup('<b>' + name + '</b>').openPopup();
                }
            </script>
        </body>
        </html>
        """;
        
        webEngine.loadContent(mapHTML);
        
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                webEngine.titleProperty().addListener((obsTitle, oldTitle, newTitle) -> {
                    if (newTitle != null && newTitle.contains(",")) {
                        String[] coords = newTitle.split(",");
                        selectedLat = Double.parseDouble(coords[0]);
                        selectedLng = Double.parseDouble(coords[1]);
                        updateCoordsLabel();
                    }
                });
            }
        });
        
        return webView;
    }
    
    private void updateRadiusOnMap(int radius) {
        webEngine.executeScript("updateRadius(" + radius + ");");
    }
    
    private void updateCoordsLabel() {
        Label coordsLabel = (Label) getScene().lookup("#coordsLabel");
        if (coordsLabel != null) {
            coordsLabel.setText(String.format("Selected: %.6f°N, %.6f°E | Radius: %dm", 
                                             selectedLat, selectedLng, selectedRadius));
        }
    }
    
    private void showHazardOnMap(HazardZone zone) {
        String color = getSeverityColor(zone.getSeverityLevel());
        String script = String.format(
            "showZone(%f, %f, %d, '%s', '%s');",
            zone.getLatitude(), zone.getLongitude(), zone.getRadiusMeters(),
            zone.getZoneName().replace("'", "\\'"), color
        );
        webEngine.executeScript(script);
    }
    
    private String getSeverityColor(String severity) {
        switch (severity.toLowerCase()) {
            case "critical": return "#e74c3c";
            case "high": return "#f39c12";
            case "medium": return "#f1c40f";
            case "low": return "#95a5a6";
            default: return "#95a5a6";
        }
    }
    
    private HBox createButtonControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(10));
        
        Button addBtn = new Button("➕ Add Zone (Use Map)");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> addHazardZoneWithMapCoords());
        
        Button editBtn = new Button("✏ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editHazardZone());
        
        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteHazardZone());
        
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> close());
        
        box.getChildren().addAll(addBtn, editBtn, deleteBtn, closeBtn);
        return box;
    }
    
    private void addHazardZoneWithMapCoords() {
        try {
            // Zone name
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Add Hazard Zone");
            nameDialog.setHeaderText("Enter zone name:");
            Optional<String> nameResult = nameDialog.showAndWait();
            if (!nameResult.isPresent() || nameResult.get().trim().isEmpty()) return;
            
            // Barangay
            TextInputDialog barangayDialog = new TextInputDialog();
            barangayDialog.setTitle("Add Hazard Zone");
            barangayDialog.setHeaderText("Enter barangay:");
            Optional<String> barangayResult = barangayDialog.showAndWait();
            if (!barangayResult.isPresent() || barangayResult.get().trim().isEmpty()) return;
            
            // Hazard type
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>("Flood", 
                "Flood", "Fire", "Landslide", "Storm Surge");
            typeDialog.setTitle("Hazard Type");
            typeDialog.setHeaderText("Select hazard type:");
            Optional<String> typeResult = typeDialog.showAndWait();
            if (!typeResult.isPresent()) return;
            
            // Severity
            ChoiceDialog<String> severityDialog = new ChoiceDialog<>("High", 
                "Low", "Medium", "High", "Critical");
            severityDialog.setTitle("Severity Level");
            severityDialog.setHeaderText("Select severity:");
            Optional<String> severityResult = severityDialog.showAndWait();
            if (!severityResult.isPresent()) return;
            
            // Description (optional)
            TextInputDialog descDialog = new TextInputDialog();
            descDialog.setTitle("Description");
            descDialog.setHeaderText("Enter description (optional):");
            Optional<String> descResult = descDialog.showAndWait();
            
            // Create hazard zone
            HazardZone zone = new HazardZone();
            zone.setZoneName(nameResult.get().trim());
            zone.setBarangay(barangayResult.get().trim());
            zone.setHazardType(typeResult.get());
            zone.setSeverityLevel(severityResult.get());
            zone.setLatitude(selectedLat);
            zone.setLongitude(selectedLng);
            zone.setRadiusMeters(selectedRadius);
            zone.setDescription(descResult.orElse(null));
            zone.setDateIdentified(Date.valueOf(LocalDate.now()));
            zone.setActive(true);
            
            // Save
            if (hazardZoneDAO.createHazardZone(zone)) {
                showSuccess("Hazard zone added at: " + 
                           String.format("%.6f, %.6f with %dm radius", 
                                       selectedLat, selectedLng, selectedRadius));
                loadHazardZones();
            } else {
                showError("Failed to add hazard zone");
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }
    
private void editHazardZone() {
    HazardZone selected = hazardTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showWarning("Please select a hazard zone to edit");
        return;
    }
    
    System.out.println("📝 Editing Hazard Zone ID: " + selected.getZoneId() + " - " + selected.getZoneName());
    
    HazardZoneFormDialog dialog = new HazardZoneFormDialog(selected);
    Optional<HazardZone> result = dialog.showAndWait();
    
    result.ifPresent(zone -> {
        try {
            System.out.println("💾 Saving changes to Hazard Zone ID: " + zone.getZoneId());
            System.out.println("   Name: " + zone.getZoneName());
            System.out.println("   Type: " + zone.getHazardType());
            System.out.println("   Severity: " + zone.getSeverityLevel());
            
            boolean success = hazardZoneDAO.updateHazardZone(zone);
            
            if (success) {
                showSuccess("Hazard zone updated successfully!");
                loadHazardZones(); // Refresh table
                System.out.println("✅ Hazard zone updated and table refreshed");
            } else {
                showError("Update failed - No rows were affected. Check if zone ID exists.");
                System.err.println("❌ Update returned false - no rows affected");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            System.err.println("❌ SQLException during update:");
            e.printStackTrace();
        }
    });
    
    if (!result.isPresent()) {
        System.out.println("❌ Edit dialog was cancelled");
    }
}
    
    private void deleteHazardZone() {
        HazardZone selected = hazardTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a hazard zone to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Hazard Zone?");
        confirm.setContentText("Delete: " + selected.getZoneName() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (hazardZoneDAO.deleteHazardZone(selected.getZoneId())) {
                    showSuccess("Hazard zone deleted!");
                    loadHazardZones();
                }
            } catch (SQLException e) {
                showError("Delete failed: " + e.getMessage());
            }
        }
    }
    
    private void loadHazardZones() {
        try {
            List<HazardZone> zones = hazardZoneDAO.getAllHazardZones();
            hazardData.clear();
            hazardData.addAll(zones);
        } catch (SQLException e) {
            showError("Failed to load zones: " + e.getMessage());
        }
    }
    
    private void loadHazardTypes() {
        try {
            List<String> types = hazardZoneDAO.getHazardTypes();
            filterTypeCombo.getItems().addAll(types);
        } catch (SQLException e) {
            System.err.println("Failed to load types: " + e.getMessage());
        }
    }
    
    private void applyFilters() {
        String type = filterTypeCombo.getValue();
        String severity = filterSeverityCombo.getValue();
        
        try {
            List<HazardZone> zones = hazardZoneDAO.getAllHazardZones();
            hazardData.clear();
            
            for (HazardZone zone : zones) {
                boolean typeMatch = type.equals("All") || zone.getHazardType().equals(type);
                boolean sevMatch = severity.equals("All") || zone.getSeverityLevel().equals(severity);
                
                if (typeMatch && sevMatch) {
                    hazardData.add(zone);
                }
            }
        } catch (SQLException e) {
            showError("Filter failed: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}