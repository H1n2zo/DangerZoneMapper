package com.dangerzone.views;

import com.dangerzone.models.EmergencyContact;
import com.dangerzone.models.EmergencyContactDAO;
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

public class EmergencyContactManagerDialog extends Stage {
    
    private EmergencyContactDAO contactDAO;
    private TableView<EmergencyContact> contactTable;
    private ObservableList<EmergencyContact> contactData;
    private ComboBox<String> filterTypeCombo;
    private TextField searchField;
    
    public EmergencyContactManagerDialog(Connection connection) {
        this.contactDAO = new EmergencyContactDAO(connection);
        this.contactData = FXCollections.observableArrayList();
        
        setTitle("Emergency Contacts Manager - Ormoc City");
        initModality(Modality.APPLICATION_MODAL);
        setWidth(1200);
        setHeight(700);
        
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + StyleManager.LIGHT_BG + ";");
        
        // TOP BAR
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(15));
        topSection.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0; -fx-border-color: " + StyleManager.BORDER_COLOR + ";");
        
        Label titleLabel = new Label("Emergency Contacts Manager");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";");
        
        HBox filterBox = createFilterControls();
        topSection.getChildren().addAll(titleLabel, filterBox);
        
        // CENTER: Table
        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle("-fx-background-color: white;");
        
        Label tableTitle = new Label("📞 All Emergency Contacts");
        tableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";");
        
        contactTable = createContactTable();
        VBox.setVgrow(contactTable, Priority.ALWAYS);
        StyleManager.styleTable(contactTable);
        
        tableContainer.getChildren().addAll(tableTitle, contactTable);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        // BOTTOM: Buttons
        HBox buttonBox = createButtonControls();
        buttonBox.setPadding(new Insets(15));
        buttonBox.setStyle("-fx-background-color: white; -fx-border-width: 1 0 0 0; -fx-border-color: " + StyleManager.BORDER_COLOR + ";");
        
        root.getChildren().addAll(topSection, tableContainer, buttonBox);
        
        Scene scene = new Scene(root);
        setScene(scene);
        
        loadContacts();
    }
    
    private HBox createFilterControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5));
        
        // Search
        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPromptText("Search department or number...");
        searchField.setPrefWidth(200);
        StyleManager.styleTextField(searchField);
        
        Button searchBtn = new Button("🔍");
        searchBtn.setTooltip(new Tooltip("Search contacts"));
        StyleManager.stylePrimaryButton(searchBtn);
        searchBtn.setOnAction(e -> performSearch());
        
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        // Type filter
        Label typeLabel = new Label("Type:");
        filterTypeCombo = new ComboBox<>();
        filterTypeCombo.getItems().add("All");
        loadContactTypes();
        filterTypeCombo.setValue("All");
        StyleManager.styleComboBox(filterTypeCombo);
        
        Button filterBtn = new Button("Apply Filter");
        StyleManager.stylePrimaryButton(filterBtn);
        filterBtn.setOnAction(e -> applyFilter());
        
        Button resetBtn = new Button("Reset");
        StyleManager.styleSecondaryButton(resetBtn);
        resetBtn.setOnAction(e -> {
            filterTypeCombo.setValue("All");
            searchField.clear();
            loadContacts();
        });
        
        box.getChildren().addAll(
            searchLabel, searchField, searchBtn, sep1,
            typeLabel, filterTypeCombo, filterBtn, resetBtn
        );
        
        return box;
    }
    
    private TableView<EmergencyContact> createContactTable() {
        TableView<EmergencyContact> table = new TableView<>();
        table.setItems(contactData);
        
        // ID Column
        TableColumn<EmergencyContact, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        idCol.setPrefWidth(50);
        
        // Priority Column
        TableColumn<EmergencyContact, Integer> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priorityOrder"));
        priorityCol.setPrefWidth(70);
        priorityCol.setCellFactory(column -> new TableCell<EmergencyContact, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    if (item <= 5) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: " + StyleManager.DANGER_COLOR + ";");
                    } else if (item <= 10) {
                        setStyle("-fx-text-fill: " + StyleManager.WARNING_COLOR + ";");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        // Department Name Column
        TableColumn<EmergencyContact, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        deptCol.setPrefWidth(200);
        
        // Type Column
        TableColumn<EmergencyContact, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("contactType"));
        typeCol.setPrefWidth(100);
        typeCol.setCellFactory(column -> new TableCell<EmergencyContact, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Emergency":
                            setStyle("-fx-background-color: " + StyleManager.DANGER_COLOR + "; -fx-text-fill: white;");
                            break;
                        case "Medical":
                            setStyle("-fx-background-color: " + StyleManager.SUCCESS_COLOR + "; -fx-text-fill: white;");
                            break;
                        case "Rescue":
                            setStyle("-fx-background-color: " + StyleManager.WARNING_COLOR + "; -fx-text-fill: white;");
                            break;
                        default:
                            setStyle("-fx-background-color: " + StyleManager.INFO_COLOR + "; -fx-text-fill: white;");
                    }
                }
            }
        });
        
        // Contact Number Column
        TableColumn<EmergencyContact, String> numberCol = new TableColumn<>("Primary Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        numberCol.setPrefWidth(130);
        
        // Alternate Number Column
        TableColumn<EmergencyContact, String> altCol = new TableColumn<>("Alt Number");
        altCol.setCellValueFactory(new PropertyValueFactory<>("alternateNumber"));
        altCol.setPrefWidth(120);
        
        // Operating Hours Column
        TableColumn<EmergencyContact, String> hoursCol = new TableColumn<>("Hours");
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("operatingHours"));
        hoursCol.setPrefWidth(100);
        
        // Email Column
        TableColumn<EmergencyContact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);
        
        // Active Column
        TableColumn<EmergencyContact, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(60);
        activeCol.setCellFactory(column -> new TableCell<EmergencyContact, Boolean>() {
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
            idCol, priorityCol, deptCol, typeCol, numberCol, 
            altCol, hoursCol, emailCol, activeCol
        );
        
        return table;
    }
    
    private HBox createButtonControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_RIGHT);
        
        Button addBtn = new Button("➕ Add Contact");
        StyleManager.styleSuccessButton(addBtn);
        addBtn.setOnAction(e -> addContact());
        
        Button editBtn = new Button("✏ Edit");
        StyleManager.stylePrimaryButton(editBtn);
        editBtn.setOnAction(e -> editContact());
        
        Button deleteBtn = new Button("🗑 Delete");
        StyleManager.styleDangerButton(deleteBtn);
        deleteBtn.setOnAction(e -> deleteContact());
        
        Button viewBtn = new Button("👁 View Details");
        StyleManager.styleSecondaryButton(viewBtn);
        viewBtn.setOnAction(e -> viewContactDetails());
        
        Button closeBtn = new Button("Close");
        StyleManager.styleGhostButton(closeBtn);
        closeBtn.setOnAction(e -> close());
        
        box.getChildren().addAll(addBtn, editBtn, deleteBtn, viewBtn, closeBtn);
        return box;
    }
    
    private void loadContacts() {
        try {
            List<EmergencyContact> contacts = contactDAO.getAllContacts();
            contactData.clear();
            contactData.addAll(contacts);
        } catch (SQLException e) {
            showError("Failed to load contacts: " + e.getMessage());
        }
    }
    
    private void loadContactTypes() {
        try {
            List<String> types = contactDAO.getContactTypes();
            filterTypeCombo.getItems().addAll(types);
        } catch (SQLException e) {
            System.err.println("Failed to load contact types: " + e.getMessage());
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadContacts();
            return;
        }
        
        try {
            List<EmergencyContact> results = contactDAO.searchContacts(searchTerm);
            contactData.clear();
            contactData.addAll(results);
            
            if (results.isEmpty()) {
                showInfo("No contacts found matching: \"" + searchTerm + "\"");
            }
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    private void applyFilter() {
        String selectedType = filterTypeCombo.getValue();
        if (selectedType == null || selectedType.equals("All")) {
            loadContacts();
            return;
        }
        
        try {
            List<EmergencyContact> filtered = contactDAO.getContactsByType(selectedType);
            contactData.clear();
            contactData.addAll(filtered);
            
            if (filtered.isEmpty()) {
                showInfo("No contacts found with type: " + selectedType);
            }
        } catch (SQLException e) {
            showError("Filter failed: " + e.getMessage());
        }
    }
    
    private void addContact() {
        EmergencyContactFormDialog dialog = new EmergencyContactFormDialog(null);
        Optional<EmergencyContact> result = dialog.showAndWait();
        
        result.ifPresent(contact -> {
            try {
                if (contactDAO.createContact(contact)) {
                    showSuccess("Emergency contact added successfully!");
                    loadContacts();
                } else {
                    showError("Failed to add contact");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
    }
    
    private void editContact() {
        EmergencyContact selected = contactTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a contact to edit");
            return;
        }
        
        EmergencyContactFormDialog dialog = new EmergencyContactFormDialog(selected);
        Optional<EmergencyContact> result = dialog.showAndWait();
        
        result.ifPresent(contact -> {
            try {
                boolean success = contactDAO.updateContact(contact);
                
                if (success) {
                    showSuccess("Contact updated successfully!");
                    loadContacts();
                } else {
                    showError("Update failed - No rows were affected");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });
    }
    
    private void deleteContact() {
        EmergencyContact selected = contactTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a contact to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Emergency Contact?");
        confirm.setContentText("Delete: " + selected.getDepartmentName() + "?\n\nThis action cannot be undone.");
        StyleManager.styleDialog(confirm.getDialogPane());
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (contactDAO.deleteContact(selected.getContactId())) {
                    showSuccess("Contact deleted successfully!");
                    loadContacts();
                } else {
                    showError("Failed to delete contact");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }
    
    private void viewContactDetails() {
        EmergencyContact selected = contactTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a contact to view");
            return;
        }
        
        Alert details = new Alert(Alert.AlertType.INFORMATION);
        details.setTitle("Emergency Contact Details");
        details.setHeaderText(selected.getDepartmentName());
        StyleManager.styleDialog(details.getDialogPane());
        
        StringBuilder content = new StringBuilder();
        content.append("Contact ID: ").append(selected.getContactId()).append("\n");
        content.append("Type: ").append(selected.getContactType()).append("\n");
        content.append("Priority: ").append(selected.getPriorityOrder()).append(" (Lower = Higher)\n\n");
        
        content.append("PRIMARY NUMBER:\n").append(selected.getContactNumber()).append("\n\n");
        
        if (selected.getAlternateNumber() != null) {
            content.append("ALTERNATE NUMBER:\n").append(selected.getAlternateNumber()).append("\n\n");
        }
        
        if (selected.getEmail() != null) {
            content.append("EMAIL:\n").append(selected.getEmail()).append("\n\n");
        }
        
        if (selected.getAddress() != null) {
            content.append("ADDRESS:\n").append(selected.getAddress()).append("\n\n");
        }
        
        content.append("OPERATING HOURS:\n").append(selected.getOperatingHours()).append("\n\n");
        
        if (selected.getDescription() != null) {
            content.append("DESCRIPTION:\n").append(selected.getDescription()).append("\n\n");
        }
        
        content.append("STATUS: ").append(selected.isActive() ? "Active" : "Inactive");
        
        TextArea textArea = new TextArea(content.toString());
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefRowCount(15);
        textArea.setPrefColumnCount(50);
        
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