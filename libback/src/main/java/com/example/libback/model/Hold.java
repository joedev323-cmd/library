package com.example.libback.model;

//import com.example.libback.model.enums.AvailabilityStatus; // Or reuse/create a custom HoldStatus enum
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.example.libback.model.enums.LoanStatus;

@Entity
@Table(name = "holds")
public class Hold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long holdId;

    // Link to the global book profile (ISBN / conceptual item)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "isbn", nullable = false)
    private Item item;

    // Optional: Leave null initially. Fill it ONLY when a physical copy is checked in 
    // and held behind the desk for this specific borrower.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accession_id")
    private Accession accession;

    // Link to the person waiting
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @Column(nullable = false)
    private LocalDateTime placedDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // Using a clear String representation of state (e.g., "PENDING", "FILLED", "EXPIRED", "CANCELLED")
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    public Hold() {}

    public Hold(Item item, Borrower borrower, LocalDateTime expiryDate,LoanStatus status) {
        this.item = item;
        this.borrower = borrower;
        this.expiryDate = expiryDate;
        this.placedDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
         this.status = status;
    }

    // Automatically updates the timestamp right before a record is updated in the database
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public long getHoldId() {
        return holdId;
    }

    public void setHoldId(long holdId) {
        this.holdId = holdId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Accession getAccession() {
        return accession;
    }

    public void setAccession(Accession accession) {
        this.accession = accession;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public LocalDateTime getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(LocalDateTime placedDate) {
        this.placedDate = placedDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

  
}