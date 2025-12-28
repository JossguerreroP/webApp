package com.company.sigess.services;
import com.company.sigess.models.DTO.IncidentCriteria;
import com.company.sigess.models.DTO.IncidentDTO;
import com.company.sigess.repositories.IncidentDAO;
import com.company.sigess.repositories.UserDAO;


import java.util.List;

public class IncidentService implements IncidentInt {
    private final IncidentDAO repository;

    public IncidentService( ) {
        this.repository = new IncidentDAO();
    }

    @Override
    public List<IncidentDTO> getAllIncidents(IncidentCriteria criteria) {
        return this.repository.findAll(criteria);
    }

    @Override
    public IncidentDTO getIncidentById(int id) {
        return null;
    }

    @Override
    public IncidentDTO createIncident(IncidentDTO incident) {
        // Por defecto, un nuevo incidente empieza como 'abierto'
        if (incident.getStatus() == null) {
            incident.setStatus("abierto");
        }
        return this.repository.create(incident);
    }
}
