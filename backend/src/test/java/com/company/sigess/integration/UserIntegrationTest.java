package com.company.sigess.integration;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.repositories.UserDAO;
import com.company.sigess.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Este es un test de integración "light" que verifica la colaboración entre el Servicio y el Repositorio.
 * En un entorno real de integración, se usaría una base de datos real o en memoria (H2),
 * pero para demostrar la estructura se simula la interacción.
 */
public class UserIntegrationTest {

    @Test
    void testServiceAndRepoCollaboration() {
        // Arrange
        UserDAO mockRepo = Mockito.mock(UserDAO.class);
        when(mockRepo.findAll()).thenReturn(Collections.singletonList(
                new UserDTO(1, "testuser", "ANALISTA")
        ));

        UserService userService = new UserService(mockRepo);

        // Act
        List<UserDTO> users = userService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertTrue(users.size() > 0);
        assertTrue(users.stream().anyMatch(u -> u.name().equals("testuser")));
    }
}
