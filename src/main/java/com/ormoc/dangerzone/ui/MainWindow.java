package com.ormoc.dangerzone.ui;

import com.ormoc.dangerzone.config.DatabaseConfig;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Main Window - JavaFX UI with embedded Leaflet map
 */
public class MainWindow {

    private final Stage primaryStage;
    private BorderPane root;
    private WebView webView;
    private WebEngine webEngine;

    public MainWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeUI();
    }

    private void initializeUI() {
        root = new BorderPane();

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create main content area with WebView for map
        webView = new WebView();
        webEngine = webView.getEngine();
        
        // Enable JavaScript
        webEngine.setJavaScriptEnabled(true);
        
        // Load map from embedded server
        DatabaseConfig config = DatabaseConfig.getInstance();
        String mapUrl = "http://" + config.getServerHost() + ":" + 
                       config.getServerPort() + "/index.html";
        webEngine.load(mapUrl);

        // Create toolbar
        ToolBar toolBar = createToolBar();
        
        // Split pane for map and controls
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        
        // Left side - Control panel
        VBox controlPanel = createControlPanel();
        
        // Right side - Map view
        BorderPane mapContainer = new BorderPane();
        mapContainer.setTop(toolBar);
        mapContainer.setCenter(webView);
        
        splitPane.getItems().addAll(controlPanel, mapContainer);
        splitPane.setDividerPositions(0.25);

        root.setCenter(splitPane);

        // Create status bar
        Label statusBar = new Label(" Ready");
        statusBar.setPadding(new Insets(5));
        root.setBottom(statusBar);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh Map");
        refreshItem.setOnAction(e -> webEngine.reload());
        
        MenuItem exportItem = new MenuItem("Export Data...");
        exportItem.setOnAction(e -> handleExportData());
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        
        fileMenu.getItems().addAll(refreshItem, exportItem, 
                                   new SeparatorMenuItem(), exitItem);

        // Data Menu
        Menu dataMenu = new Menu("Data");
        MenuItem hazardZonesItem = new MenuItem("Manage Hazard Zones");
        hazardZonesItem.setOnAction(e -> showHazardZonesManager());
        
        MenuItem landmarksItem = new MenuItem("Manage Landmarks");
        landmarksItem.setOnAction(e -> showLandmarksManager());
        
        MenuItem incidentsItem = new MenuItem("View Incidents");
        incidentsItem.setOnAction(e -> showIncidentsViewer());
        
        MenuItem guidelinesItem = new MenuItem("Safety Guidelines");
        guidelinesItem.setOnAction(e -> showSafetyGuidelines());
        
        dataMenu.getItems().addAll(hazardZonesItem, landmarksItem, 
                                   incidentsItem, guidelinesItem);

        // View Menu
        Menu viewMenu = new Menu("View");
        CheckMenuItem showLayersItem = new CheckMenuItem("Show Layer Control");
        showLayersItem.setSelected(true);
        
        CheckMenuItem showLegendItem = new CheckMenuItem("Show Legend");
        showLegendItem.setSelected(true);
        
        viewMenu.getItems().addAll(showLayersItem, showLegendItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        
        MenuItem userGuideItem = new MenuItem("User Guide");
        userGuideItem.setOnAction(e -> showUserGuide());
        
        helpMenu.getItems().addAll(userGuideItem, new SeparatorMenuItem(), aboutItem);

        menuBar.getMenus().addAll(fileMenu, dataMenu, viewMenu, helpMenu);
        return menuBar;
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        Button zoomInBtn = new Button("➕ Zoom In");
        zoomInBtn.setOnAction(e -> executeJavaScript("map.zoomIn()"));

        Button zoomOutBtn = new Button("➖ Zoom Out");
        zoomOutBtn.setOnAction(e -> executeJavaScript("map.zoomOut()"));

        Button resetViewBtn = new Button("🏠 Reset View");
        resetViewBtn.setOnAction(e -> executeJavaScript("resetMapView()"));

        Separator sep1 = new Separator();
        sep1.setOrientation(Orientation.VERTICAL);

        Button addHazardBtn = new Button("➕ Add Hazard Zone");
        addHazardBtn.setOnAction(e -> executeJavaScript("startDrawingHazardZone()"));

        Button addLandmarkBtn = new Button("📍 Add Landmark");
        addLandmarkBtn.setOnAction(e -> executeJavaScript("startAddingLandmark()"));

        Separator sep2 = new Separator();
        sep2.setOrientation(Orientation.VERTICAL);

        Button editModeBtn = new Button("✏️ Edit Mode");
        editModeBtn.setOnAction(e -> executeJavaScript("toggleEditMode()"));

        toolBar.getItems().addAll(
            zoomInBtn, zoomOutBtn, resetViewBtn, sep1,
            addHazardBtn, addLandmarkBtn, sep2, editModeBtn
        );

        return toolBar;
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f4f4f4;");

        // Title
        Label title = new Label("Filters & Controls");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Barangay filter
        Label barangayLabel = new Label("Filter by Barangay:");
        ComboBox<String> barangayCombo = new ComboBox<>();
        barangayCombo.getItems().add("All Barangays");
        barangayCombo.setValue("All Barangays");
        barangayCombo.setMaxWidth(Double.MAX_VALUE);

        // Hazard type filter
        Label hazardTypeLabel = new Label("Filter by Hazard Type:");
        ComboBox<String> hazardTypeCombo = new ComboBox<>();
        hazardTypeCombo.getItems().addAll("All Types", "Flood", "Landslide", 
                                         "Fire", "Storm Surge");
        hazardTypeCombo.setValue("All Types");
        hazardTypeCombo.setMaxWidth(Double.MAX_VALUE);

        // Severity filter
        Label severityLabel = new Label("Filter by Severity:");
        ComboBox<String> severityCombo = new ComboBox<>();
        severityCombo.getItems().addAll("All Levels", "Critical", "High", 
                                       "Medium", "Low");
        severityCombo.setValue("All Levels");
        severityCombo.setMaxWidth(Double.MAX_VALUE);

        // Apply filters button
        Button applyFilterBtn = new Button("Apply Filters");
        applyFilterBtn.setMaxWidth(Double.MAX_VALUE);
        applyFilterBtn.setOnAction(e -> {
            String barangay = barangayCombo.getValue();
            String hazardType = hazardTypeCombo.getValue();
            String severity = severityCombo.getValue();
            applyFilters(barangay, hazardType, severity);
        });

        // Clear filters button
        Button clearFilterBtn = new Button("Clear Filters");
        clearFilterBtn.setMaxWidth(Double.MAX_VALUE);
        clearFilterBtn.setOnAction(e -> {
            barangayCombo.setValue("All Barangays");
            hazardTypeCombo.setValue("All Types");
            severityCombo.setValue("All Levels");
            executeJavaScript("clearFilters()");
        });

        Separator sep = new Separator();

        // Layer toggles
        Label layersLabel = new Label("Map Layers:");
        layersLabel.setStyle("-fx-font-weight: bold;");

        CheckBox hazardZonesCheck = new CheckBox("Hazard Zones");
        hazardZonesCheck.setSelected(true);
        hazardZonesCheck.setOnAction(e -> 
            executeJavaScript("toggleLayer('hazardZones', " + 
                            hazardZonesCheck.isSelected() + ")"));

        CheckBox landmarksCheck = new CheckBox("Landmarks");
        landmarksCheck.setSelected(true);
        landmarksCheck.setOnAction(e -> 
            executeJavaScript("toggleLayer('landmarks', " + 
                            landmarksCheck.isSelected() + ")"));

        CheckBox incidentsCheck = new CheckBox("Historical Incidents");
        incidentsCheck.setSelected(false);
        incidentsCheck.setOnAction(e -> 
            executeJavaScript("toggleLayer('incidents', " + 
                            incidentsCheck.isSelected() + ")"));

        CheckBox evacuationCheck = new CheckBox("Evacuation Centers");
        evacuationCheck.setSelected(true);
        evacuationCheck.setOnAction(e -> 
            executeJavaScript("toggleLayer('evacuation', " + 
                            evacuationCheck.isSelected() + ")"));

        // Statistics section
        Separator sep2 = new Separator();
        Label statsLabel = new Label("Quick Statistics:");
        statsLabel.setStyle("-fx-font-weight: bold;");

        Label statsInfo = new Label("Loading statistics...");
        statsInfo.setWrapText(true);

        panel.getChildren().addAll(
            title,
            new Separator(),
            barangayLabel, barangayCombo,
            hazardTypeLabel, hazardTypeCombo,
            severityLabel, severityCombo,
            applyFilterBtn, clearFilterBtn,
            sep,
            layersLabel,
            hazardZonesCheck, landmarksCheck, 
            incidentsCheck, evacuationCheck,
            sep2,
            statsLabel, statsInfo
        );

        return panel;
    }

    private void executeJavaScript(String script) {
        if (webEngine != null) {
            webEngine.executeScript(script);
        }
    }

    private void applyFilters(String barangay, String hazardType, String severity) {
        String script = String.format(
            "applyFilters('%s', '%s', '%s')",
            barangay.equals("All Barangays") ? "" : barangay,
            hazardType.equals("All Types") ? "" : hazardType,
            severity.equals("All Levels") ? "" : severity
        );
        executeJavaScript(script);
    }

    private void handleExportData() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Data");
        alert.setHeaderText("Export Feature");
        alert.setContentText("Data export functionality will be implemented here.");
        alert.showAndWait();
    }

    private void showHazardZonesManager() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hazard Zones Manager");
        alert.setContentText("Use the map toolbar to add/edit hazard zones directly on the map.");
        alert.showAndWait();
    }

    private void showLandmarksManager() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Landmarks Manager");
        alert.setContentText("Use the map toolbar to add/edit landmarks directly on the map.");
        alert.showAndWait();
    }

    private void showIncidentsViewer() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Historical Incidents");
        alert.setContentText("Enable 'Historical Incidents' layer to view incidents on the map.");
        alert.showAndWait();
    }

    private void showSafetyGuidelines() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Safety Guidelines");
        alert.setHeaderText("Emergency Contacts & Guidelines");
        alert.setContentText(
            "CDRRMO: (053) 561-5027\n" +
            "Fire: (053) 561-2222\n" +
            "Emergency: 911\n\n" +
            "Click on hazard zones for specific safety information."
        );
        alert.showAndWait();
    }

    private void showUserGuide() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Guide");
        alert.setHeaderText("How to Use the System");
        alert.setContentText(
            "1. Use filters to narrow down hazard zones\n" +
            "2. Click on map markers for details\n" +
            "3. Add hazard zones using the ➕ button\n" +
            "4. Add landmarks using the 📍 button\n" +
            "5. Enable/disable layers using checkboxes\n" +
            "6. Export data using File > Export Data"
        );
        alert.showAndWait();
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Ormoc Danger Zone Mapping System");
        alert.setContentText(
            "Version: 1.0.0\n\n" +
            "An interactive mapping system for visualizing\n" +
            "hazard-prone areas and historical incidents\n" +
            "in Ormoc City.\n\n" +
            "Developed by: Gabor, Manidlangan, Pace\n" +
            "Date: October 2025"
        );
        alert.showAndWait();
    }

    public void show() {
        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ormoc Danger Zone Mapping and Historical Information System");
        primaryStage.show();
    }
}