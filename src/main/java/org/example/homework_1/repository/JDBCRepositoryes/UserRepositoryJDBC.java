package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementation of the {@link UserRepositoryInterface} that interacts with the database using JDBC.
 *
 * <p>This class is responsible for performing CRUD operations related to users in the database.
 * It provides methods to add, update, delete, retrieve, and check the existence of users based on their ID or email.
 * The operations are performed using SQL statements via a {@link Connection} object.
 */
public class UserRepositoryJDBC implements UserRepositoryInterface {
    private final Connection connection;

    public UserRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new {@link User} to the database.
     *
     * <p>This method inserts a new record into the {@code app.users} table. The user's information is passed as a {@link User}
     * object, including the user's name, email, password, roles, and status. If the connection is not established, an error
     * message will be logged.
     *
     * @param user the {@link User} object containing the user's details to be added to the database
     * @throws RuntimeException if there is an error while interacting with the database
     */
    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO app.users (name, email, password, roles, status) VALUES (?, ?, ?, ?, ?)";
        if (connection == null) {
            System.out.println(" Connection close");
        } else {
            System.out.println(" Connection open addUser");
        }
        try {
            assert connection != null;
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRoles().toString());
                stmt.setString(5, user.getStatus().toString());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении пользователя", e);
        }

    }

    /**
     * Retrieves a {@link User} from the database by its ID.
     *
     * <p>This method executes a {@code SELECT} query to fetch a user from the {@code app.users} table based on the provided
     * user ID. If a matching user is found, it is mapped to a {@link User} object and returned. If no user is found or if
     * an error occurs while accessing the database, the method returns {@code null}.
     *
     * @param userId the ID of the user to retrieve
     * @return the {@link User} object representing the user with the specified ID, or {@code null} if no such user exists
     * @throws RuntimeException if there is an error while interacting with the database or executing the SQL query
     */
    @Override
    public User getUserById(Long userId) {
        String sql = "SELECT * FROM app.users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пользователя по ID", e);
        }
        return null;
    }

    /**
     * Maps a {@link ResultSet} to a {@link User} object.
     *
     * <p>This method extracts the data from the {@code ResultSet} representing a single row from the {@code app.users} table
     * and creates a {@link User} object using the retrieved data. It retrieves the user's ID, name, email, password, roles,
     * and status, and returns the corresponding {@link User} object.
     *
     * @param rs the {@link ResultSet} containing the data for a single user
     * @return a {@link User} object with the data mapped from the {@code ResultSet}
     * @throws SQLException if there is an error retrieving data from the {@code ResultSet}
     */
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                (Long) rs.getObject("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                Roles.valueOf(rs.getString("roles")),
                Status.valueOf(rs.getString("status"))
        );

    }

    /**
     * Retrieves a {@link User} from the database by their email address.
     *
     * <p>This method performs a query on the database to retrieve a user based on the provided email address.
     * If the user is found, a {@link User} object is created and returned. If no user is found with the given email,
     * it returns {@code null}.
     *
     * @param email the email address of the user to retrieve
     * @return a {@link User} object corresponding to the provided email, or {@code null} if no user is found
     */
    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM app.users WHERE email = ?";
        if (connection == null) {
            System.out.println("connection == null getUserByEmail");
        } else {
            System.out.println("getUserByEmail не 0");
            System.out.println("🔍 Executing query: " + sql + " with email: " + email);
        }
        try {
            assert connection != null;
            System.out.println("🔍 Проверка состояния соединения: " + connection.isClosed());
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("✅ Пользователь найден: " + rs.getString("email"));
                        return mapUser(rs);
                    } else {
                        try {
                            System.out.println("🔍 Проверка состояния соединения: " + connection.isClosed());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        System.out.println("❌ Пользователь не найден в базе данных");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a user from the database by their user ID.
     *
     * <p>This method checks if the user exists in the database by calling the {@link #isUserPresent(Long)} method.
     * If the user is found, a DELETE query is executed to remove the user with the given ID from the database.
     * If the user does not exist, no action is taken.
     *
     * @param userId the ID of the user to be deleted
     */
    @Override
    public void deleteUser(Long userId) {
        if (isUserPresent(userId)) {
            String sql = "DELETE FROM app.users WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the details of an existing user in the database.
     *
     * <p>This method updates the user's name, email, password, roles, and status based on the user ID.
     * If the update is successful (i.e., one or more rows are affected), the method returns {@code true}.
     * If no rows are updated (e.g., if the user does not exist), the method returns {@code false}.
     *
     * @param updateUser the user object containing the updated information
     * @return {@code true} if the user details were successfully updated, {@code false} otherwise
     */
    @Override
    public boolean updateUser(User updateUser) {
        String sql = "UPDATE app.users SET name = ?, email = ?, password = ?, roles = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, updateUser.getName());
            stmt.setString(2, updateUser.getEmail());
            stmt.setString(3, updateUser.getPassword());
            stmt.setString(4, updateUser.getRoles().toString());
            stmt.setString(5, updateUser.getStatus().toString());
            stmt.setLong(6, updateUser.getUserId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all users from the database.
     *
     * <p>This method queries the database to retrieve all users and returns them as a list of {@link User} objects.
     * If there are no users, the method returns an empty list.
     *
     * @return a list of all users in the database
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM app.users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Checks if a user exists in the database based on the provided email.
     *
     * <p>This method queries the database to find a user by their email. If the user exists, it returns {@code true};
     * otherwise, it returns {@code false}.
     *
     * @param email the email of the user to check
     * @return {@code true} if the user exists, {@code false} otherwise
     */
    public boolean isUserPresent(String email) {
        return getUserByEmail(email) != null;
    }

    /**
     * Checks if a user exists in the database based on the provided user ID.
     *
     * <p>This method queries the database to find a user by their ID. If the user exists, it returns {@code true};
     * otherwise, it returns {@code false}.
     *
     * @param id the ID of the user to check
     * @return {@code true} if the user exists, {@code false} otherwise
     */
    public boolean isUserPresent(Long id) {
        return getUserById(id) != null;
    }
}
