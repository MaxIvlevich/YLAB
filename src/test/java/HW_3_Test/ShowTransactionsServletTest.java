package HW_3_Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework_1.dto.TransactionDTO;
import org.example.homework_1.mappers.TransactionMapper;
import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;
import org.example.homework_1.servlets.AuthFilter;
import org.example.homework_1.servlets.ShowTransactionsServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ShowTransactionsServletTest {
    @Mock
    private TransactionServiceInterface transactionService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletOutputStream mockOutputStream;
    @InjectMocks
    private ShowTransactionsServlet servlet;
    private User regularUser;
    private User adminUser;
    private List<Transaction> sampleTransactions;
    private List<TransactionDTO> sampleTransactionDTOs;

    @BeforeEach
    void setUp() throws IOException {
        regularUser = new User(1L, "Test User", "user@test.com", "hash", Roles.USER, null);
        adminUser = new User(2L, "Admin User", "admin@test.com", "hash", Roles.ADMIN, null);
        Transaction t1 = new Transaction(1L, 1L, null, BigDecimal.TEN,
                "Food", LocalDate.now(), "Lunch");
        Transaction t2 = new Transaction(2L, 1L, null, BigDecimal.ONE, "Coffee",
                LocalDate.now(), "Morning coffee");
        sampleTransactions = Arrays.asList(t1, t2);
        TransactionDTO dto1 = new TransactionDTO(1L, 1L, null, BigDecimal.TEN, "Food",
                 "Lunch" ,LocalDate.now());
        TransactionDTO dto2 = new TransactionDTO(2L, 1L, null, BigDecimal.ONE, "Coffee",
                 "Morning coffee",LocalDate.now());
        sampleTransactionDTOs = Arrays.asList(dto1, dto2);
        when(response.getOutputStream()).thenReturn(mockOutputStream);

    }
    @Test
    void doGet_UserRequestsOwnTransactions_Success() throws Exception {
        when(request.getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE)).thenReturn(regularUser);
        when(request.getParameter("userId")).thenReturn(null);
        when(transactionService.showUserTransactions(regularUser.getUserId())).thenReturn(sampleTransactions);
        when(transactionMapper.toDTO(sampleTransactions.get(0))).thenReturn(sampleTransactionDTOs.get(0));
        when(transactionMapper.toDTO(sampleTransactions.get(1))).thenReturn(sampleTransactionDTOs.get(1));
        when(request.getMethod()).thenReturn("GET");
        // Act
        servlet.service(request, response);
        // Assert
        verify(request).getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE);
        verify(request).getParameter("userId");
        verify(transactionService).showUserTransactions(regularUser.getUserId());
        verify(transactionMapper, times(sampleTransactions.size())).toDTO(any(Transaction.class));
        verify(response).setContentType("application/json");
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(objectMapper).writeValue(eq(mockOutputStream), listCaptor.capture());
        assertEquals(sampleTransactionDTOs, listCaptor.getValue());
        verify(response, never()).setStatus(anyInt());
    }
    @Test
    void doGet_AdminRequestsOtherUserTransactions_Success() throws Exception {
        long targetUserId = 5L;
        when(request.getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE)).thenReturn(adminUser); // Админ
        when(request.getParameter("userId")).thenReturn(String.valueOf(targetUserId)); // Запрашиваем конкретного пользователя
        when(transactionService.showUserTransactions(targetUserId)).thenReturn(sampleTransactions); // Сервис вернет транзакции targetUserId
        when(transactionMapper.toDTO(sampleTransactions.get(0))).thenReturn(sampleTransactionDTOs.get(0));
        when(transactionMapper.toDTO(sampleTransactions.get(1))).thenReturn(sampleTransactionDTOs.get(1));
        when(request.getMethod()).thenReturn("GET");
        // Act
        servlet.service(request, response);
        // Assert
        verify(request).getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE);
        verify(request).getParameter("userId");
        verify(transactionService).showUserTransactions(targetUserId); // Проверяем вызов с targetUserId
        verify(transactionMapper, times(sampleTransactions.size())).toDTO(any(Transaction.class));
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValue(eq(mockOutputStream), eq(sampleTransactionDTOs)); // Проверяем запись DTO
        verify(response, never()).setStatus(anyInt());
    }
    @Test
    void doGet_UserTriesToRequestOtherUserTransactions_ShouldFail() throws Exception {

        long targetUserId = 5L;
        when(request.getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE)).thenReturn(regularUser);
        when(request.getParameter("userId")).thenReturn(String.valueOf(targetUserId));
        Map<String, String> expectedError = Map.of("error", "You don't have access");
        when(request.getMethod()).thenReturn("GET");
        // Act
        servlet.service(request, response);
        // Assert
        verify(request).getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE);
        verify(request).getParameter("userId");
        verify(transactionService, never()).showUserTransactions(anyLong());
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ArgumentCaptor<Map> errorCaptor = ArgumentCaptor.forClass(Map.class);
        verify(objectMapper).writeValue(eq(mockOutputStream), errorCaptor.capture());
        assertEquals(expectedError, errorCaptor.getValue());
    }
    @Test
    void doGet_InvalidUserIdFormat_ShouldReturnBadRequest() throws Exception {

        when(request.getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE)).thenReturn(adminUser);
        when(request.getParameter("userId")).thenReturn("invalid-id");
        Map<String, String> expectedError = Map.of("error", "Invalid userId");
        when(request.getMethod()).thenReturn("GET");
        // Act
        servlet.service(request, response);
        // Assert
        verify(request).getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE);
        verify(request).getParameter("userId");
        verify(transactionService, never()).showUserTransactions(anyLong());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(objectMapper).writeValue(eq(mockOutputStream), eq(expectedError));
    }
    @Test
    void doGet_ServiceReturnsEmptyList_ShouldReturnOkWithEmptyArray() throws Exception {
        when(request.getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE)).thenReturn(regularUser);
        when(request.getParameter("userId")).thenReturn(null);
        when(transactionService.showUserTransactions(regularUser.getUserId())).thenReturn(Collections.emptyList());
        List<TransactionDTO> emptyDtoList = Collections.emptyList();
        when(request.getMethod()).thenReturn("GET");

        servlet.service(request, response);

        verify(request).getAttribute(AuthFilter.AUTHENTICATED_USER_ATTRIBUTE);
        verify(request).getParameter("userId");
        verify(transactionService).showUserTransactions(regularUser.getUserId());
        verify(transactionMapper, never()).toDTO(any());
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValue(eq(mockOutputStream), eq(emptyDtoList));
        verify(response, never()).setStatus(anyInt());
    }
}
