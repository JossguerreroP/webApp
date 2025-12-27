package com.company.sigess.services;

import com.company.sigess.models.User;
import com.company.sigess.repositories.UserRepository;

import java.util.List;

public class UserImp implements userInt{
    private final UserRepository repository = new UserRepository();

    @Override
    public List<User> getAllUsers() {
        return this.repository.findAll();
    }
}
