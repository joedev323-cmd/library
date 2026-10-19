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
import com.example.libback.notification.dto.NotificationRequest;
import com.example.libback.notification.service.NotificationService;

import java.time.Instant;
import java.time.ZoneId;


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
        private final NotificationService notificationService;

        private final LibraryCalendarService libraryCalendarService;

        public CirculationService(
                AccessionRepository accessionRepository,
                MemberRepository memberRepository,
                LoanRepository loanRepository,
                PaymentRepository paymentRepository,
                AuditLogService auditLogService,
                NotificationService notificationService,
                LibraryCalendarService libraryCalendarService) {

                this.accessionRepository = accessionRepository;
                this.memberRepository = memberRepository;
                this.loanRepository = loanRepository;
                this.paymentRepository = paymentRepository;
                this.auditLogService = auditLogService;
                this.notificationService = notificationService;
                this.libraryCalendarService = libraryCalendarService;
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

                LocalDateTime calculatedDueDate =
                        checkoutDate.plusDays(loanDays);

                LocalDateTime adjustedDueDate =
                        libraryCalendarService.moveToNextOpenDate(
                                calculatedDueDate);

                loan.setDueDate(adjustedDueDate);

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

                // notify the borrower
                notifyBorrower(
                        member,
                        "Book checkout confirmation",
                        "Hello " + member.getName()
                                + ", you borrowed \""
                                + accession.getBook().getTitle()
                                + "\". Please return it by "
                                + savedLoan.getDueDate() + ".",
                        savedLoan.getDueDate()
                );

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
                // OVERDUE FINE - EXCLUDE RECORDED CLOSURE DAYS
                // =========================================================

                if (loan.getDueDate() != null
                        && now.isAfter(loan.getDueDate())) {

                        long chargeableDays =
                                libraryCalendarService.countChargeableDays(
                                        loan.getDueDate(),
                                        now);

                        BigDecimal fineRate = BigDecimal.valueOf(15);

                        totalFine = fineRate.multiply(
                                BigDecimal.valueOf(chargeableDays));
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

                notifyBorrower(
                        loan.getMember(),
                        "Book return confirmation",
                        "Hello " + loan.getMember().getName()
                                + ", your return of \""
                                + loan.getAccession().getBook().getTitle()
                                + "\" has been recorded. Fine assessed: KSh "
                                + totalFine + ".",
                        LocalDateTime.now().plusDays(1)
                );

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

                LocalDateTime calculatedDueDate =
                        loan.getDueDate().plusDays(loanDays);

                LocalDateTime newDueDate =
                        libraryCalendarService.moveToNextOpenDate(
                                calculatedDueDate);

                loan.setDueDate(newDueDate);

                loan.setRenewalCount(
                                loan.getRenewalCount() + 1);

                Loan savedLoan = loanRepository.save(loan);

                notifyBorrower(
                        savedLoan.getMember(),
                        "Book renewal confirmation",
                        "Your loan for \""
                                + savedLoan.getAccession().getBook().getTitle()
                                + "\" has been renewed. New due date: "
                                + savedLoan.getDueDate() + ".",
                        savedLoan.getDueDate()
                );

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
public void processFinePayment(
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
        // Create payment
        // ---------------------------------------------------------

        Payment payment = new Payment();

        payment.setLoan(loan);
        payment.setAmount(amount);
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setReceivedBy(receivedBy);
        payment.setRemarks(remarks);

        // ---------------------------------------------------------
        // Save first so paymentId is generated
        // ---------------------------------------------------------

        Payment savedPayment = paymentRepository.save(payment);

        // ---------------------------------------------------------
        // Generate automatic receipt number
        // ---------------------------------------------------------

        if (receiptNumber == null || receiptNumber.isBlank()) {

                receiptNumber = String.format(
                                "RCP-%06d",
                                savedPayment.getPaymentId());
        }

        savedPayment.setReceiptNumber(receiptNumber);

        savedPayment = paymentRepository.save(savedPayment);

        // ---------------------------------------------------------
        // Update loan financial summary
        // ---------------------------------------------------------

        BigDecimal currentPaid = loan.getFinePaid() != null
                        ? loan.getFinePaid()
                        : BigDecimal.ZERO;

        loan.setFinePaid(
                        currentPaid.add(amount));

        loanRepository.save(loan);


               notifyBorrower(
                       loan.getMember(),
                       "Fine payment receipt",
                       "Hello " + loan.getMember().getName()
                               + ", your payment of KSh " + amount
                               + " has been recorded. Receipt: "
                               + receiptNumber
                               + ". Outstanding fine: KSh "
                               + loan.getOutstandingFine() + ".",
                       LocalDateTime.now().plusDays(1)
               );

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

       }

        @Transactional(readOnly = true)
        public Loan findLoanByAccession(
                        String accessionId) {

                return loanRepository
                                .findByAccessionAccessionIdAndStatus(accessionId,LoanStatus.ACTIVE)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No loan found for accession: "
                                                                + accessionId));
        }

        @Transactional(readOnly = true)
        public List<Loan> findLoansWithOutstandingFine(
                        String memberId) {

                return loanRepository
                                .findLoansWithOutstandingFineByMember(memberId);
        }


        private void notifyBorrower(
                Member member,
                String subject,
                String message,
                LocalDateTime deadline) {

                Instant now = Instant.now();

                Instant deadlineAt = deadline
                        .atZone(ZoneId.systemDefault())
                        .toInstant();

                // NotificationService requires a future deadline.
                if (!deadlineAt.isAfter(now)) {
                        deadlineAt = now.plusSeconds(86400);
                }

                notificationService.create(
                        new NotificationRequest(
                                member.getEmail(),
                                subject,
                                message,
                                deadlineAt,
                                now
                        )
                );
        }


}
