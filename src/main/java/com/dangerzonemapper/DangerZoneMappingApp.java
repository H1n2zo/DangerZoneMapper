package com.dangerzonemapper;

import com.dangerzonemapper.controller.HazardZoneController;
import com.dangerzonemapper.model.HazardZone;
import com.dangerzonemapper.ui.MapRenderer;
import com.dangerzonemapper.ui.UIComponentFactory;
import com.dangerzonemapper.utils.CoordinateUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Main application class - now refactored and modular
 */
public class DangerZoneMappingApp extends Application {
    
    // Controllers and Managers
    private HazardZoneController hazardController;
    private MapRenderer mapRenderer;
    
    // UI Components
    private Pane mapPane;
    private Label coordsLabel;
    private Label selectedCoordsLabel;
    private Label statusLabel;
    private Slider radiusSlider;
    private ComboBox<String> hazardTypeCombo;
    private TextField nameField;
    private TextArea descriptionArea;
    private ListView<String> hazardListView;
    
    // Selection state
    private double selectedLat = 0;
    private double selectedLon = 0;
    private Circle selectedMarker;
    private Circle radiusCircle;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Danger Zone Mapping - Ormoc City");
        
        // Initialize the map pane first
        mapPane = createMapPane();
        
        // Initialize renderer and controller
        mapRenderer = new MapRenderer(mapPane);
        hazardController = new HazardZoneController(mapRenderer);
        
        // Build UI
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        root.setTop(UIComponentFactory.createMenuBar(this::refreshMap, this::showAboutDialog, this::showSafetyGuidelines));
        root.setCenter(createMapView());
        root.setRight(createControlPanel());
        root.setBottom(createStatusBar());
        
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Initialize data
        initializeDatabase();
        refreshMap();
    }
    
    /**
     * Create the map pane
     */
    private Pane createMapPane() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #E8F4F8; -fx-border-color: #333; -fx-border-width: 2;");
        pane.setPrefSize(CoordinateUtils.getMapWidth(), CoordinateUtils.getMapHeight());
        pane.setMinSize(CoordinateUtils.getMapWidth(), CoordinateUtils.getMapHeight());
        pane.setMaxSize(CoordinateUtils.getMapWidth(), CoordinateUtils.getMapHeight());
        
        pane.setOnMouseMoved(this::handleMouseMoved);
        pane.setOnMouseClicked(this::handleMapClick);
        
        return pane;
    }
    
    /**
     * Create the map view section
     */
    private VBox createMapView() {
        VBox mapBox = new VBox(8);
        mapBox.setPadding(new Insets(8));
        mapBox.setStyle("-fx-background-color: white;");
        
        HBox titleBox = UIComponentFactory.createMapHeader();
        
        coordsLabel = new Label("Click on map to select a location for the hazard zone");
        coordsLabel.setFont(javafx.scene.text.Font.font("Arial", 11));
        coordsLabel.setStyle("-fx-padding: 6; -fx-background-color: #fff3cd; -fx-border-color: #ffc107; -fx-border-width: 1;");
        
        mapBox.getChildren().addAll(titleBox, mapPane, coordsLabel);
        return mapBox;
    }
    
    /**
     * Create the control panel
     */
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setPrefWidth(300);
        controlPanel.setStyle("-fx-background-color: #fafafa; -fx-border-color: #ccc; -fx-border-width: 0 0 0 2;");
        
        // Title
        Label titleLabel = UIComponentFactory.createLabel("Hazard Zone Manager", true, 14);
        
        // Hazard type
        Label typeLabel = UIComponentFactory.createLabel("Hazard Type:", true, 10);
        hazardTypeCombo = UIComponentFactory.createHazardTypeComboBox();
        hazardTypeCombo.setOnAction(e -> updateRadiusCircle());
        
        // Location name
        Label nameLabel = UIComponentFactory.createLabel("Location Name:", true, 10);
        nameField = UIComponentFactory.createTextField("e.g., Downtown Area, Barangay...");
        
        // Description
        Label descLabel = UIComponentFactory.createLabel("Description:", true, 10);
        descriptionArea = UIComponentFactory.createTextArea("Enter hazard details...", 3);
        
        // Radius slider
        Label radiusLabel = UIComponentFactory.createLabel("Hazard Radius: 500m", true, 10);
        radiusSlider = UIComponentFactory.createRadiusSlider();
        radiusSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            radiusLabel.setText(String.format("Hazard Radius: %.0fm", newVal.doubleValue()));
            updateRadiusCircle();
        });
        
        // Selected coordinates display
        Label coordsDisplayLabel = UIComponentFactory.createLabel("Selected Location:", true, 10);
        selectedCoordsLabel = new Label("Click map to select");
        selectedCoordsLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 10px;");
        
        // Buttons
        HBox buttonBox = new HBox(8);
        Button addButton = UIComponentFactory.createButton("Add Hazard", "#4CAF50", 130);
        addButton.setOnAction(e -> addHazardZone());
        
        Button clearButton = UIComponentFactory.createButton("Clear", "#9E9E9E", 70);
        clearButton.setOnAction(e -> clearSelection());
        
        buttonBox.getChildren().addAll(addButton, clearButton);
        
        Separator separator = new Separator();
        
        // Hazard list
        Label listLabel = UIComponentFactory.createLabel("Existing Hazard Zones:", true, 11);
        hazardListView = new ListView<>();
        hazardListView.setPrefHeight(160);
        hazardListView.setStyle("-fx-font-size: 10px;");
        hazardListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewHazardDetails();
            }
        });
        
        HBox listButtonBox = new HBox(8);
        Button viewButton = UIComponentFactory.createButton("View", "#2196F3", 100);
        viewButton.setOnAction(e -> viewHazardDetails());
        
        Button deleteButton = UIComponentFactory.createButton("Delete", "#f44336", 80);
        deleteButton.setOnAction(e -> deleteSelectedHazard());
        
        listButtonBox.getChildren().addAll(viewButton, deleteButton);
        
        // Put everything in a scroll pane
        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10);
        content.getChildren().addAll(
            titleLabel, new Separator(),
            typeLabel, hazardTypeCombo,
            nameLabel, nameField,
            descLabel, descriptionArea,
            radiusLabel, radiusSlider,
            coordsDisplayLabel, selectedCoordsLabel,
            buttonBox,
            separator,
            listLabel, hazardListView, listButtonBox
        );
        
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(5));
        
        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return wrapper;
    }
    
    /**
     * Create status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(4, 10, 4, 10));
        statusBar.setStyle("-fx-background-color: #2196F3;");
        
        statusLabel = new Label("Ready | Total Zones: 0");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        
        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }
    
    /**
     * Handle mouse movement over map
     */
    private void handleMouseMoved(MouseEvent event) {
        double lat = CoordinateUtils.pixelYToLat(event.getY());
        double lon = CoordinateUtils.pixelXToLon(event.getX());
        
        coordsLabel.setText(String.format("Coordinates: %s | Click to select", 
            CoordinateUtils.formatCoordinates(lat, lon)));
    }
    
    /**
     * Handle map click
     */
    private void handleMapClick(MouseEvent event) {
        selectedLat = CoordinateUtils.pixelYToLat(event.getY());
        selectedLon = CoordinateUtils.pixelXToLon(event.getX());
        
        // Remove old marker
        if (selectedMarker != null) {
            mapRenderer.removeNode(selectedMarker);
        }
        
        // Draw new marker
        selectedMarker = mapRenderer.drawSelectionMarker(selectedLat, selectedLon);
        updateRadiusCircle();
        
        // Update labels
        coordsLabel.setText(String.format("✓ Selected: %s | Click 'Add Hazard' to save", 
            CoordinateUtils.formatCoordinates(selectedLat, selectedLon)));
        selectedCoordsLabel.setText(String.format("Lat: %.6f, Lon: %.6f", selectedLat, selectedLon));
    }
    
    /**
     * Update the radius circle visualization
     */
    private void updateRadiusCircle() {
        if (selectedMarker == null) return;
        
        // Remove old circle
        if (radiusCircle != null) {
            mapRenderer.removeNode(radiusCircle);
        }
        
        // Draw new circle
        radiusCircle = mapRenderer.drawRadiusCircle(
            selectedLat, 
            selectedLon, 
            radiusSlider.getValue(), 
            hazardTypeCombo.getValue()
        );
        
        // Keep marker on top
        selectedMarker.toFront();
    }
    
    /**
     * Clear selection
     */
    private void clearSelection() {
        if (selectedMarker != null) {
            mapRenderer.removeNode(selectedMarker);
            selectedMarker = null;
        }
        if (radiusCircle != null) {
            mapRenderer.removeNode(radiusCircle);
            radiusCircle = null;
        }
        
        nameField.clear();
        descriptionArea.clear();
        selectedLat = 0;
        selectedLon = 0;
        selectedCoordsLabel.setText("Click map to select");
        coordsLabel.setText("Click on map to select a location for the hazard zone");
    }
    
    /**
     * Add new hazard zone
     */
    private void addHazardZone() {
        if (selectedLat == 0 || selectedLon == 0) {
            UIComponentFactory.showAlert("Error", "Please select a location on the map first!");
            return;
        }
        
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            UIComponentFactory.showAlert("Error", "Please enter a location name!");
            return;
        }
        
        boolean success = hazardController.addHazardZone(
            name,
            hazardTypeCombo.getValue(),
            selectedLat,
            selectedLon,
            radiusSlider.getValue(),
            descriptionArea.getText()
        );
        
        if (success) {
            UIComponentFactory.showAlert("Success", "Hazard zone '" + name + "' added successfully!");
            clearSelection();
            refreshMap();
        } else {
            UIComponentFactory.showAlert("Error", "Failed to add hazard zone!");
        }
    }
    
    /**
     * View hazard zone details
     */
    private void viewHazardDetails() {
        String selected = hazardListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIComponentFactory.showAlert("Error", "Please select a hazard zone!");
            return;
        }
        
        int id = Integer.parseInt(selected.split(":")[0].replace("#", ""));
        HazardZone zone = hazardController.getHazardZoneById(id);
        
        if (zone != null) {
            UIComponentFactory.showHazardDetails(zone);
        }
    }
    
    /**
     * Delete selected hazard zone
     */
    private void deleteSelectedHazard() {
        String selected = hazardListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIComponentFactory.showAlert("Error", "Please select a hazard zone to delete!");
            return;
        }
        
        if (!UIComponentFactory.showConfirmation("Delete Hazard Zone", 
            "Are you sure you want to delete this hazard zone?")) {
            return;
        }
        
        int id = Integer.parseInt(selected.split(":")[0].replace("#", ""));
        
        if (hazardController.deleteHazardZone(id)) {
            UIComponentFactory.showAlert("Success", "Hazard zone deleted successfully!");
            refreshMap();
        } else {
            UIComponentFactory.showAlert("Error", "Failed to delete hazard zone!");
        }
    }
    
    /**
     * Refresh the map and hazard list
     */
    private void refreshMap() {
        hazardController.loadHazardZones();
        hazardListView.setItems(hazardController.getHazardZonesAsStrings());
        statusLabel.setText("Ready | Total Zones: " + hazardController.getHazardZoneCount());
    }
    
    /**
     * Initialize database connection
     */
    private void initializeDatabase() {
        if (!hazardController.testDatabaseConnection()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Failed to connect to database");
            alert.setContentText("""
                Please ensure:
                1. MySQL/XAMPP is running
                2. Database 'danger_zone_db' exists
                3. Run database_setup.sql
                """);
            alert.showAndWait();
        }
    }
    
    /**
     * Show about dialog
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Danger Zone Mapping System");
        alert.setContentText("""
            Version: 1.0
            Location: Ormoc City, Philippines
            
            This system helps identify and visualize areas
            vulnerable to natural hazards such as floods,
            landslides, fires, earthquakes, and typhoons.
            
            Developed for disaster preparedness and planning.
            """);
        alert.showAndWait();
    }
    
    /**
     * Show safety guidelines
     */
    private void showSafetyGuidelines() {
        UIComponentFactory.showSafetyGuidelines();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}