package com.dangerzonemapper;

import com.dangerzonemapper.controller.HazardZoneController;
import com.dangerzonemapper.model.HazardZone;
import com.dangerzonemapper.ui.MapRenderer;
import com.dangerzonemapper.ui.UIComponentFactory;
import com.dangerzonemapper.utils.CoordinateUtils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Main application class - now refactored and modular with FIXED LAYOUT
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
        
        // Build UI with proper layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        root.setTop(UIComponentFactory.createMenuBar(this::refreshMap, this::showAboutDialog, this::showSafetyGuidelines));
        
        // Create center container that will hold the map properly
        StackPane centerContainer = new StackPane();
        centerContainer.setStyle("-fx-background-color: white;");
        VBox mapView = createMapView();
        centerContainer.getChildren().add(mapView);
        StackPane.setAlignment(mapView, Pos.CENTER);
        
        root.setCenter(centerContainer);
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
     * Create the map view section - IMPROVED LAYOUT
     */
    private VBox createMapView() {
        VBox mapBox = new VBox(10);
        mapBox.setPadding(new Insets(15));
        mapBox.setAlignment(Pos.CENTER);
        mapBox.setStyle("-fx-background-color: white;");
        
        // Title and legend
        HBox titleBox = UIComponentFactory.createMapHeader();
        titleBox.setPrefWidth(CoordinateUtils.getMapWidth());
        titleBox.setMaxWidth(CoordinateUtils.getMapWidth());
        
        // Coordinates label
        coordsLabel = new Label("Click on map to select a location for the hazard zone");
        coordsLabel.setFont(javafx.scene.text.Font.font("Arial", 11));
        coordsLabel.setStyle("-fx-padding: 8; -fx-background-color: #fff3cd; -fx-border-color: #ffc107; " +
                            "-fx-border-width: 1; -fx-border-radius: 3; -fx-background-radius: 3;");
        coordsLabel.setPrefWidth(CoordinateUtils.getMapWidth());
        coordsLabel.setMaxWidth(CoordinateUtils.getMapWidth());
        coordsLabel.setAlignment(Pos.CENTER);
        
        mapBox.getChildren().addAll(titleBox, mapPane, coordsLabel);
        
        return mapBox;
    }
    
    /**
     * Create the control panel - IMPROVED LAYOUT
     */
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(12);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setPrefWidth(320);
        controlPanel.setMinWidth(320);
        controlPanel.setMaxWidth(320);
        controlPanel.setStyle("-fx-background-color: #fafafa; -fx-border-color: #ddd; -fx-border-width: 0 0 0 2;");
        
        // Title
        Label titleLabel = UIComponentFactory.createLabel("Hazard Zone Manager", true, 15);
        titleLabel.setStyle("-fx-text-fill: #2196F3;");
        
        // Hazard type
        Label typeLabel = UIComponentFactory.createLabel("Hazard Type:", true, 11);
        hazardTypeCombo = UIComponentFactory.createHazardTypeComboBox();
        hazardTypeCombo.setOnAction(e -> updateRadiusCircle());
        
        // Location name
        Label nameLabel = UIComponentFactory.createLabel("Location Name:", true, 11);
        nameField = UIComponentFactory.createTextField("e.g., Downtown Area, Barangay...");
        
        // Description
        Label descLabel = UIComponentFactory.createLabel("Description:", true, 11);
        descriptionArea = UIComponentFactory.createTextArea("Enter hazard details...", 3);
        
        // Radius slider
        Label radiusLabel = UIComponentFactory.createLabel("Hazard Radius: 500m", true, 11);
        radiusSlider = UIComponentFactory.createRadiusSlider();
        radiusSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            radiusLabel.setText(String.format("Hazard Radius: %.0fm", newVal.doubleValue()));
            updateRadiusCircle();
        });
        
        // Selected coordinates display
        Label coordsDisplayLabel = UIComponentFactory.createLabel("Selected Location:", true, 11);
        selectedCoordsLabel = new Label("Click map to select");
        selectedCoordsLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 10px; -fx-font-style: italic;");
        
        // Buttons
        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.CENTER);
        Button addButton = UIComponentFactory.createButton("Add Hazard", "#4CAF50", 150);
        addButton.setOnAction(e -> addHazardZone());
        
        Button clearButton = UIComponentFactory.createButton("Clear", "#9E9E9E", 85);
        clearButton.setOnAction(e -> clearSelection());
        
        buttonBox.getChildren().addAll(addButton, clearButton);
        
        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 5 0 5 0;");
        
        // Hazard list
        Label listLabel = UIComponentFactory.createLabel("Existing Hazard Zones:", true, 12);
        listLabel.setStyle("-fx-text-fill: #2196F3;");
        
        hazardListView = new ListView<>();
        hazardListView.setPrefHeight(180);
        hazardListView.setStyle("-fx-font-size: 10px; -fx-border-color: #ddd; -fx-border-width: 1;");
        hazardListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewHazardDetails();
            }
        });
        
        HBox listButtonBox = new HBox(8);
        listButtonBox.setAlignment(Pos.CENTER);
        Button viewButton = UIComponentFactory.createButton("View Details", "#2196F3", 120);
        viewButton.setOnAction(e -> viewHazardDetails());
        
        Button deleteButton = UIComponentFactory.createButton("Delete", "#f44336", 95);
        deleteButton.setOnAction(e -> deleteSelectedHazard());
        
        listButtonBox.getChildren().addAll(viewButton, deleteButton);
        
        // Main content container
        VBox content = new VBox(12);
        content.getChildren().addAll(
            titleLabel, 
            new Separator(),
            typeLabel, hazardTypeCombo,
            nameLabel, nameField,
            descLabel, descriptionArea,
            radiusLabel, radiusSlider,
            coordsDisplayLabel, selectedCoordsLabel,
            buttonBox,
            separator,
            listLabel, hazardListView, listButtonBox
        );
        
        // Wrap in scroll pane for overflow
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(new Insets(5));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return wrapper;
    }
    
    /**
     * Create status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: #2196F3;");
        
        statusLabel = new Label("Ready | Total Zones: 0");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        
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