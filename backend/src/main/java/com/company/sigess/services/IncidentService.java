package com.company.sigess.services;
import com.company.sigess.models.DTO.HistoryDTO;
import com.company.sigess.models.DTO.IncidentCriteria;
import com.company.sigess.models.DTO.IncidentDTO;
import com.company.sigess.models.DTO.IncidentReportDTO;
import com.company.sigess.models.DTO.AttachmentDTO;
import com.company.sigess.repositories.HistoryDAO;
import com.company.sigess.repositories.IncidentDAO;
import com.company.sigess.repositories.UserDAO;
import com.company.sigess.repositories.AttachmentDAO;
import com.company.sigess.security.SecurityContext;
import com.company.sigess.security.UserPrincipal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class IncidentService implements IncidentInt {
    private final IncidentDAO repository;
    private final HistoryDAO historyRepository;
    private final UserDAO userRepository;
    private final AttachmentDAO attachmentRepository;

    private static final String ATTACHMENT_DIR = "sigess_uploads";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "application/pdf", 
        "text/plain", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    public IncidentService() {
        this.repository = new IncidentDAO();
        this.historyRepository = new HistoryDAO();
        this.userRepository = new UserDAO();
        this.attachmentRepository = new AttachmentDAO();
    }

    @Override
    public List<IncidentDTO> getAllIncidents(IncidentCriteria criteria) {
        return this.repository.findAll(criteria);
    }

    @Override
    public IncidentDTO getIncidentById(int id) {
        return this.repository.findById(id);
    }

    @Override
    public IncidentDTO updateIncident(IncidentDTO incident, int userId) {
        // Si userId es 0 o -1, intentamos obtenerlo del SecurityContext
        if (userId <= 0) {
            UserPrincipal principal = SecurityContext.getUser();
            if (principal != null) {
                userId = principal.getUserId().intValue();
            }
        }
        
        IncidentDTO existing = this.repository.findById(incident.getId());
        if (existing == null) throw new RuntimeException("Incidente no encontrado");

        // Regla de Negocio: Roles
        com.company.sigess.models.DTO.UserDTO user = userRepository.findById(userId);
        if (user == null) throw new RuntimeException("Usuario no válido");

        boolean isSupervisor = "SUPERVISOR".equalsIgnoreCase(user.role());

        // Validar cambio de estado (Solo Supervisor puede abrir o cerrar)
        if (!existing.getStatus().equalsIgnoreCase(incident.getStatus())) {
            boolean isMovingToClosed = "cerrado".equalsIgnoreCase(incident.getStatus());
            boolean isMovingToOpen = "abierto".equalsIgnoreCase(incident.getStatus());

            if ((isMovingToClosed || isMovingToOpen) && !isSupervisor) {
                throw new RuntimeException("Solo un Supervisor puede abrir o cerrar incidentes");
            }
        }

        // Auditoría: Comparar campos y registrar cambios
        checkAndLogChange(incident.getId(), userId, "title", existing.getTitle(), incident.getTitle());
        checkAndLogChange(incident.getId(), userId, "description", existing.getDescription(), incident.getDescription());
        checkAndLogChange(incident.getId(), userId, "status", existing.getStatus(), incident.getStatus());
        checkAndLogChange(incident.getId(), userId, "level", existing.getLevel(), incident.getLevel());
        checkAndLogChange(incident.getId(), userId, "type", existing.getType(), incident.getType());

        return this.repository.update(incident);
    }

    @Override
    public boolean deleteIncident(int id, int userId) {
        IncidentDTO existing = this.repository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Incidente no encontrado");
        }

        // Si userId es <= 0, intentamos obtenerlo del SecurityContext
        if (userId <= 0) {
            UserPrincipal principal = SecurityContext.getUser();
            if (principal != null) {
                userId = principal.getUserId().intValue();
            }
        }

        // Regla de Negocio: Roles (Solo Supervisor puede cerrar/eliminar)
        com.company.sigess.models.DTO.UserDTO user = userRepository.findById(userId);
        if (user == null || !"SUPERVISOR".equalsIgnoreCase(user.role())) {
            throw new RuntimeException("Solo un Supervisor puede cerrar incidentes");
        }

        String oldStatus = existing.getStatus();
        existing.setStatus("cerrado");

        // Registrar el cambio de estado en el historial
        checkAndLogChange(id, userId, "status", oldStatus, "cerrado");

        return this.repository.update(existing) != null;
    }

    private void checkAndLogChange(int incidentId, int userId, String field, String oldVal, String newVal) {
        if (newVal != null && !newVal.equals(oldVal)) {
            historyRepository.logChange(incidentId, userId, field, oldVal, newVal);
        }
    }

    @Override
    public IncidentDTO createIncident(IncidentDTO incident) {
        // Si createdBy no está establecido, intentamos obtenerlo del SecurityContext
        if (incident.getCreatedBy() <= 0) {
            UserPrincipal principal = SecurityContext.getUser();
            if (principal != null) {
                incident.setCreatedBy(principal.getUserId().intValue());
            }
        }
        
        if (incident.getStatus() == null) {
            incident.setStatus("abierto");
        }
        
        IncidentDTO created = this.repository.create(incident);
        
        // Registrar en auditoría la creación
        historyRepository.logChange(created.getId(), created.getCreatedBy(), "status", null, created.getStatus());
        
        return created;
    }

    @Override
    public List<HistoryDTO> getIncidentHistory(int incidentId) {
        return historyRepository.getHistoryByIncidentId(incidentId);
    }

    @Override
    public IncidentReportDTO getIncidentReport() {
        return new IncidentReportDTO(
            repository.getTopAreasWithMostIncidents(),
            repository.getCriticalIncidentsByWeek()
        );
    }

    @Override
    public AttachmentDTO addAttachment(int incidentId, String originalFilename, String contentType, long size, InputStream inputStream, int userId) {
        // Validation
        if (size > MAX_FILE_SIZE) {
            throw new RuntimeException("File too large (max 10MB)");
        }
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            System.err.println("[DEBUG_LOG] Rejected file type: " + contentType + " for file: " + originalFilename);
            throw new RuntimeException("Invalid file type: " + contentType + ". Allowed: JPEG, PNG, GIF, PDF, TXT, DOC, DOCX, XLS, XLSX");
        }

        // Ensure directory exists
        Path uploadPath = Paths.get(ATTACHMENT_DIR).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }

        // Generate safe name
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedFilename = UUID.randomUUID().toString() + extension;
        Path targetLocation = uploadPath.resolve(storedFilename);

        // Security check: ensure the file is still within the upload directory
        if (!targetLocation.startsWith(uploadPath)) {
            throw new RuntimeException("Invalid file path");
        }

        // Save file
        try {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error saving file to filesystem", e);
        }

        // Save metadata
        AttachmentDTO attachment = new AttachmentDTO();
        attachment.setIncidentId(incidentId);
        attachment.setOriginalFilename(originalFilename);
        attachment.setStoredFilename(storedFilename);
        attachment.setFileSize(size);
        attachment.setMimeType(contentType);
        attachment.setUploadedBy(userId);

        return attachmentRepository.create(attachment);
    }

    @Override
    public List<AttachmentDTO> getAttachmentsByIncidentId(int incidentId) {
        return attachmentRepository.findByIncidentId(incidentId);
    }
}
