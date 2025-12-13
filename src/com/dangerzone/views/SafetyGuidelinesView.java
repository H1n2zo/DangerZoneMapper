package com.dangerzone.views;

import com.dangerzone.utils.StyleManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.dangerzone.models.*;

/**
 * Safety Guidelines View - Card-based layout showing safety information
 */
public class SafetyGuidelinesView extends VBox {
    
    private Connection connection;
    private ComboBox<String> hazardTypeFilter;
    private VBox guidelinesContainer;
    
    public SafetyGuidelinesView(Connection connection) {
        this.connection = connection;
        
        setPadding(new Insets(8)); // Reduced from 10
        setSpacing(5); // Reduced from 8
        setStyle("-fx-background-color: " + StyleManager.LIGHT_BG + ";");
        
        // Title
        Label title = new Label("Emergency Safety Guidelines");
        StyleManager.styleTitleLabel(title);
        
        // Filter bar
        HBox filterBar = createFilterBar();
        
        // Emergency Contacts Card
        VBox contactsCard = createEmergencyContactsCard();
        
        // ScrollPane for guidelines - MAXIMIZE SPACE
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + StyleManager.LIGHT_BG + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // CRITICAL: Takes all available space
        
        guidelinesContainer = new VBox(8); // Reduced from 10
        guidelinesContainer.setPadding(new Insets(3)); // Reduced from 5
        scrollPane.setContent(guidelinesContainer);
        
        getChildren().addAll(title, filterBar, contactsCard, scrollPane);
        
        // Load guidelines
        loadGuidelines();
    }
    
    private HBox createFilterBar() {
        HBox filterBar = new HBox(8); // Reduced from 10
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(5, 8, 5, 8)); // Reduced from 8
        StyleManager.styleCard(filterBar);
        
        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-size: 11px;"); // Smaller font
        
        hazardTypeFilter = new ComboBox<>();
        StyleManager.styleComboBox(hazardTypeFilter);
        hazardTypeFilter.getItems().addAll("All", "Flood", "Landslide", "Storm Surge", "Fire", "Typhoon");
        hazardTypeFilter.setValue("All");
        hazardTypeFilter.setPrefWidth(110); // Reduced from 130
        hazardTypeFilter.setStyle("-fx-font-size: 11px;");
        
        Button applyBtn = new Button("Apply");
        applyBtn.setStyle(
            "-fx-background-color: " + StyleManager.PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: 11px;" +
            "-fx-background-radius: 4px;" +
            "-fx-padding: 4 12;" + // Reduced padding
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
    
    private VBox createEmergencyContactsCard() {
        VBox card = new VBox(10); // Reduced from 15
        card.setPadding(new Insets(12)); // Reduced from 20
        card.setStyle(
            "-fx-background-color: linear-gradient(to right, " + StyleManager.DANGER_COLOR + ", " + StyleManager.DANGER_LIGHT + ");" +
            "-fx-background-radius: 8px;" + // Reduced from 12px
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);"
        );
        
        Label title = new Label("EMERGENCY CONTACTS");
        title.setStyle(
            "-fx-font-size: 14px;" + // Reduced from 18px
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        GridPane contactsGrid = new GridPane();
        contactsGrid.setHgap(20); // Reduced from 30
        contactsGrid.setVgap(8); // Reduced from 12
        
        // Emergency contacts
        contactsGrid.add(createContactItem("EMERGENCY", "911"), 0, 0);
        contactsGrid.add(createContactItem("CDRRMO", "(053) 561-5027"), 1, 0);
        contactsGrid.add(createContactItem("Fire Dept", "(053) 561-2222"), 0, 1);
        contactsGrid.add(createContactItem("Police", "(053) 561-3333"), 1, 1);
        contactsGrid.add(createContactItem("Hospital", "(053) 255-2316"), 0, 2);
        contactsGrid.add(createContactItem("Medical", "(053) 561-5281"), 1, 2);
        
        card.getChildren().addAll(title, contactsGrid);
        
        return card;
    }
    
    private VBox createContactItem(String name, String number) {
        VBox item = new VBox(2); // Reduced from 3
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle(
            "-fx-font-size: 10px;" + // Reduced from 12px
            "-fx-text-fill: rgba(255,255,255,0.9);"
        );
        
        Label numberLabel = new Label(number);
        numberLabel.setStyle(
            "-fx-font-size: 14px;" + // Reduced from 18px
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        item.getChildren().addAll(nameLabel, numberLabel);
        return item;
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
        VBox card = new VBox(10); // Reduced from 15
        card.setPadding(new Insets(12)); // Reduced from 20
        StyleManager.styleElevatedCard(card);
        card.setMaxWidth(Double.MAX_VALUE);
        
        // Header with hazard type and category
        HBox header = new HBox(10); // Reduced from 15
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label hazardIcon = new Label(getHazardIcon(guideline.getHazardType()));
        hazardIcon.setStyle("-fx-font-size: 22px;"); // Reduced from 28px
        
        VBox titleBox = new VBox(2); // Reduced from 3
        Label titleLabel = new Label(guideline.getTitle());
        titleLabel.setStyle(
            "-fx-font-size: 14px;" + // Reduced from 18px
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
        );
        titleLabel.setWrapText(true);
        
        Label hazardLabel = new Label(guideline.getHazardType());
        hazardLabel.setStyle(
            "-fx-font-size: 11px;" + // Reduced from 13px
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
            "-fx-font-size: 12px;" + // Reduced from 14px
            "-fx-line-spacing: 2px;" + // Reduced from 3px
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
        );
        
        // Format bullet points if content contains newlines
        String formattedContent = formatGuidelineContent(guideline.getContent());
        contentLabel.setText(formattedContent);
        
        card.getChildren().addAll(header, contentLabel);
        
        // Emergency contact (if available)
        if (guideline.getEmergencyContact() != null && !guideline.getEmergencyContact().trim().isEmpty()) {
            HBox contactBox = new HBox(8); // Reduced from 10
            contactBox.setAlignment(Pos.CENTER_LEFT);
            contactBox.setPadding(new Insets(6)); // Reduced from 10
            contactBox.setStyle(
                "-fx-background-color: " + StyleManager.LIGHT_BG + ";" +
                "-fx-background-radius: 4px;" // Reduced from 6px
            );
            
            Label contactIcon = new Label("📞");
            contactIcon.setStyle("-fx-font-size: 14px;"); // Reduced from 16px
            
            Label contactLabel = new Label("Contact: " + guideline.getEmergencyContact());
            contactLabel.setStyle(
                "-fx-font-size: 11px;" + // Reduced from 13px
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + StyleManager.DANGER_COLOR + ";"
            );
            
            contactBox.getChildren().addAll(contactIcon, contactLabel);
            card.getChildren().add(contactBox);
        }
        
        // Priority indicator (if high priority)
        if (guideline.getPriorityLevel() != null && guideline.getPriorityLevel() == 1) {
            HBox priorityBox = new HBox(6); // Reduced from 8
            priorityBox.setAlignment(Pos.CENTER_LEFT);
            
            Label priorityIcon = new Label("⚠");
            priorityIcon.setStyle("-fx-font-size: 14px;"); // Reduced from 16px
            
            Label priorityLabel = new Label("CRITICAL");
            priorityLabel.setStyle(
                "-fx-font-size: 11px;" + // Reduced from 12px
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
        
        // Replace bullet point markers with proper bullets
        String formatted = content.replace("•", "• ");
        formatted = formatted.replace("- ", "• ");
        
        // Add spacing between paragraphs
        formatted = formatted.replace("\n\n", "\n\n");
        
        return formatted;
    }
    
    private String getHazardIcon(String hazardType) {
        switch (hazardType.toLowerCase()) {
            case "flood":
                return "🌊";
            case "landslide":
                return "⛰️";
            case "storm surge":
                return "🌀";
            case "fire":
                return "🔥";
            case "typhoon":
                return "💨";
            case "earthquake":
                return "⚡";
            default:
                return "⚠️";
        }
    }
    
    private String getCategoryColor(String category) {
        if (category == null) return StyleManager.PRIMARY_COLOR;
        
        switch (category.toLowerCase()) {
            case "during":
                return StyleManager.DANGER_COLOR;
            case "prevention":
                return StyleManager.SUCCESS_COLOR;
            case "after":
                return StyleManager.INFO_COLOR;
            default:
                return StyleManager.PRIMARY_COLOR;
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}