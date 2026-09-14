package com.youthx.backend.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class HabitResponse {

    private Long id;
    private UUID userId;
    private String name;
    private String emoji;
    private String color;
    private String frequency;
    private LocalTime timeOfDay;
    private Integer currentStreak;
    private LocalDate lastCompletedAt;
}
