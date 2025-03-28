package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.dto.TransactionDTO;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.mappers.TransactionMapper;
import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;

import java.io.IOException;


public class AddTransactionServlet extends HttpServlet {
    private final TransactionServiceInterface transactionService;
    private final ObjectMapper objectMapper;
    private final TransactionMapper transactionMapper;
    private final UserServiceInterface userService;// MapStruct Mapper


    public AddTransactionServlet(TransactionServiceInterface transactionService, ObjectMapper objectMapper, TransactionMapper transactionMapper, UserServiceInterface userService) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
        this.transactionMapper = transactionMapper;
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        User currentUser = userService.getUserByEmail(userEmail);
        try {
            Long userId = currentUser.getUserId();
            TransactionDTO transactionDTO = objectMapper.readValue(req.getInputStream(),TransactionDTO.class);
            Transaction transaction = transactionMapper.INSTANCE.toEntity(transactionDTO);
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
