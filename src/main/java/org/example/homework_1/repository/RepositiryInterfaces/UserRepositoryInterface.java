package org.example.homework_1.repository.RepositiryInterfaces;

import org.example.homework_1.models.User;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryInterface {
    void addUser(User user);

    User getUserById(Long userId);

    User getUserByEmail(String email);

    void deleteUser(Long userId);

    boolean updateUser(User updateUser);

    List<User> getAllUsers();
}
