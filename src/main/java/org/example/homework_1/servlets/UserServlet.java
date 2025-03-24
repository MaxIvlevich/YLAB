package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.dto.UserDTO;
import org.example.homework_1.mappers.UserMapper;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.UserServiceImpl;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@WebServlet("/api/users")
public class UserServlet extends HttpServlet {
    private UserServiceInterface userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public void init() {
        try {
            ConfigReader configReader = new ConfigReader("config.properties");
            Connection connection = DatabaseConfig.getConnection(configReader);
            UserRepositoryInterface userRepository = new UserRepositoryJDBC(connection);
            userService = new UserServiceImpl(userRepository);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<User> users = userService.showAllUsers();
            List<UserDTO> userRecords = users.stream().map(userMapper::toDTO).toList();

            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), userRecords);
        } else {

            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                User user = userService.getUserById(userId);

                if (user == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "User not found"));
                } else {
                    UserDTO userRecord = userMapper.toDTO(user);
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getOutputStream(), userRecord);
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid user ID"));
            }
        }
    }

    /**
     * Обновление данных пользователя (PUT /users/{id})
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "User ID is required"));
            return;
        }
        try {
            Long userId = Long.parseLong(pathInfo.substring(1));
            UserDTO userRecord = objectMapper.readValue(req.getInputStream(), UserDTO.class);
            User updatedUser = new User(userId, userRecord.name(), userRecord.email(), userRecord.password(), Roles.USER, Status.ACTIVE);

            userService.updateUser(updatedUser);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "User updated"));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid user ID"));
        }
    }

    /**
     * Удаление пользователя (DELETE /users/{id})
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "User ID is required"));
            return;
        }

        try {
            Long userId = Long.parseLong(pathInfo.substring(1));
            boolean deleted = userService.deleteUser(userId);

            if (deleted) {
                objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "User deleted"));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "User not found"));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid user ID"));
        }
    }
}






