package com.company.sigess.services;

import com.company.sigess.models.DTO.IncidentDTO;

import java.util.ArrayList;
import java.util.List;

public interface IncidentInt {
    public List<IncidentDTO> getAllIncidents() {
        return new ArrayList<>();
    }

    public IncidentDTO getIncidentById(int id) {
        return null;
    }

    public IncidentDTO createIncident(IncidentDTO incident) {
        return incident;
    }
}
