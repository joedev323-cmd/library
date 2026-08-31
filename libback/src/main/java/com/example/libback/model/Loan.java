package com.example.libback.model;

import com.example.libback.model.enums.LoanStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans", indexes = {
        @Index(name = "idx_loan_status", columnList = "status"),
        @Index(name = "idx_loan_due_date", columnList = "due_date"),
        @Index(name = "idx_loan_member", columnList = "member_id"),
        @Index(name = "idx_loan_accession", columnList = "accession_id")
})
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accession_id", nullable = false)
    private Accession accession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * The librarian/admin who issued the book.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issued_by", nullable = false)
    private User issuedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime checkoutDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime returnedDate;

    @Column(nullable = false)
    private Integer renewalCount = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fineAccrued = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finePaid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status = LoanStatus.ACTIVE;

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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public User getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(User issuedBy) {
        this.issuedBy = issuedBy;
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
    public BigDecimal getOutstandingFine() {

    BigDecimal accrued =
            fineAccrued != null
                    ? fineAccrued
                    : BigDecimal.ZERO;

    BigDecimal paid =
            finePaid != null
                    ? finePaid
                    : BigDecimal.ZERO;

    return accrued.subtract(paid).max(BigDecimal.ZERO);
}

}
