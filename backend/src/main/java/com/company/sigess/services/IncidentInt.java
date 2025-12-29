package com.company.sigess.services;

import com.company.sigess.models.DTO.IncidentCriteria;
import com.company.sigess.models.DTO.IncidentDTO;

import java.util.List;

public interface IncidentInt {
     List<IncidentDTO> getAllIncidents(IncidentCriteria criteria);
     IncidentDTO getIncidentById(int id);
     IncidentDTO createIncident(IncidentDTO incident);
     IncidentDTO updateIncident(IncidentDTO incident, int userId);
     boolean deleteIncident(int id, int userId);
}
