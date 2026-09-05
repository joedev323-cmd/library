package com.example.libback.service;

import com.example.libback.model.Accession;
import com.example.libback.model.Loan;
import com.example.libback.model.Member;
import com.example.libback.model.Payment;
import com.example.libback.model.User;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.model.enums.PaymentMethod;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.repository.MemberRepository;
import com.example.libback.repository.PaymentRepository;
import java.util.List;


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
        private final PaymentRepository paymentRepository;

        public CirculationService(
                        AccessionRepository accessionRepository,
                        MemberRepository memberRepository,
                        LoanRepository loanRepository,
                        PaymentRepository paymentRepository,
                        AuditLogService auditLogService) {

                this.accessionRepository = accessionRepository;
                this.memberRepository = memberRepository;
                this.loanRepository = loanRepository;
                this.paymentRepository = paymentRepository;
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

                if (accession.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {

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

                LocalDateTime checkoutDate = LocalDateTime.now();

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

                Loan savedLoan = loanRepository.save(loan);

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
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No active loan found for accession: "
                                                                + accessionId));

                LocalDateTime now = LocalDateTime.now();

                BigDecimal totalFine = BigDecimal.ZERO;

                // =========================================================
                // OVERDUE FINE
                // =========================================================

                if (loan.getDueDate() != null
                                && now.isAfter(loan.getDueDate())) {

                        long daysLate = ChronoUnit.DAYS.between(
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

                Accession accession = loan.getAccession();

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

        @Transactional
        public Loan renewLoan(
                        String accessionId) {

                Loan loan = loanRepository
                                .findByAccessionAccessionIdAndStatus(
                                                accessionId,
                                                LoanStatus.ACTIVE)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No active loan found for accession: "
                                                                + accessionId));

                // ---------------------------------------------------------
                // Maximum renewals
                // ---------------------------------------------------------

                int maxRenewals = 2;

                if (loan.getRenewalCount() >= maxRenewals) {
                        throw new IllegalStateException(
                                        "This loan has reached the maximum number of renewals.");
                }

                // ---------------------------------------------------------
                // Overdue loans cannot be renewed
                // ---------------------------------------------------------

                if (loan.isOverdue()) {
                        throw new IllegalStateException(
                                        "An overdue loan cannot be renewed.");
                }

                // ---------------------------------------------------------
                // Calculate new due date
                // ---------------------------------------------------------

                int loanDays = loan
                                .getAccession()
                                .getBook()
                                .getCategory()
                                .getLoanPeriodDays();

                LocalDateTime newDueDate = loan.getDueDate().plusDays(loanDays);

                loan.setDueDate(newDueDate);

                loan.setRenewalCount(
                                loan.getRenewalCount() + 1);

                Loan savedLoan = loanRepository.save(loan);

                // ---------------------------------------------------------
                // Audit
                // ---------------------------------------------------------

                auditLogService.logAction(
                                "BOOK_RENEWED",
                                "LOAN",
                                String.valueOf(loan.getLoanId()),
                                "Loan renewed for accession "
                                                + accessionId
                                                + ". New due date: "
                                                + newDueDate
                                                + ". Renewal "
                                                + loan.getRenewalCount()
                                                + " of "
                                                + maxRenewals);

                return savedLoan;
        }

        @Transactional
        public Payment processFinePayment(
                        Long loanId,
                        BigDecimal amount,
                        String receiptNumber,
                        String remarks,
                        User receivedBy) {

                if (receivedBy == null) {
                        throw new IllegalStateException(
                                        "No authenticated user was found.");
                }

                if (amount == null
                                || amount.compareTo(BigDecimal.ZERO) <= 0) {

                        throw new IllegalArgumentException(
                                        "Payment amount must be greater than zero.");
                }

                Loan loan = loanRepository
                                .findById(loanId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Loan not found: " + loanId));

                // ---------------------------------------------------------
                // Fine must already have been assessed
                // ---------------------------------------------------------

                if (loan.getFineAccrued() == null
                                || loan.getFineAccrued()
                                                .compareTo(BigDecimal.ZERO) <= 0) {

                        throw new IllegalStateException(
                                        "This loan has no assessed fine.");
                }

                BigDecimal outstanding = loan.getOutstandingFine();

                if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalStateException(
                                        "This fine has already been fully paid.");
                }

                // ---------------------------------------------------------
                // Prevent overpayment
                // ---------------------------------------------------------

                if (amount.compareTo(outstanding) > 0) {
                        throw new IllegalArgumentException(
                                        "Payment exceeds the outstanding fine of KSh "
                                                        + outstanding);
                }

                // ---------------------------------------------------------
                // Create payment transaction
                // ---------------------------------------------------------

                Payment payment = new Payment();

                payment.setLoan(loan);
                payment.setAmount(amount);
                payment.setPaymentMethod(PaymentMethod.CASH);
                payment.setReceivedBy(receivedBy);
                payment.setReceiptNumber(receiptNumber);
                payment.setRemarks(remarks);

                Payment savedPayment = paymentRepository.save(payment);

                // ---------------------------------------------------------
                // Update loan financial summary
                // ---------------------------------------------------------

                BigDecimal currentPaid = loan.getFinePaid() != null
                                ? loan.getFinePaid()
                                : BigDecimal.ZERO;

                loan.setFinePaid(
                                currentPaid.add(amount));

                loanRepository.save(loan);

                // ---------------------------------------------------------
                // Audit
                // ---------------------------------------------------------

                auditLogService.logAction(
                                "FINE_PAYMENT",
                                "LOAN",
                                String.valueOf(loan.getLoanId()),
                                "Fine payment of KSh "
                                                + amount
                                                + " received. Receipt: "
                                                + receiptNumber);

                return savedPayment;
        }

        @Transactional(readOnly = true)
        public Loan findLoanByAccession(
                        String accessionId) {

                return loanRepository
                                .findByAccessionAccessionId(accessionId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No loan found for accession: "
                                                                + accessionId));
        }

        @Transactional(readOnly = true)
        public List<Loan> findLoansWithOutstandingFine(
                        String accessionId) {

                return loanRepository
                                .findLoansWithOutstandingFineByAccession(accessionId);
        }

}
