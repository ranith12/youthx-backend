package com.youthx.backend.dto;


import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CommentResponse {

    private Long id;
    private Long postId;
    private UUID userId;
    private String content;
    private OffsetDateTime createdAt;
    private String authorFullName;
}
