package com.company.sigess.services;

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
