package com.dangerzone.views;

import com.dangerzone.models.EmergencyContact;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

public class EmergencyContactFormDialog extends Dialog<EmergencyContact> {
    
    private TextField departmentNameField;
    private ComboBox<String> contactTypeCombo;
    private TextField contactNumberField;
    private TextField alternateNumberField;
    private TextField emailField;
    private TextArea addressArea;
    private TextArea descriptionArea;
    private TextField operatingHoursField;
    private Spinner<Integer> prioritySpinner;
    private CheckBox activeCheckBox;
    
    private EmergencyContact existingContact;
    
    public EmergencyContactFormDialog(EmergencyContact contact) {
        this.existingContact = contact;
        
        setTitle(contact == null ? "Add New Emergency Contact" : "Edit Emergency Contact");
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Department Name
        departmentNameField = new TextField();
        departmentNameField.setPromptText("e.g., CDRRMO, Fire Department");
        departmentNameField.setPrefWidth(350);
        grid.add(new Label("Department Name:*"), 0, 0);
        grid.add(departmentNameField, 1, 0);
        
        // Contact Type
        contactTypeCombo = new ComboBox<>();
        contactTypeCombo.getItems().addAll("Emergency", "Medical", "Rescue", "Utility", "Information", "Social Services");
        contactTypeCombo.setPromptText("Select type");
        contactTypeCombo.setPrefWidth(200);
        grid.add(new Label("Contact Type:*"), 0, 1);
        grid.add(contactTypeCombo, 1, 1);
        
        // Primary Contact Number
        contactNumberField = new TextField();
        contactNumberField.setPromptText("(053) XXX-XXXX or 3-digit");
        contactNumberField.setPrefWidth(200);
        grid.add(new Label("Contact Number:*"), 0, 2);
        grid.add(contactNumberField, 1, 2);
        
        // Alternate Number
        alternateNumberField = new TextField();
        alternateNumberField.setPromptText("Optional alternate number");
        alternateNumberField.setPrefWidth(200);
        grid.add(new Label("Alternate Number:"), 0, 3);
        grid.add(alternateNumberField, 1, 3);
        
        // Email
        emailField = new TextField();
        emailField.setPromptText("email@example.com");
        emailField.setPrefWidth(300);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        
        // Address
        addressArea = new TextArea();
        addressArea.setPromptText("Physical address...");
        addressArea.setPrefRowCount(2);
        addressArea.setPrefWidth(350);
        addressArea.setWrapText(true);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressArea, 1, 5);
        
        // Description
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Brief description of services...");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setPrefWidth(350);
        descriptionArea.setWrapText(true);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descriptionArea, 1, 6);
        
        // Operating Hours
        operatingHoursField = new TextField();
        operatingHoursField.setPromptText("e.g., 24/7 or Office Hours");
        operatingHoursField.setText("24/7");
        operatingHoursField.setPrefWidth(200);
        grid.add(new Label("Operating Hours:"), 0, 7);
        grid.add(operatingHoursField, 1, 7);
        
        // Priority Order
        prioritySpinner = new Spinner<>(1, 100, 10, 1);
        prioritySpinner.setEditable(true);
        prioritySpinner.setPrefWidth(100);
        Label priorityHelp = new Label("(Lower = Higher Priority)");
        priorityHelp.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        grid.add(new Label("Priority Order:"), 0, 8);
        grid.add(prioritySpinner, 1, 8);
        grid.add(priorityHelp, 1, 9);
        
        // Active Status
        activeCheckBox = new CheckBox("Contact is Active");
        activeCheckBox.setSelected(true);
        grid.add(activeCheckBox, 1, 10);
        
        // Populate fields if editing
        if (existingContact != null) {
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
                return createContactFromForm();
            }
            return null;
        });
    }
    
    private void populateFields() {
        departmentNameField.setText(existingContact.getDepartmentName() != null ? existingContact.getDepartmentName() : "");
        contactTypeCombo.setValue(existingContact.getContactType());
        contactNumberField.setText(existingContact.getContactNumber() != null ? existingContact.getContactNumber() : "");
        alternateNumberField.setText(existingContact.getAlternateNumber() != null ? existingContact.getAlternateNumber() : "");
        emailField.setText(existingContact.getEmail() != null ? existingContact.getEmail() : "");
        addressArea.setText(existingContact.getAddress() != null ? existingContact.getAddress() : "");
        descriptionArea.setText(existingContact.getDescription() != null ? existingContact.getDescription() : "");
        operatingHoursField.setText(existingContact.getOperatingHours() != null ? existingContact.getOperatingHours() : "24/7");
        prioritySpinner.getValueFactory().setValue(existingContact.getPriorityOrder());
        activeCheckBox.setSelected(existingContact.isActive());
    }
    
    private boolean validateForm() {
        if (departmentNameField.getText() == null || departmentNameField.getText().trim().isEmpty()) {
            showValidationError("Department name is required");
            return false;
        }
        
        if (contactTypeCombo.getValue() == null) {
            showValidationError("Contact type is required");
            return false;
        }
        
        if (contactNumberField.getText() == null || contactNumberField.getText().trim().isEmpty()) {
            showValidationError("Contact number is required");
            return false;
        }
        
        // Validate email format if provided
        String email = emailField.getText();
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showValidationError("Invalid email format");
                return false;
            }
        }
        
        return true;
    }
    
    private EmergencyContact createContactFromForm() {
        EmergencyContact contact = existingContact != null ? existingContact : new EmergencyContact();
        
        contact.setDepartmentName(safeGetText(departmentNameField));
        contact.setContactType(contactTypeCombo.getValue());
        contact.setContactNumber(safeGetText(contactNumberField));
        
        String alternate = safeGetText(alternateNumberField);
        contact.setAlternateNumber(alternate.isEmpty() ? null : alternate);
        
        String email = safeGetText(emailField);
        contact.setEmail(email.isEmpty() ? null : email);
        
        String address = safeGetText(addressArea);
        contact.setAddress(address.isEmpty() ? null : address);
        
        String description = safeGetText(descriptionArea);
        contact.setDescription(description.isEmpty() ? null : description);
        
        String hours = safeGetText(operatingHoursField);
        contact.setOperatingHours(hours.isEmpty() ? "24/7" : hours);
        
        contact.setPriorityOrder(prioritySpinner.getValue());
        contact.setActive(activeCheckBox.isSelected());
        
        return contact;
    }
    
    private String safeGetText(TextField field) {
        String text = field.getText();
        return text == null ? "" : text.trim();
    }
    
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