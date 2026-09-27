package com.youthx.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateExpenseRequest {

    @NotNull
    private Long categoryId;

    // must be one of expense | income (chk_expenses_type)
    @NotNull
    @Pattern(regexp = "expense|income", message = "type must be one of: expense, income")
    private String type;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String note;

    @NotNull
    private LocalDate transactionDate;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}