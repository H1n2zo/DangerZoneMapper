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
 * ENHANCED VERSION - Better proportions and professional look
 */
public class StyleManager {
    
    // Color Palette - Modern, Professional
    public static final String PRIMARY_COLOR = "#2C3E50";
    public static final String SECONDARY_COLOR = "#34495E";
    public static final String ACCENT_COLOR = "#3498DB";
    public static final String SUCCESS_COLOR = "#27AE60";
    public static final String DANGER_COLOR = "#E74C3C";
    public static final String WARNING_COLOR = "#F39C12";
    public static final String INFO_COLOR = "#9B59B6";
    public static final String LIGHT_BG = "#ECF0F1";
    public static final String CARD_BG = "#FFFFFF";
    public static final String TEXT_PRIMARY = "#2C3E50";
    public static final String TEXT_SECONDARY = "#7F8C8D";
    public static final String BORDER_COLOR = "#BDC3C7";
    
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
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);" +
            "-fx-padding: 15;"
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
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #2980B9;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.5), 6, 0, 0, 3);"
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
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-effect: dropshadow(gaussian, rgba(39,174,96,0.3), 4, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #229954;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-effect: dropshadow(gaussian, rgba(39,174,96,0.5), 6, 0, 0, 3);"
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
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 4, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #C0392B;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.5), 6, 0, 0, 3);"
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
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + LIGHT_BG + ";" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-border-color: " + ACCENT_COLOR + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        ));
        
        button.setOnMouseExited(e -> styleSecondaryButton(button));
    }
    
    /**
     * Style a modern text field
     */
    public static void styleTextField(TextField field) {
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 8;" +
            "-fx-font-size: 13px;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 6;" +
                    "-fx-border-color: " + ACCENT_COLOR + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 6;" +
                    "-fx-padding: 8;" +
                    "-fx-font-size: 13px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.2), 4, 0, 0, 0);"
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
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 8;" +
            "-fx-font-size: 13px;"
        );
        
        area.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                area.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 6;" +
                    "-fx-border-color: " + ACCENT_COLOR + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 6;" +
                    "-fx-padding: 8;" +
                    "-fx-font-size: 13px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.2), 4, 0, 0, 0);"
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
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 6 10;" +
            "-fx-font-size: 13px;"
        );
    }
    
    /**
     * Style a title label - REDUCED SIZE
     */
    public static void styleTitleLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-padding: 0 0 5 0;"
        );
    }
    
    /**
     * Style a subtitle label
     */
    public static void styleSubtitleLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + SECONDARY_COLOR + ";"
        );
    }
    
    /**
     * Style a section header
     */
    public static void styleSectionHeader(Label label) {
        label.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-padding: 0 0 8 0;"
        );
    }
    
    /**
     * Create a stat card with modern design - COMPACT VERSION
     */
    public static VBox createStatCard(String title, String value, String color, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(140);
        
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + color + ", derive(" + color + ", -20%));" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 3);"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
            "-fx-font-size: 42px;" +
            "-fx-text-fill: rgba(255,255,255,0.9);"
        );
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: rgba(255,255,255,0.95);"
        );
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, derive(" + color + ", 10%), " + color + ");" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 5);" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + color + ", derive(" + color + ", -20%));" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 3);" +
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
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );
    }
    
    /**
     * Style a menu bar - COMPACT VERSION
     */
    public static void styleMenuBar(MenuBar menuBar) {
        menuBar.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + ";" +
            "-fx-padding: 0;" +
            "-fx-border-width: 0;" +
            "-fx-font-size: 13px;"
        );
        
        // Make menu items white text
        for (Menu menu : menuBar.getMenus()) {
            menu.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        }
    }
    
    /**
     * Style a modern tab pane - COMPACT VERSION
     */
    public static void styleTabPane(TabPane tabPane) {
        tabPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-tab-min-height: 36px;" +
            "-fx-tab-max-height: 36px;"
        );
    }
    
    /**
     * Create a modern toolbar - COMPACT VERSION
     */
    public static HBox createToolbar() {
        HBox toolbar = new HBox(12);
        toolbar.setPadding(new Insets(10, 15, 10, 15));
        toolbar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        toolbar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 2);"
        );
        return toolbar;
    }
    
    /**
     * Create a modern legend box - COMPACT VERSION
     */
    public static HBox createLegendBox() {
        HBox legend = new HBox(15);
        legend.setAlignment(javafx.geometry.Pos.CENTER);
        legend.setPadding(new Insets(12, 15, 12, 15));
        legend.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 2);"
        );
        return legend;
    }
    
    /**
     * Style a dialog
     */
    public static void styleDialog(DialogPane dialogPane) {
        dialogPane.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;"
        );
    }
}