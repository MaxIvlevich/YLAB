package org.example.homework_1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.homework_1.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO ( @JsonProperty("transactionUUID") Long transactionUUID,
                                @JsonProperty("userId") Long userUUID,
                               @JsonProperty("type") TransactionType type,
                               @JsonProperty("amount")  BigDecimal amount,
                               @JsonProperty("category")String category,
                               @JsonProperty("description")  String description,
                               @JsonProperty("date")LocalDate date){

}
