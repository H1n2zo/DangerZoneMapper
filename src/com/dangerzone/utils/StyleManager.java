package com.dangerzone.utils;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.SVGPath;

/**
 * Professional UI Style Manager for Danger Zone Mapping System
 * Modern, clean design without emoji or box symbols
 */
public class StyleManager {
    
    // Modern Color Palette - Professional & Accessible
    public static final String PRIMARY_COLOR = "#1a73e8";      // Google Blue
    public static final String PRIMARY_DARK = "#1557b0";
    public static final String SECONDARY_COLOR = "#34495e";
    public static final String ACCENT_COLOR = "#0d47a1";
    public static final String SUCCESS_COLOR = "#1b5e20";      // Dark Green
    public static final String SUCCESS_LIGHT = "#4caf50";
    public static final String DANGER_COLOR = "#c62828";       // Dark Red
    public static final String DANGER_LIGHT = "#ef5350";
    public static final String WARNING_COLOR = "#f57c00";      // Dark Orange
    public static final String WARNING_LIGHT = "#ff9800";
    public static final String INFO_COLOR = "#0277bd";         // Dark Cyan
    public static final String LIGHT_BG = "#f5f7fa";
    public static final String CARD_BG = "#ffffff";
    public static final String TEXT_PRIMARY = "#212121";
    public static final String TEXT_SECONDARY = "#616161";
    public static final String TEXT_HINT = "#9e9e9e";
    public static final String BORDER_COLOR = "#e0e0e0";
    public static final String BORDER_FOCUS = "#1a73e8";
    public static final String HOVER_BG = "#f5f5f5";
    
    // Typography
    public static final String FONT_FAMILY = "'Segoe UI', 'Roboto', 'San Francisco', 'Helvetica Neue', Arial, sans-serif";
    public static final String FONT_SIZE_LARGE = "24px";
    public static final String FONT_SIZE_TITLE = "20px";
    public static final String FONT_SIZE_SUBTITLE = "16px";
    public static final String FONT_SIZE_BODY = "14px";
    public static final String FONT_SIZE_SMALL = "12px";
    
    // Spacing
    public static final double SPACING_SMALL = 8;
    public static final double SPACING_MEDIUM = 16;
    public static final double SPACING_LARGE = 24;
    
    // Border Radius
    public static final String RADIUS_SMALL = "4px";
    public static final String RADIUS_MEDIUM = "8px";
    public static final String RADIUS_LARGE = "12px";
    
    /**
     * Apply modern theme to entire application
     */
    public static void applyModernTheme(Region root) {
        root.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-color: " + LIGHT_BG + ";"
        );
    }
    
    /**
     * Style a professional card container with subtle shadow
     */
    public static void styleCard(Region card) {
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);" +
            "-fx-padding: " + SPACING_MEDIUM + ";"
        );
    }
    
    /**
     * Style an elevated card with more prominence
     */
    public static void styleElevatedCard(Region card) {
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4);" +
            "-fx-padding: " + SPACING_LARGE + ";"
        );
    }
    
    /**
     * Primary action button - for main CTAs
     */
    public static void stylePrimaryButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(26,115,232,0.3), 6, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + PRIMARY_DARK + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(26,115,232,0.4), 8, 0, 0, 3);"
        ));
        
        button.setOnMouseExited(e -> stylePrimaryButton(button));
        
        button.setOnMousePressed(e -> button.setStyle(
            "-fx-background-color: " + ACCENT_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;"
        ));
    }
    
    /**
     * Success button - for positive actions
     */
    public static void styleSuccessButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + SUCCESS_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(27,94,32,0.3), 6, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + SUCCESS_LIGHT + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(27,94,32,0.4), 8, 0, 0, 3);"
        ));
        
        button.setOnMouseExited(e -> styleSuccessButton(button));
    }
    
    /**
     * Danger button - for destructive actions
     */
    public static void styleDangerButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + DANGER_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(198,40,40,0.3), 6, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + DANGER_LIGHT + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(198,40,40,0.4), 8, 0, 0, 3);"
        ));
        
        button.setOnMouseExited(e -> styleDangerButton(button));
    }
    
    /**
     * Secondary button - for secondary actions
     */
    public static void styleSecondaryButton(Button button) {
        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: " + RADIUS_SMALL + ";" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + HOVER_BG + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 24;" +
            "-fx-border-color: " + PRIMARY_COLOR + ";" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: " + RADIUS_SMALL + ";" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> styleSecondaryButton(button));
    }
    
    /**
     * Ghost button - minimal button style
     */
    public static void styleGhostButton(Button button) {
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-padding: 10 16;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + HOVER_BG + ";" +
            "-fx-text-fill: " + PRIMARY_DARK + ";" +
            "-fx-font-weight: 600;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 16;" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> styleGhostButton(button));
    }
    
    /**
     * Modern text field with subtle border and focus state
     */
    public static void styleTextField(TextField field) {
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 12;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + TEXT_HINT + ";"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: " + RADIUS_SMALL + ";" +
                    "-fx-border-color: " + BORDER_FOCUS + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: " + RADIUS_SMALL + ";" +
                    "-fx-padding: 9 11;" + // Adjust for border width
                    "-fx-font-size: " + FONT_SIZE_BODY + ";" +
                    "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + TEXT_HINT + ";"
                );
            } else {
                styleTextField(field);
            }
        });
    }
    
    /**
     * Modern text area with subtle border
     */
    public static void styleTextArea(TextArea area) {
        area.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 10 12;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );
        
        area.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                area.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: " + RADIUS_SMALL + ";" +
                    "-fx-border-color: " + BORDER_FOCUS + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: " + RADIUS_SMALL + ";" +
                    "-fx-padding: 9 11;" +
                    "-fx-font-size: " + FONT_SIZE_BODY + ";" +
                    "-fx-text-fill: " + TEXT_PRIMARY + ";"
                );
            } else {
                styleTextArea(area);
            }
        });
    }
    
    /**
     * Modern combo box
     */
    public static void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";"
        );
    }
    
    /**
     * Page title label
     */
    public static void styleTitleLabel(Label label) {
        label.setStyle(
            "-fx-font-size: " + FONT_SIZE_TITLE + ";" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-padding: 0 0 " + SPACING_SMALL + " 0;"
        );
    }
    
    /**
     * Section subtitle label
     */
    public static void styleSubtitleLabel(Label label) {
        label.setStyle(
            "-fx-font-size: " + FONT_SIZE_SUBTITLE + ";" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-padding: 0 0 " + SPACING_SMALL + " 0;"
        );
    }
    
    /**
     * Section header label
     */
    public static void styleSectionHeader(Label label) {
        label.setStyle(
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-padding: 0 0 8 0;"
        );
    }
    
    /**
     * Create a professional stat card
     */
    public static VBox createStatCard(String title, String value, String color, String iconText) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.TOP_LEFT);
        card.setMinWidth(220);
        card.setPrefHeight(140);
        
        // Use gradient for visual appeal
        card.setStyle(
            "-fx-background-color: linear-gradient(135deg, " + color + " 0%, derive(" + color + ", -15%) 100%);" +
            "-fx-background-radius: " + RADIUS_LARGE + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 4);"
        );
        
        // Icon/Label container
        HBox iconBox = new HBox();
        iconBox.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(iconText);
        iconLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: rgba(255,255,255,0.9);" +
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-background-radius: " + RADIUS_SMALL + ";" +
            "-fx-padding: 8 12;"
        );
        iconBox.getChildren().add(iconLabel);
        
        // Value
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: white;"
        );
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-font-weight: 500;" +
            "-fx-text-fill: rgba(255,255,255,0.95);"
        );
        
        VBox.setMargin(valueLabel, new Insets(4, 0, 0, 0));
        
        card.getChildren().addAll(iconBox, valueLabel, titleLabel);
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(135deg, derive(" + color + ", 5%) 0%, " + color + " 100%);" +
                "-fx-background-radius: " + RADIUS_LARGE + ";" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.16), 14, 0, 0, 6);" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(135deg, " + color + " 0%, derive(" + color + ", -15%) 100%);" +
                "-fx-background-radius: " + RADIUS_LARGE + ";" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 4);" +
                "-fx-scale-x: 1;" +
                "-fx-scale-y: 1;"
            );
        });
        
        return card;
    }
    
    /**
     * Style a modern table
     */
    public static void styleTable(TableView<?> table) {
        table.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-table-cell-border-color: " + BORDER_COLOR + ";"
        );
    }
    
    /**
     * Style menu bar
     */
    public static void styleMenuBar(MenuBar menuBar) {
        menuBar.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-width: 0 0 1 0;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-padding: 8 16;" +
            "-fx-font-size: " + FONT_SIZE_BODY + ";"
        );
        
        for (Menu menu : menuBar.getMenus()) {
            menu.setStyle(
                "-fx-font-size: " + FONT_SIZE_BODY + ";" +
                "-fx-font-weight: 500;"
            );
        }
    }
    
    /**
     * Style modern tab pane
     */
    public static void styleTabPane(TabPane tabPane) {
        tabPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-tab-min-height: 44px;" +
            "-fx-tab-max-height: 44px;"
        );
    }
    
    /**
     * Create a modern toolbar
     */
    public static HBox createToolbar() {
        HBox toolbar = new HBox(SPACING_MEDIUM);
        toolbar.setPadding(new Insets(SPACING_MEDIUM));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";"
        );
        return toolbar;
    }
    
    /**
     * Create a modern legend box
     */
    public static HBox createLegendBox() {
        HBox legend = new HBox(SPACING_LARGE);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(SPACING_MEDIUM, SPACING_LARGE, SPACING_MEDIUM, SPACING_LARGE));
        legend.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 2);"
        );
        return legend;
    }
    
    /**
     * Style dialog pane
     */
    public static void styleDialog(DialogPane dialogPane) {
        dialogPane.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: " + RADIUS_LARGE + ";" +
            "-fx-border-radius: " + RADIUS_LARGE + ";" +
            "-fx-padding: " + SPACING_LARGE + ";"
        );
    }
    
    /**
     * Create a badge label (for tags, status indicators)
     */
    public static Label createBadge(String text, String backgroundColor, String textColor) {
        Label badge = new Label(text);
        badge.setStyle(
            "-fx-background-color: " + backgroundColor + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-size: " + FONT_SIZE_SMALL + ";" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 4 10;" +
            "-fx-background-radius: 12px;"
        );
        return badge;
    }
    
    /**
     * Create info box for displaying important information
     */
    public static VBox createInfoBox(String title, String message, String type) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(SPACING_MEDIUM));
        
        String backgroundColor, borderColor, textColor;
        switch (type.toLowerCase()) {
            case "success":
                backgroundColor = "rgba(27,94,32,0.08)";
                borderColor = SUCCESS_COLOR;
                textColor = SUCCESS_COLOR;
                break;
            case "warning":
                backgroundColor = "rgba(245,124,0,0.08)";
                borderColor = WARNING_COLOR;
                textColor = WARNING_COLOR;
                break;
            case "error":
                backgroundColor = "rgba(198,40,40,0.08)";
                borderColor = DANGER_COLOR;
                textColor = DANGER_COLOR;
                break;
            default: // info
                backgroundColor = "rgba(2,119,189,0.08)";
                borderColor = INFO_COLOR;
                textColor = INFO_COLOR;
        }
        
        box.setStyle(
            "-fx-background-color: " + backgroundColor + ";" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: 1px 1px 1px 4px;" +
            "-fx-border-radius: " + RADIUS_SMALL + ";" +
            "-fx-background-radius: " + RADIUS_SMALL + ";"
        );
        
        if (title != null && !title.isEmpty()) {
            Label titleLabel = new Label(title);
            titleLabel.setStyle(
                "-fx-font-weight: 600;" +
                "-fx-font-size: " + FONT_SIZE_BODY + ";" +
                "-fx-text-fill: " + textColor + ";"
            );
            box.getChildren().add(titleLabel);
        }
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
            "-fx-font-size: " + FONT_SIZE_BODY + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );
        box.getChildren().add(messageLabel);
        
        return box;
    }
}