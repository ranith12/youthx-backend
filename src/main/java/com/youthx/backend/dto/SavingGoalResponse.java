package com.youthx.backend.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SavingGoalResponse {

    private Long id;
    private UUID userId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Integer progressPercent;
    private OffsetDateTime createdAt;
}
