package com.youthx.backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {

    @NotBlank
    private String content;

    private List<String> photoUrls;
}
