package com.ormoc.dangerzone.service;

import com.ormoc.dangerzone.dao.HazardZoneDAO;
import com.ormoc.dangerzone.model.HazardZone;

import java.util.List;

/**
 * Service layer for HazardZone business logic
 */
public class HazardZoneService {

    private final HazardZoneDAO hazardZoneDAO;

    public HazardZoneService() {
        this.hazardZoneDAO = new HazardZoneDAO();
    }

    public List<HazardZone> getAll() {
        return hazardZoneDAO.findAll();
    }

    public HazardZone getById(int zoneId) {
        return hazardZoneDAO.findById(zoneId);
    }

    public List<HazardZone> getByBarangay(String barangay) {
        return hazardZoneDAO.findByBarangay(barangay);
    }

    public List<HazardZone> getByHazardType(String hazardType) {
        return hazardZoneDAO.findByHazardType(hazardType);
    }

    public boolean create(HazardZone zone) {
        // Validation
        if (zone.getZoneName() == null || zone.getZoneName().trim().isEmpty()) {
            System.err.println("Zone name is required");
            return false;
        }
        if (zone.getHazardType() == null || zone.getHazardType().trim().isEmpty()) {
            System.err.println("Hazard type is required");
            return false;
        }

        return hazardZoneDAO.insert(zone);
    }

    public boolean update(HazardZone zone) {
        // Validation
        if (zone.getZoneId() <= 0) {
            System.err.println("Invalid zone ID");
            return false;
        }
        if (zone.getZoneName() == null || zone.getZoneName().trim().isEmpty()) {
            System.err.println("Zone name is required");
            return false;
        }

        return hazardZoneDAO.update(zone);
    }

    public boolean delete(int zoneId) {
        if (zoneId <= 0) {
            System.err.println("Invalid zone ID");
            return false;
        }
        return hazardZoneDAO.delete(zoneId);
    }

    public List<String> getAvailableBarangays() {
        return hazardZoneDAO.getDistinctBarangays();
    }

    public List<String> getAvailableHazardTypes() {
        return hazardZoneDAO.getDistinctHazardTypes();
    }
}