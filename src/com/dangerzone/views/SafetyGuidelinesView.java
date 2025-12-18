package com.dangerzone.views;

import com.dangerzone.utils.StyleManager;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.dangerzone.models.*;

/**
 * Safety Guidelines View - Card-based layout showing safety information
 * WITH COLLAPSIBLE EMERGENCY CONTACTS SIDEBAR
 */
public class SafetyGuidelinesView extends VBox {
    
    private Connection connection;
    private ComboBox<String> hazardTypeFilter;
    private VBox guidelinesContainer;
    
    // Sidebar control
    private VBox leftSidebar;
    private Button expandButton;
    private Button shrinkButton;
    private static final double SIDEBAR_MIN_WIDTH = 200;
    private static final double SIDEBAR_MAX_WIDTH = 400;
    private static final double SIDEBAR_STEP = 50; // Adjust by 50px each click
    
    public SafetyGuidelinesView(Connection connection) {
        this.connection = connection;
        
        setPadding(new Insets(8));
        setSpacing(5);
        setStyle("-fx-background-color: " + StyleManager.LIGHT_BG + ";");
        
        // Main horizontal split: Emergency contacts (left) + Guidelines (right)
        HBox mainLayout = new HBox(10);
        HBox.setHgrow(mainLayout, Priority.ALWAYS);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);
        
        // LEFT SIDEBAR: Emergency Contacts with toggle
        leftSidebar = createCollapsibleSidebar();
        
        // RIGHT AREA: Title, Filter, and Guidelines
        VBox rightArea = new VBox(8);
        HBox.setHgrow(rightArea, Priority.ALWAYS);
        
        Label title = new Label("Safety Guidelines");
        StyleManager.styleTitleLabel(title);
        
        HBox filterBar = createFilterBar();
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + StyleManager.LIGHT_BG + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        guidelinesContainer = new VBox(8);
        guidelinesContainer.setPadding(new Insets(3));
        scrollPane.setContent(guidelinesContainer);
        
        rightArea.getChildren().addAll(title, filterBar, scrollPane);
        
        mainLayout.getChildren().addAll(leftSidebar, rightArea);
        getChildren().add(mainLayout);
        
        loadGuidelines();
    }
    
    /**
     * Create resizable sidebar with expand/shrink arrows
     */
    private VBox createCollapsibleSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
        sidebar.setMinWidth(SIDEBAR_MIN_WIDTH);
        sidebar.setMaxWidth(SIDEBAR_MIN_WIDTH);
        
        // Container for title and resize buttons
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.prefWidthProperty().bind(sidebar.widthProperty());
        
        Label sidebarTitle = new Label("EMERGENCY");
        sidebarTitle.setId("sidebarTitle");
        sidebarTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
        );
        sidebarTitle.setWrapText(true);
        sidebarTitle.maxWidthProperty().bind(sidebar.widthProperty().subtract(100));
        HBox.setHgrow(sidebarTitle, Priority.ALWAYS);
        
        // Shrink button (make smaller)
        shrinkButton = new Button("◀");
        shrinkButton.setTooltip(new Tooltip("Make narrower"));
        shrinkButton.setStyle(
            "-fx-background-color: " + StyleManager.WARNING_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 8;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;"
        );
        shrinkButton.setOnAction(e -> shrinkSidebar());
        shrinkButton.setDisable(true); // Start at minimum width
        
        // Expand button (make bigger)
        expandButton = new Button("▶");
        expandButton.setTooltip(new Tooltip("Make wider"));
        expandButton.setStyle(
            "-fx-background-color: " + StyleManager.SUCCESS_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 8;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;"
        );
        expandButton.setOnAction(e -> expandSidebar());
        
        header.getChildren().addAll(sidebarTitle, shrinkButton, expandButton);
        
        // Emergency contacts card
        VBox contactsCard = createDynamicEmergencyContactsCard();
        contactsCard.setId("contactsCard");
        // BIND the card width to the sidebar width
        contactsCard.prefWidthProperty().bind(sidebar.widthProperty().subtract(20));
        contactsCard.maxWidthProperty().bind(sidebar.widthProperty().subtract(20));
        VBox.setVgrow(contactsCard, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(header, contactsCard);
        
        return sidebar;
    }
    
    /**
     * Expand sidebar (make wider)
     */
    private void expandSidebar() {
        double currentWidth = leftSidebar.getPrefWidth();
        double newWidth = Math.min(currentWidth + SIDEBAR_STEP, SIDEBAR_MAX_WIDTH);
        
        leftSidebar.setPrefWidth(newWidth);
        leftSidebar.setMinWidth(newWidth);
        leftSidebar.setMaxWidth(newWidth);
        
        // Update button states
        shrinkButton.setDisable(false);
        if (newWidth >= SIDEBAR_MAX_WIDTH) {
            expandButton.setDisable(true);
        }
    }
    
    /**
     * Shrink sidebar (make narrower)
     */
    private void shrinkSidebar() {
        double currentWidth = leftSidebar.getPrefWidth();
        double newWidth = Math.max(currentWidth - SIDEBAR_STEP, SIDEBAR_MIN_WIDTH);
        
        leftSidebar.setPrefWidth(newWidth);
        leftSidebar.setMinWidth(newWidth);
        leftSidebar.setMaxWidth(newWidth);
        
        // Update button states
        expandButton.setDisable(false);
        if (newWidth <= SIDEBAR_MIN_WIDTH) {
            shrinkButton.setDisable(true);
        }
    }
    
    private HBox createFilterBar() {
        HBox filterBar = new HBox(8);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(5, 8, 5, 8));
        StyleManager.styleCard(filterBar);
        
        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-size: 11px;");
        
        hazardTypeFilter = new ComboBox<>();
        StyleManager.styleComboBox(hazardTypeFilter);
        hazardTypeFilter.getItems().addAll("All", "Flood", "Landslide", "Storm Surge", "Fire", "Typhoon");
        hazardTypeFilter.setValue("All");
        hazardTypeFilter.setPrefWidth(110);
        hazardTypeFilter.setStyle("-fx-font-size: 11px;");
        
        Button applyBtn = new Button("Apply");
        applyBtn.setStyle(
            "-fx-background-color: " + StyleManager.PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 4px;" +
            "-fx-padding: 4 12;" +
            "-fx-cursor: hand;"
        );
        applyBtn.setOnAction(e -> loadGuidelines());
        
        Button clearBtn = new Button("Show All");
        clearBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 4px;" +
            "-fx-padding: 4 12;" +
            "-fx-border-color: " + StyleManager.BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 4px;" +
            "-fx-cursor: hand;"
        );
        clearBtn.setOnAction(e -> {
            hazardTypeFilter.setValue("All");
            loadGuidelines();
        });
        
        filterBar.getChildren().addAll(filterLabel, hazardTypeFilter, applyBtn, clearBtn);
        
        return filterBar;
    }
    
    /**
     * NEW: Create dynamic emergency contacts card that fetches from database
     */
    private VBox createDynamicEmergencyContactsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: linear-gradient(to right, " + StyleManager.DANGER_COLOR + ", " + StyleManager.DANGER_LIGHT + ");" +
            "-fx-background-radius: 8px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);"
        );
        
        Label title = new Label("EMERGENCY CONTACTS");
        title.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        title.setWrapText(true);
        // Bind title width to card width
        title.maxWidthProperty().bind(card.widthProperty().subtract(24));
        
        // ScrollPane for contacts
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        // Bind scrollpane width to card width
        scrollPane.prefWidthProperty().bind(card.widthProperty().subtract(24));
        
        VBox contactsContainer = new VBox(8);
        
        try {
            EmergencyContactDAO dao = new EmergencyContactDAO(connection);
            List<EmergencyContact> contacts = dao.getHighPriorityContacts(6);
            
            if (contacts.isEmpty()) {
                Label noContacts = new Label("No emergency contacts found.\nPlease add contacts in the database.");
                noContacts.setWrapText(true);
                noContacts.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
                // Bind label width
                noContacts.maxWidthProperty().bind(contactsContainer.widthProperty().subtract(10));
                contactsContainer.getChildren().add(noContacts);
            } else {
                for (EmergencyContact contact : contacts) {
                    VBox contactItem = createDynamicContactItem(contact);
                    // Bind each contact item width to container
                    contactItem.prefWidthProperty().bind(contactsContainer.widthProperty().subtract(5));
                    contactItem.maxWidthProperty().bind(contactsContainer.widthProperty().subtract(5));
                    contactsContainer.getChildren().add(contactItem);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to load emergency contacts: " + e.getMessage());
            Label errorLabel = new Label("Error loading contacts.\nUsing default numbers.");
            errorLabel.setWrapText(true);
            errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
            errorLabel.maxWidthProperty().bind(contactsContainer.widthProperty().subtract(10));
            contactsContainer.getChildren().add(errorLabel);
            
            contactsContainer.getChildren().addAll(
                createContactItem("EMERGENCY", "911"),
                createContactItem("CDRRMO", "(053) 561-5027"),
                createContactItem("Fire Dept", "(053) 561-2222"),
                createContactItem("Police", "(053) 561-3333")
            );
        }
        
        scrollPane.setContent(contactsContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        card.getChildren().addAll(title, scrollPane);
        
        return card;
    }
    
    /**
     * NEW: Create contact item from database EmergencyContact object
     */
    private VBox createDynamicContactItem(EmergencyContact contact) {
        VBox item = new VBox(2);
        
        Label nameLabel = new Label(contact.getShortName());
        nameLabel.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-text-fill: rgba(255,255,255,0.9);"
        );
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.maxWidthProperty().bind(item.widthProperty());
        // CRITICAL: Disable text ellipsis
        nameLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        nameLabel.setEllipsisString("");
        
        Label numberLabel = new Label(contact.getContactNumber());
        numberLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        numberLabel.setWrapText(true);
        numberLabel.setMaxWidth(Double.MAX_VALUE);
        numberLabel.maxWidthProperty().bind(item.widthProperty());
        // CRITICAL: Disable text ellipsis
        numberLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        numberLabel.setEllipsisString("");
        
        item.getChildren().addAll(nameLabel, numberLabel);
        
        if (contact.getAlternateNumber() != null && !contact.getAlternateNumber().isEmpty()) {
            Label altLabel = new Label("Alt: " + contact.getAlternateNumber());
            altLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: rgba(255,255,255,0.8);"
            );
            altLabel.setWrapText(true);
            altLabel.setMaxWidth(Double.MAX_VALUE);
            altLabel.maxWidthProperty().bind(item.widthProperty());
            // CRITICAL: Disable text ellipsis
            altLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
            altLabel.setEllipsisString("");
            item.getChildren().add(altLabel);
        }
        
        Tooltip tooltip = new Tooltip(
            contact.getDepartmentName() + "\n" +
            "Type: " + contact.getContactType() + "\n" +
            "Hours: " + contact.getOperatingHours() +
            (contact.getDescription() != null ? "\n\n" + contact.getDescription() : "")
        );
        Tooltip.install(item, tooltip);
        
        item.setStyle("-fx-cursor: hand;");
        item.setOnMouseClicked(e -> showContactDetails(contact));
        
        return item;
    }
    
    /**
     * Fallback: Create static contact item (for backward compatibility)
     */
    private VBox createContactItem(String name, String number) {
        VBox item = new VBox(2);
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-text-fill: rgba(255,255,255,0.9);"
        );
        nameLabel.setWrapText(true);
        // CRITICAL: Disable text ellipsis
        nameLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        nameLabel.setEllipsisString("");
        
        Label numberLabel = new Label(number);
        numberLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        numberLabel.setWrapText(true);
        // CRITICAL: Disable text ellipsis
        numberLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        numberLabel.setEllipsisString("");
        
        item.getChildren().addAll(nameLabel, numberLabel);
        return item;
    }
    
    /**
     * Show detailed contact information dialog
     */
    private void showContactDetails(EmergencyContact contact) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Emergency Contact Details");
        alert.setHeaderText(contact.getDepartmentName());
        StyleManager.styleDialog(alert.getDialogPane());
        
        StringBuilder content = new StringBuilder();
        content.append("Contact Type: ").append(contact.getContactType()).append("\n");
        content.append("Primary Number: ").append(contact.getContactNumber()).append("\n");
        
        if (contact.getAlternateNumber() != null) {
            content.append("Alternate Number: ").append(contact.getAlternateNumber()).append("\n");
        }
        
        if (contact.getEmail() != null) {
            content.append("Email: ").append(contact.getEmail()).append("\n");
        }
        
        if (contact.getAddress() != null) {
            content.append("Address: ").append(contact.getAddress()).append("\n");
        }
        
        content.append("Operating Hours: ").append(contact.getOperatingHours()).append("\n");
        
        if (contact.getDescription() != null) {
            content.append("\nDescription:\n").append(contact.getDescription());
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    private void loadGuidelines() {
        guidelinesContainer.getChildren().clear();
        
        String filterType = hazardTypeFilter.getValue();
        
        try {
            SafetyGuidelineDAO dao = new SafetyGuidelineDAO(connection);
            List<SafetyGuideline> guidelines = dao.getActiveGuidelines();
            
            int count = 0;
            for (SafetyGuideline guideline : guidelines) {
                boolean typeMatch = filterType.equals("All") || 
                    guideline.getHazardType().equalsIgnoreCase(filterType);
                
                if (typeMatch) {
                    guidelinesContainer.getChildren().add(createGuidelineCard(guideline));
                    count++;
                }
            }
            
            if (count == 0) {
                Label noData = new Label("No guidelines found for the selected hazard type.");
                noData.setStyle("-fx-font-size: 14px; -fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";");
                guidelinesContainer.getChildren().add(noData);
            }
            
        } catch (SQLException e) {
            showError("Failed to load guidelines: " + e.getMessage());
        }
    }
    
    private VBox createGuidelineCard(SafetyGuideline guideline) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        StyleManager.styleElevatedCard(card);
        card.setMaxWidth(Double.MAX_VALUE);
        
        // Header with hazard type and category
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label hazardIcon = new Label(getHazardIcon(guideline.getHazardType()));
        hazardIcon.setStyle("-fx-font-size: 22px;");
        
        VBox titleBox = new VBox(2);
        Label titleLabel = new Label(guideline.getTitle());
        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
        );
        titleLabel.setWrapText(true);
        
        Label hazardLabel = new Label(guideline.getHazardType());
        hazardLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";"
        );
        
        titleBox.getChildren().addAll(titleLabel, hazardLabel);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label categoryBadge = StyleManager.createBadge(
            guideline.getCategory(),
            getCategoryColor(guideline.getCategory()),
            "white"
        );
        
        header.getChildren().addAll(hazardIcon, titleBox, spacer, categoryBadge);
        
        // Content
        Label contentLabel = new Label(guideline.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-line-spacing: 2px;" +
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
        );
        
        String formattedContent = formatGuidelineContent(guideline.getContent());
        contentLabel.setText(formattedContent);
        
        card.getChildren().addAll(header, contentLabel);
        
        // Emergency contact (if available)
        if (guideline.getEmergencyContact() != null && !guideline.getEmergencyContact().trim().isEmpty()) {
            HBox contactBox = new HBox(8);
            contactBox.setAlignment(Pos.CENTER_LEFT);
            contactBox.setPadding(new Insets(6));
            contactBox.setStyle(
                "-fx-background-color: " + StyleManager.LIGHT_BG + ";" +
                "-fx-background-radius: 4px;"
            );
            
            Label contactIcon = new Label("📞");
            contactIcon.setStyle("-fx-font-size: 14px;");
            
            Label contactLabel = new Label("Contact: " + guideline.getEmergencyContact());
            contactLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + StyleManager.DANGER_COLOR + ";"
            );
            
            contactBox.getChildren().addAll(contactIcon, contactLabel);
            card.getChildren().add(contactBox);
        }
        
        // Priority indicator
        if (guideline.getPriorityLevel() != null && guideline.getPriorityLevel() == 1) {
            HBox priorityBox = new HBox(6);
            priorityBox.setAlignment(Pos.CENTER_LEFT);
            
            Label priorityIcon = new Label("⚠");
            priorityIcon.setStyle("-fx-font-size: 14px;");
            
            Label priorityLabel = new Label("CRITICAL");
            priorityLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + StyleManager.DANGER_COLOR + ";"
            );
            
            priorityBox.getChildren().addAll(priorityIcon, priorityLabel);
            card.getChildren().add(0, priorityBox);
        }
        
        return card;
    }
    
    private String formatGuidelineContent(String content) {
        if (content == null) return "";
        
        String formatted = content.replace("•", "• ");
        formatted = formatted.replace("- ", "• ");
        formatted = formatted.replace("\n\n", "\n\n");
        
        return formatted;
    }
    
    private String getHazardIcon(String hazardType) {
        switch (hazardType.toLowerCase()) {
            case "flood": return "🌊";
            case "landslide": return "⛰";
            case "storm surge": return "🌀";
            case "fire": return "🔥";
            case "typhoon": return "💨";
            case "earthquake": return "⚡";
            default: return "⚠️";
        }
    }
    
    private String getCategoryColor(String category) {
        if (category == null) return StyleManager.PRIMARY_COLOR;
        
        switch (category.toLowerCase()) {
            case "during": return StyleManager.DANGER_COLOR;
            case "prevention": return StyleManager.SUCCESS_COLOR;
            case "after": return StyleManager.INFO_COLOR;
            default: return StyleManager.PRIMARY_COLOR;
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}