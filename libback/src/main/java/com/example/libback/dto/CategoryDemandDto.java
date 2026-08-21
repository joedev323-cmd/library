package com.example.libback.dto;

public class CategoryDemandDto {

    private String category;
    private long activeLoans;

    public CategoryDemandDto() {
    }

    public CategoryDemandDto(String category, long activeLoans) {
        this.category = category;
        this.activeLoans = activeLoans;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(long activeLoans) {
        this.activeLoans = activeLoans;
    }
}
