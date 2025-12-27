package com.company.sigess.models.DTO;

public record UserDTO(
        int id,
        String name,// Solo para selects
        String role
) {}