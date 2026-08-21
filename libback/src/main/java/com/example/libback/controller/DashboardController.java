package com.example.libback.controller;

import com.example.libback.dto.CirculationLogDto;
import com.example.libback.dto.DashboardStats;
import com.example.libback.model.Loan;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.MemberRepository;
import com.example.libback.repository.LoanRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final MemberRepository borrowerRepository;
    private final LoanRepository loanRepository;
    private final AccessionRepository accessionRepository;

    @GetMapping({"/dashboard"})
    public String showDashboard(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        // 1. Calculate Real-time Statistics
        long totalBooks = accessionRepository.count();
        long booksOut = loanRepository.countByStatus(LoanStatus.ACTIVE); 
        long overdueCount = loanRepository.countByStatusAndDueDateBefore(LoanStatus.ACTIVE, LocalDateTime.now());
        long activeMembers = borrowerRepository.countByActiveTrue();

        DashboardStats stats = new DashboardStats(totalBooks, booksOut, overdueCount, activeMembers);
        model.addAttribute("stats", stats);

        // 2. Build Real-time Circulation Logs
       // 2. Build Real-time Circulation Logs
List<CirculationLogDto> logs = new ArrayList<>();

// Fetch the latest 5 transactions from the database
List<Loan> recentLoans = loanRepository.findTop5ByOrderByLoanIdDesc();

// Inside your showDashboard loop:
for (Loan loan : recentLoans) {
    // 1. Get the title from Item
    String bookTitle = "Unknown Title";
    if (loan.getAccession() != null && loan.getAccession().getBook() != null) {
        bookTitle = loan.getAccession().getBook().getTitle();
    }

    // 2. Get Borrower details
    String borrowerName = (loan.getMember() != null) ? loan.getMember().getName() : "Unknown Member";
    String borrowerId = (loan.getMember() != null) ? loan.getMember().getMemberId() : "N/A";

    // 3. Action
    String action = (loan.getStatus() == LoanStatus.RETURNED) ? "Returned" : "Issued";

    // 4. Conversion to LocalDate
    LocalDate dueDate = (loan.getDueDate() != null) ? loan.getDueDate().toLocalDate() : null;

    // 5. Dynamic Status Calculation
    String status;
    if (loan.getStatus() == LoanStatus.RETURNED) {
        status = "In Library";
    } else if (loan.getDueDate() != null && loan.getDueDate().isBefore(LocalDateTime.now())) {
        status = "Overdue";
    } else {
        status = "On Loan";
    }

    // Add to logs using our matching variable names
    logs.add(new CirculationLogDto(bookTitle, borrowerName, borrowerId, action, dueDate, status));

    }
        // Fallback demo data ONLY if your database transactions are completely empty
        if (logs.isEmpty()) {
            logs.add(new CirculationLogDto("The Greatman is a fallback for no data  Gatsby", "Robert Downey", "M-402", "Issued", LocalDate.now().minusDays(2), "On Loan"));
            logs.add(new CirculationLogDto("Introduction to Algorithms", "Sarah Jenkins", "M-115", "Returned", null, "In Library"));
        }

        model.addAttribute("logs", logs);

        return "dashboard"; // Renders dashboard.html
    }
}