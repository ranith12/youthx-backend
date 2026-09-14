package com.youthx.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;



@Data
public class UpdateSavingGoalRequest {

    @NotNull
    private String name;

    @Positive
    private BigDecimal targetAmount;
}