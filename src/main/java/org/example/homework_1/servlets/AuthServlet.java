package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.dto.LoginDTO;
import org.example.homework_1.dto.UserDTO;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.mappers.UserMapper;
import org.example.homework_1.models.User;
import org.example.homework_1.repository.JDBCRepositoryes.TransactionRepositoryJDBC;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.example.homework_1.repository.JDBCRepositoryes.WalletRepositoryJDBC;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;
import org.example.homework_1.services.EmailService;
import org.example.homework_1.services.Interfaces.EmailServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;
import org.example.homework_1.services.UserServiceImpl;
import org.example.homework_1.services.WalletServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    Connection connection;
    {
        try {
            ConfigReader configReader = new ConfigReader("config.properties");
            connection = DatabaseConfig.getConnection(configReader);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    private final UserRepositoryInterface userRepository = new UserRepositoryJDBC(connection);
    private final WalletRepositoryInterface walletRepository = new WalletRepositoryJDBC(connection);
    private final EmailServiceInterface emailService = new EmailService();
    private  final UserServiceInterface userService = new UserServiceImpl( userRepository);;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TransactionRepositoryInterface transactionRepository = new TransactionRepositoryJDBC(connection);
    private final WalletServiceInterface walletService = new WalletServiceImpl(walletRepository,transactionRepository,emailService,userService);

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
        String email = user.getEmail();
        userService.register(user.getName(), user.getEmail(), user.getPassword());
        walletService.createWalletForUser(userService.getUserByEmail(email).getUserId());
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "User registered"));
    }
    private void loginUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginDTO loginDTO = objectMapper.readValue(req.getInputStream(), LoginDTO.class);
        Optional<User> user = userService.login(loginDTO.email(), loginDTO.password());
        if (user.isPresent()) {
            String token = JwtUtil.generateToken(user.get().getEmail());
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), Map.of(
                    "message", "Login successful",
                    "token", token
            ));
        } else {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid email or password"));
        }
    }

}
