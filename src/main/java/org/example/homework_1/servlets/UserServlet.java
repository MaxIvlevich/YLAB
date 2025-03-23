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
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.UserServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet("/api/users")
public class UserServlet extends HttpServlet {
    private UserServiceInterface userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        try {
            ConfigReader configReader = new ConfigReader("src/main/resources/config.properties");
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
        List<UserDTO> users = userService.showAllUsers().stream()
                .map(UserMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());

        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), users);
    }





}
