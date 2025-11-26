package com.dangerzone.views;

import com.dangerzone.models.Incident;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class IncidentFormDialog extends Dialog<Incident> {
    
    private ComboBox<String> incidentTypeCombo;
    private DatePicker datePicker;
    private TextField barangayField;
    private ComboBox<String> severityCombo;
    private TextField casualtiesField;
    private TextField injuriesField;
    private TextField familiesField;
    private TextField structuresField;
    private TextField costField;
    private TextArea descriptionArea;
    private TextArea responseArea;
    
    private Incident existingIncident;
    
    public IncidentFormDialog(Incident incident) {
        this.existingIncident = incident;
        
        setTitle(incident == null ? "Add New Incident" : "Edit Incident");
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        incidentTypeCombo = new ComboBox<>();
        incidentTypeCombo.getItems().addAll("Flood", "Fire", "Landslide", "Storm Surge", "Typhoon", "Earthquake");
        grid.add(new Label("Incident Type:*"), 0, 0);
        grid.add(incidentTypeCombo, 1, 0);
        
        datePicker = new DatePicker(LocalDate.now());
        grid.add(new Label("Incident Date:*"), 0, 1);
        grid.add(datePicker, 1, 1);
        
        barangayField = new TextField();
        barangayField.setPromptText("Enter barangay");
        grid.add(new Label("Barangay:*"), 0, 2);
        grid.add(barangayField, 1, 2);
        
        severityCombo = new ComboBox<>();
        severityCombo.getItems().addAll("Low", "Medium", "High", "Critical");
        grid.add(new Label("Severity:"), 0, 3);
        grid.add(severityCombo, 1, 3);
        
        casualtiesField = new TextField("0");
        grid.add(new Label("Casualties:"), 0, 4);
        grid.add(casualtiesField, 1, 4);
        
        injuriesField = new TextField("0");
        grid.add(new Label("Injuries:"), 0, 5);
        grid.add(injuriesField, 1, 5);
        
        familiesField = new TextField("0");
        grid.add(new Label("Families Affected:"), 0, 6);
        grid.add(familiesField, 1, 6);
        
        structuresField = new TextField("0");
        grid.add(new Label("Structures Damaged:"), 0, 7);
        grid.add(structuresField, 1, 7);
        
        costField = new TextField("0");
        costField.setPromptText("Estimated cost in PHP");
        grid.add(new Label("Estimated Cost (₱):"), 0, 8);
        grid.add(costField, 1, 8);
        
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Describe the incident...");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        grid.add(new Label("Description:"), 0, 9);
        grid.add(descriptionArea, 1, 9);
        
        responseArea = new TextArea();
        responseArea.setPromptText("Response actions taken...");
        responseArea.setPrefRowCount(2);
        responseArea.setWrapText(true);
        grid.add(new Label("Response Actions:"), 0, 10);
        grid.add(responseArea, 1, 10);
        
        if (existingIncident != null) {
            populateFields();
        }
        
        getDialogPane().setContent(grid);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateForm()) {
                event.consume();
            }
        });
        
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createIncidentFromForm();
            }
            return null;
        });
    }
    
    private void populateFields() {
        incidentTypeCombo.setValue(existingIncident.getIncidentType());
        if (existingIncident.getIncidentDate() != null) {
            datePicker.setValue(existingIncident.getIncidentDate().toLocalDate());
        }
        barangayField.setText(existingIncident.getBarangay() != null ? existingIncident.getBarangay() : "");
        severityCombo.setValue(existingIncident.getSeverity());
        casualtiesField.setText(String.valueOf(existingIncident.getCasualties()));
        injuriesField.setText(String.valueOf(existingIncident.getInjuries()));
        familiesField.setText(String.valueOf(existingIncident.getFamiliesAffected()));
        structuresField.setText(String.valueOf(existingIncident.getStructuresDamaged()));
        if (existingIncident.getEstimatedCost() != null) {
            costField.setText(existingIncident.getEstimatedCost().toString());
        }
        descriptionArea.setText(existingIncident.getDescription() != null ? existingIncident.getDescription() : "");
        responseArea.setText(existingIncident.getResponseActions() != null ? existingIncident.getResponseActions() : "");
    }
    
    private boolean validateForm() {
        if (incidentTypeCombo.getValue() == null) {
            showError("Incident type is required");
            return false;
        }
        if (barangayField.getText() == null || barangayField.getText().trim().isEmpty()) {
            showError("Barangay is required");
            return false;
        }
        try {
            Integer.parseInt(safeGetText(casualtiesField));
            Integer.parseInt(safeGetText(injuriesField));
            Integer.parseInt(safeGetText(familiesField));
            Integer.parseInt(safeGetText(structuresField));
            new BigDecimal(safeGetText(costField));
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers");
            return false;
        }
        return true;
    }
    
    private Incident createIncidentFromForm() {
        Incident incident = existingIncident != null ? existingIncident : new Incident();
        
        incident.setIncidentType(incidentTypeCombo.getValue());
        incident.setIncidentDate(Date.valueOf(datePicker.getValue()));
        incident.setBarangay(safeGetText(barangayField));
        incident.setSeverity(severityCombo.getValue());
        incident.setCasualties(Integer.parseInt(safeGetText(casualtiesField)));
        incident.setInjuries(Integer.parseInt(safeGetText(injuriesField)));
        incident.setFamiliesAffected(Integer.parseInt(safeGetText(familiesField)));
        incident.setStructuresDamaged(Integer.parseInt(safeGetText(structuresField)));
        incident.setEstimatedCost(new BigDecimal(safeGetText(costField)));
        
        String description = safeGetText(descriptionArea);
        incident.setDescription(description.isEmpty() ? null : description);
        
        String response = safeGetText(responseArea);
        incident.setResponseActions(response.isEmpty() ? null : response);
        
        return incident;
    }
    
    /**
     * Safely get text from TextField, returning "0" if null or empty for number fields
     */
    private String safeGetText(TextField field) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            // Return "0" for numeric fields to prevent parsing errors
            return "0";
        }
        return text.trim();
    }
    
    /**
     * Safely get text from TextArea, returning empty string if null
     */
    private String safeGetText(TextArea area) {
        String text = area.getText();
        return text == null ? "" : text.trim();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}