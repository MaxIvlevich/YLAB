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
    private Long userid;
    private String goalName;
    private BigDecimal target;

}
