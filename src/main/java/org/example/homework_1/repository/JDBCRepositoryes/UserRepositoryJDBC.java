package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;

import java.util.List;
import java.util.UUID;
import java.sql.*;
import java.util.*;
public class UserRepositoryJDBC implements UserRepositoryInterface {
    private final Connection connection;

    public UserRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO app.users (name, email, password, roles, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRoles().toString());
            stmt.setString(5, user.getStatus().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении пользователя", e);
        }

    }
    @Override
    public User getUserById(UUID userId) {
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

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                (UUID) rs.getObject("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                Roles.valueOf(rs.getString("roles")),
                Status.valueOf(rs.getString("status"))
        );

    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public void deleteUser(UUID userId) {

    }

    @Override
    public boolean updateUser(User updateUser) {
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
