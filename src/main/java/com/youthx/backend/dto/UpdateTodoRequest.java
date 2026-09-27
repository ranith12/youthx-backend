package com.youthx.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTodoRequest {

    @NotBlank
    private String title;

    // must be one of high | medium | low (chk_todos_priority)
    @NotNull(message = "priority must not be null")
    private String priority;

    private LocalDate dueDate;

    @NotNull(message = "isCompleted must not be null")
    private Boolean isCompleted;
}
