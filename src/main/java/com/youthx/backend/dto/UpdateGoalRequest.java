package com.youthx.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateGoalRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    private LocalDate targetDate;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer progressPercent;

    private LocalTime dailyReminderTime;
}