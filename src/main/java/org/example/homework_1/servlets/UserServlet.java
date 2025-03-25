package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.dto.UserDTO;
import org.example.homework_1.jwt.JwtUtil;
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
    private final Connection connection;

    {
        try {
            ConfigReader configReader = new ConfigReader("config.properties");
            connection = DatabaseConfig.getConnection(configReader);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final UserRepositoryInterface userRepository = new UserRepositoryJDBC(connection);
    private final UserServiceInterface userService = new UserServiceImpl( userRepository);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getCurrentUser(req);
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "User ID is required"));
            return;
        }
        if (currentUser.isAdmin()) {
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
        } else {
            Long userId = Long.parseLong(pathInfo.substring(1));
            if (currentUser.getUserId().equals(userId)) {
                UserDTO userRecord = objectMapper.readValue(req.getInputStream(), UserDTO.class);
                User updatedUser = new User(userId, userRecord.name(), userRecord.email(), userRecord.password(), Roles.USER, Status.ACTIVE);
                userService.updateUser(updatedUser);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "User updated"));

            } else if (userService.isUserPresent(userId)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "You don't have access"));

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid user ID"));
            }
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getCurrentUser(req);
        Long userId = Long.valueOf(req.getParameter("userId"));
        if (currentUser.isAdmin()) {
            deleteUser(resp, userId);
        } else if (currentUser.getUserId().equals(userId)) {
            deleteUser(resp, userId);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "You don't have access"));
        }
    }
    private User getCurrentUser(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        return userService.getUserByEmail(userEmail);
    }
    private void deleteUser(HttpServletResponse resp, Long userId) throws IOException {

        try {
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






