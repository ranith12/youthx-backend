package com.youthx.backend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProgressRequest {

    @NotNull
    private Integer progress;
}
