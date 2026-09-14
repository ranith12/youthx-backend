package com.youthx.backend.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class GoalResponse {

    private Long id;
    private UUID userId;
    private String name;
    private String category;
    private LocalDate targetDate;
    private Integer progressPercent;
    private LocalTime dailyReminderTime;
}
