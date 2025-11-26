package com.dangerzone.utils;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Modern UI Style Manager for Danger Zone Mapping System
 * Provides consistent, modern styling across all components
 */
public class StyleManager {
    
    // Color Palette - Modern, Professional
    public static final String PRIMARY_COLOR = "#2C3E50";      // Dark Blue-Gray
    public static final String SECONDARY_COLOR = "#34495E";    // Lighter Blue-Gray
    public static final String ACCENT_COLOR = "#3498DB";       // Bright Blue
    public static final String SUCCESS_COLOR = "#27AE60";      // Green
    public static final String DANGER_COLOR = "#E74C3C";       // Red
    public static final String WARNING_COLOR = "#F39C12";      // Orange
    public static final String INFO_COLOR = "#9B59B6";         // Purple
    public static final String LIGHT_BG = "#ECF0F1";           // Light Gray
    public static final String CARD_BG = "#FFFFFF";            // White
    public static final String TEXT_PRIMARY = "#2C3E50";       // Dark Text
    public static final String TEXT_SECONDARY = "#7F8C8D";     // Gray Text
    public static final String BORDER_COLOR = "#BDC3C7";       // Light Border
    
    // Fonts
    public static final String FONT_FAMILY = "Segoe UI, Arial, sans-serif";
    
    /**
     * Apply modern theme to entire application
     */
    public static void applyModernTheme(Region root) {
        root.setStyle(
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: 14px;" +
            "-fx-background-color: " + LIGHT_BG + ";"
        );
    }
    
    /**
     * Style a modern card container
     */
    public static void styleCard(Region card) {
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);" +
            "-fx-padding: 20;"
        );
    }
    
    /**
     * Style a primary button
     */
    public static void stylePrimaryButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + ACCENT_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 6, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #2980B9;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.5), 8, 0, 0, 3);"
        ));
        
        button.setOnMouseExited(e -> stylePrimaryButton(button));
    }
    
    /**
     * Style a success button
     */
    public static void styleSuccessButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + SUCCESS_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(39,174,96,0.3), 6, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #229954;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(39,174,96,0.5), 8, 0, 0, 3);"
        ));
        
        button.setOnMouseExited(e -> styleSuccessButton(button));
    }
    
    /**
     * Style a danger button
     */
    public static void styleDangerButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + DANGER_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 6, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #C0392B;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.5), 8, 0, 0, 3);"
        ));
        
        button.setOnMouseExited(e -> styleDangerButton(button));
    }
    
    /**
     * Style a secondary button
     */
    public static void styleSecondaryButton(Button button) {
        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + LIGHT_BG + ";" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 20;" +
            "-fx-border-color: " + ACCENT_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> styleSecondaryButton(button));
    }
    
    /**
     * Style a modern text field
     */
    public static void styleTextField(TextField field) {
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: " + ACCENT_COLOR + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-padding: 10;" +
                    "-fx-font-size: 14px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 6, 0, 0, 0);"
                );
            } else {
                styleTextField(field);
            }
        });
    }
    
    /**
     * Style a modern text area
     */
    public static void styleTextArea(TextArea area) {
        area.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;"
        );
        
        area.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                area.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: " + ACCENT_COLOR + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-padding: 10;" +
                    "-fx-font-size: 14px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 6, 0, 0, 0);"
                );
            } else {
                styleTextArea(area);
            }
        });
    }
    
    /**
     * Style a modern combo box
     */
    public static void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 5 10;"
        );
    }
    
    /**
     * Style a title label
     */
    public static void styleTitleLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";"
        );
    }
    
    /**
     * Style a subtitle label
     */
    public static void styleSubtitleLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + SECONDARY_COLOR + ";"
        );
    }
    
    /**
     * Style a section header
     */
    public static void styleSectionHeader(Label label) {
        label.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-padding: 0 0 10 0;"
        );
    }
    
    /**
     * Create a stat card with modern design
     */
    public static VBox createStatCard(String title, String value, String color, String icon) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(25));
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPrefWidth(220);
        card.setPrefHeight(160);
        
        // Gradient background
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + color + ", derive(" + color + ", -20%));" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);"
        );
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-text-fill: rgba(255,255,255,0.9);"
        );
        
        // Value
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: rgba(255,255,255,0.95);"
        );
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, derive(" + color + ", 10%), " + color + ");" +
                "-fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 6);" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + color + ", derive(" + color + ", -20%));" +
                "-fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);" +
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
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
    }
    
    /**
     * Style a menu bar
     */
    public static void styleMenuBar(MenuBar menuBar) {
        menuBar.setStyle(
            "-fx-border-width: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);"
        );
    }
    
    /**
     * Style a modern tab pane
     */
    public static void styleTabPane(TabPane tabPane) {
        tabPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-width: 0;"
        );
    }
    
    /**
     * Create a modern toolbar
     */
    public static HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setPadding(new Insets(15, 20, 15, 20));
        toolbar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        toolbar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        return toolbar;
    }
    
    /**
     * Create a modern legend box
     */
    public static HBox createLegendBox() {
        HBox legend = new HBox(20);
        legend.setAlignment(javafx.geometry.Pos.CENTER);
        legend.setPadding(new Insets(20));
        legend.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        return legend;
    }
    
    /**
     * Style a dialog
     */
    public static void styleDialog(DialogPane dialogPane) {
        dialogPane.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;"
        );
    }
}