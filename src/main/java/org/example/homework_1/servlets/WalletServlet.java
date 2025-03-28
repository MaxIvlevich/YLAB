package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.dto.WalletDTO;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.mappers.WalletMapper;
import org.example.homework_1.models.User;
import org.example.homework_1.models.Wallet;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

//@WebServlet("/api/wallet")
public class WalletServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final UserServiceInterface userService;
    private final WalletServiceInterface walletService;

    public WalletServlet(ObjectMapper objectMapper, UserServiceInterface userService, WalletServiceInterface walletService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.walletService = walletService;
    }
    @Override
    public void init(ServletConfig config)  {

    }
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
    protected User getCurrentUser(HttpServletRequest req) {
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
    protected void showWallet(Long userId,HttpServletResponse resp) {
        try {
            Wallet wallet = walletService.getUserWallet(userId);
            WalletDTO walletDTO = WalletMapper.INSTANCE.toDTO(wallet);
            objectMapper.writeValue(resp.getOutputStream(), walletDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

