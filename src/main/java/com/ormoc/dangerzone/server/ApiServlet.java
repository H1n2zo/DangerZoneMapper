package com.ormoc.dangerzone.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ormoc.dangerzone.model.HazardZone;
import com.ormoc.dangerzone.model.Landmark;
import com.ormoc.dangerzone.model.Incident;
import com.ormoc.dangerzone.service.HazardZoneService;
import com.ormoc.dangerzone.service.LandmarkService;
import com.ormoc.dangerzone.service.IncidentService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Servlet
 * Handles all API requests for hazard zones, landmarks, and incidents
 */
public class ApiServlet extends HttpServlet {

    private final Gson gson;
    private final HazardZoneService hazardZoneService;
    private final LandmarkService landmarkService;
    private final IncidentService incidentService;

    public ApiServlet() {
        this.gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();
        this.hazardZoneService = new HazardZoneService();
        this.landmarkService = new LandmarkService();
        this.incidentService = new IncidentService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // Enable CORS
        resp.setHeader("Access-Control-Allow-Origin", "*");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                sendError(resp, 400, "Invalid API endpoint");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            String resource = pathParts.length > 1 ? pathParts[1] : "";

            switch (resource) {
                case "hazardzones":
                    handleGetHazardZones(req, resp, pathParts);
                    break;
                case "landmarks":
                    handleGetLandmarks(req, resp, pathParts);
                    break;
                case "incidents":
                    handleGetIncidents(req, resp, pathParts);
                    break;
                case "guidelines":
                    handleGetGuidelines(req, resp);
                    break;
                default:
                    sendError(resp, 404, "Resource not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        try {
            String[] pathParts = pathInfo.split("/");
            String resource = pathParts.length > 1 ? pathParts[1] : "";

            String body = getRequestBody(req);

            switch (resource) {
                case "hazardzones":
                    handleCreateHazardZone(body, resp);
                    break;
                case "landmarks":
                    handleCreateLandmark(body, resp);
                    break;
                default:
                    sendError(resp, 404, "Resource not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        try {
            String[] pathParts = pathInfo.split("/");
            String resource = pathParts.length > 1 ? pathParts[1] : "";
            int id = pathParts.length > 2 ? Integer.parseInt(pathParts[2]) : 0;

            String body = getRequestBody(req);

            switch (resource) {
                case "hazardzones":
                    handleUpdateHazardZone(id, body, resp);
                    break;
                case "landmarks":
                    handleUpdateLandmark(id, body, resp);
                    break;
                default:
                    sendError(resp, 404, "Resource not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        try {
            String[] pathParts = pathInfo.split("/");
            String resource = pathParts.length > 1 ? pathParts[1] : "";
            int id = pathParts.length > 2 ? Integer.parseInt(pathParts[2]) : 0;

            switch (resource) {
                case "hazardzones":
                    handleDeleteHazardZone(id, resp);
                    break;
                case "landmarks":
                    handleDeleteLandmark(id, resp);
                    break;
                default:
                    sendError(resp, 404, "Resource not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // GET Handlers
    private void handleGetHazardZones(HttpServletRequest req, 
                                     HttpServletResponse resp, 
                                     String[] pathParts) throws IOException {
        if (pathParts.length > 2) {
            // Get specific hazard zone
            int id = Integer.parseInt(pathParts[2]);
            HazardZone zone = hazardZoneService.getById(id);
            if (zone != null) {
                sendSuccess(resp, zone);
            } else {
                sendError(resp, 404, "Hazard zone not found");
            }
        } else {
            // Get all hazard zones
            String barangay = req.getParameter("barangay");
            String hazardType = req.getParameter("hazardType");
            
            List<HazardZone> zones;
            if (barangay != null && !barangay.isEmpty()) {
                zones = hazardZoneService.getByBarangay(barangay);
            } else if (hazardType != null && !hazardType.isEmpty()) {
                zones = hazardZoneService.getByHazardType(hazardType);
            } else {
                zones = hazardZoneService.getAll();
            }
            sendSuccess(resp, zones);
        }
    }

    private void handleGetLandmarks(HttpServletRequest req, 
                                   HttpServletResponse resp,
                                   String[] pathParts) throws IOException {
        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);
            Landmark landmark = landmarkService.getById(id);
            if (landmark != null) {
                sendSuccess(resp, landmark);
            } else {
                sendError(resp, 404, "Landmark not found");
            }
        } else {
            String evacuationOnly = req.getParameter("evacuationSite");
            List<Landmark> landmarks;
            
            if ("true".equalsIgnoreCase(evacuationOnly)) {
                landmarks = landmarkService.getEvacuationSites();
            } else {
                landmarks = landmarkService.getAll();
            }
            sendSuccess(resp, landmarks);
        }
    }

    private void handleGetIncidents(HttpServletRequest req, 
                                   HttpServletResponse resp,
                                   String[] pathParts) throws IOException {
        if (pathParts.length > 2 && "stats".equals(pathParts[2])) {
            // Get incident statistics
            Map<String, Object> stats = incidentService.getStatistics();
            sendSuccess(resp, stats);
        } else {
            // Get all incidents
            String hazardType = req.getParameter("type");
            List<Incident> incidents;
            
            if (hazardType != null && !hazardType.isEmpty()) {
                incidents = incidentService.getByType(hazardType);
            } else {
                incidents = incidentService.getAll();
            }
            sendSuccess(resp, incidents);
        }
    }

    private void handleGetGuidelines(HttpServletRequest req, 
                                    HttpServletResponse resp) throws IOException {
        // For now, return empty array - will implement SafetyGuidelineService later
        sendSuccess(resp, new Object[]{});
    }

    // POST Handlers
    private void handleCreateHazardZone(String body, HttpServletResponse resp) 
            throws IOException {
        HazardZone zone = gson.fromJson(body, HazardZone.class);
        boolean success = hazardZoneService.create(zone);
        
        if (success) {
            sendSuccess(resp, zone);
        } else {
            sendError(resp, 500, "Failed to create hazard zone");
        }
    }

    private void handleCreateLandmark(String body, HttpServletResponse resp) 
            throws IOException {
        Landmark landmark = gson.fromJson(body, Landmark.class);
        boolean success = landmarkService.create(landmark);
        
        if (success) {
            sendSuccess(resp, landmark);
        } else {
            sendError(resp, 500, "Failed to create landmark");
        }
    }

    // PUT Handlers
    private void handleUpdateHazardZone(int id, String body, 
                                       HttpServletResponse resp) throws IOException {
        HazardZone zone = gson.fromJson(body, HazardZone.class);
        zone.setZoneId(id);
        boolean success = hazardZoneService.update(zone);
        
        if (success) {
            sendSuccess(resp, zone);
        } else {
            sendError(resp, 500, "Failed to update hazard zone");
        }
    }

    private void handleUpdateLandmark(int id, String body, 
                                     HttpServletResponse resp) throws IOException {
        Landmark landmark = gson.fromJson(body, Landmark.class);
        landmark.setLandmarkId(id);
        boolean success = landmarkService.update(landmark);
        
        if (success) {
            sendSuccess(resp, landmark);
        } else {
            sendError(resp, 500, "Failed to update landmark");
        }
    }

    // DELETE Handlers
    private void handleDeleteHazardZone(int id, HttpServletResponse resp) 
            throws IOException {
        boolean success = hazardZoneService.delete(id);
        
        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hazard zone deleted successfully");
            sendSuccess(resp, response);
        } else {
            sendError(resp, 500, "Failed to delete hazard zone");
        }
    }

    private void handleDeleteLandmark(int id, HttpServletResponse resp) 
            throws IOException {
        boolean success = landmarkService.delete(id);
        
        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Landmark deleted successfully");
            sendSuccess(resp, response);
        } else {
            sendError(resp, 500, "Failed to delete landmark");
        }
    }

    // Utility Methods
    private String getRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(data));
    }

    private void sendError(HttpServletResponse resp, int status, String message) 
            throws IOException {
        resp.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        resp.getWriter().write(gson.toJson(error));
    }
}