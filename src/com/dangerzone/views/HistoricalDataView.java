package com.dangerzone.views;

import com.dangerzone.models.*;
import com.dangerzone.utils.StyleManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Historical Data View - Card-based layout showing incidents
 */
public class HistoricalDataView extends VBox {
    
    private Connection connection;
    private ComboBox<String> yearFilter;
    private ComboBox<String> typeFilter;
    private VBox incidentCardsContainer;
    
    public HistoricalDataView(Connection connection) {
        this.connection = connection;
        
        setPadding(new Insets(8));
        setSpacing(5);
        setStyle("-fx-background-color: " + StyleManager.LIGHT_BG + ";");
        
        // Title
        Label title = new Label("Historical Incidents");
        StyleManager.styleTitleLabel(title);
        
        // Filters
        HBox filters = createFilterBar();
        
        // Main content area: Split between stats sidebar (25%) and scrolling area (75%)
        HBox mainContent = new HBox(10);
        HBox.setHgrow(mainContent, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        
        // Left sidebar: Stats (25% width)
        VBox statsSidebar = createStatsSidebar();
        statsSidebar.setPrefWidth(250); // Fixed width for sidebar
        statsSidebar.setMaxWidth(250);
        statsSidebar.setMinWidth(250);
        
        // Right area: Scrolling cards (75% width)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + StyleManager.LIGHT_BG + ";");
        HBox.setHgrow(scrollPane, Priority.ALWAYS); // Takes remaining space
        
        incidentCardsContainer = new VBox(8);
        incidentCardsContainer.setPadding(new Insets(3));
        scrollPane.setContent(incidentCardsContainer);
        
        mainContent.getChildren().addAll(statsSidebar, scrollPane);
        
        getChildren().addAll(title, filters, mainContent);
        
        // Load initial data
        loadIncidents();
    }
    
    private VBox createStatsSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: " + StyleManager.BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );
        sidebar.setId("stats_sidebar");
        
        Label sidebarTitle = new Label("STATISTICS");
        sidebarTitle.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
        );
        
        Separator sep = new Separator();
        
        // Create vertical stat cards
        VBox statsContainer = new VBox(10);
        statsContainer.setId("stats_sidebar_container");
        
        // Initial empty stats
        updateStatsSidebar(0, 0, 0, 0);
        
        sidebar.getChildren().addAll(sidebarTitle, sep, statsContainer);
        
        return sidebar;
    }
    
    private void updateStatsSidebar(int total, int casualties, int injuries, int families) {
        VBox statsContainer = (VBox) lookup("#stats_sidebar_container");
        if (statsContainer == null) {
            // Fallback: find it in the sidebar
            VBox sidebar = (VBox) lookup("#stats_sidebar");
            if (sidebar != null && sidebar.getChildren().size() > 2) {
                statsContainer = (VBox) sidebar.getChildren().get(2);
            }
        }
        
        if (statsContainer == null) return;
        
        statsContainer.getChildren().clear();
        
        statsContainer.getChildren().addAll(
            createSidebarStatCard("Total Incidents", String.valueOf(total), StyleManager.PRIMARY_COLOR, "📊"),
            createSidebarStatCard("Total Casualties", String.valueOf(casualties), StyleManager.DANGER_COLOR, "💀"),
            createSidebarStatCard("Total Injuries", String.valueOf(injuries), StyleManager.WARNING_COLOR, "🤕"),
            createSidebarStatCard("Families Affected", String.valueOf(families), StyleManager.INFO_COLOR, "👨‍👩‍👧‍👦")
        );
    }
    
    private VBox createSidebarStatCard(String label, String value, String color, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: linear-gradient(to right, " + color + ", derive(" + color + ", 20%));" +
            "-fx-background-radius: 6px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        Label titleLabel = new Label(label);
        titleLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: rgba(255,255,255,0.95);"
        );
        titleLabel.setWrapText(true);
        
        topRow.getChildren().addAll(iconLabel, titleLabel);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        card.getChildren().addAll(topRow, valueLabel);
        
        return card;
    }
    
    private HBox createFilterBar() {
        HBox filters = new HBox(8); // Reduced from 10
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(5, 8, 5, 8)); // Reduced from 8
        StyleManager.styleCard(filters);
        
        Label yearLabel = new Label("Year:");
        yearLabel.setStyle("-fx-font-size: 11px;"); // Smaller font
        
        yearFilter = new ComboBox<>();
        StyleManager.styleComboBox(yearFilter);
        yearFilter.getItems().addAll("All", "2024", "2023", "2022", "2021", "2020", "2019", "2017", "2013", "2011", "1991");
        yearFilter.setValue("All");
        yearFilter.setPrefWidth(80); // Reduced from 100
        yearFilter.setStyle("-fx-font-size: 11px;");
        
        Label typeLabel = new Label("Type:");
        typeLabel.setStyle("-fx-font-size: 11px;");
        
        typeFilter = new ComboBox<>();
        StyleManager.styleComboBox(typeFilter);
        typeFilter.getItems().addAll("All", "Flood", "Fire", "Landslide", "Storm Surge", "Typhoon", "Earthquake");
        typeFilter.setValue("All");
        typeFilter.setPrefWidth(110); // Reduced from 130
        typeFilter.setStyle("-fx-font-size: 11px;");
        
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
        applyBtn.setOnAction(e -> loadIncidents());
        
        Button clearBtn = new Button("Clear");
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
            yearFilter.setValue("All");
            typeFilter.setValue("All");
            loadIncidents();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button manageBtn = new Button("Manage");
        manageBtn.setStyle(
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
        manageBtn.setOnAction(e -> {
            try {
                new IncidentManagerDialog(connection).show();
            } catch (Exception ex) {
                showError("Failed to open manager: " + ex.getMessage());
            }
        });
        
        filters.getChildren().addAll(
            yearLabel, yearFilter,
            typeLabel, typeFilter,
            applyBtn, clearBtn,
            spacer, manageBtn
        );
        
        return filters;
    }
    
    
    private void loadIncidents() {
        incidentCardsContainer.getChildren().clear();
        
        String year = yearFilter.getValue();
        String type = typeFilter.getValue();
        
        try {
            IncidentDAO dao = new IncidentDAO(connection);
            List<Incident> incidents = dao.getAllIncidents();
            
            int totalCasualties = 0;
            int totalInjuries = 0;
            int totalFamilies = 0;
            int count = 0;
            
            for (Incident incident : incidents) {
                boolean yearMatch = year.equals("All") || 
                    (incident.getIncidentDate() != null && 
                     incident.getIncidentDate().toString().startsWith(year));
                boolean typeMatch = type.equals("All") || 
                    incident.getIncidentType().equals(type);
                
                if (yearMatch && typeMatch) {
                    incidentCardsContainer.getChildren().add(createIncidentCard(incident));
                    totalCasualties += incident.getCasualties();
                    totalInjuries += incident.getInjuries();
                    totalFamilies += incident.getFamiliesAffected();
                    count++;
                }
            }
            
            updateStatsSidebar(count, totalCasualties, totalInjuries, totalFamilies);
            
            if (count == 0) {
                Label noData = new Label("No incidents found matching the selected filters.");
                noData.setStyle("-fx-font-size: 14px; -fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";");
                incidentCardsContainer.getChildren().add(noData);
            }
            
        } catch (SQLException e) {
            showError("Failed to load incidents: " + e.getMessage());
        }
    }
    
    private VBox createIncidentCard(Incident incident) {
        VBox card = new VBox(8); // Reduced from 12
        card.setPadding(new Insets(12)); // Reduced from 20
        StyleManager.styleElevatedCard(card);
        card.setMaxWidth(Double.MAX_VALUE);
        
        // Header: Date and Type
        HBox header = new HBox(10); // Reduced from 15
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label(incident.getIncidentDate().toString());
        dateLabel.setStyle(
            "-fx-font-size: 14px;" + // Reduced from 16px
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label typeLabel = StyleManager.createBadge(
            incident.getIncidentType(),
            StyleManager.PRIMARY_COLOR,
            "white"
        );
        
        Label severityLabel = null;
        if (incident.getSeverity() != null) {
            String severityColor = getSeverityColor(incident.getSeverity());
            severityLabel = StyleManager.createBadge(
                incident.getSeverity(),
                severityColor,
                "white"
            );
        }
        
        if (severityLabel != null) {
            header.getChildren().addAll(dateLabel, spacer, typeLabel, severityLabel);
        } else {
            header.getChildren().addAll(dateLabel, spacer, typeLabel);
        }
        
        // Location
        Label locationLabel = new Label("Location: " + incident.getBarangay());
        locationLabel.setStyle(
            "-fx-font-size: 12px;" + // Reduced from 14px
            "-fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";"
        );
        
        // Impact Grid
        GridPane impactGrid = new GridPane();
        impactGrid.setHgap(20); // Reduced from 25
        impactGrid.setVgap(6); // Reduced from 10
        impactGrid.setPadding(new Insets(6, 0, 0, 0)); // Reduced from 10
        
        impactGrid.add(createImpactItem("Casualties", String.valueOf(incident.getCasualties()), StyleManager.DANGER_COLOR), 0, 0);
        impactGrid.add(createImpactItem("Injuries", String.valueOf(incident.getInjuries()), StyleManager.WARNING_COLOR), 1, 0);
        impactGrid.add(createImpactItem("Families", String.valueOf(incident.getFamiliesAffected()), StyleManager.INFO_COLOR), 2, 0);
        impactGrid.add(createImpactItem("Structures", String.valueOf(incident.getStructuresDamaged()), StyleManager.SECONDARY_COLOR), 3, 0);
        
        card.getChildren().addAll(header, locationLabel, impactGrid);
        
        // Description - only if exists and not empty
        if (incident.getDescription() != null && !incident.getDescription().trim().isEmpty()) {
            Label descLabel = new Label("Description:");
            descLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
            
            Label descContent = new Label(incident.getDescription());
            descContent.setWrapText(true);
            descContent.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + StyleManager.TEXT_PRIMARY + ";"
            );
            
            VBox descBox = new VBox(3, descLabel, descContent);
            descBox.setPadding(new Insets(6, 0, 0, 0));
            card.getChildren().add(descBox);
        }
        
//        // Estimated Cost
//        if (incident.getEstimatedCost() != null) {
//            Label costLabel = new Label("Cost: " + incident.getFormattedCost());
//            costLabel.setStyle(
//                "-fx-font-size: 12px;" +
//                "-fx-font-weight: bold;" +
//                "-fx-text-fill: " + StyleManager.DANGER_COLOR + ";"
//            );
//            card.getChildren().add(costLabel);
//        }
        
        return card;
    }
    
    private VBox createImpactItem(String label, String value, String color) {
        VBox item = new VBox(2); // Reduced from 3
        item.setAlignment(Pos.CENTER_LEFT);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 16px;" + // Reduced from 20px
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";"
        );
        
        Label titleLabel = new Label(label);
        titleLabel.setStyle(
            "-fx-font-size: 10px;" + // Reduced from 11px
            "-fx-text-fill: " + StyleManager.TEXT_SECONDARY + ";"
        );
        
        item.getChildren().addAll(valueLabel, titleLabel);
        return item;
    }
    
    private String getSeverityColor(String severity) {
        switch (severity.toLowerCase()) {
            case "critical":
                return StyleManager.DANGER_COLOR;
            case "high":
                return StyleManager.WARNING_COLOR;
            case "medium":
                return "#f1c40f";
            case "low":
                return "#95a5a6";
            default:
                return StyleManager.TEXT_SECONDARY;
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}