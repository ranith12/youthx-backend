package com.youthx.backend.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {

    private Long categoryId;

    // must be one of expense | income (chk_expenses_type)
    private String type;

    @Positive
    private BigDecimal amount;

    private String note;

    @NotNull
    private LocalDate transactionDate;
}
