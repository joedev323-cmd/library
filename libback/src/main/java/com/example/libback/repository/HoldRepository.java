package com.example.libback.repository;

import com.example.libback.model.Loan; // or Hold
import com.example.libback.model.enums.LoanStatus;

import org.springframework.data.jpa.repository.JpaRepository;
public interface HoldRepository extends JpaRepository<Loan, Long> {
    
    // Spring Data JPA automatically turns this into:
    // SELECT COUNT(*) FROM loans WHERE borrower_id = ? AND status = ?
    long countByBorrowerBorrowerIdAndStatus(String borrowerId, String status);
    long countByBorrowerBorrowerIdAndStatus(String borrowerId, LoanStatus status);
}