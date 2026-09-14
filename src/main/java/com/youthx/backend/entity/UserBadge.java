package com.youthx.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_badges")
@Getter
@Setter
public class UserBadge {

    @EmbeddedId
    private UserBadgeId id;

    @Column(name = "earned_at", nullable = false, updatable = false)
    private OffsetDateTime earnedAt;
}