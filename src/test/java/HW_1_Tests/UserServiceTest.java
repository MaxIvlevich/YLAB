package HW_1_Tests;

import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserServiceImpl userServiceImpl;
    private UserRepositoryInterface userRepository;

    private User user;
    private User userToUpdate;
    private User userBanned;
    private Long userId;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepositoryInterface.class);
        userServiceImpl = new UserServiceImpl(userRepository);

        userId = 1L;


        user = new User("name", "email", "pass", Roles.ROLE_USER, Status.STATUS_ACTIVE);
        userToUpdate = new User("name1", "email1", "pass1", Roles.ROLE_USER, Status.STATUS_ACTIVE);
        userBanned = new User("nameBanned", "email1Banned", "pass1Banned", Roles.ROLE_USER, Status.STATUS_BANNED);

    }

    @Test
    void testRegister_UserAlreadyExists() {
        when(userRepository.getUserByEmail("email")).thenReturn(
                user);
        userServiceImpl.register("name", "email", "pass");
        verify(userRepository, times(1)).getUserByEmail("email");
        verify(userRepository, times(0)).addUser(user);
    }

    @Test
    void testRegister_Success() {
        when(userRepository.getUserByEmail("new@example.com")).thenReturn(null);
        userServiceImpl.register("NewUser", "new@example.com", "password");
        verify(userRepository, times(1)).addUser(any(User.class));

    }

    @Test
    void testLogin_UserNotFound() {
        // Мокируем поведение, что пользователь не найден
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(null);

        // Вызываем метод login
        Optional<User> result = userServiceImpl.login("test@example.com", "password");

        // Проверяем, что результат пустой
        assertTrue(result.isEmpty(), "Expected empty Optional because user is not found");
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(user);
        Optional<User> result = userServiceImpl.login("test@example.com", "wrongPassword");
        assertTrue(result.isEmpty(), "Expected empty Optional because password is incorrect");
    }

    @Test
    void testLogin_UserBanned() {
        User bannedUser = new User("Banned User", "banned@example.com", "password", Roles.ROLE_USER, Status.STATUS_BANNED);
        when(userRepository.getUserByEmail("banned@example.com")).thenReturn(bannedUser);
        Optional<User> result = userServiceImpl.login("banned@example.com", "password");
        assertTrue(result.isEmpty(), "Expected empty Optional because user is banned");
    }

    @Test
    void testLogin_Success() {
        when(userRepository.getUserByEmail("email")).thenReturn(user);
        Optional<User> result = userServiceImpl.login("email", "pass");
        assertTrue(result.isPresent(), "Expected Optional with user");
        assertEquals(user, result.get(), "Expected returned user to be the same as the mocked user");
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.updateUser(userToUpdate)).thenReturn(true);
        userServiceImpl.updateUser(userToUpdate);
        verify(userRepository).updateUser(userToUpdate);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.updateUser(userToUpdate)).thenReturn(false);
        userServiceImpl.updateUser(userToUpdate);
        verify(userRepository).updateUser(userToUpdate);
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.getUserById(userId)).thenReturn(user);
        doNothing().when(userRepository).deleteUser(userId);
        boolean result = userServiceImpl.deleteUser(userId);
        verify(userRepository).deleteUser(userId);
        assertTrue(result);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(null);
        boolean result = userServiceImpl.deleteUser(userId);
        verify(userRepository, never()).deleteUser(userId);
        assertFalse(result);
    }
    @Test
    void testGetUserEmail_Success() {
        when(userRepository.getUserById(userId)).thenReturn(user);
        String email = userServiceImpl.getUserEmail(userId);
        assertEquals("email", email);
        verify(userRepository).getUserById(userId);
    }
    @Test
    void testShowAllUsers() {
        User user1 = new User("Alice", "alice@example.com", "password", Roles.ROLE_USER, Status.STATUS_ACTIVE);
        User user2 = new User("Bob", "bob@example.com", "password", Roles.ROLE_USER, Status.STATUS_ACTIVE);
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.getAllUsers()).thenReturn(users);
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        userServiceImpl.showAllUsers();
        verify(userRepository, times(1)).getAllUsers();
        String output = baos.toString();
        assertTrue(output.contains("1. Alice"));
        assertTrue(output.contains("2. Bob"));
        System.setOut(originalOut);
    }



}
