package com.ormoc.dangerzone.service;

import com.ormoc.dangerzone.dao.IncidentDAO;
import com.ormoc.dangerzone.model.Incident;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncidentService {

    private final IncidentDAO incidentDAO;

    public IncidentService() {
        this.incidentDAO = new IncidentDAO();
    }

    public List<Incident> getAll() {
        return incidentDAO.findAll();
    }

    public Incident getById(int incidentId) {
        return incidentDAO.findById(incidentId);
    }

    public List<Incident> getByType(String incidentType) {
        return incidentDAO.findByType(incidentType);
    }

    public List<Incident> getByYear(int year) {
        return incidentDAO.findByYear(year);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        List<Incident> allIncidents = incidentDAO.findAll();

        int totalIncidents = allIncidents.size();
        int totalCasualties = 0;
        int totalInjuries = 0;
        int totalFamiliesAffected = 0;
        double totalEstimatedCost = 0.0;

        Map<String, Integer> incidentsByType = new HashMap<>();

        for (Incident incident : allIncidents) {
            totalCasualties += incident.getCasualties();
            totalInjuries += incident.getInjuries();
            totalFamiliesAffected += incident.getFamiliesAffected();
            
            if (incident.getEstimatedCost() != null) {
                totalEstimatedCost += incident.getEstimatedCost();
            }

            String type = incident.getIncidentType();
            incidentsByType.put(type, incidentsByType.getOrDefault(type, 0) + 1);
        }

        stats.put("totalIncidents", totalIncidents);
        stats.put("totalCasualties", totalCasualties);
        stats.put("totalInjuries", totalInjuries);
        stats.put("totalFamiliesAffected", totalFamiliesAffected);
        stats.put("totalEstimatedCost", totalEstimatedCost);
        stats.put("incidentsByType", incidentsByType);

        return stats;
    }
}