package org.example.homework_1.servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.jwt.JwtUtil;
import org.example.homework_1.jwt.TokenBlacklist;
import org.example.homework_1.models.User;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import java.io.IOException;
import java.util.Map;

public class AuthFilter implements Filter {
    public static final String AUTHENTICATED_USER_ATTRIBUTE = "currentUser";
    private final UserServiceInterface userService;
    private final ObjectMapper objectMapper;
    public AuthFilter(UserServiceInterface userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.objectMapper.findAndRegisterModules();
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getRequestURI();
        String contextPath = req.getContextPath();

        if (path.startsWith(contextPath + "/api/auth/") || path.equals(contextPath + "/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(resp, "Missing or invalid Authorization header");
            return;
        }
        String token = authHeader.substring(7);

        String userEmail;
        try {
            if (!JwtUtil.validateJwtToken(token)) { // Предполагаем, что validateJwtToken проверяет и подпись, и срок действия
                sendErrorResponse(resp, "Invalid or expired token");
                return;
            }
            // Проверка черного списка
            if (TokenBlacklist.isBlacklisted(token)) {
                sendErrorResponse(resp, "Token has been revoked");
                return;
            }
            // Получаем email только ПОСЛЕ всех проверок токена
            userEmail = JwtUtil.getEmailFromToken(token);
        } catch (Exception e) { // Ловим все ошибки валидации/парсинга токена
            System.err.println("Token processing error: " + e.getMessage()); // Заменить логгером
            sendErrorResponse(resp, "Invalid token format or processing error");
            return;
        }

        // --- Загрузка пользователя из БД ---
        User currentUser = userService.getUserByEmail(userEmail); // Предполагаем, что сервис возвращает User или null (или Optional)

        if (currentUser == null) {
            System.err.println("Valid token for non-existent user: " + userEmail); // Заменить логгером
            sendErrorResponse(resp, "User associated with token not found");
            return;
        }
        req.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, currentUser);
        filterChain.doFilter(request, response);
    }
    private void sendErrorResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Используем ObjectMapper для формирования ответа
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Можно использовать для получения параметров инициализации, если нужно
        System.out.println("AuthFilter initialized.");
    }
    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed.");
    }

}
