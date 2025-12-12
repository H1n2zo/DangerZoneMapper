package com.dangerzonemapper.ui;

import com.dangerzonemapper.model.HazardZone;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Factory class for creating UI components with consistent styling
 */
public class UIComponentFactory {
    
    /**
     * Create a styled label
     */
    public static Label createLabel(String text, boolean bold, int fontSize) {
        Label label = new Label(text);
        if (bold) {
            label.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        } else {
            label.setFont(Font.font("Arial", fontSize));
        }
        return label;
    }
    
    /**
     * Create hazard type combo box
     */
    public static ComboBox<String> createHazardTypeComboBox() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Flood", "Landslide", "Fire", "Earthquake", "Typhoon");
        combo.setValue("Flood");
        combo.setPrefWidth(Double.MAX_VALUE);
        combo.setStyle("-fx-font-size: 11px;");
        return combo;
    }
    
    /**
     * Create a text field
     */
    public static TextField createTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-font-size: 11px;");
        return field;
    }
    
    /**
     * Create a text area
     */
    public static TextArea createTextArea(String promptText, int rows) {
        TextArea area = new TextArea();
        area.setPrefRowCount(rows);
        area.setPromptText(promptText);
        area.setWrapText(true);
        area.setStyle("-fx-font-size: 11px;");
        return area;
    }
    
    /**
     * Create radius slider
     */
    public static Slider createRadiusSlider() {
        Slider slider = new Slider(100, 2000, 500);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(500);
        slider.setBlockIncrement(100);
        return slider;
    }
    
    /**
     * Create a styled button
     */
    public static Button createButton(String text, String color, double width) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;",
            color
        ));
        button.setPrefWidth(width);
        return button;
    }
    
    /**
     * Create menu bar
     */
    public static MenuBar createMenuBar(Runnable onRefresh, Runnable onAbout, Runnable onSafety) {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #2196F3; -fx-padding: 3 0 3 0;");
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh Map");
        refreshItem.setOnAction(e -> onRefresh.run());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), exitItem);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> onAbout.run());
        MenuItem safetyItem = new MenuItem("Safety Guidelines");
        safetyItem.setOnAction(e -> onSafety.run());
        helpMenu.getItems().addAll(aboutItem, safetyItem);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }
    
    /**
     * Create map header with title and legend
     */
    public static HBox createMapHeader() {
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Ormoc City Hazard Map");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        HBox legend = createLegend();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBox.getChildren().addAll(titleLabel, spacer, legend);
        
        return titleBox;
    }
    
    /**
     * Create legend for hazard types
     */
    private static HBox createLegend() {
        HBox legend = new HBox(12);
        legend.setAlignment(Pos.CENTER_RIGHT);
        legend.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 3;");
        
        Label legendTitle = new Label("Legend:");
        legendTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        legend.getChildren().add(legendTitle);
        
        String[] types = {"Flood", "Landslide", "Fire", "Earthquake", "Typhoon"};
        
        for (String type : types) {
            HBox item = new HBox(4);
            item.setAlignment(Pos.CENTER);
            
            Circle circle = new Circle(5);
            circle.setFill(MapRenderer.getColorForTypeDark(type));
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1.5);
            
            Label label = new Label(type);
            label.setFont(Font.font("Arial", 9));
            
            item.getChildren().addAll(circle, label);
            legend.getChildren().add(item);
        }
        
        return legend;
    }
    
    /**
     * Show simple alert dialog
     */
    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        return alert.showAndWait().get() == ButtonType.OK;
    }
    
    /**
     * Show hazard zone details
     */
    public static void showHazardDetails(HazardZone zone) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hazard Zone Details");
        alert.setHeaderText(zone.getName());
        
        TextArea textArea = new TextArea();
        textArea.setText(zone.getDetailedInfo());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(8);
        
        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefWidth(450);
        alert.showAndWait();
    }
    
    /**
     * Show safety guidelines dialog
     */
    public static void showSafetyGuidelines() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Safety Guidelines");
        alert.setHeaderText("Emergency Preparedness");
        alert.setContentText(
            "FLOOD SAFETY:\n" +
            "• Move to higher ground immediately\n" +
            "• Avoid walking through floodwater\n" +
            "• Turn off utilities before evacuating\n\n" +
            "LANDSLIDE SAFETY:\n" +
            "• Watch for cracks in walls and tilting trees\n" +
            "• Evacuate if you hear rumbling sounds\n" +
            "• Move quickly away from the path\n\n" +
            "FIRE SAFETY:\n" +
            "• Call emergency: 911\n" +
            "• Evacuate immediately\n" +
            "• Stay low, cover mouth\n\n" +
            "EARTHQUAKE SAFETY:\n" +
            "• Drop, Cover, Hold On\n" +
            "• Stay away from windows\n" +
            "• Be prepared for aftershocks\n\n" +
            "TYPHOON SAFETY:\n" +
            "• Stay indoors\n" +
            "• Secure loose objects\n" +
            "• Listen to emergency broadcasts\n\n" +
            "Emergency Hotline: 911"
        );
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }
}