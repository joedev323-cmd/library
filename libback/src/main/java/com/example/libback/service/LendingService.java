package com.example.libback.service;

import com.example.libback.model.*;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class LendingService {

    private final AccessionRepository accessionRepository;
    private final LoanRepository loanRepository;
    private final BorrowerRepository borrowerRepository;

    public LendingService(AccessionRepository accessionRepository, 
                          LoanRepository loanRepository, 
                          BorrowerRepository borrowerRepository) {
        this.accessionRepository = accessionRepository;
        this.loanRepository = loanRepository;
        this.borrowerRepository = borrowerRepository;
    }

    @Transactional
    public Loan checkoutBook(String accessionId, String borrowerId, String staffId) {
        // 1. Verify borrower limits and account standing
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found with ID: " + borrowerId));
        
        // Type-safe enum check (Assumes 'ACTIVE' or 'active' is defined in your userstatus enum)
        if (!borrower.isActive()) {
            throw new IllegalStateException("the students account is not active " );
        }

        // 2. Fetch copy and block simultaneous threads via Database Write Lock
        Accession accession = accessionRepository.findByIdForUpdate(accessionId)
                .orElseThrow(() -> new IllegalArgumentException("Physical copy not found with ID: " + accessionId));

        if (accession.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new IllegalStateException("This physical copy is not currently available for lending.");
        }

        // 3. Mark copy as borrowed
        accession.setAvailabilityStatus(AvailabilityStatus.BORROWED);
        accessionRepository.save(accession);

        // 4. Create loan entry
        Loan loan = new Loan();
        loan.setAccession(accession);
        loan.setBorrower(borrower);
        loan.setStaffId(staffId);
        
        // Dynamic due date logic based on associated item classifications
        int loanDays = accession.getItem().getCategories().stream()
                .mapToInt(Catergory::getLoanPeriodDays)
                .min()
                .orElse(14); // Default to 14 days if category rule is missing
        
        loan.setDueDate(LocalDateTime.now().plusDays(loanDays));

        return loanRepository.save(loan);
    }
}