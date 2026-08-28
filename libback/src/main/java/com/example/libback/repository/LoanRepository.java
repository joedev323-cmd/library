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
        // MEMBER LOANS
        // ---------------------------------------------------------

        List<Loan> findByMemberMemberId(String memberId);

        // ---------------------------------------------------------
        // GENERAL LOAN STATISTICS
        // ---------------------------------------------------------

        long countByStatus(
                        LoanStatus status);

        long countByStatusAndDueDateBefore(
                        LoanStatus status,
                        LocalDateTime dateTime);

        // ---------------------------------------------------------
        // ACTIVE LOAN FOR A SPECIFIC PHYSICAL COPY
        // ---------------------------------------------------------

        Optional<Loan> findByAccessionAccessionIdAndStatus(
                        String accessionId,
                        LoanStatus status);

        // ---------------------------------------------------------
        // OVERDUE LOANS
        // ---------------------------------------------------------

        List<Loan> findTop5ByStatusAndDueDateBeforeOrderByDueDateAsc(
                        LoanStatus status,
                        LocalDateTime dateTime);

        // ---------------------------------------------------------
        // FINES
        // ---------------------------------------------------------

        @Query("""
                        SELECT COALESCE(SUM(l.finePaid), 0)
                        FROM Loan l
                        WHERE l.returnedDate >= :startDate
                          AND l.returnedDate < :endDate
                        """)
        BigDecimal sumFinesCollectedBetween(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // ---------------------------------------------------------
        // POPULAR CATEGORIES
        // ---------------------------------------------------------
        //
        // New relationship:
        //
        // Loan -> Accession -> Book -> Category
        //
        // NOT:
        // Loan -> Accession -> Book -> categories
        //

        @Query("""
                        SELECT c.name, COUNT(l)
                        FROM Loan l
                        JOIN l.accession a
                        JOIN a.book b
                        JOIN b.category c
                        WHERE l.status =
                            com.example.libback.model.enums.LoanStatus.ACTIVE
                        GROUP BY c.name
                        ORDER BY COUNT(l) DESC
                        """)
        List<Object[]> findPopularCategories();

        // ---------------------------------------------------------
        // RECENT LOANS
        // ---------------------------------------------------------

        List<Loan> findTop5ByOrderByLoanIdDesc();

        long countByMemberMemberIdAndStatus(
                        String memberId,
                        LoanStatus status);
}
