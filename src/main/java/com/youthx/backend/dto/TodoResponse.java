package com.youthx.backend.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TodoResponse {

    private Long id;
    private UUID userId;
    private String title;
    private String priority;
    private LocalDate dueDate;
    private Boolean isCompleted;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
