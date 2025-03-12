package org.example.homework_1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.homework_1.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
/**
 * Represents a financial transaction in the system.
 * This class is used to store information related to a user's financial transaction,
 * including the transaction type, amount, category, date, and description.
 *
 */
@Data
@AllArgsConstructor
public class Transaction {
    private UUID transactionUUID;
    private UUID userUUID;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private LocalDate  date;
    private String description;





}
