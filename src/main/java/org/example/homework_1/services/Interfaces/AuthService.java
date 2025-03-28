package org.example.homework_1.services.Interfaces;
import jakarta.servlet.http.HttpServletRequest;
import org.example.homework_1.models.User;
import javax.security.sasl.AuthenticationException;

public interface AuthService {
    User authenticateUser(HttpServletRequest request) throws AuthenticationException;

}
