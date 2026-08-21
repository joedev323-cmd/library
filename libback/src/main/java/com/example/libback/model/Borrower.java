package com.example.libback.model;

import com.example.libback.model.enums.MemberType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "borrowers")
public class Borrower {

    @Id
    @NotBlank
    @Column(length = 50, nullable = false, updatable = false)
    private String borrowerId;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberType memberType = MemberType.staff;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDate registrationDate = LocalDate.now();

    @OneToMany(
            mappedBy = "borrower",
            cascade = CascadeType.ALL
    )
    private Set<Loan> loans = new HashSet<>();

    // --- ADDED FOR OPTION C ---
    // @Transient tells JPA to completely ignore this field during database reads & writes
    @Transient
    private Integer borrowedCount = 0; 

    public Borrower() {
    }

    public Borrower(String borrowerId, String name, String email, MemberType memberType) {
        this.borrowerId = borrowerId;
        this.name = name;
        this.email = email;
        this.memberType = memberType;
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
        loan.setBorrower(this);
    }

    public void removeLoan(Loan loan) {
        loans.remove(loan);
        loan.setBorrower(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Borrower borrower)) return false;
        return Objects.equals(borrowerId, borrower.borrowerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(borrowerId);
    }

    public boolean isActive() {
        return this.active;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<Loan> getLoans() {
        return loans;
    }

    public void setLoans(Set<Loan> loans) {
        this.loans = loans;
    }

    // --- ADDED GETTER & SETTER FOR TRANSIENT FIELD ---
    public Integer getBorrowedCount() {
        return borrowedCount != null ? borrowedCount : 0;
    }

    public void setBorrowedCount(Integer borrowedCount) {
        this.borrowedCount = borrowedCount;
    }
}