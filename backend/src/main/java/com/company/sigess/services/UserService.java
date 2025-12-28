package com.company.sigess.services;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.repositories.UserDAO;

import java.util.List;

public class UserService implements UserInt {
    private final UserDAO repository;

    public UserService( ) {
        this.repository = new UserDAO();
    }
    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll();
    }
}
