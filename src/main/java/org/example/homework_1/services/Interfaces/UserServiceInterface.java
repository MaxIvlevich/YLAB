package org.example.homework_1.services.Interfaces;

import org.example.homework_1.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserServiceInterface {
    void register(String name, String email, String password);

    Optional<User> login(String email, String password);

    void updateUser(User updateUser);

    boolean deleteUser(UUID userId);

    String getUserEmail(UUID userId);

    List<User> showAllUsers();
}
