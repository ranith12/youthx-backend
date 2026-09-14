package com.youthx.backend.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSavingGoalRequest {

    @NotNull
    private String name;

    @Positive
    private BigDecimal targetAmount;
}
