package com.dangerzone.views;

import com.dangerzone.models.SafetyGuideline;
import com.dangerzone.models.SafetyGuidelineDAO;
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
import java.util.Optional;

public class SafetyGuidelineManagerDialog extends Stage {
    
    private SafetyGuidelineDAO guidelineDAO;
    private TableView<SafetyGuideline> guidelineTable;
    private ObservableList<SafetyGuideline> guidelineData;
    private ComboBox<String> filterHazardCombo;
    private ComboBox<String> filterCategoryCombo;
    private ComboBox<String> filterPriorityCombo;
    private TextField searchField;
    
    public SafetyGuidelineManagerDialog(Connection connection) {
        this.guidelineDAO = new SafetyGuidelineDAO(connection);
        this.guidelineData = FXCollections.observableArrayList();
        
        setTitle("Safety Guidelines Manager - Ormoc City");
        initModality(Modality.APPLICATION_MODAL);
        setWidth(1300);
        setHeight(750);
        
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + StyleManager.LIGHT_BG + ";");
        
        // TOP BAR
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(15));
        topSection.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0; -fx-border-color: " + StyleManager.BORDER_COLOR + ";");
        
        Label titleLabel = new Label("Safety Guidelines Manager");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";");
        
        HBox filterBox = createFilterControls();
        topSection.getChildren().addAll(titleLabel, filterBox);
        
        // CENTER: Table
        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle("-fx-background-color: white;");
        
        Label tableTitle = new Label("🚨 All Safety Guidelines");
        tableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";");
        
        guidelineTable = createGuidelineTable();
        VBox.setVgrow(guidelineTable, Priority.ALWAYS);
        StyleManager.styleTable(guidelineTable);
        
        tableContainer.getChildren().addAll(tableTitle, guidelineTable);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        // BOTTOM: Buttons
        HBox buttonBox = createButtonControls();
        buttonBox.setPadding(new Insets(15));
        buttonBox.setStyle("-fx-background-color: white; -fx-border-width: 1 0 0 0; -fx-border-color: " + StyleManager.BORDER_COLOR + ";");
        
        root.getChildren().addAll(topSection, tableContainer, buttonBox);
        
        Scene scene = new Scene(root);
        setScene(scene);
        
        loadGuidelines();
    }
    
    private HBox createFilterControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5));
        
        // Search
        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPromptText("Search in title or content...");
        searchField.setPrefWidth(200);
        StyleManager.styleTextField(searchField);
        
        Button searchBtn = new Button("🔍");
        searchBtn.setTooltip(new Tooltip("Search guidelines"));
        StyleManager.stylePrimaryButton(searchBtn);
        searchBtn.setOnAction(e -> performSearch());
        
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        // Hazard Type filter
        Label hazardLabel = new Label("Hazard:");
        filterHazardCombo = new ComboBox<>();
        filterHazardCombo.getItems().add("All");
        loadHazardTypes();
        filterHazardCombo.setValue("All");
        StyleManager.styleComboBox(filterHazardCombo);
        
        // Category filter
        Label categoryLabel = new Label("Category:");
        filterCategoryCombo = new ComboBox<>();
        filterCategoryCombo.getItems().addAll("All", "Prevention", "During", "After");
        filterCategoryCombo.setValue("All");
        StyleManager.styleComboBox(filterCategoryCombo);
        
        // Priority filter
        Label priorityLabel = new Label("Priority:");
        filterPriorityCombo = new ComboBox<>();
        filterPriorityCombo.getItems().addAll("All", "1 - Critical", "2 - Important", "3 - General");
        filterPriorityCombo.setValue("All");
        StyleManager.styleComboBox(filterPriorityCombo);
        
        Button filterBtn = new Button("Apply Filters");
        StyleManager.stylePrimaryButton(filterBtn);
        filterBtn.setOnAction(e -> applyFilters());
        
        Button resetBtn = new Button("Reset");
        StyleManager.styleSecondaryButton(resetBtn);
        resetBtn.setOnAction(e -> {
            filterHazardCombo.setValue("All");
            filterCategoryCombo.setValue("All");
            filterPriorityCombo.setValue("All");
            searchField.clear();
            loadGuidelines();
        });
        
        box.getChildren().addAll(
            searchLabel, searchField, searchBtn, sep1,
            hazardLabel, filterHazardCombo,
            categoryLabel, filterCategoryCombo,
            priorityLabel, filterPriorityCombo,
            filterBtn, resetBtn
        );
        
        return box;
    }
    
    private TableView<SafetyGuideline> createGuidelineTable() {
        TableView<SafetyGuideline> table = new TableView<>();
        table.setItems(guidelineData);
        
        // ID Column
        TableColumn<SafetyGuideline, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("guidelineId"));
        idCol.setPrefWidth(50);
        
        // Title Column
        TableColumn<SafetyGuideline, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);
        titleCol.setCellFactory(column -> new TableCell<SafetyGuideline, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    setTooltip(new Tooltip(item));
                }
            }
        });
        
        // Hazard Type Column
        TableColumn<SafetyGuideline, String> hazardCol = new TableColumn<>("Hazard Type");
        hazardCol.setCellValueFactory(new PropertyValueFactory<>("hazardType"));
        hazardCol.setPrefWidth(100);
        
        // Category Column
        TableColumn<SafetyGuideline, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(90);
        categoryCol.setCellFactory(column -> new TableCell<SafetyGuideline, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "During":
                            setStyle("-fx-background-color: " + StyleManager.DANGER_COLOR + "; -fx-text-fill: white;");
                            break;
                        case "Prevention":
                            setStyle("-fx-background-color: " + StyleManager.SUCCESS_COLOR + "; -fx-text-fill: white;");
                            break;
                        case "After":
                            setStyle("-fx-background-color: " + StyleManager.INFO_COLOR + "; -fx-text-fill: white;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        // Priority Column
        TableColumn<SafetyGuideline, Integer> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));
        priorityCol.setPrefWidth(70);
        priorityCol.setCellFactory(column -> new TableCell<SafetyGuideline, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    if (item == 1) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: " + StyleManager.DANGER_COLOR + ";");
                    } else if (item == 2) {
                        setStyle("-fx-text-fill: " + StyleManager.WARNING_COLOR + ";");
                    } else {
                        setStyle("-fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";");
                    }
                }
            }
        });
        
        // Target Audience Column
        TableColumn<SafetyGuideline, String> audienceCol = new TableColumn<>("Target Audience");
        audienceCol.setCellValueFactory(new PropertyValueFactory<>("targetAudience"));
        audienceCol.setPrefWidth(150);
        
        // Emergency Contact Column
        TableColumn<SafetyGuideline, String> contactCol = new TableColumn<>("Emergency Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("emergencyContact"));
        contactCol.setPrefWidth(150);
        
        // Language Column
        TableColumn<SafetyGuideline, String> langCol = new TableColumn<>("Lang");
        langCol.setCellValueFactory(new PropertyValueFactory<>("language"));
        langCol.setPrefWidth(70);
        
        // Active Column
        TableColumn<SafetyGuideline, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(60);
        activeCol.setCellFactory(column -> new TableCell<SafetyGuideline, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "✓" : "✗");
                    setStyle(item ? 
                        "-fx-text-fill: " + StyleManager.SUCCESS_COLOR + "; -fx-font-weight: bold;" :
                        "-fx-text-fill: " + StyleManager.TEXT_HINT + ";");
                }
            }
        });
        
        table.getColumns().addAll(
            idCol, titleCol, hazardCol, categoryCol, priorityCol,
            audienceCol, contactCol, langCol, activeCol
        );
        
        return table;
    }
    
    private HBox createButtonControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_RIGHT);
        
        Button addBtn = new Button("➕ Add Guideline");
        StyleManager.styleSuccessButton(addBtn);
        addBtn.setOnAction(e -> addGuideline());
        
        Button editBtn = new Button("✏ Edit");
        StyleManager.stylePrimaryButton(editBtn);
        editBtn.setOnAction(e -> editGuideline());
        
        Button deleteBtn = new Button("🗑 Delete");
        StyleManager.styleDangerButton(deleteBtn);
        deleteBtn.setOnAction(e -> deleteGuideline());
        
        Button viewBtn = new Button("👁 View Details");
        StyleManager.styleSecondaryButton(viewBtn);
        viewBtn.setOnAction(e -> viewGuidelineDetails());
        
        Button closeBtn = new Button("Close");
        StyleManager.styleGhostButton(closeBtn);
        closeBtn.setOnAction(e -> close());
        
        box.getChildren().addAll(addBtn, editBtn, deleteBtn, viewBtn, closeBtn);
        return box;
    }
    
    private void loadGuidelines() {
        try {
            List<SafetyGuideline> guidelines = guidelineDAO.getAllGuidelines();
            guidelineData.clear();
            guidelineData.addAll(guidelines);
        } catch (SQLException e) {
            showError("Failed to load guidelines: " + e.getMessage());
        }
    }
    
    private void loadHazardTypes() {
        try {
            List<String> types = guidelineDAO.getHazardTypes();
            filterHazardCombo.getItems().addAll(types);
        } catch (SQLException e) {
            System.err.println("Failed to load hazard types: " + e.getMessage());
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadGuidelines();
            return;
        }
        
        try {
            List<SafetyGuideline> allGuidelines = guidelineDAO.getAllGuidelines();
            guidelineData.clear();
            
            for (SafetyGuideline guideline : allGuidelines) {
                boolean titleMatch = guideline.getTitle().toLowerCase().contains(searchTerm);
                boolean contentMatch = guideline.getContent() != null && 
                                      guideline.getContent().toLowerCase().contains(searchTerm);
                
                if (titleMatch || contentMatch) {
                    guidelineData.add(guideline);
                }
            }
            
            if (guidelineData.isEmpty()) {
                showInfo("No guidelines found matching: \"" + searchField.getText() + "\"");
            }
            
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    private void applyFilters() {
        String hazardFilter = filterHazardCombo.getValue();
        String categoryFilter = filterCategoryCombo.getValue();
        String priorityFilter = filterPriorityCombo.getValue();
        
        try {
            List<SafetyGuideline> allGuidelines = guidelineDAO.getAllGuidelines();
            guidelineData.clear();
            
            for (SafetyGuideline guideline : allGuidelines) {
                boolean hazardMatch = hazardFilter.equals("All") || 
                    guideline.getHazardType().equals(hazardFilter);
                    
                boolean categoryMatch = categoryFilter.equals("All") || 
                    (guideline.getCategory() != null && guideline.getCategory().equals(categoryFilter));
                    
                boolean priorityMatch = priorityFilter.equals("All") || 
                    (guideline.getPriorityLevel() != null && 
                     priorityFilter.startsWith(String.valueOf(guideline.getPriorityLevel())));
                
                if (hazardMatch && categoryMatch && priorityMatch) {
                    guidelineData.add(guideline);
                }
            }
            
            if (guidelineData.isEmpty()) {
                showInfo("No guidelines found with the selected filters");
            }
            
        } catch (SQLException e) {
            showError("Filter failed: " + e.getMessage());
        }
    }
    
    private void addGuideline() {
        SafetyGuidelineFormDialog dialog = new SafetyGuidelineFormDialog(null);
        Optional<SafetyGuideline> result = dialog.showAndWait();
        
        result.ifPresent(guideline -> {
            try {
                if (guidelineDAO.createGuideline(guideline)) {
                    showSuccess("Safety guideline added successfully!");
                    loadGuidelines();
                } else {
                    showError("Failed to add guideline");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
    }
    
    private void editGuideline() {
        SafetyGuideline selected = guidelineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a guideline to edit");
            return;
        }
        
        SafetyGuidelineFormDialog dialog = new SafetyGuidelineFormDialog(selected);
        Optional<SafetyGuideline> result = dialog.showAndWait();
        
        result.ifPresent(guideline -> {
            try {
                boolean success = guidelineDAO.updateGuideline(guideline);
                
                if (success) {
                    showSuccess("Guideline updated successfully!");
                    loadGuidelines();
                } else {
                    showError("Update failed - No rows were affected");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
    }
    
    private void deleteGuideline() {
        SafetyGuideline selected = guidelineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a guideline to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Safety Guideline?");
        confirm.setContentText("Delete: \"" + selected.getTitle() + "\"?\n\nThis action cannot be undone.");
        StyleManager.styleDialog(confirm.getDialogPane());
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (guidelineDAO.deleteGuideline(selected.getGuidelineId())) {
                    showSuccess("Guideline deleted successfully!");
                    loadGuidelines();
                } else {
                    showError("Failed to delete guideline");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }
    
    private void viewGuidelineDetails() {
        SafetyGuideline selected = guidelineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a guideline to view");
            return;
        }
        
        Alert details = new Alert(Alert.AlertType.INFORMATION);
        details.setTitle("Safety Guideline Details");
        details.setHeaderText(selected.getTitle());
        StyleManager.styleDialog(details.getDialogPane());
        
        StringBuilder content = new StringBuilder();
        content.append("ID: ").append(selected.getGuidelineId()).append("\n");
        content.append("Hazard Type: ").append(selected.getHazardType()).append("\n");
        content.append("Category: ").append(selected.getCategory()).append("\n");
        content.append("Priority: ").append(selected.getPriorityLevel()).append("\n");
        content.append("Language: ").append(selected.getLanguage()).append("\n");
        content.append("Active: ").append(selected.isActive() ? "Yes" : "No").append("\n\n");
        
        if (selected.getTargetAudience() != null) {
            content.append("Target Audience:\n").append(selected.getTargetAudience()).append("\n\n");
        }
        
        content.append("Content:\n").append(selected.getContent()).append("\n\n");
        
        if (selected.getEmergencyContact() != null) {
            content.append("Emergency Contact:\n").append(selected.getEmergencyContact()).append("\n\n");
        }
        
        if (selected.getVisualAidUrl() != null) {
            content.append("Visual Aid URL:\n").append(selected.getVisualAidUrl()).append("\n");
        }
        
        TextArea textArea = new TextArea(content.toString());
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
        details.getDialogPane().setContent(textArea);
        details.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        StyleManager.styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        StyleManager.styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        StyleManager.styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        StyleManager.styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }
}