package com.example.libback.service;

import com.example.libback.model.Accession;
import com.example.libback.model.Loan;
import com.example.libback.model.Member;
import com.example.libback.model.User;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.repository.MemberRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class CirculationService {

    private final AccessionRepository accessionRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final AuditLogService auditLogService;

    public CirculationService(
            AccessionRepository accessionRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository,
            AuditLogService auditLogService) {

        this.accessionRepository = accessionRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.auditLogService = auditLogService;
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    @Transactional
    public Loan checkoutItem(
            String accessionId,
            String memberId,
            User issuedBy) {

        // ---------------------------------------------------------
        // 1. Validate issuing user
        // ---------------------------------------------------------

        if (issuedBy == null) {
            throw new IllegalStateException(
                    "No authenticated user was found for this transaction.");
        }

        // ---------------------------------------------------------
        // 2. Find member
        // ---------------------------------------------------------

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found: " + memberId));

        // ---------------------------------------------------------
        // 3. Member must be active
        // ---------------------------------------------------------

        if (!member.isActive()) {
            throw new IllegalStateException(
                    "Member account is inactive.");
        }

        // ---------------------------------------------------------
        // 4. Lock physical copy
        // ---------------------------------------------------------

        Accession accession = accessionRepository
                .findByIdForUpdate(accessionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Accession not found: " + accessionId));

        // ---------------------------------------------------------
        // 5. Physical copy must be available
        // ---------------------------------------------------------

        if (accession.getAvailabilityStatus()
                != AvailabilityStatus.AVAILABLE) {

            throw new IllegalStateException(
                    "This copy is currently "
                            + accession.getAvailabilityStatus());
        }

        // ---------------------------------------------------------
        // 6. Get loan period from category
        // ---------------------------------------------------------

        int loanDays = accession
                .getBook()
                .getCategory()
                .getLoanPeriodDays();

        // ---------------------------------------------------------
        // 7. Create loan
        // ---------------------------------------------------------

        Loan loan = new Loan();

        loan.setAccession(accession);
        loan.setMember(member);

        // The authenticated librarian/admin
        loan.setIssuedBy(issuedBy);

        LocalDateTime checkoutDate =
                LocalDateTime.now();

        loan.setCheckoutDate(checkoutDate);

        loan.setDueDate(
                checkoutDate.plusDays(loanDays));

        loan.setRenewalCount(0);

        loan.setFineAccrued(
                BigDecimal.ZERO);

        loan.setFinePaid(
                BigDecimal.ZERO);

        loan.setStatus(
                LoanStatus.ACTIVE);

        // ---------------------------------------------------------
        // 8. Mark physical copy as borrowed
        // ---------------------------------------------------------

        accession.setAvailabilityStatus(
                AvailabilityStatus.BORROWED);

        accessionRepository.save(accession);

        // ---------------------------------------------------------
        // 9. Save loan
        // ---------------------------------------------------------

        Loan savedLoan =
                loanRepository.save(loan);

        // ---------------------------------------------------------
        // 10. Audit
        // ---------------------------------------------------------

        auditLogService.logAction(
                "BOOK_ISSUED",
                "ACCESSION",
                accessionId,
                "Book issued to member "
                        + memberId
                        + " by user "
                        + issuedBy.getUsername());

        return savedLoan;
    }

    // =========================================================
    // RETURN
    // =========================================================

    @Transactional
    public void returnItem(
            String accessionId,
            String condition) {

        // ---------------------------------------------------------
        // 1. Find active loan
        // ---------------------------------------------------------

        Loan loan = loanRepository
                .findByAccessionAccessionIdAndStatus(
                        accessionId,
                        LoanStatus.ACTIVE)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No active loan found for accession: "
                                        + accessionId));

        LocalDateTime now =
                LocalDateTime.now();

        BigDecimal totalFine =
                BigDecimal.ZERO;

        // =========================================================
        // OVERDUE FINE
        // =========================================================

        if (loan.getDueDate() != null
                && now.isAfter(loan.getDueDate())) {

            long daysLate =
                    ChronoUnit.DAYS.between(
                            loan.getDueDate(),
                            now);

            if (daysLate > 0) {

                // KSH 1 per day late
                totalFine = totalFine.add(
                        BigDecimal.valueOf(daysLate));
            }
        }

        // =========================================================
        // DAMAGE FINE
        // =========================================================

        if (condition != null
                && "Damaged".equalsIgnoreCase(condition)) {

            // KSH 5 damage fee
            totalFine = totalFine.add(
                    new BigDecimal("5.00"));
        }

        // =========================================================
        // UPDATE LOAN
        // =========================================================

        loan.setReturnedDate(now);

        loan.setStatus(
                LoanStatus.RETURNED);

        loan.setFineAccrued(
                totalFine);

        loanRepository.save(loan);

        // =========================================================
        // UPDATE PHYSICAL COPY
        // =========================================================

        Accession accession =
                loan.getAccession();

        accession.setAvailabilityStatus(
                AvailabilityStatus.AVAILABLE);

        accessionRepository.save(accession);

        // =========================================================
        // AUDIT
        // =========================================================

        auditLogService.logAction(
                "ACCESSION",
                accessionId,
                "BOOK_RETURNED",
                "Book returned by member "
                        + loan.getMember().getMemberId()
                        + ". Condition: "
                        + condition
                        + ". Fine: "
                        + totalFine);
    }
}
