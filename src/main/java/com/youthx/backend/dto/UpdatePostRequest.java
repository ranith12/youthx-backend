package com.youthx.backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePostRequest {

    @NotBlank
    private String content;
}
