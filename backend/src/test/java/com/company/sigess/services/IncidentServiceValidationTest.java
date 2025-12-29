package com.company.sigess.services;

import com.company.sigess.models.DTO.IncidentDTO;
import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.repositories.AttachmentDAO;
import com.company.sigess.repositories.HistoryDAO;
import com.company.sigess.repositories.IncidentDAO;
import com.company.sigess.repositories.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IncidentServiceValidationTest {

    @Mock
    private IncidentDAO repository;
    @Mock
    private HistoryDAO historyRepository;
    @Mock
    private UserDAO userRepository;
    @Mock
    private AttachmentDAO attachmentRepository;

    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        incidentService = new IncidentService(repository, historyRepository, userRepository, attachmentRepository);
    }

    @Test
    void testUpdateIncidentStatusToClosed_ByAnalyst_ShouldThrowException() {
        // Arrange
        int userId = 1;
        int incidentId = 100;
        UserDTO analystUser = new UserDTO(userId, "analista", "ANALISTA");
        
        IncidentDTO existingIncident = new IncidentDTO(incidentId, "Title", "Desc", "SST", "bajo", "abierto", null, null, 1, 1, 1, 1);
        IncidentDTO updatedIncident = new IncidentDTO(incidentId, "Title", "Desc", "SST", "bajo", "cerrado", null, null, 1, 1, 1, 1);

        when(repository.findById(incidentId)).thenReturn(existingIncident);
        when(userRepository.findById(userId)).thenReturn(analystUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            incidentService.updateIncident(updatedIncident, userId);
        }, "Solo un Supervisor puede abrir o cerrar incidentes");
    }
}
