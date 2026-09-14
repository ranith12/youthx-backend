package com.youthx.backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTodoRequest {

    @NotBlank
    private String title;

    // must be one of high | medium | low (chk_todos_priority)
    private String priority;

    private LocalDate dueDate;

    private Boolean isCompleted;
}
