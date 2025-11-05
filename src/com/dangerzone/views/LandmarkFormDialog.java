/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.views;

import com.dangerzone.models.Landmark;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

public class LandmarkFormDialog extends Dialog<Landmark> {
    
    private TextField nameField;
    private ComboBox<String> typeCombo;
    private TextField addressField;
    private TextField barangayField;
    private TextField latitudeField;
    private TextField longitudeField;
    private TextField contactField;
    private TextField capacityField;
    private TextArea descriptionArea;
    private TextField hoursField;
    private CheckBox evacCheckBox;
    
    private Landmark existingLandmark;
    
    public LandmarkFormDialog(Landmark landmark) {
        this.existingLandmark = landmark;
        
        setTitle(landmark == null ? "Add New Landmark" : "Edit Landmark");
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        nameField = new TextField();
        nameField.setPromptText("Enter landmark name");
        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);
        
        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(
            "Government", "Hospital", "School", "Evacuation Center",
            "Religious", "Commercial", "Park", "Police Station", "Other"
        );
        typeCombo.setPromptText("Select type");
        grid.add(new Label("Type:*"), 0, 1);
        grid.add(typeCombo, 1, 1);
        
        barangayField = new TextField();
        barangayField.setPromptText("Enter barangay name");
        grid.add(new Label("Barangay:*"), 0, 2);
        grid.add(barangayField, 1, 2);
        
        addressField = new TextField();
        addressField.setPromptText("Street address");
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        
        latitudeField = new TextField();
        latitudeField.setPromptText("e.g., 11.0059");
        grid.add(new Label("Latitude:*"), 0, 4);
        grid.add(latitudeField, 1, 4);
        
        longitudeField = new TextField();
        longitudeField.setPromptText("e.g., 124.6075");
        grid.add(new Label("Longitude:*"), 0, 5);
        grid.add(longitudeField, 1, 5);
        
        contactField = new TextField();
        contactField.setPromptText("(053) XXX-XXXX");
        grid.add(new Label("Contact:"), 0, 6);
        grid.add(contactField, 1, 6);
        
        hoursField = new TextField();
        hoursField.setPromptText("e.g., 8:00 AM - 5:00 PM");
        grid.add(new Label("Operating Hours:"), 0, 7);
        grid.add(hoursField, 1, 7);
        
        evacCheckBox = new CheckBox("Is Evacuation Center");
        evacCheckBox.setOnAction(e -> capacityField.setDisable(!evacCheckBox.isSelected()));
        grid.add(evacCheckBox, 1, 8);
        
        capacityField = new TextField();
        capacityField.setPromptText("Person capacity");
        capacityField.setDisable(true);
        grid.add(new Label("Capacity:"), 0, 9);
        grid.add(capacityField, 1, 9);
        
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Additional details...");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        grid.add(new Label("Description:"), 0, 10);
        grid.add(descriptionArea, 1, 10);
        
        if (existingLandmark != null) {
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
                return createLandmarkFromForm();
            }
            return null;
        });
    }
    
    private void populateFields() {
        nameField.setText(existingLandmark.getName());
        typeCombo.setValue(existingLandmark.getType());
        addressField.setText(existingLandmark.getAddress());
        barangayField.setText(existingLandmark.getBarangay());
        latitudeField.setText(String.valueOf(existingLandmark.getLatitude()));
        longitudeField.setText(String.valueOf(existingLandmark.getLongitude()));
        contactField.setText(existingLandmark.getContactNumber());
        hoursField.setText(existingLandmark.getOperatingHours());
        descriptionArea.setText(existingLandmark.getDescription());
        evacCheckBox.setSelected(existingLandmark.isEvacuationSite());
        
        if (existingLandmark.getCapacity() != null) {
            capacityField.setText(String.valueOf(existingLandmark.getCapacity()));
            capacityField.setDisable(false);
        }
    }
    
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showValidationError("Name is required");
            return false;
        }
        
        if (typeCombo.getValue() == null) {
            showValidationError("Type is required");
            return false;
        }
        
        if (barangayField.getText().trim().isEmpty()) {
            showValidationError("Barangay is required");
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
        
        if (evacCheckBox.isSelected() && !capacityField.getText().trim().isEmpty()) {
            try {
                int capacity = Integer.parseInt(capacityField.getText().trim());
                if (capacity <= 0) {
                    showValidationError("Capacity must be positive");
                    return false;
                }
            } catch (NumberFormatException e) {
                showValidationError("Invalid capacity format");
                return false;
            }
        }
        
        return true;
    }
    
    private Landmark createLandmarkFromForm() {
        Landmark landmark = existingLandmark != null ? existingLandmark : new Landmark();
        
        if (existingLandmark != null) {
            System.out.println("🔧 Editing existing landmark - ID: " + existingLandmark.getId());
            landmark.setId(existingLandmark.getId()); // Ensure ID is set!
        }
        
        landmark.setName(nameField.getText().trim());
        landmark.setType(typeCombo.getValue());
        landmark.setAddress(addressField.getText().trim());
        landmark.setBarangay(barangayField.getText().trim());
        landmark.setLatitude(Double.parseDouble(latitudeField.getText().trim()));
        landmark.setLongitude(Double.parseDouble(longitudeField.getText().trim()));
        landmark.setContactNumber(contactField.getText().trim().isEmpty() ? null : contactField.getText().trim());
        landmark.setOperatingHours(hoursField.getText().trim().isEmpty() ? null : hoursField.getText().trim());
        landmark.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());
        landmark.setEvacuationSite(evacCheckBox.isSelected());
        
        if (!capacityField.getText().trim().isEmpty()) {
            try {
                landmark.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            } catch (NumberFormatException e) {
                landmark.setCapacity(null);
            }
        } else {
            landmark.setCapacity(null);
        }
        
        System.out.println("✅ Form data extracted - ID: " + landmark.getId() + ", Name: " + landmark.getName());
        
        return landmark;
    }
    
    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}