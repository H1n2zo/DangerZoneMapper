package com.dangerzone.views;

import com.dangerzone.models.*;
import com.dangerzone.utils.StyleManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SearchDialog extends Stage {
    
    private Connection connection;
    private TextField searchField;
    private ComboBox<String> searchTypeCombo;
    private TableView<SearchResult> resultsTable;
    private ObservableList<SearchResult> resultsData;
    private Label resultCountLabel;
    
    public SearchDialog(Connection connection) {
        this.connection = connection;
        this.resultsData = FXCollections.observableArrayList();
        
        setTitle("🔍 Global Search - Ormoc City");
        initModality(Modality.NONE);
        setWidth(900);
        setHeight(650);
        
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + StyleManager.LIGHT_BG + ";");
        
        // TOP BAR
        VBox topSection = new VBox(15);
        topSection.setPadding(new Insets(20));
        topSection.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0; -fx-border-color: " + StyleManager.BORDER_COLOR + ";");
        
        Label titleLabel = new Label("🔍 Global Search");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";");
        
        HBox searchBox = createSearchBox();
        
        topSection.getChildren().addAll(titleLabel, searchBox);
        
        // CENTER: Results Table
        VBox centerSection = new VBox(10);
        centerSection.setPadding(new Insets(15));
        centerSection.setStyle("-fx-background-color: white;");
        
        HBox resultsHeader = new HBox(10);
        resultsHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label resultsLabel = new Label("Search Results");
        resultsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";");
        
        resultCountLabel = new Label("0 results");
        resultCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";");
        
        resultsHeader.getChildren().addAll(resultsLabel, resultCountLabel);
        
        resultsTable = createResultsTable();
        VBox.setVgrow(resultsTable, Priority.ALWAYS);
        
        centerSection.getChildren().addAll(resultsHeader, resultsTable);
        VBox.setVgrow(centerSection, Priority.ALWAYS);
        
        // BOTTOM: Button
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setStyle("-fx-background-color: white; -fx-border-width: 1 0 0 0; -fx-border-color: " + StyleManager.BORDER_COLOR + ";");
        
        Button closeBtn = new Button("Close");
        StyleManager.styleSecondaryButton(closeBtn);
        closeBtn.setOnAction(e -> close());
        buttonBox.getChildren().add(closeBtn);
        
        root.getChildren().addAll(topSection, centerSection, buttonBox);
        
        Scene scene = new Scene(root);
        setScene(scene);
    }
    
    private HBox createSearchBox() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Search:");
        searchLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";");
        
        searchField = new TextField();
        searchField.setPromptText("Enter search term...");
        searchField.setPrefWidth(300);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        StyleManager.styleTextField(searchField);
        
        searchTypeCombo = new ComboBox<>();
        searchTypeCombo.getItems().addAll("All", "Landmarks", "Hazard Zones", "Incidents");
        searchTypeCombo.setValue("All");
        StyleManager.styleComboBox(searchTypeCombo);
        
        Button searchBtn = new Button("🔍 Search");
        StyleManager.stylePrimaryButton(searchBtn);
        searchBtn.setOnAction(e -> performSearch());
        
        Button clearBtn = new Button("Clear");
        StyleManager.styleGhostButton(clearBtn);
        clearBtn.setOnAction(e -> clearSearch());
        
        box.getChildren().addAll(searchLabel, searchField, searchTypeCombo, searchBtn, clearBtn);
        
        // Allow Enter key to search
        searchField.setOnAction(e -> performSearch());
        
        return box;
    }
    
    private TableView<SearchResult> createResultsTable() {
        TableView<SearchResult> table = new TableView<>();
        table.setItems(resultsData);
        table.setPlaceholder(new Label("Enter a search term and click Search to see results"));
        StyleManager.styleTable(table);
        
        // Type column with icon
        TableColumn<SearchResult, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(120);
        typeCol.setCellFactory(column -> new TableCell<SearchResult, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    String icon = "";
                    String color = "";
                    switch (item) {
                        case "Landmark":
                            icon = "📍";
                            color = StyleManager.ACCENT_COLOR;
                            break;
                        case "Hazard Zone":
                            icon = "⚠";
                            color = StyleManager.DANGER_COLOR;
                            break;
                        case "Incident":
                            icon = "📋";
                            color = StyleManager.WARNING_COLOR;
                            break;
                    }
                    setText(icon + " " + item);
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });
        
        // Name column
        TableColumn<SearchResult, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        // Category column (type/severity)
        TableColumn<SearchResult, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);
        
        // Location column
        TableColumn<SearchResult, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(150);
        
        // Details column
        TableColumn<SearchResult, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        detailsCol.setPrefWidth(250);
        
        table.getColumns().addAll(typeCol, nameCol, categoryCol, locationCol, detailsCol);
        
        // Allow row selection to show more info
        table.setRowFactory(tv -> {
            TableRow<SearchResult> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    SearchResult result = row.getItem();
                    showResultDetails(result);
                }
            });
            return row;
        });
        
        return table;
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showWarning("Please enter a search term");
            return;
        }
        
        resultsData.clear();
        String searchType = searchTypeCombo.getValue();
        
        try {
            if ("All".equals(searchType) || "Landmarks".equals(searchType)) {
                searchLandmarks(searchTerm);
            }
            
            if ("All".equals(searchType) || "Hazard Zones".equals(searchType)) {
                searchHazardZones(searchTerm);
            }
            
            if ("All".equals(searchType) || "Incidents".equals(searchType)) {
                searchIncidents(searchTerm);
            }
            
            updateResultCount();
            
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    private void searchLandmarks(String searchTerm) throws SQLException {
        LandmarkDAO dao = new LandmarkDAO(connection);
        List<Landmark> landmarks = dao.searchLandmarksByName(searchTerm);
        
        for (Landmark landmark : landmarks) {
            SearchResult result = new SearchResult(
                "Landmark",
                landmark.getName(),
                landmark.getType(),
                landmark.getBarangay(),
                landmark.isEvacuationSite() ? "Evacuation Center - Capacity: " + landmark.getCapacity() : landmark.getDescription()
            );
            resultsData.add(result);
        }
    }
    
    private void searchHazardZones(String searchTerm) throws SQLException {
        HazardZoneDAO dao = new HazardZoneDAO(connection);
        List<HazardZone> zones = dao.getAllHazardZones();
        
        for (HazardZone zone : zones) {
            if (zone.getZoneName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                zone.getBarangay().toLowerCase().contains(searchTerm.toLowerCase()) ||
                zone.getHazardType().toLowerCase().contains(searchTerm.toLowerCase())) {
                
                SearchResult result = new SearchResult(
                    "Hazard Zone",
                    zone.getZoneName(),
                    zone.getHazardType() + " - " + zone.getSeverityLevel(),
                    zone.getBarangay(),
                    "Radius: " + zone.getRadiusMeters() + "m, Pop: " + zone.getAffectedPopulation()
                );
                resultsData.add(result);
            }
        }
    }
    
    private void searchIncidents(String searchTerm) throws SQLException {
        IncidentDAO dao = new IncidentDAO(connection);
        List<Incident> incidents = dao.getAllIncidents();
        
        for (Incident incident : incidents) {
            if (incident.getBarangay().toLowerCase().contains(searchTerm.toLowerCase()) ||
                incident.getIncidentType().toLowerCase().contains(searchTerm.toLowerCase()) ||
                (incident.getDescription() != null && incident.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))) {
                
                SearchResult result = new SearchResult(
                    "Incident",
                    incident.getIncidentType(),
                    incident.getSeverity(),
                    incident.getBarangay(),
                    incident.getIncidentDate() + " - " + incident.getCasualties() + " casualties, " + incident.getInjuries() + " injuries"
                );
                resultsData.add(result);
            }
        }
    }
    
    private void updateResultCount() {
        int count = resultsData.size();
        resultCountLabel.setText(count + (count == 1 ? " result" : " results"));
        
        if (count == 0) {
            resultsTable.setPlaceholder(new Label("No results found for: \"" + searchField.getText() + "\""));
        }
    }
    
    private void showResultDetails(SearchResult result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Result Details");
        alert.setHeaderText(result.getName());
        StyleManager.styleDialog(alert.getDialogPane());
        
        StringBuilder content = new StringBuilder();
        content.append("Type: ").append(result.getType()).append("\n");
        content.append("Category: ").append(result.getCategory()).append("\n");
        content.append("Location: ").append(result.getLocation()).append("\n");
        content.append("\nDetails:\n").append(result.getDetails());
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    private void clearSearch() {
        searchField.clear();
        resultsData.clear();
        resultCountLabel.setText("0 results");
        resultsTable.setPlaceholder(new Label("Enter a search term and click Search to see results"));
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Inner class for search results
    public static class SearchResult {
        private String type;
        private String name;
        private String category;
        private String location;
        private String details;
        
        public SearchResult(String type, String name, String category, String location, String details) {
            this.type = type;
            this.name = name;
            this.category = category;
            this.location = location;
            this.details = details != null ? details : "";
        }
        
        public String getType() { return type; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getLocation() { return location; }
        public String getDetails() { return details; }
    }
}