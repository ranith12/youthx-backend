package com.youthx.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_challenges")
@Getter
@Setter
public class UserChallenge {

    @EmbeddedId
    private UserChallengeId id;

    @Column(name = "progress", nullable = false)
    private Integer progress;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}