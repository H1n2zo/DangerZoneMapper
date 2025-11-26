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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.io.InputStream;

public class HazardZoneManagerDialog extends Stage {
    
    private HazardZoneDAO hazardZoneDAO;
    private TableView<HazardZone> hazardTable;
    private ObservableList<HazardZone> hazardData;
    private ComboBox<String> filterTypeCombo;
    private ComboBox<String> filterSeverityCombo;
    private ImageView mapImageView;
    private Pane mapOverlayPane;
    private Label coordsLabel;
    private Circle selectionMarker;
    private Circle radiusCircle;
    
    // Map bounds for Ormoc City
    private static final double MAP_MIN_LAT = 10.85;
    private static final double MAP_MAX_LAT = 11.20;
    private static final double MAP_MIN_LNG = 124.45;
    private static final double MAP_MAX_LNG = 124.80;
    private static final double MAP_WIDTH = 1920;
    private static final double MAP_HEIGHT = 988;
    
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
        Label mapLabel = new Label("🗺 Click map to select location + adjust radius slider");
        mapLabel.setStyle("-fx-font-weight: bold;");
        
        HBox radiusControl = createRadiusControl();
        
        ScrollPane mapScrollPane = createInteractiveMap();
        VBox.setVgrow(mapScrollPane, Priority.ALWAYS);
        
        coordsLabel = new Label(String.format("Selected: %.6f°N, %.6f°E | Radius: %dm", 
                                             selectedLat, selectedLng, selectedRadius));
        coordsLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; " +
                            "-fx-padding: 8; -fx-background-color: #ecf0f1; " +
                            "-fx-border-color: #3498db; -fx-border-width: 2;");
        coordsLabel.setAlignment(Pos.CENTER);
        
        mapBox.getChildren().addAll(mapLabel, radiusControl, mapScrollPane, coordsLabel);
        
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
            updateRadiusVisualization();
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
    
    private ScrollPane createInteractiveMap() {
        try {
            InputStream mapStream = getClass().getResourceAsStream("/resources/ormoc_map.png");
            if (mapStream != null) {
                Image mapImage = new Image(mapStream);
                mapImageView = new ImageView(mapImage);
                mapImageView.setPreserveRatio(true);
                mapImageView.setFitWidth(MAP_WIDTH * 0.5); // Scale down for dialog
                mapImageView.setFitHeight(MAP_HEIGHT * 0.5);
                
                // Create overlay pane for markers
                mapOverlayPane = new Pane();
                mapOverlayPane.setPrefSize(MAP_WIDTH * 0.5, MAP_HEIGHT * 0.5);
                mapOverlayPane.setMaxSize(MAP_WIDTH * 0.5, MAP_HEIGHT * 0.5);
                
                // Add mouse tracking
                mapOverlayPane.setOnMouseMoved(event -> {
                    double mouseX = event.getX();
                    double mouseY = event.getY();
                    
                    // Convert pixel to coordinates
                    double scaledWidth = mapImageView.getBoundsInLocal().getWidth();
                    double scaledHeight = mapImageView.getBoundsInLocal().getHeight();
                    
                    double latitude = MAP_MAX_LAT - (mouseY / scaledHeight) * (MAP_MAX_LAT - MAP_MIN_LAT);
                    double longitude = MAP_MIN_LNG + (mouseX / scaledWidth) * (MAP_MAX_LNG - MAP_MIN_LNG);
                    
                    coordsLabel.setText(String.format("Hover: %.6f°N, %.6f°E | Radius: %dm", 
                                                     latitude, longitude, selectedRadius));
                });
                
                mapOverlayPane.setOnMouseExited(event -> {
                    updateCoordsLabel();
                });
                
                // Click to select location
                mapOverlayPane.setOnMouseClicked(event -> {
                    double mouseX = event.getX();
                    double mouseY = event.getY();
                    
                    double scaledWidth = mapImageView.getBoundsInLocal().getWidth();
                    double scaledHeight = mapImageView.getBoundsInLocal().getHeight();
                    
                    selectedLat = MAP_MAX_LAT - (mouseY / scaledHeight) * (MAP_MAX_LAT - MAP_MIN_LAT);
                    selectedLng = MAP_MIN_LNG + (mouseX / scaledWidth) * (MAP_MAX_LNG - MAP_MIN_LNG);
                    
                    updateSelectionMarker(mouseX, mouseY);
                    updateCoordsLabel();
                });
                
                // Stack map and overlay
                StackPane mapStack = new StackPane();
                mapStack.getChildren().addAll(mapImageView, mapOverlayPane);
                mapStack.setStyle("-fx-background-color: white;");
                
                ScrollPane scrollPane = new ScrollPane(mapStack);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                scrollPane.setStyle("-fx-background-color: #ecf0f1;");
                
                return scrollPane;
            }
        } catch (Exception e) {
            System.err.println("Failed to load map: " + e.getMessage());
        }
        
        // Fallback
        ScrollPane scrollPane = new ScrollPane(new Label("Map not found"));
        return scrollPane;
    }
    
    private void updateSelectionMarker(double x, double y) {
        // Remove old markers
        if (selectionMarker != null) {
            mapOverlayPane.getChildren().remove(selectionMarker);
        }
        if (radiusCircle != null) {
            mapOverlayPane.getChildren().remove(radiusCircle);
        }
        
        // Calculate radius in pixels
        double scaledWidth = mapImageView.getBoundsInLocal().getWidth();
        double scaledHeight = mapImageView.getBoundsInLocal().getHeight();
        double radiusPixels = metersToPixels(selectedRadius, scaledHeight);
        
        // Create radius circle
        radiusCircle = new Circle(x, y, radiusPixels);
        radiusCircle.setFill(Color.web("#f39c12", 0.3)); // Orange with transparency
        radiusCircle.setStroke(Color.web("#f39c12"));
        radiusCircle.setStrokeWidth(2);
        
        // Create center marker
        selectionMarker = new Circle(x, y, 8);
        selectionMarker.setFill(Color.web("#e74c3c"));
        selectionMarker.setStroke(Color.WHITE);
        selectionMarker.setStrokeWidth(3);
        
        // Add to overlay (circle first, then marker on top)
        mapOverlayPane.getChildren().addAll(radiusCircle, selectionMarker);
    }
    
    private void updateRadiusVisualization() {
        // If we have a selection, update the radius circle
        if (selectionMarker != null) {
            double x = selectionMarker.getCenterX();
            double y = selectionMarker.getCenterY();
            updateSelectionMarker(x, y);
        }
    }
    
    private double metersToPixels(int meters, double mapHeightPixels) {
        // Approximate conversion: 1 degree latitude ≈ 111km
        double degreesLat = meters / 111000.0;
        double pixelsPerDegree = mapHeightPixels / (MAP_MAX_LAT - MAP_MIN_LAT);
        return degreesLat * pixelsPerDegree;
    }
    
    private void updateCoordsLabel() {
        coordsLabel.setText(String.format("Selected: %.6f°N, %.6f°E | Radius: %dm", 
                                         selectedLat, selectedLng, selectedRadius));
    }
    
    private void showHazardOnMap(HazardZone zone) {
        selectedLat = zone.getLatitude();
        selectedLng = zone.getLongitude();
        selectedRadius = zone.getRadiusMeters();
        
        // Calculate pixel position
        double scaledWidth = mapImageView.getBoundsInLocal().getWidth();
        double scaledHeight = mapImageView.getBoundsInLocal().getHeight();
        
        double x = ((selectedLng - MAP_MIN_LNG) / (MAP_MAX_LNG - MAP_MIN_LNG)) * scaledWidth;
        double y = ((MAP_MAX_LAT - selectedLat) / (MAP_MAX_LAT - MAP_MIN_LAT)) * scaledHeight;
        
        updateSelectionMarker(x, y);
        updateCoordsLabel();
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
        
        HazardZoneFormDialog dialog = new HazardZoneFormDialog(selected);
        Optional<HazardZone> result = dialog.showAndWait();
        
        result.ifPresent(zone -> {
            try {
                boolean success = hazardZoneDAO.updateHazardZone(zone);
                
                if (success) {
                    showSuccess("Hazard zone updated successfully!");
                    loadHazardZones();
                } else {
                    showError("Update failed - No rows were affected.");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
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