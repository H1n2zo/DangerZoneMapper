package com.dangerzonemapper.ui;

import com.dangerzonemapper.model.HazardZone;
import com.dangerzonemapper.utils.CoordinateUtils;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

/**
 * Handles rendering of the map and hazard zones
 */
public class MapRenderer {
    private final Pane mapPane;
    
    public MapRenderer(Pane mapPane) {
        this.mapPane = mapPane;
        initializeMap();
    }
    
    /**
     * Initialize the map with grid and labels
     */
    private void initializeMap() {
        drawMapGrid();
        addMapLabels();
    }
    
    /**
     * Draw grid lines on the map
     */
    private void drawMapGrid() {
        double width = CoordinateUtils.getMapWidth();
        double height = CoordinateUtils.getMapHeight();
        
        // Vertical grid lines
        for (int i = 0; i <= 17; i++) {
            double x = i * 50;
            Rectangle line = new Rectangle(x, 0, 0.5, height);
            line.setFill(Color.rgb(200, 200, 200, 0.4));
            mapPane.getChildren().add(line);
        }
        
        // Horizontal grid lines
        for (int i = 0; i <= 11; i++) {
            double y = i * 50;
            Rectangle line = new Rectangle(0, y, width, 0.5);
            line.setFill(Color.rgb(200, 200, 200, 0.4));
            mapPane.getChildren().add(line);
        }
    }
    
    /**
     * Add city name and other labels to the map
     */
    private void addMapLabels() {
        // City name watermark
        Label cityLabel = new Label("ORMOC CITY");
        cityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        cityLabel.setTextFill(Color.rgb(100, 100, 100, 0.25));
        cityLabel.setLayoutX(320);
        cityLabel.setLayoutY(260);
        mapPane.getChildren().add(cityLabel);
        
        // Coastal label
        Label coastLabel = new Label("← Ormoc Bay");
        coastLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        coastLabel.setTextFill(Color.rgb(0, 100, 200));
        coastLabel.setLayoutX(40);
        coastLabel.setLayoutY(290);
        mapPane.getChildren().add(coastLabel);
        
        // Compass
        VBox compass = new VBox(2);
        compass.setAlignment(Pos.CENTER);
        Label northLabel = new Label("N");
        northLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        northLabel.setTextFill(Color.RED);
        Label compassSymbol = new Label("↑");
        compassSymbol.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        compassSymbol.setTextFill(Color.RED);
        compass.getChildren().addAll(northLabel, compassSymbol);
        compass.setLayoutX(800);
        compass.setLayoutY(15);
        mapPane.getChildren().add(compass);
    }
    
    /**
     * Draw a hazard zone on the map
     */
    public void drawHazardZone(HazardZone zone) {
        double x = CoordinateUtils.lonToPixelX(zone.getLongitude());
        double y = CoordinateUtils.latToPixelY(zone.getLatitude());
        double radiusPixels = CoordinateUtils.metersToPixels(zone.getRadius());
        
        // Draw hazard circle
        Circle circle = new Circle(x, y, radiusPixels);
        Color fillColor = getColorForType(zone.getType());
        circle.setFill(fillColor);
        circle.setStroke(fillColor.darker());
        circle.setStrokeWidth(2);
        
        // Add tooltip
        Tooltip tooltip = createTooltip(zone);
        Tooltip.install(circle, tooltip);
        
        mapPane.getChildren().add(circle);
        circle.toBack();
        
        // Draw center marker
        Circle marker = new Circle(x, y, 5);
        marker.setFill(fillColor.darker());
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(1.5);
        Tooltip.install(marker, tooltip);
        mapPane.getChildren().add(marker);
    }
    
    /**
     * Draw selection marker at given coordinates
     */
    public Circle drawSelectionMarker(double lat, double lon) {
        double x = CoordinateUtils.lonToPixelX(lon);
        double y = CoordinateUtils.latToPixelY(lat);
        
        Circle marker = new Circle(x, y, 7);
        marker.setFill(Color.RED);
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(2);
        
        mapPane.getChildren().add(marker);
        return marker;
    }
    
    /**
     * Draw radius circle for selection
     */
    public Circle drawRadiusCircle(double lat, double lon, double radiusMeters, String hazardType) {
        double x = CoordinateUtils.lonToPixelX(lon);
        double y = CoordinateUtils.latToPixelY(lat);
        double radiusPixels = CoordinateUtils.metersToPixels(radiusMeters);
        
        Circle circle = new Circle(x, y, radiusPixels);
        Color color = getColorForType(hazardType);
        circle.setFill(color);
        circle.setStroke(color.darker());
        circle.setStrokeWidth(2);
        circle.getStrokeDashArray().addAll(5.0, 5.0);
        
        mapPane.getChildren().add(circle);
        circle.toBack();
        
        return circle;
    }
    
    /**
     * Clear all hazard zones from map (except grid and labels)
     */
    public void clearHazardZones() {
        mapPane.getChildren().removeIf(node -> node instanceof Circle);
    }
    
    /**
     * Remove a specific node from the map
     */
    public void removeNode(javafx.scene.Node node) {
        if (node != null) {
            mapPane.getChildren().remove(node);
        }
    }
    
    /**
     * Get color for hazard type
     */
    private Color getColorForType(String type) {
        return switch (type.toLowerCase()) {
            case "flood" -> Color.rgb(0, 150, 255, 0.3);
            case "landslide" -> Color.rgb(139, 69, 19, 0.3);
            case "fire" -> Color.rgb(255, 0, 0, 0.3);
            case "earthquake" -> Color.rgb(255, 165, 0, 0.3);
            case "typhoon" -> Color.rgb(128, 0, 128, 0.3);
            default -> Color.rgb(128, 128, 128, 0.3);
        };
    }
    
    /**
     * Create tooltip for hazard zone
     */
    private Tooltip createTooltip(HazardZone zone) {
        String tooltipText = String.format(
            "%s\n%s\nRadius: %.0fm\nAdded: %s",
            zone.getName(),
            zone.getType(),
            zone.getRadius(),
            zone.getDateAdded()
        );
        return new Tooltip(tooltipText);
    }
    
    /**
     * Get color for legend (darker version)
     */
    public static Color getColorForTypeDark(String type) {
        return switch (type.toLowerCase()) {
            case "flood" -> Color.rgb(0, 150, 255).darker();
            case "landslide" -> Color.rgb(139, 69, 19).darker();
            case "fire" -> Color.rgb(255, 0, 0).darker();
            case "earthquake" -> Color.rgb(255, 165, 0).darker();
            case "typhoon" -> Color.rgb(128, 0, 128).darker();
            default -> Color.rgb(128, 128, 128).darker();
        };
    }
}