package com.youthx.backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateHabitRequest {

    @NotBlank
    private String name;

    private String emoji;

    private String color;

    @NotBlank
    private String frequency;

    private LocalTime timeOfDay;
}
