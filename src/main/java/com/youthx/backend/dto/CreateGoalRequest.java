package com.youthx.backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class CreateGoalRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    private LocalDate targetDate;

    @Min(0)
    @Max(100)
    private Integer progressPercent;

    private LocalTime dailyReminderTime;
}
