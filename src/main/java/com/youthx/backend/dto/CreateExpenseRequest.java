package com.youthx.backend.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {

    private Long categoryId;

    // must be one of expense | income (chk_expenses_type)
    @NotNull(message = "type must not be null")
    @Pattern(regexp = "expense|income", message = "type must be one of: expense, income")
    private String type;

    @NotNull(message = "amount must not be null")
    @Positive(message = "amount must be greater than 0")
    private BigDecimal amount;

    private String note;

    @NotNull
    private LocalDate transactionDate;
}
