package com.example.libback.controller;

import com.example.libback.model.Borrower;
import com.example.libback.repository.BorrowerRepository;
import com.example.libback.repository.HoldRepository;
 
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import com.example.libback.model.enums.LoanStatus;

@Controller
@RequiredArgsConstructor // Lombok automatically autowires borrowerRepository, holdRepository, and loanRepository
public class MemberController {

    private final BorrowerRepository borrowerRepository;
    private final HoldRepository holdRepository; 

    @GetMapping("/admin/add/member")
    public String showAddMemberForm(Model model) {
        model.addAttribute("borrower", new Borrower()); 
        return "add-member"; 
    }

    @PostMapping("/admin/add/member")
    public String registerMember(@ModelAttribute("borrower") Borrower borrower, Model model) {
        try {
            if (borrowerRepository.existsByEmail(borrower.getEmail())) {
                model.addAttribute("errorMessage", "This email address is already registered!");
                model.addAttribute("borrower", borrower);
                return "add-member";
            }

            borrowerRepository.save(borrower);
            return "redirect:/members";

        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Registration failed: A duplicate constraint was violated.");
            model.addAttribute("borrower", borrower);
            return "add-member";
        }
    }

    @GetMapping("/members")
    public String showMembersPage(java.security.Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        
        // 1. Fetch the real database records
        List<Borrower> members = borrowerRepository.findAll();

        // 2. Compute the borrowed count in memory
        for (Borrower member : members) {
            long activeLoans = holdRepository.countByBorrowerBorrowerIdAndStatus(member.getBorrowerId(), LoanStatus.ACTIVE);
            
            // Note: If you want to show 'activeLoans' on your HTML frontend,
            // you will either need to map these to a DTO or set a transient field on your Borrower model!
        }
        
        // 3. Send the fully calculated list straight to members.html
        model.addAttribute("members", members);
        
        return "members"; 
    }
}