package com.dangerzone;

import com.dangerzone.models.*;
import com.dangerzone.views.*;
import com.dangerzone.utils.DataExporter;
import com.dangerzone.utils.StyleManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.List;

public class Main extends Application {
    
    private BorderPane root;
    private TabPane mapTabPane;
    private ImageView mapImageView;
    private Pane mapOverlayPane;
    private ScrollPane mapScrollPane;
    private HBox legendBox;
    private Label mouseCoordinatesLabel;
    
    // Map bounds for Ormoc City (adjust based on your actual map)
    private static final double MAP_MIN_LAT = 10.85;  // Bottom of your map
    private static final double MAP_MAX_LAT = 11.20;  // Top of your map
    private static final double MAP_MIN_LNG = 124.45; // Left edge
    private static final double MAP_MAX_LNG = 124.80; // Right edge

    private static final double MAP_WIDTH = 1920;
    private static final double MAP_HEIGHT = 988;
    
@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Danger Zone Mapping System - Ormoc City");
        
        root = new BorderPane();
        root.setPadding(new Insets(0)); // Remove padding to maximize space
        
        // Apply modern theme to root
        StyleManager.applyModernTheme(root);
        
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        
        mapTabPane = createMainContent();
        root.setCenter(mapTabPane);
        
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        Scene scene = new Scene(root, 1400, 900); // Larger default size
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Start maximized
        primaryStage.show();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        StyleManager.styleMenuBar(menuBar);
        
        Menu fileMenu = new Menu("File");
        MenuItem exportLandmarks = new MenuItem("Export Landmarks to CSV");
        MenuItem exportHazards = new MenuItem("Export Hazard Zones to CSV");
        MenuItem exportIncidents = new MenuItem("Export Incidents to CSV");
        MenuItem exit = new MenuItem("Exit");

        exportLandmarks.setOnAction(e -> exportData("landmarks"));
        exportHazards.setOnAction(e -> exportData("hazards"));
        exportIncidents.setOnAction(e -> exportData("incidents"));
        exit.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(exportLandmarks, exportHazards, exportIncidents, 
                                   new SeparatorMenuItem(), exit);
        
        Menu viewMenu = new Menu("View");
        MenuItem globalSearch = new MenuItem("🔍 Global Search");
        MenuItem refreshMap = new MenuItem("🔄 Refresh Map");
        globalSearch.setOnAction(e -> openGlobalSearch());
        refreshMap.setOnAction(e -> refreshMapMarkers());
        viewMenu.getItems().addAll(globalSearch, refreshMap);
        
        Menu adminMenu = new Menu("Admin");
        MenuItem manageLandmarks = new MenuItem("📍 Manage Landmarks");
        MenuItem manageHazards = new MenuItem("⚠ Manage Hazard Zones");
        MenuItem manageIncidents = new MenuItem("📋 Manage Historical Incidents");
        
        manageLandmarks.setOnAction(e -> openLandmarkManager());
        manageHazards.setOnAction(e -> openHazardManager());
        manageIncidents.setOnAction(e -> openIncidentManager());
        
        adminMenu.getItems().addAll(manageLandmarks, manageHazards, manageIncidents);
        
        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(about);
        
        menuBar.getMenus().addAll(fileMenu, viewMenu, adminMenu, helpMenu);
        return menuBar;
    }
    
    private TabPane createMainContent() {
        TabPane tabPane = new TabPane();
        StyleManager.styleTabPane(tabPane);
        
        Tab dashboardTab = new Tab("📊 Dashboard");
        dashboardTab.setClosable(false);
        try {
            Connection conn = DatabaseConnection.getConnection();
            dashboardTab.setContent(new DashboardPanel(conn));
        } catch (SQLException e) {
            dashboardTab.setContent(new Label("Failed to load dashboard: " + e.getMessage()));
        }
        
        Tab mapTab = new Tab("🗺 Interactive Map");
        mapTab.setClosable(false);
        mapTab.setContent(createMapView());
        
        Tab historyTab = new Tab("📜 Historical Data");
        historyTab.setClosable(false);
        historyTab.setContent(createHistoricalView());
        
        Tab safetyTab = new Tab("🚨 Safety Guidelines");
        safetyTab.setClosable(false);
        safetyTab.setContent(createSafetyView());
        
        tabPane.getTabs().addAll(dashboardTab, mapTab, historyTab, safetyTab);
        return tabPane;
    }
    
private VBox createMapView() {
        VBox container = new VBox(3); // ULTRA-COMPACT: Reduced from 10 to 3
        container.setPadding(new Insets(5)); // ULTRA-COMPACT: Reduced from 10 to 5
        container.setStyle("-fx-background-color: " + StyleManager.LIGHT_BG + ";");

        // SINGLE LINE: Title + Controls + Mouse Coords
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(5, 10, 5, 10)); // Minimal padding
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-background-radius: 6; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 3, 0, 0, 1);");
        
        Label titleLabel = new Label("🗺 Ormoc Hazard Map");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + 
                           StyleManager.PRIMARY_COLOR + ";");
        
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        CheckBox showLandmarks = new CheckBox("Landmarks");
        showLandmarks.setSelected(true);
        showLandmarks.setStyle("-fx-font-size: 11px;");
        
        CheckBox showHazards = new CheckBox("Hazards");
        showHazards.setSelected(true);
        showHazards.setStyle("-fx-font-size: 11px;");
        
        Button refreshBtn = new Button("🔄");
        refreshBtn.setTooltip(new Tooltip("Refresh Map"));
        refreshBtn.setStyle("-fx-background-color: " + StyleManager.SUCCESS_COLOR + "; " +
                           "-fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-background-radius: 4; -fx-padding: 4 8; -fx-font-size: 11px;");
        refreshBtn.setOnAction(e -> refreshMapMarkers());
        
        showLandmarks.setOnAction(e -> loadMarkersFromDatabase(showLandmarks.isSelected(), showHazards.isSelected()));
        showHazards.setOnAction(e -> loadMarkersFromDatabase(showLandmarks.isSelected(), showHazards.isSelected()));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        mouseCoordinatesLabel = new Label("Hover over map");
        mouseCoordinatesLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";");
        
        topBar.getChildren().addAll(titleLabel, sep1, showLandmarks, showHazards, refreshBtn, 
                                    spacer, mouseCoordinatesLabel);
        
        container.getChildren().add(topBar);
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            MapDAO mapDAO = new MapDAO(conn);
            
            com.dangerzone.models.Map defaultMap = mapDAO.getDefaultMap();
            if (defaultMap != null) {
                loadMapWithOverlays(defaultMap, container, showLandmarks, showHazards);
            } else {
                showNoMapMessage(container);
            }
            
        } catch (SQLException e) {
            showErrorMessage(container, "Failed to load map: " + e.getMessage());
        }
        
        // COMPACT LEGEND - Single line at bottom
        legendBox = createUltraCompactLegend();
        container.getChildren().add(legendBox);
        
        return container;
    }
    
    private HBox createUltraCompactLegend() {
        HBox legend = new HBox(10); // Reduced spacing
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(5, 10, 5, 10)); // Minimal padding
        legend.setStyle("-fx-background-color: white; " +
                       "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                       "-fx-border-width: 1; -fx-border-radius: 6; " +
                       "-fx-background-radius: 6; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 3, 0, 0, 1);");

        Label legendTitle = new Label("LEGEND:");
        legendTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; " +
                            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";");
        
        legend.getChildren().addAll(
            legendTitle,
            createMiniLegendItem("📍", StyleManager.ACCENT_COLOR),
            createMiniLegendItem("🏥", StyleManager.SUCCESS_COLOR),
            createMiniLegendItem("🔴", StyleManager.DANGER_COLOR),
            createMiniLegendItem("🟠", StyleManager.WARNING_COLOR),
            createMiniLegendItem("🟡", "#f1c40f"),
            createMiniLegendItem("⚪", "#95a5a6")
        );
        
        return legend;
    }    

    private HBox createMiniLegendItem(String icon, String color) {
        HBox item = new HBox(3); // Minimal spacing
        item.setAlignment(Pos.CENTER);
        
        Region colorBox = new Region();
        colorBox.setPrefSize(12, 12); // Smaller boxes
        colorBox.setStyle("-fx-background-color: " + color + "; " +
                         "-fx-border-color: white; " +
                         "-fx-border-width: 1.5; -fx-border-radius: 2; " +
                         "-fx-background-radius: 2;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 10px;"); // Smaller font
        
        item.getChildren().addAll(colorBox, iconLabel);
        return item;
    }

    private void loadMapWithOverlays(com.dangerzone.models.Map map, VBox container, 
                                     CheckBox showLandmarks, CheckBox showHazards) {
        try {
            InputStream mapStream = getClass().getResourceAsStream(map.getFilePath());
            if (mapStream != null) {
                Image mapImage = new Image(mapStream);
                mapImageView = new ImageView(mapImage);
                mapImageView.setPreserveRatio(true);
                
                // FULL SIZE MAP
                mapImageView.setFitWidth(MAP_WIDTH);
                mapImageView.setFitHeight(MAP_HEIGHT);
                
                // Create overlay pane for markers - FULL SIZE
                mapOverlayPane = new Pane();
                mapOverlayPane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
                mapOverlayPane.setMaxSize(MAP_WIDTH, MAP_HEIGHT);
                mapOverlayPane.setMouseTransparent(false);
                
                // Add mouse move listener for coordinates
                mapOverlayPane.setOnMouseMoved(event -> {
                    double x = event.getX();
                    double y = event.getY();
                    double[] latLng = pixelToLatLng(x, y);
                    if (latLng != null) {
                        mouseCoordinatesLabel.setText(String.format("%.6f°N, %.6f°E", latLng[0], latLng[1]));
                    }
                });
                
                mapOverlayPane.setOnMouseExited(event -> {
                    mouseCoordinatesLabel.setText("Hover over map");
                });
                
                // Stack map and overlay
                StackPane mapStack = new StackPane();
                mapStack.getChildren().addAll(mapImageView, mapOverlayPane);
                mapStack.setStyle("-fx-background-color: #f5f5f5;");
                
                // SCROLLPANE WITH PROPER SIZE
                mapScrollPane = new ScrollPane(mapStack);
                mapScrollPane.setFitToWidth(false);
                mapScrollPane.setFitToHeight(false);
                mapScrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f5f5f5;");
                mapScrollPane.setPannable(true);
                
                // Give map MAXIMUM vertical space
                VBox.setVgrow(mapScrollPane, Priority.ALWAYS);
                
                container.getChildren().add(1, mapScrollPane); // Insert after top bar
                
                // Load markers from database
                loadMarkersFromDatabase(showLandmarks.isSelected(), showHazards.isSelected());
                
                System.out.println("✅ Map loaded at FULL SIZE: " + map.getMapName());
                
            } else {
                showErrorMessage(container, "⚠️ Map not found: " + map.getFilePath());
            }
        } catch (Exception e) {
            showErrorMessage(container, "⚠️ Failed to load map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMarkersFromDatabase(boolean showLandmarks, boolean showHazards) {
        if (mapOverlayPane == null) return;
        
        mapOverlayPane.getChildren().clear();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Load and display landmarks
            if (showLandmarks) {
                LandmarkDAO landmarkDAO = new LandmarkDAO(conn);
                List<Landmark> landmarks = landmarkDAO.getAllLandmarks();
                
                for (Landmark landmark : landmarks) {
                    addLandmarkMarker(landmark);
                }
                System.out.println("📍 Loaded " + landmarks.size() + " landmarks");
            }
            
            // Load and display hazard zones
            if (showHazards) {
                HazardZoneDAO hazardDAO = new HazardZoneDAO(conn);
                List<HazardZone> hazards = hazardDAO.getActiveHazardZones();
                
                for (HazardZone hazard : hazards) {
                    addHazardMarker(hazard);
                }
                System.out.println("⚠️ Loaded " + hazards.size() + " hazard zones");
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading markers: " + e.getMessage());
        }
    }
    
    private void addLandmarkMarker(Landmark landmark) {
        double[] xy = latLngToPixel(landmark.getLatitude(), landmark.getLongitude());
        
        if (xy == null) return; // Outside map bounds
        
        // Create marker
        Circle marker = new Circle(8);
        marker.setCenterX(xy[0]);
        marker.setCenterY(xy[1]);
        
        // Color based on type
        if (landmark.isEvacuationSite()) {
            marker.setFill(Color.web(StyleManager.SUCCESS_COLOR)); // Green for evacuation
            marker.setRadius(10);
        } else {
            marker.setFill(Color.web(StyleManager.ACCENT_COLOR)); // Blue for landmark
        }
        
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(2);
        
        // Tooltip
        Tooltip tooltip = new Tooltip(
            landmark.getName() + "\n" +
            landmark.getType() + "\n" +
            landmark.getBarangay() +
            (landmark.isEvacuationSite() ? "\n🏥 EVACUATION CENTER" : "")
        );
        Tooltip.install(marker, tooltip);
        
        // Click to show details
        marker.setOnMouseClicked(e -> showLandmarkDetails(landmark));
        marker.setStyle("-fx-cursor: hand;");
        
        mapOverlayPane.getChildren().add(marker);
    }
    
    private void addHazardMarker(HazardZone hazard) {
        double[] xy = latLngToPixel(hazard.getLatitude(), hazard.getLongitude());
        
        if (xy == null) return;
        
        // Convert radius from meters to pixels
        double radiusPixels = metersToPixels(hazard.getRadiusMeters());
        
        // Create circle for hazard zone
        Circle hazardCircle = new Circle(radiusPixels);
        hazardCircle.setCenterX(xy[0]);
        hazardCircle.setCenterY(xy[1]);
        
        // Color based on severity
        Color fillColor;
        switch (hazard.getSeverityLevel().toLowerCase()) {
            case "critical":
                fillColor = Color.web(StyleManager.DANGER_COLOR, 0.4);
                break;
            case "high":
                fillColor = Color.web(StyleManager.WARNING_COLOR, 0.35);
                break;
            case "medium":
                fillColor = Color.web("#f1c40f", 0.3);
                break;
            default:
                fillColor = Color.web("#95a5a6", 0.25);
        }
        
        hazardCircle.setFill(fillColor);
        hazardCircle.setStroke(fillColor.darker());
        hazardCircle.setStrokeWidth(2);
        
        // Tooltip
        Tooltip tooltip = new Tooltip(
            hazard.getZoneName() + "\n" +
            hazard.getHazardType() + " - " + hazard.getSeverityLevel() + "\n" +
            hazard.getBarangay() + "\n" +
            "Radius: " + hazard.getRadiusMeters() + "m\n" +
            "Population: " + hazard.getAffectedPopulation()
        );
        Tooltip.install(hazardCircle, tooltip);
        
        hazardCircle.setOnMouseClicked(e -> showHazardDetails(hazard));
        hazardCircle.setStyle("-fx-cursor: hand;");
        
        // Center marker
        Circle centerMarker = new Circle(5);
        centerMarker.setCenterX(xy[0]);
        centerMarker.setCenterY(xy[1]);
        centerMarker.setFill(fillColor.darker());
        centerMarker.setStroke(Color.WHITE);
        centerMarker.setStrokeWidth(2);
        
        mapOverlayPane.getChildren().addAll(hazardCircle, centerMarker);
    }
    
    private double[] latLngToPixel(double lat, double lng) {
        // Check if coordinates are within map bounds
        if (lat < MAP_MIN_LAT || lat > MAP_MAX_LAT || 
            lng < MAP_MIN_LNG || lng > MAP_MAX_LNG) {
            return null;
        }
        
        // Convert lat/lng to pixel coordinates
        double x = ((lng - MAP_MIN_LNG) / (MAP_MAX_LNG - MAP_MIN_LNG)) * MAP_WIDTH;
        double y = ((MAP_MAX_LAT - lat) / (MAP_MAX_LAT - MAP_MIN_LAT)) * MAP_HEIGHT;
        
        return new double[]{x, y};
    }
    
    private double[] pixelToLatLng(double x, double y) {
        // Convert pixel coordinates back to lat/lng
        double lng = MAP_MIN_LNG + (x / MAP_WIDTH) * (MAP_MAX_LNG - MAP_MIN_LNG);
        double lat = MAP_MAX_LAT - (y / MAP_HEIGHT) * (MAP_MAX_LAT - MAP_MIN_LAT);
        
        // Validate bounds
        if (lat < MAP_MIN_LAT || lat > MAP_MAX_LAT || 
            lng < MAP_MIN_LNG || lng > MAP_MAX_LNG) {
            return null;
        }
        
        return new double[]{lat, lng};
    }
    
    private double metersToPixels(int meters) {
        // Approximate conversion (1 degree ≈ 111km)
        double degreesLat = meters / 111000.0;
        double pixelsPerDegree = MAP_HEIGHT / (MAP_MAX_LAT - MAP_MIN_LAT);
        return degreesLat * pixelsPerDegree;
    }
    
    private void refreshMapMarkers() {
        Tab currentTab = mapTabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null && currentTab.getText().contains("Map")) {
            VBox mapContainer = (VBox) currentTab.getContent();
            HBox topBar = (HBox) mapContainer.getChildren().get(0);
            
            // Find checkboxes
            CheckBox showLandmarks = null;
            CheckBox showHazards = null;
            
            for (javafx.scene.Node node : topBar.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox cb = (CheckBox) node;
                    if (cb.getText().contains("Landmarks")) {
                        showLandmarks = cb;
                    } else if (cb.getText().contains("Hazards")) {
                        showHazards = cb;
                    }
                }
            }
            
            if (showLandmarks != null && showHazards != null) {
                loadMarkersFromDatabase(showLandmarks.isSelected(), showHazards.isSelected());
                showSuccessAlert("Map refreshed!");
            }
        }
    }
    
    private void showLandmarkDetails(Landmark landmark) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Landmark Details");
        alert.setHeaderText(landmark.getName());
        
        StyleManager.styleDialog(alert.getDialogPane());
        
        StringBuilder content = new StringBuilder();
        content.append("Type: ").append(landmark.getType()).append("\n");
        content.append("Barangay: ").append(landmark.getBarangay()).append("\n");
        content.append("Coordinates: ").append(landmark.getCoordinates()).append("\n");
        if (landmark.getContactNumber() != null) {
            content.append("Contact: ").append(landmark.getContactNumber()).append("\n");
        }
        if (landmark.isEvacuationSite()) {
            content.append("\n🏥 EVACUATION CENTER\n");
            content.append("Capacity: ").append(landmark.getCapacity()).append(" persons\n");
        }
        if (landmark.getDescription() != null) {
            content.append("\n").append(landmark.getDescription());
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    private void showHazardDetails(HazardZone hazard) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Hazard Zone Details");
        alert.setHeaderText(hazard.getZoneName());
        
        StyleManager.styleDialog(alert.getDialogPane());
        
        StringBuilder content = new StringBuilder();
        content.append("Type: ").append(hazard.getHazardType()).append("\n");
        content.append("Severity: ").append(hazard.getSeverityLevel()).append("\n");
        content.append("Barangay: ").append(hazard.getBarangay()).append("\n");
        content.append("Coordinates: ").append(hazard.getCoordinates()).append("\n");
        content.append("Radius: ").append(hazard.getRadiusMeters()).append(" meters\n");
        content.append("Affected Population: ").append(hazard.getAffectedPopulation()).append("\n");
        if (hazard.getDescription() != null) {
            content.append("\n").append(hazard.getDescription());
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    private void showNoMapMessage(VBox container) {
        VBox messageBox = new VBox(15);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(50));
        StyleManager.styleCard(messageBox);
        
        Label icon = new Label("🗺️");
        icon.setStyle("-fx-font-size: 48;");
        
        Label message = new Label("No map available");
        StyleManager.styleSubtitleLabel(message);
        
        messageBox.getChildren().addAll(icon, message);
        VBox.setVgrow(messageBox, Priority.ALWAYS);
        container.getChildren().add(messageBox);
    }
    
    private void showErrorMessage(VBox container, String message) {
        VBox errorBox = new VBox(10);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(30));
        errorBox.setStyle("-fx-background-color: #fee; -fx-border-color: " + 
                         StyleManager.DANGER_COLOR + "; -fx-border-radius: 8; " +
                         "-fx-background-radius: 8;");
        
        Label errorLabel = new Label(message);
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-text-fill: " + StyleManager.DANGER_COLOR + ";");
        errorBox.getChildren().add(errorLabel);
        VBox.setVgrow(errorBox, Priority.ALWAYS);
        container.getChildren().add(errorBox);
    }
    
    private VBox createHistoricalView() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            return new HistoricalDataView(conn);
        } catch (SQLException e) {
            VBox errorBox = new VBox(10);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(30));
            Label errorLabel = new Label("Failed to load historical data: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: " + StyleManager.DANGER_COLOR + ";");
            errorBox.getChildren().add(errorLabel);
            return errorBox;
        }
    }
    
    private VBox createSafetyView() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            return new SafetyGuidelinesView(conn);
        } catch (SQLException e) {
            VBox errorBox = new VBox(10);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(30));
            Label errorLabel = new Label("Failed to load safety guidelines: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: " + StyleManager.DANGER_COLOR + ";");
            errorBox.getChildren().add(errorLabel);
            return errorBox;
        }
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(4, 15, 4, 15)); // Ultra-compact
        statusBar.setStyle("-fx-background-color: " + StyleManager.PRIMARY_COLOR + ";");
        
        Label status = new Label("✅ Ready - Danger Zone Mapping v1.0");
        status.setStyle("-fx-text-fill: white; -fx-font-size: 10px;"); // Smaller
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label location = new Label("📍 Ormoc City, Leyte");
        location.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        
        statusBar.getChildren().addAll(status, spacer, location);
        return statusBar;
    }
    
    private void exportData(String type) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Stage stage = (Stage) root.getScene().getWindow();
            
            switch (type) {
                case "landmarks": DataExporter.exportLandmarks(stage, conn); break;
                case "hazards": DataExporter.exportHazardZones(stage, conn); break;
                case "incidents": DataExporter.exportIncidents(stage, conn); break;
            }
            showSuccessAlert("Export successful!");
        } catch (SQLException ex) {
            showErrorAlert("Export failed: " + ex.getMessage());
        }
    }
    
    private void openGlobalSearch() {
        try {
            new SearchDialog(DatabaseConnection.getConnection()).show();
        } catch (SQLException ex) {
            showErrorAlert("Failed to open search: " + ex.getMessage());
        }
    }
    
    private void openLandmarkManager() {
        try {
            new LandmarkManagerDialog(DatabaseConnection.getConnection()).show();
        } catch (SQLException ex) {
            showErrorAlert("Failed to connect: " + ex.getMessage());
        }
    }
    
    private void openHazardManager() {
        try {
            new HazardZoneManagerDialog(DatabaseConnection.getConnection()).show();
        } catch (SQLException ex) {
            showErrorAlert("Failed to connect: " + ex.getMessage());
        }
    }
    
    private void openIncidentManager() {
        try {
            new IncidentManagerDialog(DatabaseConnection.getConnection()).show();
        } catch (SQLException ex) {
            showErrorAlert("Failed to connect: " + ex.getMessage());
        }
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Danger Zone Mapping System");
        alert.setContentText(
            "Version 1.0 - Ormoc City\n\n" +
            "Features:\n" +
            "• Interactive map with database markers\n" +
            "• Landmarks and hazard zones visualization\n" +
            "• Click markers for details\n" +
            "• Historical disaster tracking\n" +
            "• Safety guidelines"
        );
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}