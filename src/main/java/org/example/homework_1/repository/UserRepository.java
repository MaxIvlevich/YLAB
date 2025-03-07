package org.example.homework_1.repository;

import org.example.homework_1.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserRepository {
    private final Map<UUID,User> users = new HashMap<>();

    /**
     * adds a user to the repository
     * @param user  model Instance User
     */
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }
    public User getUserById(UUID userId) {
        return users.get(userId);
    }

    /**
     * getting a user by email
     * @param email user email String value
     * @return User
     */
    public User getUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * deleting a user from the repository
     * @param userId unique user ID
     */
    public void deleteUser(UUID userId) {
         users.remove(userId);
    }


    public boolean updateUser(User updateUser) {
        User user = users.get(updateUser.getUserId());
        if (user != null) {
            user.setName(updateUser.getName());
            user.setEmail(updateUser.getEmail());
            user.setPassword(user.getPassword());
            return true;
        }
        return false;


    }
}
