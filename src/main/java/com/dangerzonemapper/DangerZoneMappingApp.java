package com.dangerzonemapper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class DangerZoneMappingApp extends Application {
    
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/danger_zone_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Ormoc City coordinates (approximate bounds)
    private static final double ORMOC_LAT_MIN = 11.0;
    private static final double ORMOC_LAT_MAX = 11.2;
    private static final double ORMOC_LON_MIN = 124.55;
    private static final double ORMOC_LON_MAX = 124.65;
    
    private Pane mapPane;
    private Label coordsLabel;
    private Slider radiusSlider;
    private ComboBox<String> hazardTypeCombo;
    private TextField nameField;
    private TextArea descriptionArea;
    private ListView<String> hazardListView;
    private ObservableList<HazardZone> hazardZones;
    private Label selectedCoordsLabel;
    
    private double selectedLat = 0;
    private double selectedLon = 0;
    private Circle selectedMarker;
    private Circle radiusCircle;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Danger Zone Mapping System - Ormoc City");
        
        hazardZones = FXCollections.observableArrayList();
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Top menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        
        // Center - Map view
        root.setCenter(createMapView());
        
        // Right - Control panel
        root.setRight(createControlPanel());
        
        // Bottom - Status bar
        root.setBottom(createStatusBar());
        
        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initialize database
        initializeDatabase();
        loadHazardZones();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem exportItem = new MenuItem("Export Data");
        exportItem.setOnAction(e -> showAlert("Export", "Export feature coming soon!"));
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(exportItem, new SeparatorMenuItem(), exitItem);
        
        Menu viewMenu = new Menu("View");
        MenuItem refreshItem = new MenuItem("Refresh Map");
        refreshItem.setOnAction(e -> loadHazardZones());
        MenuItem clearItem = new MenuItem("Clear All Filters");
        viewMenu.getItems().addAll(refreshItem, clearItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        MenuItem safetyItem = new MenuItem("Safety Guidelines");
        safetyItem.setOnAction(e -> showSafetyGuidelines());
        helpMenu.getItems().addAll(aboutItem, safetyItem);
        
        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);
        return menuBar;
    }
    
    private VBox createMapView() {
        VBox mapBox = new VBox(10);
        mapBox.setPadding(new Insets(10));
        mapBox.setStyle("-fx-background-color: white;");
        
        // Title with legend
        HBox titleBox = new HBox(20);
        titleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Ormoc City Hazard Map");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Legend
        HBox legend = createLegend();
        titleBox.getChildren().addAll(titleLabel, new Region(), legend);
        HBox.setHgrow(titleBox.getChildren().get(1), Priority.ALWAYS);
        
        // Create map pane (800x600)
        mapPane = new Pane();
        mapPane.setStyle("-fx-background-color: #E8F4F8; -fx-border-color: #333; -fx-border-width: 2;");
        mapPane.setPrefSize(800, 600);
        mapPane.setMinSize(800, 600);
        mapPane.setMaxSize(800, 600);
        
        // Add grid lines
        drawMapGrid();
        
        // Add map labels
        addMapLabels();
        
        // Mouse events for map interaction
        mapPane.setOnMouseMoved(this::handleMouseMoved);
        mapPane.setOnMouseClicked(this::handleMapClick);
        
        coordsLabel = new Label("Hover over map to see coordinates | Click to select location");
        coordsLabel.setFont(Font.font("Arial", 12));
        coordsLabel.setStyle("-fx-padding: 5; -fx-background-color: #fff3cd; -fx-border-color: #ffc107; -fx-border-width: 1;");
        
        mapBox.getChildren().addAll(titleBox, mapPane, coordsLabel);
        return mapBox;
    }
    
    private HBox createLegend() {
        HBox legend = new HBox(15);
        legend.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        String[] types = {"Flood", "Landslide", "Fire", "Earthquake", "Typhoon"};
        
        for (String type : types) {
            HBox item = new HBox(5);
            item.setAlignment(javafx.geometry.Pos.CENTER);
            
            Circle circle = new Circle(6);
            circle.setFill(getColorForType(type).darker());
            circle.setStroke(Color.WHITE);
            
            Label label = new Label(type);
            label.setFont(Font.font("Arial", 10));
            
            item.getChildren().addAll(circle, label);
            legend.getChildren().add(item);
        }
        
        return legend;
    }
    
    private void drawMapGrid() {
        // Draw vertical grid lines
        for (int i = 0; i <= 8; i++) {
            double x = i * 100;
            Rectangle line = new Rectangle(x, 0, 1, 600);
            line.setFill(Color.rgb(200, 200, 200, 0.5));
            mapPane.getChildren().add(line);
        }
        
        // Draw horizontal grid lines
        for (int i = 0; i <= 6; i++) {
            double y = i * 100;
            Rectangle line = new Rectangle(0, y, 800, 1);
            line.setFill(Color.rgb(200, 200, 200, 0.5));
            mapPane.getChildren().add(line);
        }
    }
    
    private void addMapLabels() {
        // Add "Ormoc City" label in center
        Label cityLabel = new Label("ORMOC CITY");
        cityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        cityLabel.setTextFill(Color.rgb(100, 100, 100, 0.3));
        cityLabel.setLayoutX(300);
        cityLabel.setLayoutY(270);
        mapPane.getChildren().add(cityLabel);
        
        // Add coastal indicator
        Label coastLabel = new Label("← Ormoc Bay");
        coastLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        coastLabel.setTextFill(Color.rgb(0, 100, 200));
        coastLabel.setLayoutX(50);
        coastLabel.setLayoutY(300);
        mapPane.getChildren().add(coastLabel);
        
        // Add compass rose
        Label northLabel = new Label("N");
        northLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        northLabel.setTextFill(Color.RED);
        northLabel.setLayoutX(760);
        northLabel.setLayoutY(20);
        mapPane.getChildren().add(northLabel);
    }
    
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setPrefWidth(350);
        controlPanel.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 0 0 0 2;");
        
        Label titleLabel = new Label("Hazard Zone Manager");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Hazard type selection
        Label typeLabel = new Label("Hazard Type:");
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        hazardTypeCombo = new ComboBox<>();
        hazardTypeCombo.getItems().addAll("Flood", "Landslide", "Fire", "Earthquake", "Typhoon");
        hazardTypeCombo.setValue("Flood");
        hazardTypeCombo.setPrefWidth(Double.MAX_VALUE);
        
        // Name field
        Label nameLabel = new Label("Location Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        nameField = new TextField();
        nameField.setPromptText("e.g., Downtown Area, Barangay...");
        
        // Description
        Label descLabel = new Label("Description:");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setPromptText("Enter hazard details and historical information...");
        descriptionArea.setWrapText(true);
        
        // Radius slider
        Label radiusLabel = new Label("Hazard Radius: 500m");
        radiusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        radiusSlider = new Slider(100, 2000, 500);
        radiusSlider.setShowTickLabels(true);
        radiusSlider.setShowTickMarks(true);
        radiusSlider.setMajorTickUnit(500);
        radiusSlider.setBlockIncrement(100);
        radiusSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            radiusLabel.setText(String.format("Hazard Radius: %.0fm", newVal.doubleValue()));
            updateRadiusCircle();
        });
        
        // Selected coordinates display
        Label coordsDisplayLabel = new Label("Selected Location:");
        coordsDisplayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        selectedCoordsLabel = new Label("Click map to select");
        selectedCoordsLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 11;");
        
        // Buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Add Hazard Zone");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setPrefWidth(150);
        addButton.setOnAction(e -> addHazardZone());
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        clearButton.setPrefWidth(80);
        clearButton.setOnAction(e -> clearSelection());
        
        buttonBox.getChildren().addAll(addButton, clearButton);
        
        Separator separator1 = new Separator();
        
        // Hazard list
        Label listLabel = new Label("Existing Hazard Zones:");
        listLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        hazardListView = new ListView<>();
        hazardListView.setPrefHeight(180);
        hazardListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewHazardDetails();
            }
        });
        
        HBox listButtonBox = new HBox(10);
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewButton.setPrefWidth(120);
        viewButton.setOnAction(e -> viewHazardDetails());
        
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setPrefWidth(80);
        deleteButton.setOnAction(e -> deleteSelectedHazard());
        
        listButtonBox.getChildren().addAll(viewButton, deleteButton);
        
        controlPanel.getChildren().addAll(
            titleLabel, new Separator(),
            typeLabel, hazardTypeCombo,
            nameLabel, nameField,
            descLabel, descriptionArea,
            radiusLabel, radiusSlider,
            coordsDisplayLabel, selectedCoordsLabel,
            buttonBox,
            separator1,
            listLabel, hazardListView, listButtonBox
        );
        
        return controlPanel;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #2196F3;");
        
        Label statusLabel = new Label("Ready | Ormoc City Danger Zone Mapping System v1.0 | Total Zones: 0");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        statusLabel.setId("statusLabel");
        
        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }
    
    private void handleMouseMoved(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        
        // Convert pixel coordinates to lat/lon
        double lat = pixelYToLat(y);
        double lon = pixelXToLon(x);
        
        coordsLabel.setText(String.format("Coordinates: %.6f°N, %.6f°E | Click to select location", lat, lon));
    }
    
    private void handleMapClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        
        selectedLat = pixelYToLat(y);
        selectedLon = pixelXToLon(x);
        
        // Remove previous marker
        if (selectedMarker != null) {
            mapPane.getChildren().remove(selectedMarker);
        }
        
        // Add new marker
        selectedMarker = new Circle(x, y, 8);
        selectedMarker.setFill(Color.RED);
        selectedMarker.setStroke(Color.WHITE);
        selectedMarker.setStrokeWidth(2);
        mapPane.getChildren().add(selectedMarker);
        
        updateRadiusCircle();
        
        coordsLabel.setText(String.format("✓ Selected: %.6f°N, %.6f°E | Click 'Add Hazard Zone' to save", selectedLat, selectedLon));
        selectedCoordsLabel.setText(String.format("Lat: %.6f, Lon: %.6f", selectedLat, selectedLon));
    }
    
    private void updateRadiusCircle() {
        if (selectedMarker == null) return;
        
        if (radiusCircle != null) {
            mapPane.getChildren().remove(radiusCircle);
        }
        
        double radiusMeters = radiusSlider.getValue();
        double radiusPixels = metersToPixels(radiusMeters);
        
        radiusCircle = new Circle(selectedMarker.getCenterX(), selectedMarker.getCenterY(), radiusPixels);
        
        String type = hazardTypeCombo.getValue();
        Color color = getColorForType(type);
        
        radiusCircle.setFill(color);
        radiusCircle.setStroke(color.darker());
        radiusCircle.setStrokeWidth(2);
        radiusCircle.getStrokeDashArray().addAll(5.0, 5.0);
        
        mapPane.getChildren().add(radiusCircle);
        radiusCircle.toBack();
        selectedMarker.toFront();
    }
    
    private void clearSelection() {
        if (selectedMarker != null) {
            mapPane.getChildren().remove(selectedMarker);
            selectedMarker = null;
        }
        if (radiusCircle != null) {
            mapPane.getChildren().remove(radiusCircle);
            radiusCircle = null;
        }
        nameField.clear();
        descriptionArea.clear();
        selectedLat = 0;
        selectedLon = 0;
        selectedCoordsLabel.setText("Click map to select");
        coordsLabel.setText("Hover over map to see coordinates | Click to select location");
    }
    
    private void addHazardZone() {
        if (selectedLat == 0 || selectedLon == 0) {
            showAlert("Error", "Please select a location on the map first!");
            return;
        }
        
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Error", "Please enter a location name!");
            return;
        }
        
        String type = hazardTypeCombo.getValue();
        String description = descriptionArea.getText().trim();
        double radius = radiusSlider.getValue();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO hazard_zones (name, type, latitude, longitude, radius, description, date_added) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setDouble(3, selectedLat);
            pstmt.setDouble(4, selectedLon);
            pstmt.setDouble(5, radius);
            pstmt.setString(6, description);
            pstmt.setDate(7, Date.valueOf(LocalDate.now()));
            
            pstmt.executeUpdate();
            
            showAlert("Success", "Hazard zone '" + name + "' added successfully!");
            clearSelection();
            loadHazardZones();
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add hazard zone: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void viewHazardDetails() {
        String selected = hazardListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a hazard zone to view!");
            return;
        }
        
        int id = Integer.parseInt(selected.split(":")[0].replace("#", ""));
        
        for (HazardZone zone : hazardZones) {
            if (zone.id == id) {
                String details = String.format(
                    "Hazard Zone Details\n\n" +
                    "ID: #%d\n" +
                    "Name: %s\n" +
                    "Type: %s\n" +
                    "Latitude: %.6f\n" +
                    "Longitude: %.6f\n" +
                    "Radius: %.0fm\n" +
                    "Description: %s\n" +
                    "Date Added: %s",
                    zone.id, zone.name, zone.type, zone.latitude, zone.longitude,
                    zone.radius, zone.description, zone.dateAdded
                );
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hazard Zone Details");
                alert.setHeaderText(zone.name);
                alert.setContentText(details);
                alert.getDialogPane().setPrefWidth(400);
                alert.showAndWait();
                break;
            }
        }
    }
    
    private void deleteSelectedHazard() {
        String selected = hazardListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a hazard zone to delete!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Hazard Zone");
        confirm.setContentText("Are you sure you want to delete this hazard zone?");
        
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }
        
        int id = Integer.parseInt(selected.split(":")[0].replace("#", ""));
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM hazard_zones WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
            showAlert("Success", "Hazard zone deleted successfully!");
            loadHazardZones();
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to delete hazard zone: " + e.getMessage());
        }
    }
    
    private void loadHazardZones() {
        hazardZones.clear();
        ObservableList<String> displayList = FXCollections.observableArrayList();
        
        // Clear existing markers (except selected marker and radius circle)
        mapPane.getChildren().removeIf(node -> 
            node instanceof Circle && node != selectedMarker && node != radiusCircle &&
            !(node.getLayoutX() > 0 || node.getLayoutY() > 0)
        );
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM hazard_zones ORDER BY date_added DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                HazardZone zone = new HazardZone(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getDouble("radius"),
                    rs.getString("description"),
                    rs.getDate("date_added").toString()
                );
                
                hazardZones.add(zone);
                displayList.add(String.format("#%d: %s - %s", zone.id, zone.name, zone.type));
                
                // Draw on map
                drawHazardZone(zone);
            }
            
            hazardListView.setItems(displayList);
            
            // Update status bar
            updateStatusBar();
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load hazard zones: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void drawHazardZone(HazardZone zone) {
        double x = lonToPixelX(zone.longitude);
        double y = latToPixelY(zone.latitude);
        
        // Draw radius circle
        double radiusPixels = metersToPixels(zone.radius);
        Circle circle = new Circle(x, y, radiusPixels);
        
        Color fillColor = getColorForType(zone.type);
        circle.setFill(fillColor);
        circle.setStroke(fillColor.darker());
        circle.setStrokeWidth(2);
        
        // Add tooltip
        Tooltip tooltip = new Tooltip(
            zone.name + "\n" + 
            zone.type + "\n" +
            "Radius: " + zone.radius + "m\n" +
            "Added: " + zone.dateAdded
        );
        Tooltip.install(circle, tooltip);
        
        mapPane.getChildren().add(circle);
        circle.toBack();
        
        // Draw marker
        Circle marker = new Circle(x, y, 6);
        marker.setFill(fillColor.darker());
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(2);
        Tooltip.install(marker, tooltip);
        mapPane.getChildren().add(marker);
    }
    
    private Color getColorForType(String type) {
        switch (type.toLowerCase()) {
            case "flood": return Color.rgb(0, 150, 255, 0.3);
            case "landslide": return Color.rgb(139, 69, 19, 0.3);
            case "fire": return Color.rgb(255, 0, 0, 0.3);
            case "earthquake": return Color.rgb(255, 165, 0, 0.3);
            case "typhoon": return Color.rgb(128, 0, 128, 0.3);
            default: return Color.rgb(128, 128, 128, 0.3);
        }
    }
    
    // Coordinate conversion methods
    private double pixelXToLon(double x) {
        return ORMOC_LON_MIN + (x / 800.0) * (ORMOC_LON_MAX - ORMOC_LON_MIN);
    }
    
    private double pixelYToLat(double y) {
        return ORMOC_LAT_MAX - (y / 600.0) * (ORMOC_LAT_MAX - ORMOC_LAT_MIN);
    }
    
    private double lonToPixelX(double lon) {
        return ((lon - ORMOC_LON_MIN) / (ORMOC_LON_MAX - ORMOC_LON_MIN)) * 800.0;
    }
    
    private double latToPixelY(double lat) {
        return ((ORMOC_LAT_MAX - lat) / (ORMOC_LAT_MAX - ORMOC_LAT_MIN)) * 600.0;
    }
    
    private double metersToPixels(double meters) {
        // Approximate conversion (1 degree ≈ 111km at this latitude)
        double degreesLat = meters / 111000.0;
        return (degreesLat / (ORMOC_LAT_MAX - ORMOC_LAT_MIN)) * 600.0;
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String createTable = "CREATE TABLE IF NOT EXISTS hazard_zones (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "type VARCHAR(50) NOT NULL," +
                "latitude DOUBLE NOT NULL," +
                "longitude DOUBLE NOT NULL," +
                "radius DOUBLE NOT NULL," +
                "description TEXT," +
                "date_added DATE NOT NULL" +
                ")";
            Statement stmt = conn.createStatement();
            stmt.execute(createTable);
            
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Failed to connect to database");
            alert.setContentText(
                "Please ensure:\n" +
                "1. MySQL/XAMPP is running\n" +
                "2. Database 'danger_zone_db' exists\n" +
                "3. Username and password are correct\n\n" +
                "Error: " + e.getMessage()
            );
            alert.showAndWait();
        }
    }
    
    private void updateStatusBar() {
        Label statusLabel = (Label) ((HBox) ((BorderPane) 
            hazardListView.getScene().getRoot()).getBottom()).getChildren().get(0);
        statusLabel.setText(String.format(
            "Ready | Ormoc City Danger Zone Mapping System v1.0 | Total Zones: %d",
            hazardZones.size()
        ));
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Danger Zone Mapping System");
        alert.setContentText(
            "Version: 1.0\n" +
            "Location: Ormoc City, Philippines\n\n" +
            "This system helps identify and visualize areas\n" +
            "vulnerable to natural hazards such as floods,\n" +
            "landslides, fires, earthquakes, and typhoons.\n\n" +
            "Developed for disaster preparedness and planning."
        );
        alert.showAndWait();
    }
    
    private void showSafetyGuidelines() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Safety Guidelines");
        alert.setHeaderText("Emergency Preparedness");
        alert.setContentText(
            "FLOOD SAFETY:\n" +
            "• Move to higher ground immediately\n" +
            "• Avoid walking through floodwater\n\n" +
            "LANDSLIDE SAFETY:\n" +
            "• Evacuate if you hear rumbling sounds\n" +
            "• Stay away from steep slopes\n\n" +
            "FIRE SAFETY:\n" +
            "• Call emergency services: 911\n" +
            "• Evacuate and stay low\n\n" +
            "EMERGENCY HOTLINES:\n" +
            "• NDRRMC: 911\n" +
            "• Red Cross: 143"
        );
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    // Inner class for Hazard Zone data
    static class HazardZone {
        int id;
        String name;
        String type;
        double latitude;
        double longitude;
        double radius;
        String description;
        String dateAdded;
        
        HazardZone(int id, String name, String type, double latitude, double longitude, 
                   double radius, String description, String dateAdded) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
            this.description = description;
            this.dateAdded = dateAdded;
        }
    }
}