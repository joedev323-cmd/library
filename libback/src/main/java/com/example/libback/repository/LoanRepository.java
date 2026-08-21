package com.example.libback.repository;

import com.example.libback.model.Loan;
import com.example.libback.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // ---------------------------------------------------------
    // Borrower loans
    // ---------------------------------------------------------

    @Query("""
        SELECT l
        FROM Loan l
        WHERE l.borrower.borrowerId = :borrowerId
        """)
    List<Loan> findLoansByBorrowerId(
            @Param("borrowerId") String borrowerId
    );

    long countByBorrowerBorrowerIdAndStatus(
            String borrowerId,
            LoanStatus status
    );


    // ---------------------------------------------------------
    // General loan statistics
    // ---------------------------------------------------------

    long countByStatus(
            LoanStatus status
    );

    long countByStatusAndDueDateBefore(
            LoanStatus status,
            LocalDateTime dateTime
    );


    // ---------------------------------------------------------
    // Active loan for a specific accession
    // ---------------------------------------------------------

    Optional<Loan> findByAccessionAccessionIdAndStatus(
            String accessionId,
            LoanStatus status
    );


    // ---------------------------------------------------------
    // Overdue loans
    // ---------------------------------------------------------

    List<Loan> findTop5ByStatusAndDueDateBeforeOrderByDueDateAsc(
            LoanStatus status,
            LocalDateTime dateTime
    );


    // ---------------------------------------------------------
    // Fines
    // ---------------------------------------------------------

    /**
     * Total fines actually paid within a date range.
     *
     * This is what the Reports page should use for
     * "Fines Collected (MTD)".
     */
    @Query("""
        SELECT COALESCE(SUM(l.finePaid), 0)
        FROM Loan l
        WHERE l.returnedDate >= :startDate
          AND l.returnedDate < :endDate
        """)
    BigDecimal sumFinesCollectedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    // ---------------------------------------------------------
    // Popular catalogue categories
    // ---------------------------------------------------------

    /**
     * Counts currently active loans grouped by category.
     *
     * Item -> categories is ManyToMany, so we join through
     * l.accession.item.categories.
     */
    @Query("""
        SELECT c.name, COUNT(l)
        FROM Loan l
        JOIN l.accession a
        JOIN a.item i
        JOIN i.categories c
        WHERE l.status = com.example.libback.model.enums.LoanStatus.ACTIVE
        GROUP BY c.name
        ORDER BY COUNT(l) DESC
        """)
    List<Object[]> findPopularCategories();
    List<Loan> findTop5ByOrderByLoanIdDesc();

}
