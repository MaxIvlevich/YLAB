package org.example.homework_1.services.Interfaces;

import org.example.homework_1.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserServiceInterface {
    void register(String name, String email, String password);

    Optional<User> login(String email, String password);

    void updateUser(User updateUser);

    boolean deleteUser(Long userId);

    String getUserEmail(Long userId);

    List<User> showAllUsers();
}
