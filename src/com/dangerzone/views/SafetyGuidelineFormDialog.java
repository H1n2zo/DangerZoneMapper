package com.dangerzone.views;

import com.dangerzone.models.SafetyGuideline;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

public class SafetyGuidelineFormDialog extends Dialog<SafetyGuideline> {
    
    private TextField titleField;
    private ComboBox<String> hazardTypeCombo;
    private ComboBox<String> categoryCombo;
    private ComboBox<Integer> priorityCombo;
    private TextArea contentArea;
    private TextField targetAudienceField;
    private TextField emergencyContactField;
    private TextField visualAidField;
    private ComboBox<String> languageCombo;
    private CheckBox activeCheckBox;
    
    private SafetyGuideline existingGuideline;
    
    public SafetyGuidelineFormDialog(SafetyGuideline guideline) {
        this.existingGuideline = guideline;
        
        setTitle(guideline == null ? "Add New Safety Guideline" : "Edit Safety Guideline");
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Title
        titleField = new TextField();
        titleField.setPromptText("Enter guideline title");
        titleField.setPrefWidth(400);
        grid.add(new Label("Title:*"), 0, 0);
        grid.add(titleField, 1, 0);
        
        // Hazard Type
        hazardTypeCombo = new ComboBox<>();
        hazardTypeCombo.getItems().addAll("Flood", "Fire", "Landslide", "Storm Surge", "Typhoon", "Earthquake");
        hazardTypeCombo.setPromptText("Select hazard type");
        hazardTypeCombo.setPrefWidth(200);
        grid.add(new Label("Hazard Type:*"), 0, 1);
        grid.add(hazardTypeCombo, 1, 1);
        
        // Category
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Prevention", "During", "After");
        categoryCombo.setPromptText("Select category");
        categoryCombo.setPrefWidth(200);
        grid.add(new Label("Category:*"), 0, 2);
        grid.add(categoryCombo, 1, 2);
        
        // Priority Level
        priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll(1, 2, 3);
        priorityCombo.setPromptText("1=Critical, 2=Important, 3=General");
        priorityCombo.setPrefWidth(200);
        grid.add(new Label("Priority Level:"), 0, 3);
        grid.add(priorityCombo, 1, 3);
        
        // Content
        contentArea = new TextArea();
        contentArea.setPromptText("Enter guideline content...\nUse bullet points with:\n• Point 1\n• Point 2");
        contentArea.setPrefRowCount(8);
        contentArea.setPrefWidth(400);
        contentArea.setWrapText(true);
        grid.add(new Label("Content:*"), 0, 4);
        grid.add(contentArea, 1, 4);
        
        // Target Audience
        targetAudienceField = new TextField();
        targetAudienceField.setPromptText("e.g., General Public, Coastal residents");
        targetAudienceField.setPrefWidth(400);
        grid.add(new Label("Target Audience:"), 0, 5);
        grid.add(targetAudienceField, 1, 5);
        
        // Emergency Contact
        emergencyContactField = new TextField();
        emergencyContactField.setPromptText("e.g., CDRRMO: (053) 561-5027");
        emergencyContactField.setPrefWidth(400);
        grid.add(new Label("Emergency Contact:"), 0, 6);
        grid.add(emergencyContactField, 1, 6);
        
        // Visual Aid URL
        visualAidField = new TextField();
        visualAidField.setPromptText("URL to image/infographic (optional)");
        visualAidField.setPrefWidth(400);
        grid.add(new Label("Visual Aid URL:"), 0, 7);
        grid.add(visualAidField, 1, 7);
        
        // Language
        languageCombo = new ComboBox<>();
        languageCombo.getItems().addAll("English", "Cebuano", "Tagalog");
        languageCombo.setValue("English");
        languageCombo.setPrefWidth(200);
        grid.add(new Label("Language:"), 0, 8);
        grid.add(languageCombo, 1, 8);
        
        // Active Status
        activeCheckBox = new CheckBox("Guideline is Active");
        activeCheckBox.setSelected(true);
        grid.add(activeCheckBox, 1, 9);
        
        // Populate fields if editing
        if (existingGuideline != null) {
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
                return createGuidelineFromForm();
            }
            return null;
        });
    }
    
    private void populateFields() {
        titleField.setText(existingGuideline.getTitle() != null ? existingGuideline.getTitle() : "");
        hazardTypeCombo.setValue(existingGuideline.getHazardType());
        categoryCombo.setValue(existingGuideline.getCategory());
        
        if (existingGuideline.getPriorityLevel() != null) {
            priorityCombo.setValue(existingGuideline.getPriorityLevel());
        }
        
        contentArea.setText(existingGuideline.getContent() != null ? existingGuideline.getContent() : "");
        targetAudienceField.setText(existingGuideline.getTargetAudience() != null ? existingGuideline.getTargetAudience() : "");
        emergencyContactField.setText(existingGuideline.getEmergencyContact() != null ? existingGuideline.getEmergencyContact() : "");
        visualAidField.setText(existingGuideline.getVisualAidUrl() != null ? existingGuideline.getVisualAidUrl() : "");
        languageCombo.setValue(existingGuideline.getLanguage() != null ? existingGuideline.getLanguage() : "English");
        activeCheckBox.setSelected(existingGuideline.isActive());
    }
    
    private boolean validateForm() {
        // Check required fields
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            showValidationError("Title is required");
            return false;
        }
        
        if (hazardTypeCombo.getValue() == null) {
            showValidationError("Hazard type is required");
            return false;
        }
        
        if (categoryCombo.getValue() == null) {
            showValidationError("Category is required");
            return false;
        }
        
        if (contentArea.getText() == null || contentArea.getText().trim().isEmpty()) {
            showValidationError("Content is required");
            return false;
        }
        
        // Check content length
        if (contentArea.getText().trim().length() < 20) {
            showValidationError("Content must be at least 20 characters long");
            return false;
        }
        
        return true;
    }
    
    private SafetyGuideline createGuidelineFromForm() {
        SafetyGuideline guideline = existingGuideline != null ? existingGuideline : new SafetyGuideline();
        
        guideline.setTitle(safeGetText(titleField));
        guideline.setHazardType(hazardTypeCombo.getValue());
        guideline.setCategory(categoryCombo.getValue());
        guideline.setPriorityLevel(priorityCombo.getValue());
        guideline.setContent(safeGetText(contentArea));
        
        String audience = safeGetText(targetAudienceField);
        guideline.setTargetAudience(audience.isEmpty() ? null : audience);
        
        String contact = safeGetText(emergencyContactField);
        guideline.setEmergencyContact(contact.isEmpty() ? null : contact);
        
        String visualAid = safeGetText(visualAidField);
        guideline.setVisualAidUrl(visualAid.isEmpty() ? null : visualAid);
        
        guideline.setLanguage(languageCombo.getValue());
        guideline.setActive(activeCheckBox.isSelected());
        
        return guideline;
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