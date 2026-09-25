package com.youthx.backend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExpenseCategoryResponse {

    private Long id;
    private String name;
    private String icon;

    @JsonProperty("isIncome")
    private boolean isIncome;

    @JsonProperty("isIncome")
    public boolean isIncome() {
        return isIncome;
    }

    @JsonProperty("isIncome")
    public void setIncome(boolean isIncome) {
        this.isIncome = isIncome;
    }
}
