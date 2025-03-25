package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.dto.UserDTO;
import org.example.homework_1.dto.WalletDTO;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.mappers.WalletMapper;
import org.example.homework_1.models.User;
import org.example.homework_1.models.Wallet;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/wallet")
public class WalletServlet extends HttpServlet {
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
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        checkValidParam(req, resp);
        Long userId = Long.valueOf(req.getParameter("userId"));
        String action = req.getParameter("action");
            if (currentUser.getUserId().equals(userId)) {
                if(action==null){
                    showWallet(userId,resp);
                }else checkWalletParam(action, userId, resp);
            } else if (currentUser.isAdmin()) {
                if(action==null){
                    showWallet(userId,resp);
                }else checkWalletParam(action, userId, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "You don't have access"));
            }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        checkValidParam(req, resp);
        Long userId = Long.valueOf(req.getParameter("userId"));
        String action = req.getParameter("action");
        if (currentUser.getUserId().equals(userId)) {
            addWalletParam(action, userId, resp, req);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "You don't have access"));
        }
    }
    private User getCurrentUser(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        String token = authHeader.substring(7);
        String userEmail = JwtUtil.getEmailFromToken(token);
        return userService.getUserByEmail(userEmail);
    }
    private void checkWalletParam(String action, Long userId, HttpServletResponse resp) throws IOException {
        switch (action) {
            case "balance": {
                BigDecimal balance = walletService.getBalance(userId);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("balance", balance));
            }
            case "budget": {
                double budget = walletService.getBudget(userId);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("budget", budget));
            }
            default: {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid action"));
            }
        }
    }
    private void checkValidParam(HttpServletRequest req, HttpServletResponse resp) {
        String userIdParam = req.getParameter("userId");
        if (userIdParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Missing userId parameter"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void addWalletParam(String action, Long userId, HttpServletResponse resp, HttpServletRequest req) {
        try {
            WalletDTO walletDTO = objectMapper.readValue(req.getInputStream(), WalletDTO.class);
            if (action.equals("setBudget")) {
                BigDecimal budget = walletDTO.monthlyBudget();
                walletService.setBudget(userId, budget);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Budget set to " + budget));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Invalid action"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void showWallet(Long userId,HttpServletResponse resp) {
        try {
            Wallet wallet = walletService.getUserWallet(userId);
            WalletDTO walletDTO = WalletMapper.INSTANCE.toDTO(wallet);
            objectMapper.writeValue(resp.getOutputStream(), walletDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

