package com.company.sigess.services;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.repositories.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDAO);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<UserDTO> mockUsers = Arrays.asList(
                new UserDTO(1, "analista", "ANALISTA"),
                new UserDTO(2, "supervisor", "SUPERVISOR")
        );
        when(userDAO.findAll()).thenReturn(mockUsers);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("analista", result.get(0).name());
        assertEquals("SUPERVISOR", result.get(1).role());
    }
}
