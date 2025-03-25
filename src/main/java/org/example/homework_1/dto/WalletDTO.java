package org.example.homework_1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WalletDTO (
        @JsonProperty("userId") Long userId,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("monthlyBudget") BigDecimal monthlyBudget,
        @JsonProperty("lastUpdated")  LocalDate lastUpdated

){
}
