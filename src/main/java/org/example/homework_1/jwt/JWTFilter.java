package org.example.homework_1.jwt;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebFilter("/*")
public class JWTFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация (можно оставить пустым)
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        // Пропускаем запросы на аутентификацию
        String path = req.getRequestURI();
        if (path.startsWith("/Y_LAB_HW_war/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
            // Получаем токен из заголовка Authorization
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
            return;
            }
        String token = authHeader.substring(7); // Убираем "Bearer "
        if (!JwtUtil.validateJwtToken(token)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
            }
            // Добавляем email пользователя в запрос
           req.setAttribute("userEmail", JwtUtil.getEmailFromToken(token));

           filterChain.doFilter(request, response);
        }
    }

