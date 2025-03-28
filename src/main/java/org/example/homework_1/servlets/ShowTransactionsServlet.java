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
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

//@WebServlet("/api/transactions")
public class ShowTransactionsServlet extends HttpServlet {
    private final TransactionServiceInterface transactionService;
    private final ObjectMapper objectMapper;
    private final TransactionMapper transactionMapper;
    private final UserServiceInterface userService;


    public ShowTransactionsServlet(TransactionServiceInterface transactionService, ObjectMapper objectMapper, TransactionMapper transactionMapper, UserServiceInterface userService) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
        this.transactionMapper = transactionMapper;
        this.userService = userService;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        User currentUser = userService.getUserByEmail(userEmail);

        if(currentUser.isAdmin()) {
            try {
                String userIdParam = req.getParameter("userId");
                if (userIdParam!= null){
                    Long userId = Long.parseLong(userIdParam);
                    System.out.println("userId : " + userId);
                    List<Transaction> transactions = transactionService.showUserTransactions(userId);
                    List<TransactionDTO> transactionDTOS = transactions.stream().map(transactionMapper::toDTO).toList();
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getOutputStream(), transactionDTOS);
                } else {
                    Long userId = currentUser.getUserId();
                    List<Transaction> transactions = transactionService.showUserTransactions(userId);
                    List<TransactionDTO> transactionDTOS = transactions.stream().map(transactionMapper::toDTO).toList();
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getOutputStream(), transactionDTOS);
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid userId\"}");
            }
        }else if(!currentUser.isAdmin()) {
            String userIdParam = req.getParameter("userId");
            if (userIdParam != null){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"you don't have access\"}");

            }else {
                Long userId = currentUser.getUserId();
                List<Transaction> transactions = transactionService.showUserTransactions(userId);
                List<TransactionDTO> transactionDTOS = transactions.stream().map(transactionMapper::toDTO).toList();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getOutputStream(), transactionDTOS);
            }
        }
    }
}
