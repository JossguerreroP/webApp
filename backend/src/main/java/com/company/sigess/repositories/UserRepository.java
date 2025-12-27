package com.company.sigess.repositories;

import com.company.sigess.models.User;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static List<User> users = new ArrayList<>();
    private static int nextId = 1;

    static {
        // Initialize with sample data
        users.add(new User(nextId++, "John Doe", "john@example.com"));
        users.add(new User(nextId++, "Jane Smith", "jane@example.com"));
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public User findById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public User save(User user) {
        if (user.getId() == 0) {
            user.setId(nextId++);
        }
        users.add(user);
        return user;
    }

    public boolean delete(int id) {
        return users.removeIf(user -> user.getId() == id);
    }
}
