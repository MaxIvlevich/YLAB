package HW_1_Tests;

import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.RepositoryInMap.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
    @Spy
    private Map<Long, User> users = new HashMap<>();
    private UserRepository userRepository;
    private Long userId;
    private User user;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        userRepository = new UserRepository();

        Field field = UserRepository.class.getDeclaredField("users");
        field.setAccessible(true);
        field.set(userRepository, users);

         userId = 1L;
        user = new User(userId, "Alice", "alice@example.com", "password", Roles.ROLE_USER, Status.STATUS_ACTIVE);
    }

    @Test
    void testAddUser() {
        userRepository.addUser(user);
        assertTrue(users.containsKey(userId));
        assertEquals(user, users.get(userId));
    }

    @Test
    void testAddUser_AlreadyExists() {
        userRepository.addUser(user);
        User newUser = new User(userId, "Alice", "alice@example.com", "password", Roles.ROLE_USER, Status.STATUS_ACTIVE);
        userRepository.addUser(newUser);
        assertEquals(newUser, users.get(userId));
    }

    @Test
    void testGetUserById_UserExists() {
        users.put(userId, user);
        User result = userRepository.getUserById(userId);
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testGetUserById_UserNotFound() {
        users.put(userId, user);
        Long nonExistentId = 3L;
        User result = userRepository.getUserById(nonExistentId);
        assertNull(result);
    }

    @Test
    void testGetUserByEmail_UserExists() {
        users.put(userId, user);
        User result = userRepository.getUserByEmail("alice@example.com");
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        User result = userRepository.getUserByEmail("alice@example.com");
        assertNull(result);
    }

    @Test
    void testDeleteUser_UserExists() {
        users.put(userId, user);
        assertTrue(users.containsKey(userId));
        userRepository.deleteUser(userId);
        assertFalse(users.containsKey(userId));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        Long nonExistentUserId = 1L;
        assertFalse(users.containsKey(nonExistentUserId));
        userRepository.deleteUser(nonExistentUserId);
        assertFalse(users.containsKey(nonExistentUserId));
    }

    @Test
    void testUpdateUser_UserExists() {
        users.put(userId, user);
        User updatedUser = new User(userId, "Max", "Max@example.com", "password112233", Roles.ROLE_USER, Status.STATUS_ACTIVE);
        boolean result = userRepository.updateUser(updatedUser);
        assertTrue(result);
        User updatedUserFromRepo = users.get(userId);
        assertEquals("Max", updatedUserFromRepo.getName());
        assertEquals("Max@example.com", updatedUserFromRepo.getEmail());
        assertEquals("password112233", updatedUserFromRepo.getPassword());
    }

    @Test
    void testGetAllUsers_WhenUsersExist() {
        users.put(userId, user);
        List<User> users = userRepository.getAllUsers();
        assertFalse(users.isEmpty(), "Список пользователей не должен быть пустым.");
        assertEquals(1, users.size(), "В списке должен быть только один пользователь.");
        assertEquals(user, users.get(0), "Пользователь в списке должен быть тот же, что мы добавили.");
    }

    @Test
    void testGetAllUsers_WhenNoUsersExist() {
        userRepository.getAllUsers();
        assertTrue(users.isEmpty(), "Список пользователей должен быть пустым.");
    }


}
