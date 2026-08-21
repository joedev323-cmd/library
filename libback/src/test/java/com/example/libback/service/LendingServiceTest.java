package com.example.libback.service;

import com.example.libback.model.Accession;
import com.example.libback.model.Book;
import com.example.libback.model.Category;
import com.example.libback.model.Loan;
import com.example.libback.model.Member;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CirculationServiceTest {

    @Mock
    private AccessionRepository accessionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CirculationService circulationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckoutItem_Success() {

        // ---------------------------------------------------------
        // Arrange
        // ---------------------------------------------------------

        String accessionId = "ACC-101";
        String memberId = "MEM-202";

        // Member
        Member member = new Member();
        member.setMemberId(memberId);
        member.setName("Test Member");
        member.setEmail("member@test.com");
        member.setActive(true);

        // Category
        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("General");
        category.setLoanPeriodDays(7);
        category.setMaxRenewals(2);

        // Book
        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setCategory(category);

        // Physical copy
        Accession accession = new Accession();
        accession.setAccessionId(accessionId);
        accession.setBook(book);
        accession.setAvailabilityStatus(
                AvailabilityStatus.AVAILABLE
        );

        // Repository responses
        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(accessionRepository.findByIdForUpdate(accessionId))
                .thenReturn(Optional.of(accession));

        when(accessionRepository.save(any(Accession.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // ---------------------------------------------------------
        // Act
        // ---------------------------------------------------------

        Loan loan =
                circulationService.checkoutItem(
                        accessionId,
                        memberId
                );

        // ---------------------------------------------------------
        // Assert
        // ---------------------------------------------------------

        assertNotNull(loan);

        assertEquals(
                accession,
                loan.getAccession()
        );

        assertEquals(
                member,
                loan.getMember()
        );

        assertEquals(
                LoanStatus.ACTIVE,
                loan.getStatus()
        );

        assertEquals(
                0,
                loan.getRenewalCount()
        );

        assertEquals(
                AvailabilityStatus.BORROWED,
                accession.getAvailabilityStatus()
        );

        assertNotNull(loan.getCheckoutDate());

        assertNotNull(loan.getDueDate());

        assertTrue(
                loan.getDueDate()
                        .isAfter(loan.getCheckoutDate())
        );

        assertEquals(
                7,
                java.time.Duration.between(
                        loan.getCheckoutDate(),
                        loan.getDueDate()
                ).toDays()
        );

        // ---------------------------------------------------------
        // Verification
        // ---------------------------------------------------------

        verify(memberRepository, times(1))
                .findById(memberId);

        verify(accessionRepository, times(1))
                .findByIdForUpdate(accessionId);

        verify(accessionRepository, times(1))
                .save(accession);

        verify(loanRepository, times(1))
                .save(any(Loan.class));

        verify(auditLogService, times(1))
                .logAction(
                        "ACCESSION",
                        accessionId,
                        "BOOK_ISSUED",
                        "Book issued to member " + memberId
                );
    }

    @Test
    void testCheckoutItem_ThrowsException_WhenAccessionNotAvailable() {

        // ---------------------------------------------------------
        // Arrange
        // ---------------------------------------------------------

        String accessionId = "ACC-999";
        String memberId = "MEM-202";

        Member member = new Member();
        member.setMemberId(memberId);
        member.setActive(true);

        Accession occupiedAccession = new Accession();

        occupiedAccession.setAccessionId(accessionId);

        occupiedAccession.setAvailabilityStatus(
                AvailabilityStatus.BORROWED
        );

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(accessionRepository.findByIdForUpdate(accessionId))
                .thenReturn(Optional.of(occupiedAccession));

        // ---------------------------------------------------------
        // Act + Assert
        // ---------------------------------------------------------

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> circulationService.checkoutItem(
                                accessionId,
                                memberId
                        )
                );

        assertEquals(
                "This copy is currently BORROWED",
                exception.getMessage()
        );

        // No loan should be created
        verify(loanRepository, never())
                .save(any(Loan.class));

        // No inventory update should occur
        verify(accessionRepository, never())
                .save(any(Accession.class));

        // No audit should occur
        verify(auditLogService, never())
                .logAction(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void testCheckoutItem_ThrowsException_WhenMemberNotFound() {

        String accessionId = "ACC-101";
        String memberId = "MEM-404";

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> circulationService.checkoutItem(
                                accessionId,
                                memberId
                        )
                );

        assertEquals(
                "Member not found: " + memberId,
                exception.getMessage()
        );

        verify(accessionRepository, never())
                .findByIdForUpdate(anyString());

        verify(loanRepository, never())
                .save(any(Loan.class));
    }

    @Test
    void testCheckoutItem_ThrowsException_WhenMemberInactive() {

        String accessionId = "ACC-101";
        String memberId = "MEM-303";

        Member member = new Member();
        member.setMemberId(memberId);
        member.setActive(false);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> circulationService.checkoutItem(
                                accessionId,
                                memberId
                        )
                );

        assertEquals(
                "Member account is inactive.",
                exception.getMessage()
        );

        verify(accessionRepository, never())
                .findByIdForUpdate(anyString());

        verify(loanRepository, never())
                .save(any(Loan.class));
    }

    @Test
    void testCheckoutItem_ThrowsException_WhenAccessionNotFound() {

        String accessionId = "ACC-404";
        String memberId = "MEM-202";

        Member member = new Member();
        member.setMemberId(memberId);
        member.setActive(true);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(accessionRepository.findByIdForUpdate(accessionId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> circulationService.checkoutItem(
                                accessionId,
                                memberId
                        )
                );

        assertEquals(
                "Accession not found: " + accessionId,
                exception.getMessage()
        );

        verify(loanRepository, never())
                .save(any(Loan.class));
    }
}
