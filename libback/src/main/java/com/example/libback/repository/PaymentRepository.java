package com.example.libback.repository;

import com.example.libback.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

        List<Payment> findByLoanLoanIdOrderByPaymentDateDesc(Long loanId);

        @Query("""
                        SELECT COALESCE(SUM(p.amount), 0)
                        FROM Payment p
                        WHERE p.paymentDate >= :startDate
                          AND p.paymentDate < :endDate
                        """)
        BigDecimal sumPaymentsCollectedBetween(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

}
