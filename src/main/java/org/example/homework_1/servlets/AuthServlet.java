package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.dto.LoginDTO;
import org.example.homework_1.dto.UserDTO;
import org.example.homework_1.mappers.UserMapper;
import org.example.homework_1.models.User;
import org.example.homework_1.services.Interfaces.UserServiceInterface;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {
    private  UserServiceInterface userService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/register")) {
            registerUser(req, resp);
        } else if (path.equals("/login")) {
            loginUser(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Not found"));
        }
    }

    private void registerUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO userDTO = objectMapper.readValue(req.getInputStream(), UserDTO.class);
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        userService.register(user.getName(), user.getEmail(), user.getPassword());

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "User registered"));
    }

    private void loginUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginDTO loginDTO = objectMapper.readValue(req.getInputStream(), LoginDTO.class);
        Optional<User> user = userService.login(loginDTO.email(), loginDTO.password());

        if (user.isPresent()) {
            UserDTO userDTO = UserMapper.INSTANCE.toDTO(user.get());

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), Map.of(
                    "message", "Login successful",
                    "user", userDTO
            ));
        } else {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid email or password"));
        }
    }

}
