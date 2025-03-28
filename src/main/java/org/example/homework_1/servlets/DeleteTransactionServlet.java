package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
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

//@WebServlet("/api/transactions/delete")
public class DeleteTransactionServlet extends HttpServlet {
    private final TransactionServiceInterface transactionService;
    private final UserServiceInterface userService;
    public DeleteTransactionServlet(TransactionServiceInterface transactionService, UserServiceInterface userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        User currentUser = userService.getUserByEmail(userEmail);
        try {
            Long userId = currentUser.getUserId();
            Long transactionId = Long.valueOf(req.getParameter("transactionId"));
            if(currentUser.isAdmin()){
            boolean isDeleted = transactionService.deleteTransaction(userId, transactionId);
            if (isDeleted) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\": \"Transaction deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Transaction not found\"}");
            }
            }else {
                Long currentUserId = currentUser.getUserId();
                Long transactionUserId = Long.valueOf(req.getParameter("transactionId"));
                Transaction transaction = transactionService.getTransactionById(transactionUserId);
                if(transaction.getUserUUID().equals(currentUserId)){
                    transactionService.deleteTransaction(currentUserId, transactionUserId);
                }else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\": \"You don't have access\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid input or internal error\"}");
        }
    }
}
