package org.example.homework_1.services;

import org.example.homework_1.aop.Audit;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A service for working with the User entity
 */
public class UserServiceImpl implements UserServiceInterface {
    public UserServiceImpl(UserRepositoryInterface userRepositoryInterface) {
        this.userRepositoryInterface=userRepositoryInterface;
    }
    private final UserRepositoryInterface userRepositoryInterface ;

    /**
     * a method for registering a user based on incoming data
     *
     * @param name     The name chosen by the user
     * @param email    email selected by the user
     * @param password user's password
     */
    @Override
    public void register(String name, String email, String password) {

        if (userRepositoryInterface.getUserByEmail(email) != null) {
            System.out.println("Пользователь с таким email уже существует!");
        }else {
            User newUser = new User(name, email, password, Roles.USER, Status.ACTIVE);
            userRepositoryInterface.addUser(newUser);

            System.out.println("Пользователь успешно  зарегистрирован! ");
        }
    }

    /**
     * The method for the user's login
     * @param email    User's email address String value
     * @param password user's password String value
     * @return Optional<User> a container that can contain a User object or be empty. Used to avoid null and NullPointerException
     */
    @Override
    @Audit
    public Optional<User> login(String email, String password) {
        User user = userRepositoryInterface.getUserByEmail(email);
        if (user == null) {
            System.out.println("Пользователь с таким email не найден");
            return Optional.empty();
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("Неверный пароль.");
            return Optional.empty();
        }
        if (user.getStatus() == Status.BANNED) {
            System.out.println("Вы забанены !!!");
            return Optional.empty();
        }
        System.out.println("Вход выполнен успешно! Ваш уникальный ID: " + user.getUserId());
        return Optional.of(user);
    }

    /**
     * Updates the user of the submitted
     *
     * @param updateUser An instance of the user class that contains the updated parameters
     */
    @Override
    @Audit
    public void updateUser(User updateUser) {
        if (userRepositoryInterface.updateUser(updateUser)) {
            System.out.println("Пользователь обновлен");
        } else System.out.println("Пользователь не найден");

    }
    /**
     * Deletes a user by the specified user ID
     *
     * @param userId unique user ID ,UUID value
     * @return True if the user is deleted
     */
    @Override
    @Audit
    public boolean deleteUser(Long userId) {
        if (userRepositoryInterface.getUserById(userId) != null) {
            userRepositoryInterface.deleteUser(userId);
            System.out.println("Пользователь удален");
            return true;

        } else System.out.println("Пользователь не найден");
        return false;
    }
    /**
     * Retrieves the email address of a user based on their user ID.
     *
     * @param userId UUID of the user whose email is being retrieved.
     * @return The email address of the user.
     */
    @Override
    @Audit
    public String getUserEmail(Long userId) {
        return userRepositoryInterface.getUserById(userId).getEmail();
    }
    /**
     * Displays all users and their names, and returns a list of all users.
     *
     * @return A list of all users.
     */
    @Override
    @Audit
    public List<User> showAllUsers() {
        AtomicInteger i = new AtomicInteger(1);
        List<User> users = userRepositoryInterface.getAllUsers();
        users.forEach(user -> System.out.println(i.getAndIncrement() + ". " + user.getName()));
        return users;
    }
    public User getUserByEmail(String userEmail){
       return userRepositoryInterface.getUserByEmail(userEmail);
    }

    @Override
    public boolean isUserPresent(String email) {
        return userRepositoryInterface.isUserPresent(email);
    }

    @Override
    public boolean isUserPresent(Long id) {
        return userRepositoryInterface.isUserPresent(id);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepositoryInterface.getUserById(userId);
    }


}
