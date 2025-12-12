package com.dangerzonemapper.utils;

/**
 * Utility class for coordinate conversions between geographic and pixel coordinates
 */
public class CoordinateUtils {
    // Ormoc City boundaries
    private static final double ORMOC_LAT_MIN = 11.0;
    private static final double ORMOC_LAT_MAX = 11.2;
    private static final double ORMOC_LON_MIN = 124.55;
    private static final double ORMOC_LON_MAX = 124.65;
    
    // Map dimensions in pixels
    private static final double MAP_WIDTH = 850.0;
    private static final double MAP_HEIGHT = 580.0;
    
    /**
     * Convert pixel X coordinate to longitude
     */
    public static double pixelXToLon(double x) {
        return ORMOC_LON_MIN + (x / MAP_WIDTH) * (ORMOC_LON_MAX - ORMOC_LON_MIN);
    }
    
    /**
     * Convert pixel Y coordinate to latitude
     */
    public static double pixelYToLat(double y) {
        return ORMOC_LAT_MAX - (y / MAP_HEIGHT) * (ORMOC_LAT_MAX - ORMOC_LAT_MIN);
    }
    
    /**
     * Convert longitude to pixel X coordinate
     */
    public static double lonToPixelX(double lon) {
        return ((lon - ORMOC_LON_MIN) / (ORMOC_LON_MAX - ORMOC_LON_MIN)) * MAP_WIDTH;
    }
    
    /**
     * Convert latitude to pixel Y coordinate
     */
    public static double latToPixelY(double lat) {
        return ((ORMOC_LAT_MAX - lat) / (ORMOC_LAT_MAX - ORMOC_LAT_MIN)) * MAP_HEIGHT;
    }
    
    /**
     * Convert meters to pixels (for radius display)
     * Uses approximate conversion: 1 degree latitude ≈ 111km
     */
    public static double metersToPixels(double meters) {
        double degreesLat = meters / 111000.0;
        return (degreesLat / (ORMOC_LAT_MAX - ORMOC_LAT_MIN)) * MAP_HEIGHT;
    }
    
    /**
     * Convert pixels to meters (for radius calculation)
     */
    public static double pixelsToMeters(double pixels) {
        double degreesLat = (pixels / MAP_HEIGHT) * (ORMOC_LAT_MAX - ORMOC_LAT_MIN);
        return degreesLat * 111000.0;
    }
    
    /**
     * Check if coordinates are within Ormoc City bounds
     */
    public static boolean isWithinBounds(double lat, double lon) {
        return lat >= ORMOC_LAT_MIN && lat <= ORMOC_LAT_MAX &&
               lon >= ORMOC_LON_MIN && lon <= ORMOC_LON_MAX;
    }
    
    /**
     * Format coordinates for display
     */
    public static String formatCoordinates(double lat, double lon) {
        return String.format("%.6f°N, %.6f°E", lat, lon);
    }
    
    // Getters for map boundaries
    public static double getMapWidth() { return MAP_WIDTH; }
    public static double getMapHeight() { return MAP_HEIGHT; }
    public static double getLatMin() { return ORMOC_LAT_MIN; }
    public static double getLatMax() { return ORMOC_LAT_MAX; }
    public static double getLonMin() { return ORMOC_LON_MIN; }
    public static double getLonMax() { return ORMOC_LON_MAX; }
}