package com.company.sigess.services;
import com.company.sigess.models.DTO.IncidentCriteria;
import com.company.sigess.models.DTO.IncidentDTO;
import com.company.sigess.repositories.HistoryDAO;
import com.company.sigess.repositories.IncidentDAO;
import com.company.sigess.repositories.UserDAO;
import com.company.sigess.security.SecurityContext;
import com.company.sigess.security.UserPrincipal;


import java.util.List;

public class IncidentService implements IncidentInt {
    private final IncidentDAO repository;
    private final HistoryDAO historyRepository;
    private final UserDAO userRepository;

    public IncidentService() {
        this.repository = new IncidentDAO();
        this.historyRepository = new HistoryDAO();
        this.userRepository = new UserDAO();
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

        // Validar cambio de estado (Solo Supervisor puede cerrar/reabrir)
        if (!existing.getStatus().equals(incident.getStatus())) {
            boolean switchingToClosed = "cerrado".equalsIgnoreCase(incident.getStatus());
            boolean switchingFromClosed = "cerrado".equalsIgnoreCase(existing.getStatus());

            if ((switchingToClosed || switchingFromClosed) && !isSupervisor) {
                throw new RuntimeException("Solo un Supervisor puede cerrar o reabrir incidentes");
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
        if (incident.getStatus() == null) {
            incident.setStatus("abierto");
        }
        
        IncidentDTO created = this.repository.create(incident);
        
        // Registrar en auditoría la creación
        historyRepository.logChange(created.getId(), created.getCreatedBy(), "status", null, created.getStatus());
        
        return created;
    }
}
