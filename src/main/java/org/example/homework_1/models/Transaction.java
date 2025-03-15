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
    private Long  transactionUUID;
    private Long  userUUID;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private LocalDate  date;
    private String description;

    public Transaction(Long userUUID, TransactionType type, BigDecimal amount, String category, LocalDate date, String description) {
        this.userUUID = userUUID;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }
    public Transaction(){

    }
}
