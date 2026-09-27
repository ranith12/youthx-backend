package com.youthx.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTodoRequest {

    @NotBlank
    private String title;

    // must be one of high | medium | low (chk_todos_priority)
    @Pattern(regexp = "high|medium|low", message = "priority must be one of: high, medium, low")
    private String priority;

    private LocalDate dueDate;

    private Boolean isCompleted;
}
