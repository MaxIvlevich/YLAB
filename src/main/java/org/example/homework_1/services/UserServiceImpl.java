package org.example.homework_1.services;

import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.UserRepository;
import org.example.homework_1.services.Interfaces.UserServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A service for working with the User entity
 */
public class UserServiceImpl implements UserServiceInterface {
    private final UserRepository userRepository = new UserRepository();

    /**
     * a method for registering a user based on incoming data
     * @param name The name chosen by the user
     * @param email email selected by the user
     * @param password user's password
     */
    @Override
    public void register(String name, String email, String password) {

        if (userRepository.getUserByEmail(email) != null) {
            System.out.println("Пользователь с таким email уже существует!");
        }
        User newUser = new User(name, email, password, Roles.ROLE_USER, Status.STATUS_AKTiVE);
        userRepository.addUser(newUser);
        System.out.println("Пользователь зарегистрирован! Ваш уникальный ID: " + newUser.getUserId());
    }

    /**
     * The method for the user's login
     * @param email User's email address String value
     * @param password user's password String value
     * @return Optional<User> a container that can contain a User object or be empty. Used to avoid null and NullPointerException
     */
    @Override
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

    /**
     * Updates the user of the submitted
     * @param updateUser  An instance of the user class that contains the updated parameters
     */
    @Override
    public void updateUser(User updateUser) {
        if(userRepository.updateUser(updateUser)){
            System.out.println("Пользователь обновлен");
        }else System.out.println("Пользователь не найден");

    }

    /**
     * Deletes a user by the specified user ID
     * @param userId unique user ID ,UUID value
     * @return True if the user is deleted
     */
    @Override
    public boolean deleteUser(UUID userId) {
        if(userRepository.getUserById(userId)!= null) {
            userRepository.deleteUser(userId);
            System.out.println("Пользователь удален");
            return true;

        } else System.out.println("Пользователь не найден");
        return false;

    }
    @Override
    public String getUserEmail(UUID userId){
       return userRepository.getUserById(userId).getEmail();

    }

    @Override
    public List<User> showAllUsers() {
        AtomicInteger i = new AtomicInteger(1);
       List<User> users =  userRepository.getAllUsers();
        users.forEach(user -> System.out.println(i.getAndIncrement() + ". " + user.getName()));
        return users;
    }


}
