package com.company.sigess.services;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.repositories.UserRepository;
import java.sql.Connection;
import java.util.List;

public class UserImp implements userInt{
    private final UserRepository repository;

    public UserImp( ) {
        this.repository = new UserRepository();
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll();
    }
}
