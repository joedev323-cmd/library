package com.example.libback.dto;

import java.util.List;

public class ReportsResponseDto {

    private ReportMetricsDto summary;

    private List<CategoryDemandDto> popularCategories;

    private List<OverdueLoanDto> overdueLoans;

    public ReportsResponseDto() {
    }

    public ReportsResponseDto(
            ReportMetricsDto summary,
            List<CategoryDemandDto> popularCategories,
            List<OverdueLoanDto> overdueLoans) {

        this.summary = summary;
        this.popularCategories = popularCategories;
        this.overdueLoans = overdueLoans;
    }

    public ReportMetricsDto getSummary() {
        return summary;
    }

    public void setSummary(ReportMetricsDto summary) {
        this.summary = summary;
    }

    public List<CategoryDemandDto> getPopularCategories() {
        return popularCategories;
    }

    public void setPopularCategories(List<CategoryDemandDto> popularCategories) {
        this.popularCategories = popularCategories;
    }

    public List<OverdueLoanDto> getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(List<OverdueLoanDto> overdueLoans) {
        this.overdueLoans = overdueLoans;
    }
}
