package com.example.libback.service;

import com.example.libback.model.*;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.BorrowerRepository;
import com.example.libback.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LendingServiceTest {

    @Mock 
    private AccessionRepository accessionRepository;
    
    @Mock 
    private BorrowerRepository borrowerRepository;
    
    @Mock 
    private LoanRepository loanRepository;

    @InjectMocks 
    private LendingService lendingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckoutBook_Success() {
        // Arrange
        String accessionId = "ACC-101";
        String borrowerId = "BOR-202";
        String staffId = "STF-303";

        // Setup Borrower
        Borrower borrower = new Borrower();
        borrower.setBorrowerId(borrowerId);
        borrower.setActive(true); // Bypasses the "is student account active" check

        // Setup Item & Category relationship using a Set
        Catergory category = new Catergory();
        category.setLoanPeriodDays(7);

        Set<Catergory> categories = new HashSet<>();
        categories.add(category);

        Item item = new Item();
        item.setIsbn("1234567890123");
        item.setCategories(categories);

        // Setup Accession with exact setter: setAccessionId
        Accession accession = new Accession();
        accession.setAccessionId(accessionId);
        accession.setItem(item);
        accession.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        // Stub repository responses
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(accessionRepository.findByIdForUpdate(accessionId)).thenReturn(Optional.of(accession));
        when(accessionRepository.save(any(Accession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan loan = lendingService.checkoutBook(accessionId, borrowerId, staffId);

        // Assert
        assertNotNull(loan);
        assertEquals(accession, loan.getAccession());
        assertEquals(borrower, loan.getBorrower());
        assertEquals(staffId, loan.getStaffId());
        assertEquals(AvailabilityStatus.BORROWED, accession.getAvailabilityStatus());
        assertTrue(loan.getDueDate().isAfter(LocalDateTime.now()));

        // Verification
        verify(borrowerRepository, times(1)).findById(borrowerId);
        verify(accessionRepository, times(1)).findByIdForUpdate(accessionId);
        verify(accessionRepository, times(1)).save(accession);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testCheckoutBook_ThrowsException_WhenAccessionNotAvailable() {
        // Arrange
        String accessionId = "ACC-999";
        String borrowerId = "BOR-202";
        String staffId = "STF-303";

        Borrower borrower = new Borrower();
        borrower.setBorrowerId(borrowerId);
        borrower.setActive(true);

        Accession occupiedAccession = new Accession();
        occupiedAccession.setAccessionId(accessionId);
        occupiedAccession.setAvailabilityStatus(AvailabilityStatus.BORROWED); 

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(accessionRepository.findByIdForUpdate(accessionId)).thenReturn(Optional.of(occupiedAccession));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            lendingService.checkoutBook(accessionId, borrowerId, staffId);
        });

        assertEquals("This physical copy is not currently available for lending.", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
    }
}