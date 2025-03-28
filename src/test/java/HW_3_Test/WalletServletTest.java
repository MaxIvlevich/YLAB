package HW_3_Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;
import org.example.homework_1.servlets.WalletServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class WalletServletTest {
    @Mock
    private UserServiceInterface userService;
    @Mock
    private WalletServiceInterface walletService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter printWriter;
    @InjectMocks
    private WalletServlet walletServlet;
    @BeforeEach
    void setUp() throws Exception {

        when(response.getWriter()).thenReturn(printWriter);
    }
    @Test
    void doGet_UserAccessingOwnWallet_Success() throws Exception {

    }

}
