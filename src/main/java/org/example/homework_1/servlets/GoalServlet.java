package org.example.homework_1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.dto.GoalDTO;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.mappers.GoalMapper;
import org.example.homework_1.models.Goal;
import org.example.homework_1.models.User;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;

import java.io.IOException;
import java.util.List;

//@WebServlet("/api/goals")
public class GoalServlet extends HttpServlet {
    private final WalletServiceInterface walletService ;
    private final ObjectMapper objectMapper;
    private final GoalMapper goalMapper;
    private final UserServiceInterface userService;
    public GoalServlet(WalletServiceInterface walletService, ObjectMapper objectMapper, GoalMapper goalMapper, UserServiceInterface userService) {
        this.walletService = walletService;
        this.objectMapper = objectMapper;
        this.goalMapper = goalMapper;
        this.userService = userService;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
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
