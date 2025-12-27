package com.company.sigess.models.DTO;

public record UserDTO(
        int id,
        String name,
        String email,     // Solo para selects
        String role
) {}