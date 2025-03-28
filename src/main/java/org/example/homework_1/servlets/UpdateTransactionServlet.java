package org.example.homework_1.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

//@WebServlet("/api/transactions/update")
public class UpdateTransactionServlet extends HttpServlet {

    private final TransactionServiceInterface transactionService;
    private final UserServiceInterface userService;

    public UpdateTransactionServlet(TransactionServiceInterface transactionService,
                                     UserServiceInterface userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }
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
