package org.example.homework_1.servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.dto.GoalDTO;
import org.example.homework_1.dto.TransactionDTO;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.mappers.GoalMapper;
import org.example.homework_1.mappers.TransactionMapper;
import org.example.homework_1.mappers.UserMapper;
import org.example.homework_1.models.Goal;
import org.example.homework_1.models.Transaction;
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
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/goals")
public class GoalServlet extends HttpServlet {
    private final Connection connection;
    {
        try {
            ConfigReader configReader = new ConfigReader("config.properties");
            connection = DatabaseConfig.getConnection(configReader);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private final WalletRepositoryInterface walletRepository = new WalletRepositoryJDBC(connection);
    private final TransactionRepositoryInterface transactionRepository = new TransactionRepositoryJDBC(connection);
    private final EmailServiceInterface emailService = new EmailService();
    private final UserRepositoryInterface userRepository = new UserRepositoryJDBC(connection);
    private final UserServiceInterface userService = new UserServiceImpl(userRepository);
    private final WalletServiceInterface walletService = new WalletServiceImpl(walletRepository, transactionRepository, emailService, userService);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        Long id = currentUser.getUserId();
        List<Goal> goals = walletService.getUserGoals(id);
        List<GoalDTO> goalDTOS = goals.stream().map(goalMapper::toDTO).toList();
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), goalDTOS);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User currentUser = getCurrentUser(req);
            Long id = currentUser.getUserId();
            GoalDTO goalDTO = objectMapper.readValue(req.getInputStream(), GoalDTO.class);
            Goal goal = GoalMapper.INSTANCE.toEntity(goalDTO);
            walletService.addGoal(
                    id,
                    goal.getGoalName(),
                    goal.getTarget()
            );
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Goal added successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid input or internal error\"}");
        }
    }
    private User getCurrentUser(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        return userService.getUserByEmail(userEmail);
    }

}
