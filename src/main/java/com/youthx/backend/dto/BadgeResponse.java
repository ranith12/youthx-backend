package com.youthx.backend.dto;


import lombok.Data;

@Data
public class BadgeResponse {

    private Long id;
    private String name;
    private String description;
    private String icon;
}
