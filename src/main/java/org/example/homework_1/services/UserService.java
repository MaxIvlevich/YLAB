package org.example.homework_1.services;

import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class UserService {
    private final UserRepository userRepository = new UserRepository();
    public void register(String name, String email, String password) {

        if (userRepository.getUserByEmail(email) != null) {
            System.out.println("Пользователь с таким email уже существует!");
        }
        User newUser = new User(name, email, password, Roles.ROLE_USER, Status.STATUS_AKTiVE);
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
        if(user.getStatus() == Status.STATUS_BUN){
            System.out.println("Вы забанены !!!");
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
    public String getUserEmail(UUID userId){
       return userRepository.getUserById(userId).getEmail();

    }

    public List<User> showAllUsers() {
        AtomicInteger i = new AtomicInteger(1);
       List<User> users =  userRepository.getAllUsers();
        users.forEach(user -> System.out.println(i.getAndIncrement() + ". " + user.getName()));
        return users;
    }


}
