package com.company.sigess.services;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.repositories.UserDAO;

import java.util.List;

public class UserService implements UserInt {
    private final UserDAO repository;

    public UserService() {
        this(new UserDAO());
    }

    public UserService(UserDAO repository) {
        this.repository = repository;
    }
    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll();
    }
}
