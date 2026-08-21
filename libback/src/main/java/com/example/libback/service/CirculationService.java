package com.example.libback.service;

import com.example.libback.model.*;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.libback.model.enums.AvailabilityStatus; 

@Service
public class CirculationService {

    private final AccessionRepository accessionRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;

    public CirculationService(AccessionRepository accessionRepository, 
                              BorrowerRepository borrowerRepository, 
                              LoanRepository loanRepository) {
        this.accessionRepository = accessionRepository;
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public void checkoutItem(String accessionId, String borrowerId) {
        // 1. Fetch managed Accession entity reference
        Accession accession = accessionRepository.findById(accessionId)
                .orElseThrow(() -> new IllegalArgumentException("Accession ID/Barcode not found."));

        // 2. Validate availability status
        if (!"AVAILABLE".equals(accession.getAvailabilityStatus().name())) {
            throw new IllegalStateException("This book copy is currently " + accession.getAvailabilityStatus());
        }

        // 3. Fetch managed Borrower entity reference
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower ID not found."));
        
        if (!borrower.isActive()) {
            throw new IllegalStateException("Borrower account is inactive.");
        }

        // 4. Populate object mappings inside Loan transaction
        Loan loan = new Loan();
        loan.setAccession(accession); // Assigns object graph reference
        loan.setBorrower(borrower);   // Assigns object graph reference
        loan.setCheckoutDate(LocalDateTime.now());
        loan.setDueDate(LocalDateTime.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE); // Uses your custom Enum
        loan.setFineAccrued(BigDecimal.ZERO); // Uses your BigDecimal layout
        loan.setRenewalCount(0);
        
        loanRepository.save(loan);

        // 5. Update book copy state flags to complete integration
        // Ensure your AvailabilityStatus enum matching maps to your target states
        accession.setAvailabilityStatus(AvailabilityStatus.BORROWED);
        accessionRepository.save(accession);
    }


    @Transactional
public void returnItem(String accessionId, String condition) {
    // 1. Locate the active loan transaction record for this copy
    Loan loan = loanRepository.findByAccessionAccessionIdAndStatus(accessionId, LoanStatus.ACTIVE)
            .orElseThrow(() -> new IllegalArgumentException("No active loan transaction found for this book copy barcode."));

    LocalDateTime now = LocalDateTime.now();
    BigDecimal totalFine = BigDecimal.ZERO;

    // 2. Overdue Check Rule: Charge ksh1.00 per day late
    if (now.isAfter(loan.getDueDate())) {
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), now);
        if (daysLate > 0) {
            totalFine = totalFine.add(BigDecimal.valueOf(daysLate * 1.00));
        }
    }

    // 3. Condition Damage Assessment Rule: Flat fee addition
    if ("Damaged".equalsIgnoreCase(condition)) {
        totalFine = totalFine.add(new BigDecimal("5.00"));
    }

    // 4. Update core Loan state metrics
    loan.setReturnedDate(now);
    loan.setStatus(LoanStatus.RETURNED);
    loan.setFineAccrued(totalFine);
    loanRepository.save(loan);

    // 5. Release book copy back to inventory availability
    Accession accession = loan.getAccession();
    // Use Accession.AvailabilityStatus.AVAILABLE if nested inside class
    accession.setAvailabilityStatus(AvailabilityStatus.AVAILABLE); 
    accessionRepository.save(accession);
}
@Transactional
public void payFine(Long loanId, BigDecimal paymentAmount) {
    Loan loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        
    // Update the paid amount
    BigDecimal newPaidTotal = loan.getFinePaid().add(paymentAmount);
    loan.setFinePaid(newPaidTotal);
    
    loanRepository.save(loan);
}
}