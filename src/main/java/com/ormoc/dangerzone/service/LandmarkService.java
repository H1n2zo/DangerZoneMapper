package com.ormoc.dangerzone.service;

import com.ormoc.dangerzone.dao.LandmarkDAO;
import com.ormoc.dangerzone.model.Landmark;

import java.util.List;

public class LandmarkService {

    private final LandmarkDAO landmarkDAO;

    public LandmarkService() {
        this.landmarkDAO = new LandmarkDAO();
    }

    public List<Landmark> getAll() {
        return landmarkDAO.findAll();
    }

    public Landmark getById(int landmarkId) {
        return landmarkDAO.findById(landmarkId);
    }

    public List<Landmark> getEvacuationSites() {
        return landmarkDAO.findEvacuationSites();
    }

    public boolean create(Landmark landmark) {
        // Validation
        if (landmark.getLandmarkName() == null || 
            landmark.getLandmarkName().trim().isEmpty()) {
            System.err.println("Landmark name is required");
            return false;
        }

        return landmarkDAO.insert(landmark);
    }

    public boolean update(Landmark landmark) {
        if (landmark.getLandmarkId() <= 0) {
            System.err.println("Invalid landmark ID");
            return false;
        }
        if (landmark.getLandmarkName() == null || 
            landmark.getLandmarkName().trim().isEmpty()) {
            System.err.println("Landmark name is required");
            return false;
        }

        return landmarkDAO.update(landmark);
    }

    public boolean delete(int landmarkId) {
        if (landmarkId <= 0) {
            System.err.println("Invalid landmark ID");
            return false;
        }
        return landmarkDAO.delete(landmarkId);
    }
}