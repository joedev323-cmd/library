package com.example.libback.dto;

import java.math.BigDecimal;

public class ReportMetricsDto {

    private long totalBooks;
    private long totalCopies;
    private long totalMembers;

    private long totalLoans;
    private long activeLoans;
    private long overdueLoans;
    private long returnedLoans;

    private long availableCopies;

    private BigDecimal finesCollectedMtd;

    private boolean inventoryAvailable;

    private double availablePercentage;
    private double activeLoanPercentage;
    private double overduePercentage;

    public ReportMetricsDto() {
    }

    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(long totalCopies) {
        this.totalCopies = totalCopies;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getTotalLoans() {
        return totalLoans;
    }

    public void setTotalLoans(long totalLoans) {
        this.totalLoans = totalLoans;
    }

    public long getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(long activeLoans) {
        this.activeLoans = activeLoans;
    }

    public long getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(long overdueLoans) {
        this.overdueLoans = overdueLoans;
    }

    public long getReturnedLoans() {
        return returnedLoans;
    }

    public void setReturnedLoans(long returnedLoans) {
        this.returnedLoans = returnedLoans;
    }

    public long getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(long availableCopies) {
        this.availableCopies = availableCopies;
    }

    public BigDecimal getFinesCollectedMtd() {
        return finesCollectedMtd;
    }

    public void setFinesCollectedMtd(BigDecimal finesCollectedMtd) {
        this.finesCollectedMtd = finesCollectedMtd;
    }

    public boolean isInventoryAvailable() {
        return inventoryAvailable;
    }

    public void setInventoryAvailable(boolean inventoryAvailable) {
        this.inventoryAvailable = inventoryAvailable;
    }

    public double getAvailablePercentage() {
        return availablePercentage;
    }

    public void setAvailablePercentage(double availablePercentage) {
        this.availablePercentage = availablePercentage;
    }

    public double getActiveLoanPercentage() {
        return activeLoanPercentage;
    }

    public void setActiveLoanPercentage(double activeLoanPercentage) {
        this.activeLoanPercentage = activeLoanPercentage;
    }

    public double getOverduePercentage() {
        return overduePercentage;
    }

    public void setOverduePercentage(double overduePercentage) {
        this.overduePercentage = overduePercentage;
    }
}
