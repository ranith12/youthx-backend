package com.youthx.backend.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ChallengeResponse {

    private Long id;
    private String title;
    private String description;
    private String type;
    private Integer xpReward;
    private LocalDate startDate;
    private LocalDate endDate;
}
