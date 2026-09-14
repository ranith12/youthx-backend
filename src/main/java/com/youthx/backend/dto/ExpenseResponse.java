package com.youthx.backend.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class ExpenseResponse {

    private Long id;
    private UUID userId;
    private Long categoryId;
    private String type;
    private BigDecimal amount;
    private String note;
    private LocalDate transactionDate;
    private OffsetDateTime createdAt;
}
