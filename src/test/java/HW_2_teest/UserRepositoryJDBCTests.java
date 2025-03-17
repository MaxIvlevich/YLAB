package HW_2_teest;

import org.example.homework_1.database.LiquibaseHelper;
import org.example.homework_1.models.User;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.example.homework_1.models.enums.Roles.ADMIN;
import static org.example.homework_1.models.enums.Roles.USER;
import static org.example.homework_1.models.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        User user = new User(null, "ylab app", "ylab@example.com", "hashed_password",ADMIN, ACTIVE);
        userRepository.addUser(user);

        User fetchedUser = userRepository.getUserByEmail("ylab@example.com");
        assertNotNull(fetchedUser);
        assertEquals("ylab app", fetchedUser.getName());

        User fetchedById = userRepository.getUserById(fetchedUser.getUserId());
        assertNotNull(fetchedById);
        assertEquals("ylab app", fetchedById.getName());
    }



}


