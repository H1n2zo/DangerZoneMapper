package com.dangerzone;

import com.dangerzone.models.DatabaseConnection;
import com.dangerzone.views.LandmarkManagerDialog;
import com.dangerzone.views.HazardZoneManagerDialog;
import com.dangerzone.views.IncidentManagerDialog;
import com.dangerzone.models.IncidentDAO;
import com.dangerzone.models.Incident;
import java.util.List;
import com.dangerzone.utils.DataExporter;
import com.dangerzone.utils.MapDataLoader;
import com.dangerzone.views.DashboardPanel;
import com.dangerzone.views.SearchDialog;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class Main extends Application {
    
    private BorderPane root;
    private TabPane mapTabPane;
    private WebView mapWebView;
    private WebEngine webEngine;
    private MapDataLoader mapDataLoader;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Danger Zone Mapping System - Ormoc City");
        
        root = new BorderPane();
        root.setPadding(new Insets(10));
        
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        
        mapTabPane = createMainContent();
        root.setCenter(mapTabPane);
        
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File Menu
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
        
        // View Menu (Simplified)
        Menu viewMenu = new Menu("View");
        MenuItem globalSearch = new MenuItem("🔍 Global Search");
        globalSearch.setOnAction(e -> openGlobalSearch());
        viewMenu.getItems().add(globalSearch);
        
        // Admin Menu
        Menu adminMenu = new Menu("Admin");
        MenuItem manageLandmarks = new MenuItem("Manage Landmarks");
        MenuItem manageHazards = new MenuItem("Manage Hazard Zones");
        MenuItem manageIncidents = new MenuItem("Manage Historical Incidents");
        
        manageLandmarks.setOnAction(e -> openLandmarkManager());
        manageHazards.setOnAction(e -> openHazardManager());
        manageIncidents.setOnAction(e -> openIncidentManager());
        
        adminMenu.getItems().addAll(manageLandmarks, manageHazards, manageIncidents);
        
        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(about);
        
        menuBar.getMenus().addAll(fileMenu, viewMenu, adminMenu, helpMenu);
        return menuBar;
    }
    
    private TabPane createMainContent() {
        TabPane tabPane = new TabPane();
        
        // Dashboard Tab
        Tab dashboardTab = new Tab("📊 Dashboard");
        dashboardTab.setClosable(false);
        try {
            Connection conn = DatabaseConnection.getConnection();
            dashboardTab.setContent(new DashboardPanel(conn));
        } catch (SQLException e) {
            dashboardTab.setContent(new Label("Failed to load dashboard: " + e.getMessage()));
        }
        
        // Map Tab
        Tab mapTab = new Tab("🗺 Map View");
        mapTab.setClosable(false);
        mapTab.setContent(createMapView());
        
        // Historical Data Tab
        Tab historyTab = new Tab("📜 Historical Data");
        historyTab.setClosable(false);
        historyTab.setContent(createHistoricalView());
        
        // Safety Guidelines Tab
        Tab safetyTab = new Tab("🚨 Safety Guidelines");
        safetyTab.setClosable(false);
        safetyTab.setContent(createSafetyView());
        
        tabPane.getTabs().addAll(dashboardTab, mapTab, historyTab, safetyTab);
        return tabPane;
    }
    
    private VBox createMapView() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        // Controls
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPrefWidth(250);
        searchField.setPromptText("Search landmark or barangay...");

        Button searchBtn = new Button("Search");
        Button resetBtn = new Button("Reset View");
        
        searchBtn.setOnAction(e -> {
            if (!searchField.getText().trim().isEmpty()) {
                searchLocationInDB(searchField.getText().trim());
            }
        });
        
        resetBtn.setOnAction(e -> {
            resetMapView();
            searchField.clear();
        });

        controls.getChildren().addAll(
            new Label("Search:"), searchField, searchBtn, resetBtn
        );

        // WebView for Map
        mapWebView = new WebView();
        webEngine = mapWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.loadContent(getLeafletMapHTML());
        
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                loadMapData();
            }
        });
        
        VBox.setVgrow(mapWebView, Priority.ALWAYS);

        // Map Controls
        HBox mapButtons = new HBox(10);
        mapButtons.setAlignment(Pos.CENTER);
        mapButtons.setPadding(new Insets(5));

        Button showAllBtn = new Button("Show All");
        Button landmarksOnlyBtn = new Button("Landmarks Only");
        Button hazardsOnlyBtn = new Button("Hazard Zones Only");
        Button refreshBtn = new Button("Refresh Map");
        
        showAllBtn.setOnAction(e -> executeMapScript("showAllLayers();"));
        landmarksOnlyBtn.setOnAction(e -> executeMapScript("showLandmarksOnly();"));
        hazardsOnlyBtn.setOnAction(e -> executeMapScript("showHazardsOnly();"));
        refreshBtn.setOnAction(e -> webEngine.reload());

        mapButtons.getChildren().addAll(showAllBtn, landmarksOnlyBtn, hazardsOnlyBtn, refreshBtn);

        // Legend
        HBox legend = createLegend();

        container.getChildren().addAll(controls, mapWebView, mapButtons, legend);
        return container;
    }
    
    private HBox createLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(5));
        legend.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7;");

        legend.getChildren().addAll(
            createLegendItem("🔵 Landmark", "#3498db"),
            createLegendItem("🔺 Evacuation Center", "#27ae60"),
            createLegendItem("🔴 Critical", "#e74c3c"),
            createLegendItem("🟠 High", "#f39c12"),
            createLegendItem("🟡 Medium", "#f1c40f"),
            createLegendItem("⚪ Low", "#95a5a6")
        );
        
        return legend;
    }
    
    private HBox createLegendItem(String label, String color) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);
        
        Region colorBox = new Region();
        colorBox.setPrefSize(20, 20);
        colorBox.setStyle("-fx-background-color: " + color + "; -fx-border-color: #2c3e50;");
        
        item.getChildren().addAll(colorBox, new Label(label));
        return item;
    }
    
    private String getLeafletMapHTML() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { width: 100%; height: 100vh; }
                .popup-title { font-weight: bold; font-size: 14px; }
                .popup-info { font-size: 12px; margin: 2px 0; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([11.0059, 124.6075], 13);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '© OpenStreetMap',
                    maxZoom: 18
                }).addTo(map);

                var landmarkLayer = L.layerGroup().addTo(map);
                var floodLayer = L.layerGroup().addTo(map);
                var fireLayer = L.layerGroup().addTo(map);
                var landslideLayer = L.layerGroup().addTo(map);
                var stormSurgeLayer = L.layerGroup().addTo(map);

                var blueIcon = L.icon({
                    iconUrl: 'data:image/svg+xml,%3Csvg width="24" height="24" xmlns="http://www.w3.org/2000/svg"%3E%3Ccircle cx="12" cy="12" r="8" fill="%233498db" stroke="white" stroke-width="2"/%3E%3C/svg%3E',
                    iconSize: [24, 24],
                    iconAnchor: [12, 12]
                });

                var greenIcon = L.icon({
                    iconUrl: 'data:image/svg+xml,%3Csvg width="24" height="24" xmlns="http://www.w3.org/2000/svg"%3E%3Cpolygon points="12,2 22,22 2,22" fill="%2327ae60" stroke="white" stroke-width="2"/%3E%3C/svg%3E',
                    iconSize: [24, 24],
                    iconAnchor: [12, 22]
                });

                function showAllLayers() {
                    map.addLayer(landmarkLayer);
                    map.addLayer(floodLayer);
                    map.addLayer(fireLayer);
                    map.addLayer(landslideLayer);
                    map.addLayer(stormSurgeLayer);
                }

                function showLandmarksOnly() {
                    map.addLayer(landmarkLayer);
                    map.removeLayer(floodLayer);
                    map.removeLayer(fireLayer);
                    map.removeLayer(landslideLayer);
                    map.removeLayer(stormSurgeLayer);
                }

                function showHazardsOnly() {
                    map.removeLayer(landmarkLayer);
                    map.addLayer(floodLayer);
                    map.addLayer(fireLayer);
                    map.addLayer(landslideLayer);
                    map.addLayer(stormSurgeLayer);
                }

                function resetView() {
                    map.setView([11.0059, 124.6075], 13);
                }

                console.log('Map ready');
            </script>
        </body>
        </html>
        """;
    }
    
    // Helper methods
    
    private void executeMapScript(String script) {
        if (webEngine != null) {
            try {
                webEngine.executeScript(script);
            } catch (Exception e) {
                System.err.println("JS error: " + e.getMessage());
            }
        }
    }
    
    private void resetMapView() {
        executeMapScript("resetView();");
    }
    
    private void loadMapData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            mapDataLoader = new MapDataLoader(conn);
            
            executeMapScript(mapDataLoader.generateLandmarksScript());
            executeMapScript(mapDataLoader.generateHazardZonesScript());
            
            System.out.println("✅ Map data loaded");
        } catch (SQLException e) {
            showErrorAlert("Failed to load map: " + e.getMessage());
        }
    }
    
    public void refreshMapData() {
        if (mapWebView != null && webEngine != null) {
            loadMapData();
        }
    }
    
    private void searchLocationInDB(String query) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT landmark_name, latitude, longitude FROM landmarks " +
                        "WHERE landmark_name LIKE ? OR barangay LIKE ? LIMIT 1";
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                String pattern = "%" + query + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
                
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double lat = rs.getDouble("latitude");
                        double lng = rs.getDouble("longitude");
                        String name = rs.getString("landmark_name");
                        
                        executeMapScript(String.format(
                            "map.setView([%f, %f], 16); " +
                            "L.popup().setLatLng([%f, %f])" +
                            ".setContent('<b>%s</b>').openOn(map);",
                            lat, lng, lat, lng, name.replace("'", "\\'")
                        ));
                    } else {
                        showErrorAlert("Location not found: " + query);
                    }
                }
            }
        } catch (SQLException e) {
            showErrorAlert("Search failed: " + e.getMessage());
        }
    }
    
    private void showEditModeDialog() {
        Stage editStage = new Stage();
        editStage.setTitle("Map Edit Mode");
        
        VBox editBox = new VBox(15);
        editBox.setPadding(new Insets(20));
        editBox.setAlignment(Pos.CENTER);
        
        Label instruction = new Label("Select an action:");
        instruction.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        Button addLandmarkBtn = new Button("📍 Add Landmark");
        addLandmarkBtn.setPrefWidth(200);
        addLandmarkBtn.setOnAction(e -> { addLandmarkManually(); editStage.close(); });
        
        Button addHazardBtn = new Button("⚠️ Add Hazard Zone");
        addHazardBtn.setPrefWidth(200);
        addHazardBtn.setOnAction(e -> { addHazardZoneManually(); editStage.close(); });
        
        Button closeBtn = new Button("Close");
        closeBtn.setPrefWidth(200);
        closeBtn.setOnAction(e -> editStage.close());
        
        editBox.getChildren().addAll(instruction, addLandmarkBtn, addHazardBtn, closeBtn);
        
        editStage.setScene(new Scene(editBox, 300, 250));
        editStage.show();
    }
    
    private void addLandmarkManually() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Add Landmark");
            nameDialog.setHeaderText("Enter landmark name:");
            Optional<String> nameResult = nameDialog.showAndWait();
            if (!nameResult.isPresent()) return;
            
            TextInputDialog barangayDialog = new TextInputDialog();
            barangayDialog.setHeaderText("Enter barangay:");
            Optional<String> barangayResult = barangayDialog.showAndWait();
            if (!barangayResult.isPresent()) return;
            
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>("School", 
                "School", "Hospital", "Government Office", "Church", "Market", "Other");
            typeDialog.setHeaderText("Select type:");
            Optional<String> typeResult = typeDialog.showAndWait();
            if (!typeResult.isPresent()) return;
            
            TextInputDialog latDialog = new TextInputDialog("11.0059");
            latDialog.setHeaderText("Enter latitude:");
            Optional<String> latResult = latDialog.showAndWait();
            if (!latResult.isPresent()) return;
            
            TextInputDialog lngDialog = new TextInputDialog("124.6075");
            lngDialog.setHeaderText("Enter longitude:");
            Optional<String> lngResult = lngDialog.showAndWait();
            if (!lngResult.isPresent()) return;
            
            Alert evacuationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            evacuationAlert.setHeaderText("Is this an evacuation site?");
            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");
            evacuationAlert.getButtonTypes().setAll(yes, no);
            Optional<ButtonType> evacResult = evacuationAlert.showAndWait();
            boolean isEvac = evacResult.isPresent() && evacResult.get() == yes;
            
            int capacity = 0;
            if (isEvac) {
                TextInputDialog capDialog = new TextInputDialog("0");
                capDialog.setHeaderText("Enter capacity (persons):");
                Optional<String> capResult = capDialog.showAndWait();
                if (capResult.isPresent()) {
                    capacity = Integer.parseInt(capResult.get());
                }
            }
            
            String sql = "INSERT INTO landmarks (landmark_name, landmark_type, barangay, " +
                        "latitude, longitude, is_evacuation_site, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nameResult.get());
                stmt.setString(2, typeResult.get());
                stmt.setString(3, barangayResult.get());
                stmt.setDouble(4, Double.parseDouble(latResult.get()));
                stmt.setDouble(5, Double.parseDouble(lngResult.get()));
                stmt.setBoolean(6, isEvac);
                stmt.setInt(7, capacity);
                stmt.executeUpdate();
                
                showSuccessAlert("Landmark added!");
                refreshMapData();
            }
            
        } catch (Exception ex) {
            showErrorAlert("Failed: " + ex.getMessage());
        }
    }
    
    private void addHazardZoneManually() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setHeaderText("Enter zone name:");
            Optional<String> nameResult = nameDialog.showAndWait();
            if (!nameResult.isPresent()) return;
            
            TextInputDialog barangayDialog = new TextInputDialog();
            barangayDialog.setHeaderText("Enter barangay:");
            Optional<String> barangayResult = barangayDialog.showAndWait();
            if (!barangayResult.isPresent()) return;
            
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>("Flood", 
                "Flood", "Fire", "Landslide", "Storm Surge");
            typeDialog.setHeaderText("Select hazard type:");
            Optional<String> typeResult = typeDialog.showAndWait();
            if (!typeResult.isPresent()) return;
            
            ChoiceDialog<String> sevDialog = new ChoiceDialog<>("High", 
                "Low", "Medium", "High", "Critical");
            sevDialog.setHeaderText("Select severity:");
            Optional<String> sevResult = sevDialog.showAndWait();
            if (!sevResult.isPresent()) return;
            
            TextInputDialog latDialog = new TextInputDialog("11.0059");
            latDialog.setHeaderText("Enter latitude:");
            Optional<String> latResult = latDialog.showAndWait();
            if (!latResult.isPresent()) return;
            
            TextInputDialog lngDialog = new TextInputDialog("124.6075");
            lngDialog.setHeaderText("Enter longitude:");
            Optional<String> lngResult = lngDialog.showAndWait();
            if (!lngResult.isPresent()) return;
            
            TextInputDialog radDialog = new TextInputDialog("500");
            radDialog.setHeaderText("Enter radius (meters):");
            Optional<String> radResult = radDialog.showAndWait();
            if (!radResult.isPresent()) return;
            
            String sql = "INSERT INTO hazard_zones (zone_name, barangay, hazard_type, " +
                        "severity_level, latitude, longitude, radius_meters, is_active, date_identified) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 1, CURRENT_DATE)";
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nameResult.get());
                stmt.setString(2, barangayResult.get());
                stmt.setString(3, typeResult.get());
                stmt.setString(4, sevResult.get());
                stmt.setDouble(5, Double.parseDouble(latResult.get()));
                stmt.setDouble(6, Double.parseDouble(lngResult.get()));
                stmt.setInt(7, Integer.parseInt(radResult.get()));
                stmt.executeUpdate();
                
                showSuccessAlert("Hazard zone added!");
                refreshMapData();
            }
            
        } catch (Exception ex) {
            showErrorAlert("Failed: " + ex.getMessage());
        }
    }
    
    // Create other views (simplified)
    
    private VBox createHistoricalView() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        Label title = new Label("Historical Incidents - Ormoc City");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> yearFilter = new ComboBox<>();
        yearFilter.getItems().addAll("All", "2024", "2023", "2022", "2021", "2020", "2013", "2011", "1991");
        yearFilter.setValue("All");

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All", "Flood", "Fire", "Landslide", "Storm Surge", "Typhoon");
        typeFilter.setValue("All");

        TextArea summaryArea = new TextArea();
        summaryArea.setEditable(false);
        summaryArea.setWrapText(true);
        VBox.setVgrow(summaryArea, Priority.ALWAYS);

        Button applyBtn = new Button("Apply Filter");
        Button manageBtn = new Button("Manage Incidents");
        
        applyBtn.setOnAction(e -> filterIncidents(yearFilter.getValue(), typeFilter.getValue(), summaryArea));
        manageBtn.setOnAction(e -> openIncidentManager());

        filters.getChildren().addAll(
            new Label("Year:"), yearFilter,
            new Label("Type:"), typeFilter,
            applyBtn, manageBtn
        );

        summaryArea.setText("Click 'Apply Filter' to view incident records");

        container.getChildren().addAll(title, filters, summaryArea);
        return container;
    }
    
    private void filterIncidents(String year, String type, TextArea area) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            IncidentDAO dao = new IncidentDAO(conn);
            List<Incident> incidents = dao.getAllIncidents();

            StringBuilder result = new StringBuilder();
            result.append("INCIDENTS - ").append(year).append(" | ").append(type).append("\n");
            result.append("=".repeat(60)).append("\n\n");

            int count = 0;
            for (Incident inc : incidents) {
                boolean yearMatch = year.equals("All") || inc.getIncidentDate().toString().contains(year);
                boolean typeMatch = type.equals("All") || inc.getIncidentType().equals(type);

                if (yearMatch && typeMatch) {
                    result.append(String.format("📅 %s - %s\n", inc.getIncidentDate(), inc.getIncidentType()));
                    result.append(String.format("   📍 %s | ⚠ %s\n", inc.getBarangay(), inc.getSeverity()));
                    result.append(String.format("   💀 %d casualties | %d injuries\n\n", 
                                  inc.getCasualties(), inc.getInjuries()));
                    count++;
                }
            }

            if (count == 0) result.append("No incidents found\n");
            area.setText(result.toString());
        } catch (Exception ex) {
            area.setText("Error: " + ex.getMessage());
        }
    }
    
    private VBox createSafetyView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        
        Label title = new Label("Emergency Safety Guidelines - Ormoc City");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        TextArea guidelines = new TextArea();
        guidelines.setWrapText(true);
        guidelines.setEditable(false);
        guidelines.setText(
            "=== 🌊 FLOOD SAFETY ===\n" +
            "• Move to higher ground during warnings\n" +
            "• Never drive through flood waters\n" +
            "• Know evacuation routes (especially Anilao-Malbasag area)\n\n" +
            
            "=== 🏔 LANDSLIDE SAFETY ===\n" +
            "• Watch for cracks in ground/walls\n" +
            "• Evacuate immediately if imminent\n" +
            "• High-risk: Can-adieng, Donghol, Alta Vista\n\n" +
            
            "=== 🌀 STORM SURGE SAFETY ===\n" +
            "• Evacuate coastal areas for Category 3+ typhoons\n" +
            "• Move 1km inland or to 3+ floor buildings\n\n" +
            
            "=== 🔥 FIRE SAFETY ===\n" +
            "• Keep fire extinguishers accessible\n" +
            "• Have 2 exit routes\n" +
            "• Install smoke detectors\n\n" +
            
            "=== 📞 EMERGENCY CONTACTS ===\n" +
            "🚨 Emergency: 911\n" +
            "🛡 CDRRMO: (053) 561-5027\n" +
            "🚒 Fire Dept: (053) 561-2222\n" +
            "🏥 Hospital: (053) 255-2316\n\n" +
            
            "=== 🏥 EVACUATION CENTERS ===\n" +
            "1. Superdome - 2,000 capacity\n" +
            "2. Multi-Purpose Gym - 1,500 capacity\n" +
            "3. Divine Word College - 500 capacity"
        );
        VBox.setVgrow(guidelines, Priority.ALWAYS);
        
        container.getChildren().addAll(title, guidelines);
        return container;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #34495e;");
        
        Label status = new Label("Ready - Danger Zone Mapping v1.0");
        status.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label location = new Label("📍 Ormoc City, Leyte");
        location.setStyle("-fx-text-fill: white;");
        
        statusBar.getChildren().addAll(status, spacer, location);
        return statusBar;
    }
    
    // Menu action handlers
    
    private void exportData(String type) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Stage stage = (Stage) root.getScene().getWindow();
            
            switch (type) {
                case "landmarks":
                    DataExporter.exportLandmarks(stage, conn);
                    break;
                case "hazards":
                    DataExporter.exportHazardZones(stage, conn);
                    break;
                case "incidents":
                    DataExporter.exportIncidents(stage, conn);
                    break;
            }
            showSuccessAlert("Export successful!");
        } catch (SQLException ex) {
            showErrorAlert("Export failed: " + ex.getMessage());
        }
    }
    
    private void openGlobalSearch() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            new SearchDialog(conn).show();
        } catch (SQLException ex) {
            showErrorAlert("Failed to open search: " + ex.getMessage());
        }
    }
    
    private void openLandmarkManager() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            LandmarkManagerDialog dialog = new LandmarkManagerDialog(conn);
            dialog.show();
            dialog.setOnHidden(e -> refreshMapData());
        } catch (SQLException ex) {
            showErrorAlert("Failed to connect: " + ex.getMessage());
        }
    }
    
    private void openHazardManager() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            HazardZoneManagerDialog dialog = new HazardZoneManagerDialog(conn);
            dialog.show();
            dialog.setOnHidden(e -> refreshMapData());
        } catch (SQLException ex) {
            showErrorAlert("Failed to connect: " + ex.getMessage());
        }
    }
    
    private void openIncidentManager() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            new IncidentManagerDialog(conn).show();
        } catch (SQLException ex) {
            showErrorAlert("Failed to connect: " + ex.getMessage());
        }
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Danger Zone Mapping System");
        alert.setContentText(
            "Version 1.0 - Ormoc City Edition\n\n" +
            "Features:\n" +
            "• Interactive hazard mapping\n" +
            "• Historical disaster tracking\n" +
            "• Landmark management\n" +
            "• Safety guidelines\n\n" +
            "Target Users:\n" +
            "• Residents\n" +
            "• Local Government\n" +
            "• Disaster Management\n" +
            "• Researchers"
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