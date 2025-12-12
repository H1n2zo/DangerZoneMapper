package com.dangerzonemapper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/danger_zone_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
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
    private Label statusLabel;
    
    private double selectedLat = 0;
    private double selectedLon = 0;
    private Circle selectedMarker;
    private Circle radiusCircle;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Danger Zone Mapping - Ormoc City");
        
        hazardZones = FXCollections.observableArrayList();
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        root.setTop(createMenuBar());
        root.setCenter(createMapView());
        root.setRight(createControlPanel());
        root.setBottom(createStatusBar());
        
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        initializeDatabase();
        loadHazardZones();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #2196F3; -fx-padding: 3 0 3 0;");
        
        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh Map");
        refreshItem.setOnAction(e -> loadHazardZones());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), exitItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        MenuItem safetyItem = new MenuItem("Safety Guidelines");
        safetyItem.setOnAction(e -> showSafetyGuidelines());
        helpMenu.getItems().addAll(aboutItem, safetyItem);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }
    
    private VBox createMapView() {
        VBox mapBox = new VBox(8);
        mapBox.setPadding(new Insets(8));
        mapBox.setStyle("-fx-background-color: white;");
        
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Ormoc City Hazard Map");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        HBox legend = createLegend();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBox.getChildren().addAll(titleLabel, spacer, legend);
        
        mapPane = new Pane();
        mapPane.setStyle("-fx-background-color: #E8F4F8; -fx-border-color: #333; -fx-border-width: 2;");
        mapPane.setPrefSize(850, 580);
        mapPane.setMinSize(850, 580);
        mapPane.setMaxSize(850, 580);
        
        drawMapGrid();
        addMapLabels();
        
        mapPane.setOnMouseMoved(this::handleMouseMoved);
        mapPane.setOnMouseClicked(this::handleMapClick);
        
        coordsLabel = new Label("Click on map to select a location for the hazard zone");
        coordsLabel.setFont(Font.font("Arial", 11));
        coordsLabel.setStyle("-fx-padding: 6; -fx-background-color: #fff3cd; -fx-border-color: #ffc107; -fx-border-width: 1;");
        
        mapBox.getChildren().addAll(titleBox, mapPane, coordsLabel);
        return mapBox;
    }
    
    private HBox createLegend() {
        HBox legend = new HBox(12);
        legend.setAlignment(Pos.CENTER_RIGHT);
        legend.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 3;");
        
        Label legendTitle = new Label("Legend:");
        legendTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        legend.getChildren().add(legendTitle);
        
        String[] types = {"Flood", "Landslide", "Fire", "Earthquake", "Typhoon"};
        
        for (String type : types) {
            HBox item = new HBox(4);
            item.setAlignment(Pos.CENTER);
            
            Circle circle = new Circle(5);
            circle.setFill(getColorForType(type).darker());
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1.5);
            
            Label label = new Label(type);
            label.setFont(Font.font("Arial", 9));
            
            item.getChildren().addAll(circle, label);
            legend.getChildren().add(item);
        }
        
        return legend;
    }
    
    private void drawMapGrid() {
        for (int i = 0; i <= 17; i++) {
            double x = i * 50;
            Rectangle line = new Rectangle(x, 0, 0.5, 580);
            line.setFill(Color.rgb(200, 200, 200, 0.4));
            mapPane.getChildren().add(line);
        }
        
        for (int i = 0; i <= 11; i++) {
            double y = i * 50;
            Rectangle line = new Rectangle(0, y, 850, 0.5);
            line.setFill(Color.rgb(200, 200, 200, 0.4));
            mapPane.getChildren().add(line);
        }
    }
    
    private void addMapLabels() {
        Label cityLabel = new Label("ORMOC CITY");
        cityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        cityLabel.setTextFill(Color.rgb(100, 100, 100, 0.25));
        cityLabel.setLayoutX(320);
        cityLabel.setLayoutY(260);
        mapPane.getChildren().add(cityLabel);
        
        Label coastLabel = new Label("← Ormoc Bay");
        coastLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        coastLabel.setTextFill(Color.rgb(0, 100, 200));
        coastLabel.setLayoutX(40);
        coastLabel.setLayoutY(290);
        mapPane.getChildren().add(coastLabel);
        
        VBox compass = new VBox(2);
        compass.setAlignment(Pos.CENTER);
        Label northLabel = new Label("N");
        northLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        northLabel.setTextFill(Color.RED);
        Label compassSymbol = new Label("↑");
        compassSymbol.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        compassSymbol.setTextFill(Color.RED);
        compass.getChildren().addAll(northLabel, compassSymbol);
        compass.setLayoutX(800);
        compass.setLayoutY(15);
        mapPane.getChildren().add(compass);
    }
    
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setPrefWidth(300);
        controlPanel.setStyle("-fx-background-color: #fafafa; -fx-border-color: #ccc; -fx-border-width: 0 0 0 2;");
        
        Label titleLabel = new Label("Hazard Zone Manager");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label typeLabel = new Label("Hazard Type:");
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        hazardTypeCombo = new ComboBox<>();
        hazardTypeCombo.getItems().addAll("Flood", "Landslide", "Fire", "Earthquake", "Typhoon");
        hazardTypeCombo.setValue("Flood");
        hazardTypeCombo.setPrefWidth(Double.MAX_VALUE);
        hazardTypeCombo.setStyle("-fx-font-size: 11px;");
        hazardTypeCombo.setOnAction(e -> updateRadiusCircle());
        
        Label nameLabel = new Label("Location Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        nameField = new TextField();
        nameField.setPromptText("e.g., Downtown Area, Barangay...");
        nameField.setStyle("-fx-font-size: 11px;");
        
        Label descLabel = new Label("Description:");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setPromptText("Enter hazard details...");
        descriptionArea.setWrapText(true);
        descriptionArea.setStyle("-fx-font-size: 11px;");
        
        Label radiusLabel = new Label("Hazard Radius: 500m");
        radiusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        radiusSlider = new Slider(100, 2000, 500);
        radiusSlider.setShowTickLabels(true);
        radiusSlider.setShowTickMarks(true);
        radiusSlider.setMajorTickUnit(500);
        radiusSlider.setBlockIncrement(100);
        radiusSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            radiusLabel.setText(String.format("Hazard Radius: %.0fm", newVal.doubleValue()));
            updateRadiusCircle();
        });
        
        Label coordsDisplayLabel = new Label("Selected Location:");
        coordsDisplayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        selectedCoordsLabel = new Label("Click map to select");
        selectedCoordsLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 10px;");
        
        HBox buttonBox = new HBox(8);
        Button addButton = new Button("Add Hazard");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        addButton.setPrefWidth(130);
        addButton.setOnAction(e -> addHazardZone());
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-size: 11px;");
        clearButton.setPrefWidth(70);
        clearButton.setOnAction(e -> clearSelection());
        
        buttonBox.getChildren().addAll(addButton, clearButton);
        
        Separator separator = new Separator();
        
        Label listLabel = new Label("Existing Hazard Zones:");
        listLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        
        hazardListView = new ListView<>();
        hazardListView.setPrefHeight(160);
        hazardListView.setStyle("-fx-font-size: 10px;");
        hazardListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewHazardDetails();
            }
        });
        
        HBox listButtonBox = new HBox(8);
        Button viewButton = new Button("View");
        viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 10px;");
        viewButton.setPrefWidth(100);
        viewButton.setOnAction(e -> viewHazardDetails());
        
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px;");
        deleteButton.setPrefWidth(80);
        deleteButton.setOnAction(e -> deleteSelectedHazard());
        
        listButtonBox.getChildren().addAll(viewButton, deleteButton);
        
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
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(4, 10, 4, 10));
        statusBar.setStyle("-fx-background-color: #2196F3;");
        
        statusLabel = new Label("Ready | Total Zones: 0");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        
        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }
    
    private void handleMouseMoved(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        
        double lat = pixelYToLat(y);
        double lon = pixelXToLon(x);
        
        coordsLabel.setText(String.format("Coordinates: %.6f°N, %.6f°E | Click to select", lat, lon));
    }
    
    private void handleMapClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        
        selectedLat = pixelYToLat(y);
        selectedLon = pixelXToLon(x);
        
        if (selectedMarker != null) {
            mapPane.getChildren().remove(selectedMarker);
        }
        
        selectedMarker = new Circle(x, y, 7);
        selectedMarker.setFill(Color.RED);
        selectedMarker.setStroke(Color.WHITE);
        selectedMarker.setStrokeWidth(2);
        mapPane.getChildren().add(selectedMarker);
        
        updateRadiusCircle();
        
        coordsLabel.setText(String.format("✓ Selected: %.6f°N, %.6f°E | Click 'Add Hazard' to save", selectedLat, selectedLon));
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
        coordsLabel.setText("Click on map to select a location for the hazard zone");
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
        }
    }
    
    private void viewHazardDetails() {
        String selected = hazardListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a hazard zone!");
            return;
        }
        
        int id = Integer.parseInt(selected.split(":")[0].replace("#", ""));
        
        for (HazardZone zone : hazardZones) {
            if (zone.id == id) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hazard Zone Details");
                alert.setHeaderText(zone.name);
                
                TextArea textArea = new TextArea();
                textArea.setText(String.format(
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
                ));
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setPrefRowCount(8);
                
                alert.getDialogPane().setContent(textArea);
                alert.getDialogPane().setPrefWidth(450);
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
            showAlert("Database Error", "Failed to delete: " + e.getMessage());
        }
    }
    
    private void loadHazardZones() {
        hazardZones.clear();
        ObservableList<String> displayList = FXCollections.observableArrayList();
        
        mapPane.getChildren().removeIf(node -> 
            node instanceof Circle && node != selectedMarker && node != radiusCircle
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
                drawHazardZone(zone);
            }
            
            hazardListView.setItems(displayList);
            statusLabel.setText("Ready | Total Zones: " + hazardZones.size());
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load hazard zones: " + e.getMessage());
        }
    }
    
    private void drawHazardZone(HazardZone zone) {
        double x = lonToPixelX(zone.longitude);
        double y = latToPixelY(zone.latitude);
        
        double radiusPixels = metersToPixels(zone.radius);
        Circle circle = new Circle(x, y, radiusPixels);
        
        Color fillColor = getColorForType(zone.type);
        circle.setFill(fillColor);
        circle.setStroke(fillColor.darker());
        circle.setStrokeWidth(2);
        
        Tooltip tooltip = new Tooltip(
            zone.name + "\n" + 
            zone.type + "\n" +
            "Radius: " + zone.radius + "m\n" +
            "Added: " + zone.dateAdded
        );
        Tooltip.install(circle, tooltip);
        
        mapPane.getChildren().add(circle);
        circle.toBack();
        
        Circle marker = new Circle(x, y, 5);
        marker.setFill(fillColor.darker());
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(1.5);
        Tooltip.install(marker, tooltip);
        mapPane.getChildren().add(marker);
    }
    
    private Color getColorForType(String type) {
        return switch (type.toLowerCase()) {
            case "flood" -> Color.rgb(0, 150, 255, 0.3);
            case "landslide" -> Color.rgb(139, 69, 19, 0.3);
            case "fire" -> Color.rgb(255, 0, 0, 0.3);
            case "earthquake" -> Color.rgb(255, 165, 0, 0.3);
            case "typhoon" -> Color.rgb(128, 0, 128, 0.3);
            default -> Color.rgb(128, 128, 128, 0.3);
        };
    }
    
    private double pixelXToLon(double x) {
        return ORMOC_LON_MIN + (x / 850.0) * (ORMOC_LON_MAX - ORMOC_LON_MIN);
    }
    
    private double pixelYToLat(double y) {
        return ORMOC_LAT_MAX - (y / 580.0) * (ORMOC_LAT_MAX - ORMOC_LAT_MIN);
    }
    
    private double lonToPixelX(double lon) {
        return ((lon - ORMOC_LON_MIN) / (ORMOC_LON_MAX - ORMOC_LON_MIN)) * 850.0;
    }
    
    private double latToPixelY(double lat) {
        return ((ORMOC_LAT_MAX - lat) / (ORMOC_LAT_MAX - ORMOC_LAT_MIN)) * 580.0;
    }
    
    private double metersToPixels(double meters) {
        double degreesLat = meters / 111000.0;
        return (degreesLat / (ORMOC_LAT_MAX - ORMOC_LAT_MIN)) * 580.0;
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Connection successful
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Failed to connect to database");
            alert.setContentText(
                "Please ensure:\n" +
                "1. MySQL/XAMPP is running\n" +
                "2. Database 'danger_zone_db' exists\n" +
                "3. Run database_setup.sql\n\n" +
                "Error: " + e.getMessage()
            );
            alert.showAndWait();
        }
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
            "• Avoid walking through floodwater\n" +
            "• Turn off utilities before evacuating\n\n" +
            "LANDSLIDE SAFETY:\n" +
            "• Watch for cracks in walls and tilting trees\n" +
            "• Evacuate if you hear rumbling sounds\n" +
            "• Move quickly away from the path\n\n" +
            "FIRE SAFETY:\n" +
            "• Call emergency: 911\n" +
            "• Evacuate immediately\n" +
            "• Stay low, cover mouth\n\n" +
            "EARTHQUAKE SAFETY:\n" +
            "• Drop, Cover, Hold On\n" +
            "• Stay away from windows\n" +
            "• Be prepared for aftershocks\n\n" +
            "TYPHOON SAFETY:\n" +
            "• Stay indoors\n" +
            "• Secure loose objects\n" +
            "• Listen to emergency broadcasts\n\n" +
            "Emergency Hotline: 911"
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
    
    public class JavaScriptBridge {
        public void handleMapClick(double lat, double lon) {
            javafx.application.Platform.runLater(() -> {
                selectedLat = lat;
                selectedLon = lon;
                selectedCoordsLabel.setText(String.format("Lat: %.6f, Lon: %.6f", lat, lon));
                updateRadiusCircle();
            });
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
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