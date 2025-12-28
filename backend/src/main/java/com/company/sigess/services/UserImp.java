package com.company.sigess.services;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.repositories.UserDAO;

import java.util.List;

public class UserImp implements userInt{
    private final UserDAO repository;

    public UserImp( ) {
        this.repository = new UserDAO();
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll();
    }
}
