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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.io.InputStream;

public class LandmarkManagerDialog extends Stage {
    
    private LandmarkDAO landmarkDAO;
    private TableView<Landmark> landmarkTable;
    private ObservableList<Landmark> landmarkData;
    private ComboBox<String> filterTypeCombo;
    private TextField searchField;
    private ImageView mapImageView;
    private Pane mapOverlayPane;
    private Label coordsLabel;
    private Circle selectionMarker;
    
    // Map bounds for Ormoc City
    private static final double MAP_MIN_LAT = 10.85;
    private static final double MAP_MAX_LAT = 11.20;
    private static final double MAP_MIN_LNG = 124.45;
    private static final double MAP_MAX_LNG = 124.80;
    private static final double MAP_WIDTH = 1920;
    private static final double MAP_HEIGHT = 988;
    
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
        
        ScrollPane mapScrollPane = createInteractiveMap();
        VBox.setVgrow(mapScrollPane, Priority.ALWAYS);
        
        coordsLabel = new Label(String.format("Selected: %.6f°N, %.6f°E", selectedLat, selectedLng));
        coordsLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; " +
                            "-fx-padding: 8; -fx-background-color: #ecf0f1; " +
                            "-fx-border-color: #3498db; -fx-border-width: 2;");
        coordsLabel.setAlignment(Pos.CENTER);
        
        mapBox.getChildren().addAll(mapLabel, mapScrollPane, coordsLabel);
        
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
                    
                    coordsLabel.setText(String.format("Hover: %.6f°N, %.6f°E", latitude, longitude));
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
        // Remove old marker
        if (selectionMarker != null) {
            mapOverlayPane.getChildren().remove(selectionMarker);
        }
        
        // Create new marker
        selectionMarker = new Circle(x, y, 10);
        selectionMarker.setFill(Color.web("#e74c3c"));
        selectionMarker.setStroke(Color.WHITE);
        selectionMarker.setStrokeWidth(3);
        
        mapOverlayPane.getChildren().add(selectionMarker);
    }
    
    private void updateCoordsLabel() {
        coordsLabel.setText(String.format("Selected: %.6f°N, %.6f°E", selectedLat, selectedLng));
    }
    
    private void showLandmarkOnMap(Landmark landmark) {
        selectedLat = landmark.getLatitude();
        selectedLng = landmark.getLongitude();
        
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
        // Create a new empty landmark with the selected coordinates
        Landmark newLandmark = new Landmark();
        newLandmark.setLatitude(selectedLat);
        newLandmark.setLongitude(selectedLng);
        
        // Open the full form dialog (same as edit)
        LandmarkFormDialog dialog = new LandmarkFormDialog(newLandmark);
        Optional<Landmark> result = dialog.showAndWait();
        
        result.ifPresent(landmark -> {
            try {
                if (landmarkDAO.createLandmark(landmark)) {
                    showSuccess("Landmark added successfully at: " + 
                               String.format("%.6f, %.6f", selectedLat, selectedLng));
                    loadLandmarks();
                } else {
                    showError("Failed to add landmark");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
    }
    
    private void editLandmark() {
        Landmark selected = landmarkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a landmark to edit");
            return;
        }
        
        LandmarkFormDialog dialog = new LandmarkFormDialog(selected);
        Optional<Landmark> result = dialog.showAndWait();
        
        result.ifPresent(landmark -> {
            try {
                boolean success = landmarkDAO.updateLandmark(landmark);
                
                if (success) {
                    showSuccess("Landmark updated successfully!");
                    loadLandmarks();
                } else {
                    showError("Update failed - No rows were affected.");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
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