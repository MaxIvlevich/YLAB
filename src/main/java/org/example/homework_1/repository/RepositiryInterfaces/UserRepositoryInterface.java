package org.example.homework_1.repository.RepositiryInterfaces;

import org.example.homework_1.models.User;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryInterface {
    void addUser(User user);

    User getUserById(UUID userId);

    User getUserByEmail(String email);

    void deleteUser(UUID userId);

    boolean updateUser(User updateUser);

    List<User> getAllUsers();
}
