package org.example.homework_1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GoalDTO(
        @JsonProperty("userId") Long userId,
        @JsonProperty("goalName") String goalName,
        @JsonProperty("target") BigDecimal target
) {
}
