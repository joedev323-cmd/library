 package com.example.libback.dto;

import java.math.BigDecimal;

public class ReportMetricsDto {

    // ============================
    // CATALOGUE
    // ============================

    private long totalBooks;
    private long totalCopies;
    private long totalMembers;

    // ============================
    // CIRCULATION
    // ============================

    private long totalLoans;
    private long activeLoans;
    private long overdueLoans;
    private long returnedLoans;

    private double returnRate;
    private double overdueRate;

    // ============================
    // INVENTORY
    // ============================

    private long availableCopies;

    private boolean inventoryAvailable;

    private double availablePercentage;
    private double activeLoanPercentage;

    // ============================
    // FINANCIAL
    // ============================

    private BigDecimal finesCollectedMtd;
    private BigDecimal outstandingFines;
    private long outstandingFineLoans;

    public ReportMetricsDto() {
    }

    // ============================
    // CATALOGUE
    // ============================

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

    // ============================
    // CIRCULATION
    // ============================

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

    public double getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(double returnRate) {
        this.returnRate = returnRate;
    }

    public double getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(double overdueRate) {
        this.overdueRate = overdueRate;
    }

    // ============================
    // INVENTORY
    // ============================

    public long getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(long availableCopies) {
        this.availableCopies = availableCopies;
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

    // ============================
    // FINANCIAL
    // ============================

    public BigDecimal getFinesCollectedMtd() {
        return finesCollectedMtd;
    }

    public void setFinesCollectedMtd(BigDecimal finesCollectedMtd) {
        this.finesCollectedMtd = finesCollectedMtd;
    }

    public BigDecimal getOutstandingFines() {
        return outstandingFines;
    }

    public void setOutstandingFines(BigDecimal outstandingFines) {
        this.outstandingFines = outstandingFines;
    }

    public long getOutstandingFineLoans() {
        return outstandingFineLoans;
    }

    public void setOutstandingFineLoans(long outstandingFineLoans) {
        this.outstandingFineLoans = outstandingFineLoans;
    }
}