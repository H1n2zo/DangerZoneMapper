package com.dangerzone.views;

import com.dangerzone.models.Landmark;
import com.dangerzone.models.LandmarkDAO;
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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LandmarkManagerDialog extends Stage {
    
    private LandmarkDAO landmarkDAO;
    private TableView<Landmark> landmarkTable;
    private ObservableList<Landmark> landmarkData;
    private ComboBox<String> filterTypeCombo;
    private TextField searchField;
    private WebView mapView;
    private WebEngine webEngine;
    
    // For storing clicked coordinates
    private double selectedLat = 11.0059;
    private double selectedLng = 124.6075;
    
    public LandmarkManagerDialog(Connection connection) {
        this.landmarkDAO = new LandmarkDAO(connection);
        this.landmarkData = FXCollections.observableArrayList();
        
        setTitle("Landmark Manager - Ormoc City");
        initModality(Modality.APPLICATION_MODAL);
        setWidth(1400);
        setHeight(800);
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        
        // Top: Title and search
        VBox topSection = new VBox(10);
        Label titleLabel = new Label("Manage Landmarks - Click on Map to Set Location");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        HBox searchBox = createSearchControls();
        topSection.getChildren().addAll(titleLabel, searchBox);
        root.setTop(topSection);
        
        // Center: Split between table and map
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);
        
        // Left: Table
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        landmarkTable = createLandmarkTable();
        VBox.setVgrow(landmarkTable, Priority.ALWAYS);
        tableBox.getChildren().add(landmarkTable);
        
        // Right: Interactive Map
        VBox mapBox = new VBox(10);
        mapBox.setPadding(new Insets(10));
        Label mapLabel = new Label("🗺 Click on map to select coordinates");
        mapLabel.setStyle("-fx-font-weight: bold;");
        mapView = createInteractiveMap();
        VBox.setVgrow(mapView, Priority.ALWAYS);
        
        Label coordsLabel = new Label("Selected: 11.0059°N, 124.6075°E");
        coordsLabel.setId("coordsLabel");
        coordsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
        
        mapBox.getChildren().addAll(mapLabel, mapView, coordsLabel);
        
        splitPane.getItems().addAll(tableBox, mapBox);
        root.setCenter(splitPane);
        
        // Bottom: Buttons
        HBox buttonBox = createButtonControls();
        root.setBottom(buttonBox);
        
        Scene scene = new Scene(root);
        setScene(scene);
        setMaximized(true);
        
        loadLandmarks();
    }
    
    private HBox createSearchControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5));
        
        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPromptText("Enter landmark name...");
        searchField.setPrefWidth(250);
        
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> searchLandmarks());
        
        Label filterLabel = new Label("Type:");
        filterTypeCombo = new ComboBox<>();
        filterTypeCombo.getItems().add("All");
        loadLandmarkTypes();
        filterTypeCombo.setValue("All");
        
        Button filterBtn = new Button("Filter");
        filterBtn.setOnAction(e -> filterByType());
        
        Button resetBtn = new Button("Show All");
        resetBtn.setOnAction(e -> loadLandmarks());
        
        box.getChildren().addAll(
            searchLabel, searchField, searchBtn,
            new Separator(),
            filterLabel, filterTypeCombo, filterBtn, resetBtn
        );
        
        return box;
    }
    
    private TableView<Landmark> createLandmarkTable() {
        TableView<Landmark> table = new TableView<>();
        table.setItems(landmarkData);
        
        TableColumn<Landmark, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Landmark, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Landmark, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);
        
        TableColumn<Landmark, String> barangayCol = new TableColumn<>("Barangay");
        barangayCol.setCellValueFactory(new PropertyValueFactory<>("barangay"));
        barangayCol.setPrefWidth(120);
        
        TableColumn<Landmark, Boolean> evacCol = new TableColumn<>("Evac");
        evacCol.setCellValueFactory(new PropertyValueFactory<>("evacuationSite"));
        evacCol.setPrefWidth(60);
        
        table.getColumns().addAll(idCol, nameCol, typeCol, barangayCol, evacCol);
        
        // When selecting a landmark, show it on map
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showLandmarkOnMap(newVal);
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
                .click-marker { 
                    background: #e74c3c;
                    border: 2px solid white;
                    border-radius: 50%;
                    width: 12px;
                    height: 12px;
                }
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
                
                var clickMarker = null;
                
                // Click handler
                map.on('click', function(e) {
                    var lat = e.latlng.lat.toFixed(6);
                    var lng = e.latlng.lng.toFixed(6);
                    
                    // Remove old marker
                    if (clickMarker) {
                        map.removeLayer(clickMarker);
                    }
                    
                    // Add new marker
                    clickMarker = L.circleMarker(e.latlng, {
                        radius: 8,
                        fillColor: '#e74c3c',
                        color: 'white',
                        weight: 2,
                        fillOpacity: 0.8
                    }).addTo(map);
                    
                    clickMarker.bindPopup('<b>Selected Location</b><br>Lat: ' + lat + '<br>Lng: ' + lng).openPopup();
                    
                    // Call Java method
                    if (window.javaHandler) {
                        window.javaHandler.setCoordinates(lat, lng);
                    }
                });
                
                function showLocation(lat, lng, name) {
                    map.setView([lat, lng], 15);
                    L.marker([lat, lng]).addTo(map)
                        .bindPopup('<b>' + name + '</b>').openPopup();
                }
            </script>
        </body>
        </html>
        """;
        
        webEngine.loadContent(mapHTML);
        
        // Set up JavaScript bridge
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                try {
                    // Create bridge object
                    webEngine.executeScript(
                        "window.javaHandler = {" +
                        "  setCoordinates: function(lat, lng) {" +
                        "    console.log('Coordinates: ' + lat + ', ' + lng);" +
                        "  }" +
                        "};"
                    );
                    
                    // Listen for coordinate changes via document title
                    webEngine.documentProperty().addListener((obsDoc, oldDoc, newDoc) -> {
                        if (newDoc != null) {
                            webEngine.executeScript(
                                "window.javaHandler.setCoordinates = function(lat, lng) {" +
                                "  document.title = lat + ',' + lng;" +
                                "};"
                            );
                        }
                    });
                    
                    webEngine.titleProperty().addListener((obsTitle, oldTitle, newTitle) -> {
                        if (newTitle != null && newTitle.contains(",")) {
                            String[] coords = newTitle.split(",");
                            selectedLat = Double.parseDouble(coords[0]);
                            selectedLng = Double.parseDouble(coords[1]);
                            updateCoordsLabel();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Bridge error: " + e.getMessage());
                }
            }
        });
        
        return webView;
    }
    
    private void updateCoordsLabel() {
        Label coordsLabel = (Label) getScene().lookup("#coordsLabel");
        if (coordsLabel != null) {
            coordsLabel.setText(String.format("Selected: %.6f°N, %.6f°E", selectedLat, selectedLng));
        }
    }
    
    private void showLandmarkOnMap(Landmark landmark) {
        String script = String.format(
            "showLocation(%f, %f, '%s');",
            landmark.getLatitude(),
            landmark.getLongitude(),
            landmark.getName().replace("'", "\\'")
        );
        webEngine.executeScript(script);
    }
    
    private HBox createButtonControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(10));
        
        Button addBtn = new Button("➕ Add New (Use Map)");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> addLandmarkWithMapCoords());
        
        Button editBtn = new Button("✏ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editLandmark());
        
        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteLandmark());
        
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> close());
        
        box.getChildren().addAll(addBtn, editBtn, deleteBtn, closeBtn);
        
        return box;
    }
    
    private void addLandmarkWithMapCoords() {
        try {
            // Name
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Add Landmark");
            nameDialog.setHeaderText("Enter landmark name:");
            Optional<String> nameResult = nameDialog.showAndWait();
            if (!nameResult.isPresent() || nameResult.get().trim().isEmpty()) return;
            
            // Barangay
            TextInputDialog barangayDialog = new TextInputDialog();
            barangayDialog.setTitle("Add Landmark");
            barangayDialog.setHeaderText("Enter barangay:");
            Optional<String> barangayResult = barangayDialog.showAndWait();
            if (!barangayResult.isPresent() || barangayResult.get().trim().isEmpty()) return;
            
            // Type
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>("School", 
                "School", "Hospital", "Government Office", "Church", "Market", 
                "Evacuation Center", "Police Station", "Other");
            typeDialog.setTitle("Landmark Type");
            typeDialog.setHeaderText("Select landmark type:");
            Optional<String> typeResult = typeDialog.showAndWait();
            if (!typeResult.isPresent()) return;
            
            // Evacuation site?
            Alert evacuationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            evacuationAlert.setTitle("Evacuation Site");
            evacuationAlert.setHeaderText("Is this an evacuation site?");
            ButtonType yesBtn = new ButtonType("Yes");
            ButtonType noBtn = new ButtonType("No");
            evacuationAlert.getButtonTypes().setAll(yesBtn, noBtn);
            Optional<ButtonType> evacResult = evacuationAlert.showAndWait();
            boolean isEvacuation = evacResult.isPresent() && evacResult.get() == yesBtn;
            
            // Capacity
            int capacity = 0;
            if (isEvacuation) {
                TextInputDialog capacityDialog = new TextInputDialog("0");
                capacityDialog.setTitle("Capacity");
                capacityDialog.setHeaderText("Enter evacuation capacity (persons):");
                Optional<String> capResult = capacityDialog.showAndWait();
                if (capResult.isPresent()) {
                    try {
                        capacity = Integer.parseInt(capResult.get());
                    } catch (NumberFormatException e) {
                        capacity = 0;
                    }
                }
            }
            
            // Contact (optional)
            TextInputDialog contactDialog = new TextInputDialog();
            contactDialog.setTitle("Contact Number");
            contactDialog.setHeaderText("Enter contact number (optional):");
            contactDialog.setContentText("Contact:");
            Optional<String> contactResult = contactDialog.showAndWait();
            String contact = contactResult.orElse(null);
            
            // Create landmark object
            Landmark landmark = new Landmark();
            landmark.setName(nameResult.get().trim());
            landmark.setBarangay(barangayResult.get().trim());
            landmark.setType(typeResult.get());
            landmark.setLatitude(selectedLat);
            landmark.setLongitude(selectedLng);
            landmark.setEvacuationSite(isEvacuation);
            landmark.setCapacity(capacity > 0 ? capacity : null);
            landmark.setContactNumber(contact != null && !contact.trim().isEmpty() ? contact.trim() : null);
            
            // Save to database
            if (landmarkDAO.createLandmark(landmark)) {
                showSuccess("Landmark added successfully at coordinates: " + 
                           String.format("%.6f, %.6f", selectedLat, selectedLng));
                loadLandmarks();
            } else {
                showError("Failed to add landmark");
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }
    
private void editLandmark() {
    Landmark selected = landmarkTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showWarning("Please select a landmark to edit");
        return;
    }
    
    System.out.println("📝 Editing Landmark ID: " + selected.getId() + " - " + selected.getName());
    
    LandmarkFormDialog dialog = new LandmarkFormDialog(selected);
    Optional<Landmark> result = dialog.showAndWait();
    
    result.ifPresent(landmark -> {
        try {
            System.out.println("💾 Saving changes to Landmark ID: " + landmark.getId());
            System.out.println("   Name: " + landmark.getName());
            System.out.println("   Coords: " + landmark.getLatitude() + ", " + landmark.getLongitude());
            
            boolean success = landmarkDAO.updateLandmark(landmark);
            
            if (success) {
                showSuccess("Landmark updated successfully!");
                loadLandmarks(); // Refresh table
                System.out.println("✅ Landmark updated and table refreshed");
            } else {
                showError("Update failed - No rows were affected. Check if landmark ID exists.");
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
    
    private void deleteLandmark() {
        Landmark selected = landmarkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a landmark to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Landmark?");
        confirm.setContentText("Delete: " + selected.getName() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (landmarkDAO.deleteLandmark(selected.getId())) {
                    showSuccess("Landmark deleted!");
                    loadLandmarks();
                }
            } catch (SQLException e) {
                showError("Delete failed: " + e.getMessage());
            }
        }
    }
    
    private void loadLandmarks() {
        try {
            List<Landmark> landmarks = landmarkDAO.getAllLandmarks();
            landmarkData.clear();
            landmarkData.addAll(landmarks);
        } catch (SQLException e) {
            showError("Failed to load landmarks: " + e.getMessage());
        }
    }
    
    private void loadLandmarkTypes() {
        try {
            List<String> types = landmarkDAO.getLandmarkTypes();
            filterTypeCombo.getItems().addAll(types);
        } catch (SQLException e) {
            System.err.println("Failed to load types: " + e.getMessage());
        }
    }
    
    private void searchLandmarks() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadLandmarks();
            return;
        }
        
        try {
            List<Landmark> results = landmarkDAO.searchLandmarksByName(searchTerm);
            landmarkData.clear();
            landmarkData.addAll(results);
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    private void filterByType() {
        String selectedType = filterTypeCombo.getValue();
        if (selectedType == null || selectedType.equals("All")) {
            loadLandmarks();
            return;
        }
        
        try {
            List<Landmark> filtered = landmarkDAO.getLandmarksByType(selectedType);
            landmarkData.clear();
            landmarkData.addAll(filtered);
        } catch (SQLException e) {
            showError("Filter failed: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
}