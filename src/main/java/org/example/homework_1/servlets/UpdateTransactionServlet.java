package org.example.homework_1.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.JDBCRepositoryes.TransactionRepositoryJDBC;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.TransactionService;
import org.example.homework_1.services.UserServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/api/transactions/update")
public class UpdateTransactionServlet extends HttpServlet {
    private final Connection connection;
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
    private final UserServiceInterface userService = new UserServiceImpl( userRepository);

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        User currentUser = userService.getUserByEmail(userEmail);
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"User not found\"}");
            return;
        }

        try {
                Long userId = Long.valueOf(req.getParameter("userId"));
            if (currentUser.getEmail().equals(userService.getUserEmail(userId))) {
                Long transactionId = Long.valueOf(req.getParameter("transactionId"));
                TransactionType type = TransactionType.valueOf(req.getParameter("type"));
                BigDecimal amount = new BigDecimal(req.getParameter("amount"));
                String category = req.getParameter("category");
                String description = req.getParameter("description");

                Transaction updatedTransaction = new Transaction(userId, type, amount, category, LocalDate.now(), description);

                boolean isUpdated = transactionService.updateTransaction(userId, transactionId, updatedTransaction);

                if (isUpdated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\": \"Transaction updated successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Transaction not found\"}");
                }
            }
            }catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid input or internal error\"}");

        }
    }

}
