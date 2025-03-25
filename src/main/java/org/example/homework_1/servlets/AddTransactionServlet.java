package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.dto.TransactionDTO;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.mappers.TransactionMapper;
import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.repository.JDBCRepositoryes.TransactionRepositoryJDBC;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.TransactionService;
import org.example.homework_1.services.UserServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/api/transactions/add")
public class AddTransactionServlet extends HttpServlet {
    private final Connection connection;
    private final ObjectMapper objectMapper = new ObjectMapper();
    {
        try {
            ConfigReader configReader = new ConfigReader("config.properties");
            connection = DatabaseConfig.getConnection(configReader);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private final TransactionRepositoryInterface transactionRepository = new TransactionRepositoryJDBC(connection);
    private final TransactionServiceInterface transactionService = new TransactionService(transactionRepository);
    private final UserRepositoryInterface userRepository = new UserRepositoryJDBC(connection);
    private final UserServiceInterface userService = new UserServiceImpl(userRepository);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        User currentUser = userService.getUserByEmail(userEmail);
        try {
            Long userId = currentUser.getUserId();
            System.out.println("userId :" + userId);
            TransactionDTO transactionDTO = objectMapper.readValue(req.getInputStream(),TransactionDTO.class);
            Transaction transaction = TransactionMapper.INSTANCE.toEntity(transactionDTO);
            transactionService.addTransaction(
                    userId,
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getCategory(),
                    transaction.getDescription());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Transaction added successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid input or internal error\"}");
        }
    }
}
