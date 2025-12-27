package com.company.sigess.services;

import com.company.sigess.models.User;
import com.company.sigess.repositories.UserRepository;
import java.util.List;

public class UserService {
    private UserRepository repository = new UserRepository();

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getUserById(int id) {
        return repository.findById(id);
    }

    public User createUser(String name, String email) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Name and email cannot be empty");
        }
        User user = new User(0, name, email);
        return repository.save(user);
    }

    public boolean deleteUser(int id) {
        return repository.delete(id);
    }
}
