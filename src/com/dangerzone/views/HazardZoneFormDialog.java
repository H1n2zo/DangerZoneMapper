package com.dangerzone.views;

import com.dangerzone.models.HazardZone;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import java.sql.Date;
import java.time.LocalDate;

public class HazardZoneFormDialog extends Dialog<HazardZone> {
    
    private TextField zoneNameField;
    private TextField barangayField;
    private ComboBox<String> hazardTypeCombo;
    private ComboBox<String> severityCombo;
    private TextField latitudeField;
    private TextField longitudeField;
    private TextField radiusField;
    private TextArea descriptionArea;
    private TextArea riskFactorsArea;
    private TextField populationField;
    private DatePicker dateIdentifiedPicker;
    private CheckBox activeCheckBox;
    
    private HazardZone existingZone;
    
    public HazardZoneFormDialog(HazardZone zone) {
        this.existingZone = zone;
        
        setTitle(zone == null ? "Add New Hazard Zone" : "Edit Hazard Zone");
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Zone Name
        zoneNameField = new TextField();
        zoneNameField.setPromptText("Enter zone name");
        grid.add(new Label("Zone Name:*"), 0, 0);
        grid.add(zoneNameField, 1, 0);
        
        // Barangay
        barangayField = new TextField();
        barangayField.setPromptText("Enter barangay");
        grid.add(new Label("Barangay:*"), 0, 1);
        grid.add(barangayField, 1, 1);
        
        // Hazard Type
        hazardTypeCombo = new ComboBox<>();
        hazardTypeCombo.getItems().addAll("Flood", "Fire", "Landslide", "Storm Surge", "Earthquake", "Typhoon");
        hazardTypeCombo.setPromptText("Select hazard type");
        grid.add(new Label("Hazard Type:*"), 0, 2);
        grid.add(hazardTypeCombo, 1, 2);
        
        // Severity Level
        severityCombo = new ComboBox<>();
        severityCombo.getItems().addAll("Low", "Medium", "High", "Critical");
        severityCombo.setPromptText("Select severity");
        grid.add(new Label("Severity Level:*"), 0, 3);
        grid.add(severityCombo, 1, 3);
        
        // Latitude
        latitudeField = new TextField();
        latitudeField.setPromptText("e.g., 11.0059");
        grid.add(new Label("Latitude:*"), 0, 4);
        grid.add(latitudeField, 1, 4);
        
        // Longitude
        longitudeField = new TextField();
        longitudeField.setPromptText("e.g., 124.6075");
        grid.add(new Label("Longitude:*"), 0, 5);
        grid.add(longitudeField, 1, 5);
        
        // Radius (meters)
        radiusField = new TextField();
        radiusField.setPromptText("Affected area radius in meters");
        grid.add(new Label("Radius (meters):*"), 0, 6);
        grid.add(radiusField, 1, 6);
        
        // Affected Population
        populationField = new TextField();
        populationField.setPromptText("Estimated affected population");
        grid.add(new Label("Affected Population:"), 0, 7);
        grid.add(populationField, 1, 7);
        
        // Date Identified
        dateIdentifiedPicker = new DatePicker();
        dateIdentifiedPicker.setValue(LocalDate.now());
        grid.add(new Label("Date Identified:"), 0, 8);
        grid.add(dateIdentifiedPicker, 1, 8);
        
        // Active Status
        activeCheckBox = new CheckBox("Zone is Active");
        activeCheckBox.setSelected(true);
        grid.add(activeCheckBox, 1, 9);
        
        // Description
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Describe the hazard zone...");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        grid.add(new Label("Description:"), 0, 10);
        grid.add(descriptionArea, 1, 10);
        
        // Risk Factors
        riskFactorsArea = new TextArea();
        riskFactorsArea.setPromptText("List risk factors (comma-separated)");
        riskFactorsArea.setPrefRowCount(2);
        riskFactorsArea.setWrapText(true);
        grid.add(new Label("Risk Factors:"), 0, 11);
        grid.add(riskFactorsArea, 1, 11);
        
        if (existingZone != null) {
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
                return createHazardZoneFromForm();
            }
            return null;
        });
    }
    
    private void populateFields() {
        zoneNameField.setText(existingZone.getZoneName() != null ? existingZone.getZoneName() : "");
        barangayField.setText(existingZone.getBarangay() != null ? existingZone.getBarangay() : "");
        hazardTypeCombo.setValue(existingZone.getHazardType());
        severityCombo.setValue(existingZone.getSeverityLevel());
        latitudeField.setText(String.valueOf(existingZone.getLatitude()));
        longitudeField.setText(String.valueOf(existingZone.getLongitude()));
        radiusField.setText(String.valueOf(existingZone.getRadiusMeters()));
        populationField.setText(String.valueOf(existingZone.getAffectedPopulation()));
        descriptionArea.setText(existingZone.getDescription() != null ? existingZone.getDescription() : "");
        riskFactorsArea.setText(existingZone.getRiskFactors() != null ? existingZone.getRiskFactors() : "");
        activeCheckBox.setSelected(existingZone.isActive());
        
        if (existingZone.getDateIdentified() != null) {
            dateIdentifiedPicker.setValue(existingZone.getDateIdentified().toLocalDate());
        }
    }
    
    private boolean validateForm() {
        if (zoneNameField.getText() == null || zoneNameField.getText().trim().isEmpty()) {
            showValidationError("Zone name is required");
            return false;
        }
        
        if (barangayField.getText() == null || barangayField.getText().trim().isEmpty()) {
            showValidationError("Barangay is required");
            return false;
        }
        
        if (hazardTypeCombo.getValue() == null) {
            showValidationError("Hazard type is required");
            return false;
        }
        
        if (severityCombo.getValue() == null) {
            showValidationError("Severity level is required");
            return false;
        }
        
        try {
            double lat = Double.parseDouble(latitudeField.getText().trim());
            if (lat < 10.5 || lat > 11.5) {
                showValidationError("Latitude should be between 10.5 and 11.5 for Ormoc");
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Invalid latitude format");
            return false;
        }
        
        try {
            double lon = Double.parseDouble(longitudeField.getText().trim());
            if (lon < 124.0 || lon > 125.5) {
                showValidationError("Longitude should be between 124.0 and 125.5 for Ormoc");
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Invalid longitude format");
            return false;
        }
        
        try {
            int radius = Integer.parseInt(radiusField.getText().trim());
            if (radius <= 0) {
                showValidationError("Radius must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Invalid radius format");
            return false;
        }
        
        if (populationField.getText() != null && !populationField.getText().trim().isEmpty()) {
            try {
                int pop = Integer.parseInt(populationField.getText().trim());
                if (pop < 0) {
                    showValidationError("Population cannot be negative");
                    return false;
                }
            } catch (NumberFormatException e) {
                showValidationError("Invalid population format");
                return false;
            }
        }
        
        return true;
    }
    
    private HazardZone createHazardZoneFromForm() {
        HazardZone zone = existingZone != null ? existingZone : new HazardZone();
        
        zone.setZoneName(safeGetText(zoneNameField));
        zone.setBarangay(safeGetText(barangayField));
        zone.setHazardType(hazardTypeCombo.getValue());
        zone.setSeverityLevel(severityCombo.getValue());
        zone.setLatitude(Double.parseDouble(latitudeField.getText().trim()));
        zone.setLongitude(Double.parseDouble(longitudeField.getText().trim()));
        zone.setRadiusMeters(Integer.parseInt(radiusField.getText().trim()));
        
        String popText = safeGetText(populationField);
        if (!popText.isEmpty()) {
            zone.setAffectedPopulation(Integer.parseInt(popText));
        } else {
            zone.setAffectedPopulation(0);
        }
        
        String description = safeGetText(descriptionArea);
        zone.setDescription(description.isEmpty() ? null : description);
        
        String riskFactors = safeGetText(riskFactorsArea);
        zone.setRiskFactors(riskFactors.isEmpty() ? null : riskFactors);
        
        zone.setActive(activeCheckBox.isSelected());
        
        if (dateIdentifiedPicker.getValue() != null) {
            zone.setDateIdentified(Date.valueOf(dateIdentifiedPicker.getValue()));
        }
        
        return zone;
    }
    
    /**
     * Safely get text from TextField, returning empty string if null
     */
    private String safeGetText(TextField field) {
        String text = field.getText();
        return text == null ? "" : text.trim();
    }
    
    /**
     * Safely get text from TextArea, returning empty string if null
     */
    private String safeGetText(TextArea area) {
        String text = area.getText();
        return text == null ? "" : text.trim();
    }
    
    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}