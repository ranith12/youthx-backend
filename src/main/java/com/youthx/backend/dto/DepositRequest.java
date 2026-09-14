package com.youthx.backend.dto;


import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {

    @Positive
    private BigDecimal amount;
}
