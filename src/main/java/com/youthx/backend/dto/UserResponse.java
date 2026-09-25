package com.youthx.backend.dto;


import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String email;
    private String fullName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
