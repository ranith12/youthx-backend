package com.youthx.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "habits")
@Getter
@Setter
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "emoji")
    private String emoji;

    @Column(name = "color")
    private String color;

    @Column(name = "frequency", nullable = false)
    private String frequency;

    @Column(name = "time_of_day")
    private LocalTime timeOfDay;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak;

    @Column(name = "last_completed_at")
    private LocalDate lastCompletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}