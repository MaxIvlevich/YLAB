package org.example.homework_1.repository.RepositoryInMap;

import org.example.homework_1.models.User;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;

import java.util.*;


/**
 * UserRepository is responsible for managing user data in the system.
 * It provides functionality to add, update, retrieve, and delete users.
 * This implementation stores users in an in-memory map, but can be adapted to use a database.
 */
public class UserRepository implements UserRepositoryInterface {
    private final Map<Long, User> users = new HashMap<>();



    /**
     * adds a user to the repository
     *
     * @param user model Instance User
     */
    @Override
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    /**
     * getting a user by email
     *
     * @param email user email String value
     * @return User
     */
    @Override
    public User getUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * deleting a user from the repository
     *
     * @param userId unique user ID
     */
    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    /**
     * Updates the details of an existing user in the system.
     *
     * @param updateUser The user object containing the updated information.
     * @return true if the user was found and updated successfully,
     *         false if the user could not be found and thus was not updated.
     */
    @Override
    public boolean updateUser(User updateUser) {
        User user = users.get(updateUser.getUserId());
        if (user != null) {
            user.setName(updateUser.getName());
            user.setEmail(updateUser.getEmail());
            user.setPassword(updateUser.getPassword());
            user.setRoles(updateUser.getRoles());
            return true;
        }
        return false;
    }
    /**
     * Retrieves a list of all users in the system.
     *
     * @return A list containing all the users. If no users are found, an empty list is returned.
     */
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());


    }

    @Override
    public boolean isUserPresent(String email) {
        return false;
    }

    @Override
    public boolean isUserPresent(Long id) {
        return false;
    }
}
