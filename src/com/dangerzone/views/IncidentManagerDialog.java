/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.views;

import com.dangerzone.models.Incident;
import com.dangerzone.models.IncidentDAO;
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
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class IncidentManagerDialog extends Stage {
    
    private IncidentDAO incidentDAO;
    private TableView<Incident> incidentTable;
    private ObservableList<Incident> incidentData;
    private ComboBox<String> filterYearCombo;
    private ComboBox<String> filterTypeCombo;
    
    public IncidentManagerDialog(Connection connection) {
        this.incidentDAO = new IncidentDAO(connection);
        this.incidentData = FXCollections.observableArrayList();
        
        setTitle("Historical Incidents Manager - Ormoc City");
        initModality(Modality.APPLICATION_MODAL);
        setWidth(1200);
        setHeight(700);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        
        Label titleLabel = new Label("Manage Historical Incidents - Ormoc City");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        HBox filterBox = createFilterControls();
        
        incidentTable = createIncidentTable();
        VBox.setVgrow(incidentTable, Priority.ALWAYS);
        
        HBox buttonBox = createButtonControls();
        
        root.getChildren().addAll(titleLabel, filterBox, incidentTable, buttonBox);
        
        Scene scene = new Scene(root);
        setScene(scene);
        
        loadIncidents();
    }
    
    private HBox createFilterControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5));
        
        Label yearLabel = new Label("Year:");
        filterYearCombo = new ComboBox<>();
        filterYearCombo.getItems().addAll("All", "2024", "2023", "2022", "2021", "2020", "2019", "2017", "2013", "2011", "1991");
        filterYearCombo.setValue("All");
        
        Label typeLabel = new Label("Type:");
        filterTypeCombo = new ComboBox<>();
        filterTypeCombo.getItems().addAll("All", "Flood", "Fire", "Landslide", "Storm Surge", "Typhoon");
        filterTypeCombo.setValue("All");
        
        Button filterBtn = new Button("Apply Filter");
        filterBtn.setOnAction(e -> applyFilters());
        
        Button resetBtn = new Button("Show All");
        resetBtn.setOnAction(e -> loadIncidents());
        
        box.getChildren().addAll(yearLabel, filterYearCombo, typeLabel, filterTypeCombo, filterBtn, resetBtn);
        
        return box;
    }
    
    private TableView<Incident> createIncidentTable() {
        TableView<Incident> table = new TableView<>();
        table.setItems(incidentData);
        
        TableColumn<Incident, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Incident, Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("incidentDate"));
        dateCol.setPrefWidth(100);
        
        TableColumn<Incident, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        typeCol.setPrefWidth(100);
        
        TableColumn<Incident, String> barangayCol = new TableColumn<>("Barangay");
        barangayCol.setCellValueFactory(new PropertyValueFactory<>("barangay"));
        barangayCol.setPrefWidth(120);
        
        TableColumn<Incident, String> severityCol = new TableColumn<>("Severity");
        severityCol.setCellValueFactory(new PropertyValueFactory<>("severity"));
        severityCol.setPrefWidth(80);
        
        TableColumn<Incident, Integer> casualtiesCol = new TableColumn<>("Casualties");
        casualtiesCol.setCellValueFactory(new PropertyValueFactory<>("casualties"));
        casualtiesCol.setPrefWidth(80);
        
        TableColumn<Incident, Integer> injuriesCol = new TableColumn<>("Injuries");
        injuriesCol.setCellValueFactory(new PropertyValueFactory<>("injuries"));
        injuriesCol.setPrefWidth(80);
        
        TableColumn<Incident, Integer> familiesCol = new TableColumn<>("Families");
        familiesCol.setCellValueFactory(new PropertyValueFactory<>("familiesAffected"));
        familiesCol.setPrefWidth(80);
        
        TableColumn<Incident, Integer> structuresCol = new TableColumn<>("Structures");
        structuresCol.setCellValueFactory(new PropertyValueFactory<>("structuresDamaged"));
        structuresCol.setPrefWidth(90);
        
        table.getColumns().addAll(idCol, dateCol, typeCol, barangayCol, severityCol, 
                                  casualtiesCol, injuriesCol, familiesCol, structuresCol);
        
        return table;
    }
    
    private HBox createButtonControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(5));
        
        Button addBtn = new Button("Add New Incident");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setOnAction(e -> addIncident());
//        addBtn.getStyleClass().add("success");
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editIncident());
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteIncident());
//        deleteBtn.getStyleClass().add("danger");
        
        Button viewBtn = new Button("View Details");
        viewBtn.setOnAction(e -> viewIncidentDetails());
        
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> close());
//        closeBtn.getStyleClass().add("secondary");
        
        box.getChildren().addAll(addBtn, editBtn, deleteBtn, viewBtn, closeBtn);
        
        return box;
    }
    
    private void loadIncidents() {
        try {
            List<Incident> incidents = incidentDAO.getAllIncidents();
            incidentData.clear();
            incidentData.addAll(incidents);
        } catch (SQLException e) {
            showError("Failed to load incidents: " + e.getMessage());
        }
    }
    
    private void applyFilters() {
        String year = filterYearCombo.getValue();
        String type = filterTypeCombo.getValue();
        
        try {
            List<Incident> incidents = incidentDAO.getAllIncidents();
            incidentData.clear();
            
            for (Incident incident : incidents) {
                boolean yearMatch = year.equals("All") || 
                    (incident.getIncidentDate() != null && 
                     incident.getIncidentDate().toString().startsWith(year));
                boolean typeMatch = type.equals("All") || 
                    incident.getIncidentType().equals(type);
                
                if (yearMatch && typeMatch) {
                    incidentData.add(incident);
                }
            }
        } catch (SQLException e) {
            showError("Filter failed: " + e.getMessage());
        }
    }
    
    private void addIncident() {
        IncidentFormDialog dialog = new IncidentFormDialog(null);
        Optional<Incident> result = dialog.showAndWait();
        
        result.ifPresent(incident -> {
            try {
                if (incidentDAO.createIncident(incident)) {
                    showSuccess("Incident added successfully!");
                    loadIncidents();
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
    }
    
private void editIncident() {
    Incident selected = incidentTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showWarning("Please select an incident to edit");
        return;
    }
    
    System.out.println("📝 Editing Incident ID: " + selected.getIncidentId());
    
    IncidentFormDialog dialog = new IncidentFormDialog(selected);
    Optional<Incident> result = dialog.showAndWait();
    
    result.ifPresent(incident -> {
        try {
            System.out.println("💾 Saving changes to Incident ID: " + incident.getIncidentId());
            System.out.println("   Type: " + incident.getIncidentType());
            System.out.println("   Date: " + incident.getIncidentDate());
            System.out.println("   Barangay: " + incident.getBarangay());
            
            boolean success = incidentDAO.updateIncident(incident);
            
            if (success) {
                showSuccess("Incident updated successfully!");
                loadIncidents(); // Refresh table
                System.out.println("✅ Incident updated and table refreshed");
            } else {
                showError("Update failed - No rows were affected. Check if incident ID exists.");
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
    
    private void deleteIncident() {
        Incident selected = incidentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an incident to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Delete this incident record?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (incidentDAO.deleteIncident(selected.getIncidentId())) {
                    showSuccess("Incident deleted!");
                    loadIncidents();
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }
    
    private void viewIncidentDetails() {
        Incident selected = incidentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an incident to view");
            return;
        }
        
        Alert details = new Alert(Alert.AlertType.INFORMATION);
        details.setTitle("Incident Details");
        details.setHeaderText(selected.getIncidentType() + " - " + selected.getBarangay());
        
        StringBuilder content = new StringBuilder();
        content.append("Date: ").append(selected.getIncidentDate()).append("\n");
        content.append("Severity: ").append(selected.getSeverity()).append("\n\n");
        content.append("Impact:\n");
        content.append("- Casualties: ").append(selected.getCasualties()).append("\n");
        content.append("- Injuries: ").append(selected.getInjuries()).append("\n");
        content.append("- Families Affected: ").append(selected.getFamiliesAffected()).append("\n");
        content.append("- Structures Damaged: ").append(selected.getStructuresDamaged()).append("\n");
        content.append("- Estimated Cost: ").append(selected.getFormattedCost()).append("\n\n");
        
        if (selected.getDescription() != null) {
            content.append("Description:\n").append(selected.getDescription()).append("\n\n");
        }
        
        if (selected.getResponseActions() != null) {
            content.append("Response:\n").append(selected.getResponseActions());
        }
        
        details.setContentText(content.toString());
        details.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}