package com.youthx.backend.dto;


import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PostResponse {

    private Long id;
    private UUID userId;
    private String content;
    private List<String> photoUrls;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String authorFullName;
    private long likeCount;
    private long commentCount;
    private boolean likedByMe;
}
