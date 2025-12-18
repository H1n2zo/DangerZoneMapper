/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dangerzone.utils;

import com.dangerzone.models.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataExporter {
    
    public static void exportLandmarks(Stage stage, Connection conn) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Landmarks to CSV");
        fileChooser.setInitialFileName("landmarks_" + getTimestamp() + ".csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                LandmarkDAO dao = new LandmarkDAO(conn);
                List<Landmark> landmarks = dao.getAllLandmarks();
                
                FileWriter writer = new FileWriter(file);
                
                // Header
                writer.append("ID,Name,Type,Address,Barangay,Latitude,Longitude,Contact,Capacity,Evacuation Site,Description\n");
                
                // Data
                for (Landmark landmark : landmarks) {
                    writer.append(String.valueOf(landmark.getId())).append(",");
                    writer.append(escape(landmark.getName())).append(",");
                    writer.append(escape(landmark.getType())).append(",");
                    writer.append(escape(landmark.getAddress())).append(",");
                    writer.append(escape(landmark.getBarangay())).append(",");
                    writer.append(String.valueOf(landmark.getLatitude())).append(",");
                    writer.append(String.valueOf(landmark.getLongitude())).append(",");
                    writer.append(escape(landmark.getContactNumber())).append(",");
                    writer.append(landmark.getCapacity() != null ? String.valueOf(landmark.getCapacity()) : "").append(",");
                    writer.append(landmark.isEvacuationSite() ? "Yes" : "No").append(",");
                    writer.append(escape(landmark.getDescription())).append("\n");
                }
                
                writer.flush();
                writer.close();
                
                System.out.println("✅ Landmarks exported to: " + file.getAbsolutePath());
                
            } catch (SQLException | IOException e) {
                System.err.println("❌ Export failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public static void exportHazardZones(Stage stage, Connection conn) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Hazard Zones to CSV");
        fileChooser.setInitialFileName("hazard_zones_" + getTimestamp() + ".csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                HazardZoneDAO dao = new HazardZoneDAO(conn);
                List<HazardZone> zones = dao.getAllHazardZones();
                
                FileWriter writer = new FileWriter(file);
                
                // Header
                writer.append("ID,Zone Name,Barangay,Hazard Type,Severity,Latitude,Longitude,Radius(m),Population,Active,Date Identified,Description\n");
                
                // Data
                for (HazardZone zone : zones) {
                    writer.append(String.valueOf(zone.getZoneId())).append(",");
                    writer.append(escape(zone.getZoneName())).append(",");
                    writer.append(escape(zone.getBarangay())).append(",");
                    writer.append(escape(zone.getHazardType())).append(",");
                    writer.append(escape(zone.getSeverityLevel())).append(",");
                    writer.append(String.valueOf(zone.getLatitude())).append(",");
                    writer.append(String.valueOf(zone.getLongitude())).append(",");
                    writer.append(String.valueOf(zone.getRadiusMeters())).append(",");
                    writer.append(String.valueOf(zone.getAffectedPopulation())).append(",");
                    writer.append(zone.isActive() ? "Yes" : "No").append(",");
                    writer.append(zone.getDateIdentified() != null ? zone.getDateIdentified().toString() : "").append(",");
                    writer.append(escape(zone.getDescription())).append("\n");
                }
                
                writer.flush();
                writer.close();
                
                System.out.println("✅ Hazard zones exported to: " + file.getAbsolutePath());
                
            } catch (SQLException | IOException e) {
                System.err.println("❌ Export failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public static void exportIncidents(Stage stage, Connection conn) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Incidents to CSV");
        fileChooser.setInitialFileName("incidents_" + getTimestamp() + ".csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                IncidentDAO dao = new IncidentDAO(conn);
                List<Incident> incidents = dao.getAllIncidents();
                
                FileWriter writer = new FileWriter(file);
                
                // Header
                writer.append("ID,Date,Type,Barangay,Severity,Casualties,Injuries,Families Affected,Structures Damaged,Estimated Cost,Description\n");
                
                // Data
                for (Incident incident : incidents) {
                    writer.append(String.valueOf(incident.getIncidentId())).append(",");
                    writer.append(incident.getIncidentDate() != null ? incident.getIncidentDate().toString() : "").append(",");
                    writer.append(escape(incident.getIncidentType())).append(",");
                    writer.append(escape(incident.getBarangay())).append(",");
                    writer.append(escape(incident.getSeverity())).append(",");
                    writer.append(String.valueOf(incident.getCasualties())).append(",");
                    writer.append(String.valueOf(incident.getInjuries())).append(",");
                    writer.append(String.valueOf(incident.getFamiliesAffected())).append(",");
                    writer.append(String.valueOf(incident.getStructuresDamaged())).append(",");
                    writer.append(incident.getEstimatedCost() != null ? incident.getEstimatedCost().toString() : "0").append(",");
                    writer.append(escape(incident.getDescription())).append("\n");
                }
                
                writer.flush();
                writer.close();
                
                System.out.println("✅ Incidents exported to: " + file.getAbsolutePath());
                
            } catch (SQLException | IOException e) {
                System.err.println("❌ Export failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private static String escape(String value) {
        if (value == null) return "";
        // Escape quotes and wrap in quotes if contains comma
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
    
    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}