package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.dto.TransactionDTO;
import org.example.homework_1.mappers.TransactionMapper;
import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//@WebServlet("/api/transactions")
public class ShowTransactionsServlet extends HttpServlet {
    private final TransactionServiceInterface transactionService;
    private final ObjectMapper objectMapper;
    private final TransactionMapper transactionMapper;



    public ShowTransactionsServlet(TransactionServiceInterface transactionService, ObjectMapper objectMapper,
                                   TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
        this.transactionMapper = transactionMapper;

    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {

        User currentUser =(User) req.getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE);

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
                    sendErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN, "You don't have access");
                    objectMapper.writeValue(resp.getOutputStream(), transactionDTOS);
                }
            } catch (Exception e) {
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", e));
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid userId");

            }
        }else if(!currentUser.isAdmin()) {
            String userIdParam = req.getParameter("userId");
            if (userIdParam != null){
                sendErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN, "You don't have access");
            }else {
                Long userId = currentUser.getUserId();
                List<Transaction> transactions = transactionService.showUserTransactions(userId);
                List<TransactionDTO> transactionDTOS = transactions.stream().map(transactionMapper::toDTO).toList();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getOutputStream(), transactionDTOS);
            }
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));

    }
}
