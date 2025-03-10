package org.example.homework_1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.homework_1.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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
