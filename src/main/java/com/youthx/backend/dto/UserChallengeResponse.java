package com.youthx.backend.dto;


import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserChallengeResponse {

    private UUID userId;
    private Long challengeId;
    private Integer progress;
    private Boolean isCompleted;
    private OffsetDateTime completedAt;
}
