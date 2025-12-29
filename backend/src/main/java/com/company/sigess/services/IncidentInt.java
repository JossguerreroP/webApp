package com.company.sigess.services;

import com.company.sigess.models.DTO.HistoryDTO;
import com.company.sigess.models.DTO.IncidentCriteria;
import com.company.sigess.models.DTO.IncidentDTO;
import com.company.sigess.models.DTO.IncidentReportDTO;
import com.company.sigess.models.DTO.AttachmentDTO;

import java.io.InputStream;
import java.util.List;

public interface IncidentInt {
     List<IncidentDTO> getAllIncidents(IncidentCriteria criteria);
     IncidentDTO getIncidentById(int id);
     IncidentDTO createIncident(IncidentDTO incident);
     IncidentDTO updateIncident(IncidentDTO incident, int userId);
     boolean deleteIncident(int id, int userId);
     List<HistoryDTO> getIncidentHistory(int incidentId);
     IncidentReportDTO getIncidentReport();
     AttachmentDTO addAttachment(int incidentId, String originalFilename, String contentType, long size, InputStream inputStream, int userId);
     List<AttachmentDTO> getAttachmentsByIncidentId(int incidentId);
}
