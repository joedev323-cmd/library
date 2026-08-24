package com.example.libback.controller;

import com.example.libback.model.Member;
import com.example.libback.repository.MemberRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.model.enums.LoanStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    @GetMapping("/admin/add/member")
    public String showAddMemberForm(Model model) {
        model.addAttribute("member", new Member());
        return "members/add";
    }

    @PostMapping("/admin/add/member")
    public String registerMember(
            @ModelAttribute("member") Member member,
            Model model) {

        try {

            if (memberRepository.existsByEmail(member.getEmail())) {
                model.addAttribute(
                        "errorMessage",
                        "This email address is already registered!");

                model.addAttribute("member", member);

                return "members/add";
            }

            memberRepository.save(member);

            return "redirect:/members";

        } catch (DataIntegrityViolationException e) {

            model.addAttribute(
                    "errorMessage",
                    "Registration failed: A duplicate constraint was violated.");

            model.addAttribute("member", member);

            return "members/add";

        }
    }

    @GetMapping("/members")
    public String showMembersPage(
            java.security.Principal principal,
            Model model) {

        if (principal != null) {
            model.addAttribute(
                    "username",
                    principal.getName());
        }

        List<Member> members = memberRepository.findAll();

        /*
         * Calculate active loans using LoanRepository.
         *
         * The old code incorrectly queried HoldRepository.
         */
        for (Member member : members) {

            long activeLoans = loanRepository
                    .countByMemberMemberIdAndStatus(
                            member.getMemberId(),
                            LoanStatus.ACTIVE);

            /*
             * Do not attach activeLoans to Member unless
             * Member has a suitable @Transient field.
             *
             * For now this simply verifies the calculation
             * is based on the new Loan -> Member relationship.
             */
        }

        model.addAttribute("members", members);

        return "members/list";
    }
}
