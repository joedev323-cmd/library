package com.example.libback.model;

import com.example.libback.model.enums.LoanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "loans",
    indexes = {
        @Index(name = "idx_loan_status", columnList = "status"),
        @Index(name = "idx_due_date", columnList = "dueDate"),
        @Index(name = "idx_borrower", columnList = "borrower_id"),
        @Index(name = "idx_accession", columnList = "accession_id")
    }
)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accession_id", nullable = false)
    private Accession accession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @Column(nullable = false, updatable = false)
    private LocalDateTime checkoutDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime returnedDate;

    @Column(nullable = false)
    private Integer renewalCount = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fineAccrued = BigDecimal.ZERO;

    // ADDED: Tracks actual payments collected manually at the desk
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finePaid = BigDecimal.ZERO; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status = LoanStatus.ACTIVE;

    @Column(length = 50)
    private String staffId;

    public Loan() {
    }

    @PrePersist
    public void prePersist() {
        if (checkoutDate == null) {
            checkoutDate = LocalDateTime.now();
        }
    }

    public boolean isReturned() {
        return status == LoanStatus.RETURNED;
    }

    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE
                && dueDate.isBefore(LocalDateTime.now());
    }

    // --- GETTERS AND SETTERS ---

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
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

    public LocalDateTime getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDateTime checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    public BigDecimal getFineAccrued() {
        return fineAccrued;
    }

    public void setFineAccrued(BigDecimal fineAccrued) {
        this.fineAccrued = fineAccrued;
    }

    // ADDED: Getter and Setter for finePaid
    public BigDecimal getFinePaid() {
        return finePaid;
    }

    public void setFinePaid(BigDecimal finePaid) {
        this.finePaid = finePaid;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan loan)) return false;
        return Objects.equals(loanId, loan.loanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanId);
    }
}