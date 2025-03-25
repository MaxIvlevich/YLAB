package org.example.homework_1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goal {
    private Long goalId;
    private Long userId;
    private String goalName;
    private BigDecimal target;

    public Goal(Long userId, String goalName, BigDecimal targetAmount) {
    }
}

