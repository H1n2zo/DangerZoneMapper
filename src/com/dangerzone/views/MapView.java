package com.dangerzone.views;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MapView extends Pane {
    private ImageView mapImageView;
    private double mapWidth = 1200;  // Adjust based on your image
    private double mapHeight = 800;  // Adjust based on your image
    
    public MapView() {
        try {
            Image mapImage = new Image(getClass().getResourceAsStream("/resources/ormoc_map.png"));
            mapImageView = new ImageView(mapImage);
            mapImageView.setFitWidth(mapWidth);
            mapImageView.setFitHeight(mapHeight);
            mapImageView.setPreserveRatio(true);
            
            // Add click handler
            mapImageView.setOnMouseClicked(e -> {
                double x = e.getX();
                double y = e.getY();
                // Convert pixel coordinates to lat/long if needed
                System.out.println("Map clicked at: " + x + ", " + y);
            });
            
            getChildren().add(mapImageView);
            
        } catch (Exception e) {
            System.err.println("Error loading map image: " + e.getMessage());
        }
    }
    
    public void addMarker(double x, double y, String type) {
        // Add marker overlay at specified coordinates
        // This would create a small circle or icon at the position
    }
    
    public void clearMarkers() {
        // Remove all markers except base map
        getChildren().removeIf(node -> node != mapImageView);
    }
}