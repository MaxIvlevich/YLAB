package HW_2_teest;

import org.example.homework_1.database.LiquibaseHelper;
import org.example.homework_1.models.User;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.example.homework_1.models.enums.Roles.ADMIN;
import static org.example.homework_1.models.enums.Roles.USER;
import static org.example.homework_1.models.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryJDBCTests {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;
    private UserRepositoryJDBC userRepository;


    @BeforeAll
    void setUp() throws SQLException {

        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        LiquibaseHelper.runLiquibase(connection);

        userRepository = new UserRepositoryJDBC(connection);

    }

    @BeforeEach
    void cleanUp() throws SQLException {
        String sql = "DELETE FROM app.users";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    @AfterAll
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testAddUser() {
        User user = new User(null, "ylab app", "ylab.app@example.com", "hashed_password", USER, ACTIVE);
        userRepository.addUser(user);

        User fetchedUser = userRepository.getUserByEmail("ylab.app@example.com");
        assertNotNull(fetchedUser);
        assertEquals("ylab app", fetchedUser.getName());
    }
    @Test
    void testGetUserById() {
        User user = new User( "ylab app", "ylab@example.com", "hashed_password",ADMIN, ACTIVE);
        userRepository.addUser(user);

        User fetchedUser = userRepository.getUserByEmail("ylab@example.com");
        assertNotNull(fetchedUser);
        assertEquals("ylab app", fetchedUser.getName());

        User fetchedById = userRepository.getUserById(fetchedUser.getUserId());
        assertNotNull(fetchedById);
        assertEquals("ylab app", fetchedById.getName());
    }
    @Test
    public void testUpdateUser() throws SQLException {
        String name = "Max Iv";
        String email = "MaxIv@example.com";
        User user = new User(name, email, "password", USER, ACTIVE);
        userRepository.addUser(user);
        String newName = "IvMax";

        User updatedUser = new User(newName , email, "newpassword",ADMIN, ACTIVE);
        updatedUser.setUserId(userRepository.getUserByEmail(email).getUserId());
        boolean isUpdated = userRepository.updateUser(updatedUser);

        assertTrue(isUpdated, "User should be updated.");
        User retrievedUser = userRepository.getUserByEmail(email);
        assertEquals(newName, retrievedUser.getName(), "User name should be updated.");
    }
    @Test
    public void testDeleteUser() throws SQLException {
        String name = "Max Iv";
        String email = "MaxIv@example.com";
        User user = new User(name, email, "password", USER, ACTIVE);
        userRepository.addUser(user);
        userRepository.deleteUser(userRepository.getUserByEmail(email).getUserId());
        assertFalse(userRepository.isUserPresent(email), "User should be deleted.");
    }
    @Test
    public void testGetAllUsers() throws SQLException {
        String name = "Max Iv";
        String email = "MaxIv@example.com";
        String name2 ="Iv Max";
        String email2="IvMax@example.com";
        User user = new User(name, email, "password", USER, ACTIVE);
        User user2 = new User(name2, email2, "password2", ADMIN, ACTIVE);
        userRepository.addUser(user);
        userRepository.addUser(user2);

        List<User> users = userRepository.getAllUsers();
        System.out.println(users.toString());
        assertEquals(2, users.size(), "There should be two users.");
    }
}


