package org.example.homework_1.services;

import org.example.homework_1.models.User;
import org.example.homework_1.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository = new UserRepository();
    public void register(String name, String email, String password) {

        if (userRepository.getUserByEmail(email) != null) {
            System.out.println("Пользователь с таким email уже существует!");
        }
        User newUser = new User(name, email, password);
        userRepository.addUser(newUser);
        System.out.println("Пользователь зарегистрирован! Ваш уникальный ID: " + newUser.getUserId());
    }

    public Optional<User> login(String email, String password) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            System.out.println("Пользователь с таким email не найден");
            return Optional.empty();
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("Неверный пароль.");
            return Optional.empty();
        }
        System.out.println("Вход выполнен успешно! Ваш уникальный ID: " + user.getUserId());
        return Optional.of(user);
    }


    public void updateUser(User updateUser) {
        if(userRepository.updateUser(updateUser)){
            System.out.println("Пользователь обновлен");
        }else System.out.println("Пользователь не найден");

    }

    public boolean deleteUser(UUID userId) {
        if(userRepository.getUserById(userId)!= null) {
            userRepository.deleteUser(userId);
            System.out.println("Пользователь удален");
            return true;

        } else System.out.println("Пользователь не найден");
        return false;

    }
}
