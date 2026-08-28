package com.example.libback.controller;

import com.example.libback.model.Member;
import com.example.libback.repository.MemberRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.service.AuditLogService;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final AuditLogService auditLogService;
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
         * Calculate the number of currently active loans
         * for each member.
         *
         * The count comes from LoanRepository because Loan
         * is the entity that owns the member relationship.
         */
        for (Member member : members) {

            long activeLoans = loanRepository
                    .countByMemberMemberIdAndStatus(
                            member.getMemberId(),
                            LoanStatus.ACTIVE);

            member.setActiveLoans(activeLoans);
        }

        model.addAttribute("members", members);

        return "members/list";
    }

    @GetMapping("/admin/members/edit/{memberId}")
    public String showEditMemberForm(
            @PathVariable("memberId") String memberId,
            Model model) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found: " + memberId));

        model.addAttribute("member", member);

        return "members/edit";
    }

    @PostMapping("/admin/members/edit/{memberId}")
    public String updateMember(
            @PathVariable("memberId") String memberId,
            @ModelAttribute("member") Member updatedMember,
            RedirectAttributes redirectAttributes) {

        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found: " + memberId));

        existingMember.setName(updatedMember.getName());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setMemberType(updatedMember.getMemberType());

        memberRepository.save(existingMember);

        auditLogService.logAction(
                "UPDATE_MEMBER",
                "MEMBER",
                existingMember.getMemberId(),
                "Updated member: " + existingMember.getName());

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Member successfully updated.");

        return "redirect:/members";
    }

}
